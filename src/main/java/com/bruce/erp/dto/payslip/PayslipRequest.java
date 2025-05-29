package com.bruce.erp.dto.payslip;

import com.bruce.erp.model.entity.Payslip;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayslipRequest {

    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @NotNull(message = "House amount is required")
    @Positive(message = "House amount must be positive")
    private BigDecimal houseAmount;

    @NotNull(message = "Transport amount is required")
    @Positive(message = "Transport amount must be positive")
    private BigDecimal transportAmount;

    @NotNull(message = "Employee taxed amount is required")
    @Positive(message = "Employee taxed amount must be positive")
    private BigDecimal employeeTaxedAmount;

    @NotNull(message = "Pension amount is required")
    @Positive(message = "Pension amount must be positive")
    private BigDecimal pensionAmount;

    @NotNull(message = "Medical insurance amount is required")
    @Positive(message = "Medical insurance amount must be positive")
    private BigDecimal medicalInsuranceAmount;

    @NotNull(message = "Other taxed amount is required")
    @Positive(message = "Other taxed amount must be positive")
    private BigDecimal otherTaxedAmount;

    @NotNull(message = "Gross salary is required")
    @Positive(message = "Gross salary must be positive")
    private BigDecimal grossSalary;

    @NotNull(message = "Net salary is required")
    @Positive(message = "Net salary must be positive")
    private BigDecimal netSalary;

    @NotNull(message = "Month is required")
    private Integer month;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Status is required")
    private Payslip.PayslipStatus status;
}