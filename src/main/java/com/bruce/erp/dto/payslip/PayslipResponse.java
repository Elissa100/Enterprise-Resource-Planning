package com.bruce.erp.dto.payslip;

import com.bruce.erp.dto.employee.EmployeeResponse;
import com.bruce.erp.model.entity.Payslip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayslipResponse {

    private Long id;
    private EmployeeResponse employee;
    private BigDecimal houseAmount;
    private BigDecimal transportAmount;
    private BigDecimal employeeTaxedAmount;
    private BigDecimal pensionAmount;
    private BigDecimal medicalInsuranceAmount;
    private BigDecimal otherTaxedAmount;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private Integer month;
    private Integer year;
    private Payslip.PayslipStatus status;

    public static PayslipResponse fromEntity(Payslip payslip) {
        return PayslipResponse.builder()
                .id(payslip.getId())
                .employee(EmployeeResponse.fromEntity(payslip.getEmployee()))
                .houseAmount(payslip.getHouseAmount())
                .transportAmount(payslip.getTransportAmount())
                .employeeTaxedAmount(payslip.getEmployeeTaxedAmount())
                .pensionAmount(payslip.getPensionAmount())
                .medicalInsuranceAmount(payslip.getMedicalInsuranceAmount())
                .otherTaxedAmount(payslip.getOtherTaxedAmount())
                .grossSalary(payslip.getGrossSalary())
                .netSalary(payslip.getNetSalary())
                .month(payslip.getMonth())
                .year(payslip.getYear())
                .status(payslip.getStatus())
                .build();
    }
}