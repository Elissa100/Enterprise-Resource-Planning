package com.bruce.erp.dto.deduction;

import com.bruce.erp.model.Deduction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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