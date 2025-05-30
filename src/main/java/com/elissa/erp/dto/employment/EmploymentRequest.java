package com.elissa.erp.dto.employment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.elissa.erp.model.Employment;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentRequest {

    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Base salary is required")
    @Positive(message = "Base salary must be positive")
    private BigDecimal baseSalary;

    @NotNull(message = "Status is required")
    private Employment.EmploymentStatus status;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;
}