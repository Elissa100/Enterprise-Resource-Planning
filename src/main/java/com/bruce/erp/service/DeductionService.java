package com.bruce.erp.service;

import com.bruce.erp.dto.deduction.DeductionRequest;
import com.bruce.erp.dto.deduction.DeductionResponse;
import com.bruce.erp.dto.deduction.DeductionUpdateRequest;
import com.bruce.erp.model.entity.Deduction;
import com.bruce.erp.repository.DeductionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeductionService {

    private final DeductionRepository deductionRepository;

    @PostConstruct
    public void init() {
        // Initialize default deductions if they don't exist
        if (deductionRepository.count() == 0) {
            createDefaultDeductions();
        }
    }

    private void createDefaultDeductions() {
        // Employee Tax: 30%
        createDeduction("Employee Tax", new BigDecimal("30.0"));

        // Pension: 6%
        createDeduction("Pension", new BigDecimal("6.0"));

        // Medical Insurance: 5%
        createDeduction("Medical Insurance", new BigDecimal("5.0"));

        // Housing: 14%
        createDeduction("Housing", new BigDecimal("14.0"));

        // Transport: 14%
        createDeduction("Transport", new BigDecimal("14.0"));

        // Others: 5%
        createDeduction("Others", new BigDecimal("5.0"));
    }

    private void createDeduction(String name, BigDecimal percentage) {
        if (!deductionRepository.existsByDeductionNameIgnoreCase(name)) {
            var deduction = Deduction.builder()
                    .code(generateDeductionCode())
                    .deductionName(name)
                    .percentage(percentage)
                    .build();
            deductionRepository.save(deduction);
        }
    }

    public List<DeductionResponse> getAllDeductions() {
        return deductionRepository.findAll().stream()
                .map(DeductionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public DeductionResponse getDeductionById(Long id) {
        return deductionRepository.findById(id)
                .map(DeductionResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Deduction not found with id: " + id));
    }

    public DeductionResponse getDeductionByCode(String code) {
        return deductionRepository.findByCodeIgnoreCase(code)
                .map(DeductionResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Deduction not found with code: " + code));
    }

    public DeductionResponse getDeductionByName(String name) {
        return deductionRepository.findByDeductionNameIgnoreCase(name)
                .map(DeductionResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Deduction not found with name: " + name));
    }

    public DeductionResponse createDeduction(DeductionRequest request) {
        if (deductionRepository.existsByDeductionNameIgnoreCase(request.getDeductionName())) {
            throw new IllegalArgumentException("Deduction name already exists");
        }

        var deduction = Deduction.builder()
                .code(generateDeductionCode())
                .deductionName(request.getDeductionName())
                .percentage(request.getPercentage())
                .build();

        return DeductionResponse.fromEntity(deductionRepository.save(deduction));
    }

    public DeductionResponse updateDeduction(Long id, DeductionRequest request) {
        var deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Deduction not found with id: " + id));

        // Check if name is being changed and if it already exists
        if (!deduction.getDeductionName().equals(request.getDeductionName()) && 
                deductionRepository.existsByDeductionNameIgnoreCase(request.getDeductionName())) {
            throw new IllegalArgumentException("Deduction name already exists");
        }

        deduction.setDeductionName(request.getDeductionName());
        deduction.setPercentage(request.getPercentage());

        return DeductionResponse.fromEntity(deductionRepository.save(deduction));
    }

    public DeductionResponse updateDeduction(Long id, DeductionUpdateRequest request) {
        var deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Deduction not found with id: " + id));

        // Only update deductionName if it's provided
        if (request.getDeductionName() != null && !request.getDeductionName().isEmpty()) {
            // Check if name is being changed and if it already exists
            if (!deduction.getDeductionName().equals(request.getDeductionName()) && 
                    deductionRepository.existsByDeductionNameIgnoreCase(request.getDeductionName())) {
                throw new IllegalArgumentException("Deduction name already exists");
            }
            deduction.setDeductionName(request.getDeductionName());
        }

        // Only update percentage if it's provided
        if (request.getPercentage() != null) {
            deduction.setPercentage(request.getPercentage());
        }

        return DeductionResponse.fromEntity(deductionRepository.save(deduction));
    }

    public void deleteDeduction(Long id) {
        if (!deductionRepository.existsById(id)) {
            throw new IllegalArgumentException("Deduction not found with id: " + id);
        }
        deductionRepository.deleteById(id);
    }

    private String generateDeductionCode() {
        return "DED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
