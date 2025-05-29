package com.bruce.erp.dto.employment;

import com.bruce.erp.model.Employment;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentUpdateRequest {

    private String employeeCode;

    private String department;

    private String position;

    @Positive(message = "Base salary must be positive")
    private BigDecimal baseSalary;

    private Employment.EmploymentStatus status;

    private LocalDate joiningDate;
}