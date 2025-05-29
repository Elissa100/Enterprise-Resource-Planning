package com.bruce.erp.dto.payslip;

import com.bruce.erp.model.entity.Payslip;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayslipStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private Payslip.PayslipStatus status;
}