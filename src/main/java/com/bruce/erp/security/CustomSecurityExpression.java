package com.bruce.erp.security;

import com.bruce.erp.model.Employee;
import com.bruce.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomSecurityExpression {

    private final EmployeeRepository employeeRepository;

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Employee)) {
            return false;
        }

        Employee employee = (Employee) principal;
        return employee.getId().equals(userId);
    }

    public boolean isCurrentUserByCode(String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Employee)) {
            return false;
        }

        Employee employee = (Employee) principal;
        return employee.getCode().equals(code);
    }

    public boolean isCurrentUserByEmail(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Employee)) {
            return false;
        }

        Employee employee = (Employee) principal;
        return employee.getEmail().equals(email);
    }
}