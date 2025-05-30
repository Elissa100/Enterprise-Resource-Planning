package com.elissa.erp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.elissa.erp.dto.deduction.DeductionRequest;
import com.elissa.erp.dto.deduction.DeductionResponse;
import com.elissa.erp.dto.deduction.DeductionUpdateRequest;
import com.elissa.erp.service.DeductionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deductions")
@RequiredArgsConstructor
@Tag(name = "Deduction Management", description = "Deduction Management API")
@SecurityRequirement(name = "bearerAuth")
public class DeductionController {
    private final DeductionService deductionService;

    @GetMapping
    @Operation(summary = "Get all deductions", description = "Get all deductions in the system")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<DeductionResponse>> getAllDeductions() {
        return ResponseEntity.ok(deductionService.getAllDeductions());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get deduction by ID", description = "Get deduction by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DeductionResponse> getDeductionById(@PathVariable Long id) {
        return ResponseEntity.ok(deductionService.getDeductionById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get deduction by code", description = "Get deduction by code")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DeductionResponse> getDeductionByCode(@PathVariable String code) {
        return ResponseEntity.ok(deductionService.getDeductionByCode(code));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get deduction by name", description = "Get deduction by name")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DeductionResponse> getDeductionByName(@PathVariable String name) {
        return ResponseEntity.ok(deductionService.getDeductionByName(name));
    }

    @PostMapping
    @Operation(summary = "Create a new deduction", description = "Create a new deduction")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DeductionResponse> createDeduction(@Valid @RequestBody DeductionRequest request) {
        return ResponseEntity.ok(deductionService.createDeduction(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a deduction", description = "Update a deduction")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DeductionResponse> updateDeduction(@PathVariable Long id, @Valid @RequestBody DeductionUpdateRequest request) {
        return ResponseEntity.ok(deductionService.updateDeduction(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a deduction", description = "Delete a deduction")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDeduction(@PathVariable Long id) {
        deductionService.deleteDeduction(id);
        return ResponseEntity.noContent().build();
    }
}
