package com.bruce.erp.controller;

import com.bruce.erp.dto.message.MessageResponse;
import com.bruce.erp.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management", description = "Message Management API")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Get all messages", description = "Get all messages in the system")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<MessageResponse>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/employee/{employeeCode}")
    @Operation(summary = "Get messages by employee code", description = "Get messages by employee code")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @customSecurityExpression.isCurrentUserByCode(#employeeCode)")
    public ResponseEntity<List<MessageResponse>> getMessagesByEmployeeCode(@PathVariable String employeeCode) {
        return ResponseEntity.ok(messageService.getMessagesByEmployeeCode(employeeCode));
    }

    @GetMapping("/month/{month}/year/{year}")
    @Operation(summary = "Get messages by month and year", description = "Get messages by month and year")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<MessageResponse>> getMessagesByMonthAndYear(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(messageService.getMessagesByMonthAndYear(month, year));
    }
}