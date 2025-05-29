package com.bruce.erp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payslips", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
})
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnore
    private Employee employee;

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

    @Enumerated(EnumType.STRING)
    private PayslipStatus status;

    public enum PayslipStatus {
        PENDING, PAID
    }
}
