package com.elissa.erp.dto.deduction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.elissa.erp.model.Deduction;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeductionResponse {

    private Long id;
    private String code;
    private String deductionName;
    private BigDecimal percentage;

    public static DeductionResponse fromEntity(Deduction deduction) {
        return DeductionResponse.builder()
                .id(deduction.getId())
                .code(deduction.getCode())
                .deductionName(deduction.getDeductionName())
                .percentage(deduction.getPercentage())
                .build();
    }
}