package com.bruce.erp.controller;

import com.bruce.erp.dto.payslip.PayslipGenerationRequest;
import com.bruce.erp.dto.payslip.PayslipResponse;
import com.bruce.erp.dto.payslip.PayslipStatusUpdateRequest;
import com.bruce.erp.service.PayslipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payslips")
@RequiredArgsConstructor
@Tag(name = "Payslip Management", description = "Payslip Management API")
@SecurityRequirement(name = "bearerAuth")
public class PayslipController {

    private final PayslipService payslipService;

    @GetMapping
    @Operation(summary = "Get all payslips", description = "Get all payslips in the system")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PayslipResponse>> getAllPayslips() {
        return ResponseEntity.ok(payslipService.getAllPayslips());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payslip by ID", description = "Get payslip by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PayslipResponse> getPayslipById(@PathVariable Long id) {
        return ResponseEntity.ok(payslipService.getPayslipById(id));
    }

    @GetMapping("/employee/{employeeCode}")
    @Operation(summary = "Get payslips by employee code", description = "Get payslips by employee code")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @customSecurityExpression.isCurrentUserByCode(#employeeCode)")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByEmployeeCode(@PathVariable String employeeCode) {
        return ResponseEntity.ok(payslipService.getPayslipsByEmployeeCode(employeeCode));
    }

    @GetMapping("/month/{month}/year/{year}")
    @Operation(summary = "Get payslips by month and year", description = "Get payslips by month and year")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PayslipResponse>> getPayslipsByMonthAndYear(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.getPayslipsByMonthAndYear(month, year));
    }

    @GetMapping("/employee/{employeeCode}/month/{month}/year/{year}")
    @Operation(summary = "Get payslip by employee code, month, and year", description = "Get payslip by employee code, month, and year")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @customSecurityExpression.isCurrentUserByCode(#employeeCode)")
    public ResponseEntity<PayslipResponse> getPayslipByEmployeeAndMonthAndYear(
            @PathVariable String employeeCode,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.getPayslipByEmployeeAndMonthAndYear(employeeCode, month, year));
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate payroll", description = "Generate payroll for a given month and year")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PayslipResponse>> generatePayroll(@Valid @RequestBody PayslipGenerationRequest request) {
        return ResponseEntity.ok(payslipService.generatePayroll(request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update payslip status", description = "Update payslip status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PayslipResponse> updatePayslipStatus(
            @PathVariable Long id,
            @Valid @RequestBody PayslipStatusUpdateRequest request) {
        return ResponseEntity.ok(payslipService.updatePayslipStatus(id, request));
    }
}