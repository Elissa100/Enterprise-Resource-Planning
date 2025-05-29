package com.bruce.erp.service;

import com.bruce.erp.dto.employment.EmploymentRequest;
import com.bruce.erp.dto.employment.EmploymentResponse;
import com.bruce.erp.dto.employment.EmploymentUpdateRequest;
import com.bruce.erp.model.entity.Employment;
import com.bruce.erp.repository.EmployeeRepository;
import com.bruce.erp.repository.EmploymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final EmployeeRepository employeeRepository;

    public List<EmploymentResponse> getAllEmployments() {
        return employmentRepository.findAll().stream()
                .map(EmploymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public EmploymentResponse getEmploymentById(Long id) {
        return employmentRepository.findById(id)
                .map(EmploymentResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Employment not found with id: " + id));
    }

    public EmploymentResponse getEmploymentByCode(String code) {
        return employmentRepository.findByCodeIgnoreCase(code)
                .map(EmploymentResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Employment not found with code: " + code));
    }

    public List<EmploymentResponse> getEmploymentsByEmployeeCode(String employeeCode) {
        var employee = employeeRepository.findByCodeIgnoreCase(employeeCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with code: " + employeeCode));

        return employmentRepository.findByEmployee(employee).stream()
                .map(EmploymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EmploymentResponse> getActiveEmploymentsByEmployeeCode(String employeeCode) {
        var employee = employeeRepository.findByCodeIgnoreCase(employeeCode)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with code: " + employeeCode));

        return employmentRepository.findByEmployeeAndStatus(employee, Employment.EmploymentStatus.ACTIVE).stream()
                .map(EmploymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public EmploymentResponse createEmployment(EmploymentRequest request) {
        var employee = employeeRepository.findByCodeIgnoreCase(request.getEmployeeCode())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with code: " + request.getEmployeeCode()));

        var employment = Employment.builder()
                .code(generateEmploymentCode())
                .employee(employee)
                .department(request.getDepartment())
                .position(request.getPosition())
                .baseSalary(request.getBaseSalary())
                .status(request.getStatus())
                .joiningDate(request.getJoiningDate())
                .build();

        return EmploymentResponse.fromEntity(employmentRepository.save(employment));
    }

    public EmploymentResponse updateEmployment(Long id, EmploymentRequest request) {
        var employment = employmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employment not found with id: " + id));

        var employee = employeeRepository.findByCodeIgnoreCase(request.getEmployeeCode())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with code: " + request.getEmployeeCode()));

        employment.setEmployee(employee);
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        employment.setStatus(request.getStatus());
        employment.setJoiningDate(request.getJoiningDate());

        return EmploymentResponse.fromEntity(employmentRepository.save(employment));
    }

    public EmploymentResponse updateEmployment(Long id, EmploymentUpdateRequest request) {
        var employment = employmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employment not found with id: " + id));

        if (request.getEmployeeCode() != null) {
            var employee = employeeRepository.findByCodeIgnoreCase(request.getEmployeeCode())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with code: " + request.getEmployeeCode()));
            employment.setEmployee(employee);
        }

        if (request.getDepartment() != null) {
            employment.setDepartment(request.getDepartment());
        }

        if (request.getPosition() != null) {
            employment.setPosition(request.getPosition());
        }

        if (request.getBaseSalary() != null) {
            employment.setBaseSalary(request.getBaseSalary());
        }

        if (request.getStatus() != null) {
            employment.setStatus(request.getStatus());
        }

        if (request.getJoiningDate() != null) {
            employment.setJoiningDate(request.getJoiningDate());
        }

        return EmploymentResponse.fromEntity(employmentRepository.save(employment));
    }

    public void deleteEmployment(Long id) {
        if (!employmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Employment not found with id: " + id);
        }
        employmentRepository.deleteById(id);
    }

    private String generateEmploymentCode() {
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
