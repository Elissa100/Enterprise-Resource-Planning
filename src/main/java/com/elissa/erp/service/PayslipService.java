package com.elissa.erp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elissa.erp.dto.payslip.PayslipGenerationRequest;
import com.elissa.erp.dto.payslip.PayslipResponse;
import com.elissa.erp.dto.payslip.PayslipStatusUpdateRequest;
import com.elissa.erp.model.Employee;
import com.elissa.erp.model.Employment;
import com.elissa.erp.model.Payslip;
import com.elissa.erp.repository.DeductionRepository;
import com.elissa.erp.repository.EmployeeRepository;
import com.elissa.erp.repository.EmploymentRepository;
import com.elissa.erp.repository.PayslipRepository;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayslipService {

    private final PayslipRepository payslipRepository;
    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;
    private final DeductionRepository deductionRepository;
    private final MessageService messageService;

    public List<PayslipResponse> getAllPayslips() {
        return payslipRepository.findAll().stream()
                .map(PayslipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public PayslipResponse getPayslipById(Long id) {
        return payslipRepository.findById(id)
                .map(PayslipResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Payslip not found with id: " + id));
    }

    public List<PayslipResponse> getPayslipsByEmployeeCode(String employeeCode) {
        var employee = employeeRepository.findByCodeIgnoreCase(employeeCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with code: " + employeeCode));

        return payslipRepository.findByEmployee(employee).stream()
                .map(PayslipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PayslipResponse> getPayslipsByMonthAndYear(Integer month, Integer year) {
        return payslipRepository.findByMonthAndYear(month, year).stream()
                .map(PayslipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public PayslipResponse getPayslipByEmployeeAndMonthAndYear(String employeeCode, Integer month, Integer year) {
        var employee = employeeRepository.findByCodeIgnoreCase(employeeCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with code: " + employeeCode));

        return payslipRepository.findByEmployeeAndMonthAndYear(employee, month, year)
                .map(PayslipResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Payslip not found for employee with code: " + employeeCode + " for month: " + month + " and year: " + year));
    }

    @Transactional
    public List<PayslipResponse> generatePayroll(PayslipGenerationRequest request) {
        Integer month = request.getMonth();
        Integer year = request.getYear();

        // Get all active employees with active employments
        List<Employee> activeEmployees = employeeRepository.findAll().stream()
                .filter(employee -> employee.getStatus() == Employee.EmployeeStatus.ACTIVE)
                .collect(Collectors.toList());

        List<Payslip> generatedPayslips = new ArrayList<>();

        for (Employee employee : activeEmployees) {
            // Check if payslip already exists for this employee, month, and year
            if (payslipRepository.existsByEmployeeAndMonthAndYear(employee, month, year)) {
                continue; // Skip this employee
            }

            // Get active employment for this employee
            List<Employment> activeEmployments = employmentRepository.findByEmployeeAndStatus(employee, Employment.EmploymentStatus.ACTIVE);
            if (activeEmployments.isEmpty()) {
                continue; // Skip this employee
            }

            // Use the first active employment (assuming an employee can have only one active employment)
            Employment employment = activeEmployments.get(0);
            BigDecimal baseSalary = employment.getBaseSalary();

            // Get deduction percentages
            BigDecimal employeeTaxPercentage = getDeductionPercentage("Employee Tax");
            BigDecimal pensionPercentage = getDeductionPercentage("Pension");
            BigDecimal medicalInsurancePercentage = getDeductionPercentage("Medical Insurance");
            BigDecimal housingPercentage = getDeductionPercentage("Housing");
            BigDecimal transportPercentage = getDeductionPercentage("Transport");
            BigDecimal othersPercentage = getDeductionPercentage("Others");

            // Calculate amounts
            BigDecimal houseAmount = calculatePercentage(baseSalary, housingPercentage);
            BigDecimal transportAmount = calculatePercentage(baseSalary, transportPercentage);
            BigDecimal grossSalary = baseSalary.add(houseAmount).add(transportAmount);

            BigDecimal employeeTaxedAmount = calculatePercentage(baseSalary, employeeTaxPercentage);
            BigDecimal pensionAmount = calculatePercentage(baseSalary, pensionPercentage);
            BigDecimal medicalInsuranceAmount = calculatePercentage(baseSalary, medicalInsurancePercentage);
            BigDecimal otherTaxedAmount = calculatePercentage(baseSalary, othersPercentage);

            BigDecimal totalDeductions = employeeTaxedAmount.add(pensionAmount).add(medicalInsuranceAmount).add(otherTaxedAmount);
            BigDecimal netSalary = grossSalary.subtract(totalDeductions);

            // Create payslip
            Payslip payslip = Payslip.builder()
                    .employee(employee)
                    .houseAmount(houseAmount)
                    .transportAmount(transportAmount)
                    .employeeTaxedAmount(employeeTaxedAmount)
                    .pensionAmount(pensionAmount)
                    .medicalInsuranceAmount(medicalInsuranceAmount)
                    .otherTaxedAmount(otherTaxedAmount)
                    .grossSalary(grossSalary)
                    .netSalary(netSalary)
                    .month(month)
                    .year(year)
                    .status(Payslip.PayslipStatus.PENDING)
                    .build();

            generatedPayslips.add(payslipRepository.save(payslip));
        }

        return generatedPayslips.stream()
                .map(PayslipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public PayslipResponse updatePayslipStatus(Long id, PayslipStatusUpdateRequest request) {
        var payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payslip not found with id: " + id));

        payslip.setStatus(request.getStatus());
        var updatedPayslip = payslipRepository.save(payslip);

        // If status is changed to PAID, create a message
        if (request.getStatus() == Payslip.PayslipStatus.PAID) {
            messageService.createPayslipPaidMessage(updatedPayslip);
        }

        return PayslipResponse.fromEntity(updatedPayslip);
    }

    private BigDecimal getDeductionPercentage(String deductionName) {
        return deductionRepository.findByDeductionNameIgnoreCase(deductionName)
                .map(deduction -> deduction.getPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                .orElseThrow(() -> new IllegalArgumentException("Deduction not found with name: " + deductionName));
    }

    private BigDecimal calculatePercentage(BigDecimal amount, BigDecimal percentage) {
        return amount.multiply(percentage).setScale(2, RoundingMode.HALF_UP);
    }

    public List<PayslipResponse> getPendingPayslipsForCurrentEmployee(Employee currentEmployee) {
        return payslipRepository.findByEmployeeAndStatus(currentEmployee, Payslip.PayslipStatus.PENDING).stream()
                .map(PayslipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ResponseEntity<Resource> downloadPayslip(Long payslipId, Employee currentEmployee, String format) {
        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new IllegalArgumentException("Payslip not found with id: " + payslipId));

        // Check if the payslip belongs to the current employee
        if (!payslip.getEmployee().getId().equals(currentEmployee.getId())) {
            throw new IllegalArgumentException("You are not authorized to download this payslip");
        }

        // Note: This method allows multiple downloads of the same payslip
        // The payslip status is not changed after download

        // Generate payslip content
        String content = generatePayslipContent(payslip);

        // Determine file extension and content type based on format
        String fileExtension;
        MediaType contentType;
        byte[] fileContent;

        if ("pdf".equalsIgnoreCase(format)) {
            fileExtension = ".pdf";
            contentType = MediaType.APPLICATION_PDF;
            fileContent = generatePdfContent(content);
        } else {
            // Default to txt format
            fileExtension = ".txt";
            contentType = MediaType.TEXT_PLAIN;
            fileContent = content.getBytes(StandardCharsets.UTF_8);
        }

        // Create resource from content
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payslip_" + 
                payslip.getEmployee().getCode() + "_" + 
                payslip.getMonth() + "_" + 
                payslip.getYear() + fileExtension);

        // Return response entity
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(contentType)
                .body(resource);
    }

    /**
     * Generates PDF content from text content
     * This is a simple implementation without using external PDF libraries
     */
    private byte[] generatePdfContent(String textContent) {
        try {
            // Create a simple PDF document using Java's built-in classes
            // This is a very basic implementation that simulates a PDF
            // In a real-world scenario, you would use a proper PDF library

            // Create a ByteArrayOutputStream to hold the PDF content
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Add a simple PDF header
            String pdfHeader = "%PDF-1.4\n";
            baos.write(pdfHeader.getBytes(StandardCharsets.UTF_8));

            // Add a simple object that contains the text content
            String objectStart = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";
            baos.write(objectStart.getBytes(StandardCharsets.UTF_8));

            // Add a simple pages object
            String pagesObj = "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n";
            baos.write(pagesObj.getBytes(StandardCharsets.UTF_8));

            // Add a page object
            String pageObj = "3 0 obj\n<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n";
            baos.write(pageObj.getBytes(StandardCharsets.UTF_8));

            // Add a font object
            String fontObj = "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n";
            baos.write(fontObj.getBytes(StandardCharsets.UTF_8));

            // Add the content stream with the text content
            String escapedContent = textContent.replace("\n", "\\n");
            String contentObj = "5 0 obj\n<< /Length " + escapedContent.length() + " >>\nstream\nBT /F1 12 Tf 50 700 Td (" + escapedContent + ") Tj ET\nendstream\nendobj\n";
            baos.write(contentObj.getBytes(StandardCharsets.UTF_8));

            // Add a simple trailer
            String trailer = "trailer\n<< /Root 1 0 R /Size 6 >>\nstartxref\n0\n%%EOF";
            baos.write(trailer.getBytes(StandardCharsets.UTF_8));

            return baos.toByteArray();
        } catch (Exception e) {
            // If PDF generation fails, return the text content as bytes
            return textContent.getBytes(StandardCharsets.UTF_8);
        }
    }

    private String generatePayslipContent(Payslip payslip) {
        Employee employee = payslip.getEmployee();

        StringBuilder sb = new StringBuilder();
        sb.append("PAYSLIP\n");
        sb.append("==============================\n\n");
        sb.append("Employee: ").append(employee.getFirstName()).append(" ").append(employee.getLastName()).append("\n");
        sb.append("Employee Code: ").append(employee.getCode()).append("\n");
        sb.append("Month: ").append(payslip.getMonth()).append("\n");
        sb.append("Year: ").append(payslip.getYear()).append("\n");
        sb.append("Status: ").append(payslip.getStatus()).append("\n\n");

        sb.append("EARNINGS\n");
        sb.append("------------------------------\n");
        sb.append("Basic Salary: ").append(payslip.getGrossSalary().subtract(payslip.getHouseAmount()).subtract(payslip.getTransportAmount())).append("\n");
        sb.append("House Allowance: ").append(payslip.getHouseAmount()).append("\n");
        sb.append("Transport Allowance: ").append(payslip.getTransportAmount()).append("\n");
        sb.append("Gross Salary: ").append(payslip.getGrossSalary()).append("\n\n");

        sb.append("DEDUCTIONS\n");
        sb.append("------------------------------\n");
        sb.append("Employee Tax: ").append(payslip.getEmployeeTaxedAmount()).append("\n");
        sb.append("Pension: ").append(payslip.getPensionAmount()).append("\n");
        sb.append("Medical Insurance: ").append(payslip.getMedicalInsuranceAmount()).append("\n");
        sb.append("Other Deductions: ").append(payslip.getOtherTaxedAmount()).append("\n");
        sb.append("Total Deductions: ").append(
                payslip.getEmployeeTaxedAmount()
                        .add(payslip.getPensionAmount())
                        .add(payslip.getMedicalInsuranceAmount())
                        .add(payslip.getOtherTaxedAmount())
        ).append("\n\n");

        sb.append("NET SALARY: ").append(payslip.getNetSalary()).append("\n");
        sb.append("==============================\n");

        return sb.toString();
    }
}
