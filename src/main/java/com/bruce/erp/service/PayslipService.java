package com.bruce.erp.service;

import com.bruce.erp.dto.payslip.PayslipGenerationRequest;
import com.bruce.erp.dto.payslip.PayslipResponse;
import com.bruce.erp.dto.payslip.PayslipStatusUpdateRequest;
import com.bruce.erp.model.entity.Employee;
import com.bruce.erp.model.entity.Employment;
import com.bruce.erp.model.entity.Payslip;
import com.bruce.erp.repository.DeductionRepository;
import com.bruce.erp.repository.EmployeeRepository;
import com.bruce.erp.repository.EmploymentRepository;
import com.bruce.erp.repository.PayslipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
}
