package com.elissa.erp.dto.deduction;

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
public class DeductionUpdateRequest {

    private String deductionName;

    @Positive(message = "Percentage must be positive")
    private BigDecimal percentage;
}