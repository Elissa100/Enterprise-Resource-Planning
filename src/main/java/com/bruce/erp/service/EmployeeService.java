package com.bruce.erp.service;

import com.bruce.erp.dto.employee.EmployeeRequest;
import com.bruce.erp.dto.employee.EmployeeResponse;
import com.bruce.erp.dto.employee.EmployeeUpdateRequest;
import com.bruce.erp.exception.AuthenticationException;
import com.bruce.erp.exception.DuplicateResourceException;
import com.bruce.erp.exception.ResourceNotFoundException;
import com.bruce.erp.model.Employee;
import com.bruce.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public EmployeeResponse getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    public EmployeeResponse getEmployeeByCode(String code) {
        return employeeRepository.findByCodeIgnoreCase(code)
                .map(EmployeeResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "code", code));
    }

    public EmployeeResponse getEmployeeByEmail(String email) {
        return employeeRepository.findByEmailIgnoreCase(email)
                .map(EmployeeResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "email", email));
    }

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("Employee", "email", request.getEmail());
        }

        var employee = Employee.builder()
                .code(generateEmployeeCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode("password")) // Default password
                .mobile(request.getMobile())
                .dateOfBirth(request.getDateOfBirth())
                .roles(request.getRoles())
                .status(request.getStatus())
                .build();

        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        // Check if email is being changed and if it already exists
        if (request.getEmail() != null && !employee.getEmail().equals(request.getEmail()) 
                && employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("Employee", "email", request.getEmail());
        }

        if (request.getFirstName() != null) {
            employee.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            employee.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {
            employee.setEmail(request.getEmail());
        }

        if (request.getMobile() != null) {
            employee.setMobile(request.getMobile());
        }

        if (request.getDateOfBirth() != null) {
            employee.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getRoles() != null) {
            employee.setRoles(request.getRoles());
        }

        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }

        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        employeeRepository.deleteById(id);
    }

    public EmployeeResponse getCurrentEmployee() {
        Employee employee = getCurrentEmployeeEntity();
        return EmployeeResponse.fromEntity(employee);
    }

    public Employee getCurrentEmployeeEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Employee)) {
            throw new AuthenticationException("User not found");
        }

        return (Employee) principal;
    }

    private String generateEmployeeCode() {
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
