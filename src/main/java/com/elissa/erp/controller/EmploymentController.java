package com.elissa.erp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.elissa.erp.dto.employment.EmploymentRequest;
import com.elissa.erp.dto.employment.EmploymentResponse;
import com.elissa.erp.dto.employment.EmploymentUpdateRequest;
import com.elissa.erp.service.EmploymentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employments")
@RequiredArgsConstructor
@Tag(name = "Employment Management", description = "Employment Management API")
@SecurityRequirement(name = "bearerAuth")
public class EmploymentController {

    private final EmploymentService employmentService;

    @GetMapping
    @Operation(summary = "Get all employments", description = "Get all employments in the system")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmploymentResponse>> getAllEmployments() {
        return ResponseEntity.ok(employmentService.getAllEmployments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employment by ID", description = "Get employment by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponse> getEmploymentById(@PathVariable Long id) {
        return ResponseEntity.ok(employmentService.getEmploymentById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get employment by code", description = "Get employment by code")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponse> getEmploymentByCode(@PathVariable String code) {
        return ResponseEntity.ok(employmentService.getEmploymentByCode(code));
    }

    @GetMapping("/employee/{employeeCode}")
    @Operation(summary = "Get employments by employee code", description = "Get employments by employee code")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @customSecurityExpression.isCurrentUserByCode(#employeeCode)")
    public ResponseEntity<List<EmploymentResponse>> getEmploymentsByEmployeeCode(@PathVariable String employeeCode) {
        return ResponseEntity.ok(employmentService.getEmploymentsByEmployeeCode(employeeCode));
    }

    @GetMapping("/employee/{employeeCode}/active")
    @Operation(summary = "Get active employments by employee code", description = "Get active employments by employee code")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @customSecurityExpression.isCurrentUserByCode(#employeeCode)")
    public ResponseEntity<List<EmploymentResponse>> getActiveEmploymentsByEmployeeCode(@PathVariable String employeeCode) {
        return ResponseEntity.ok(employmentService.getActiveEmploymentsByEmployeeCode(employeeCode));
    }

    @PostMapping
    @Operation(summary = "Create a new employment", description = "Create a new employment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponse> createEmployment(@Valid @RequestBody EmploymentRequest request) {
        return ResponseEntity.ok(employmentService.createEmployment(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employment", description = "Update an employment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentResponse> updateEmployment(@PathVariable Long id, @Valid @RequestBody EmploymentUpdateRequest request) {
        return ResponseEntity.ok(employmentService.updateEmployment(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employment", description = "Delete an employment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployment(@PathVariable Long id) {
        employmentService.deleteEmployment(id);
        return ResponseEntity.noContent().build();
    }
}
