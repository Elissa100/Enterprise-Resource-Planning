package com.elissa.erp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.elissa.erp.dto.auth.AuthRequest;
import com.elissa.erp.dto.auth.AuthResponse;
import com.elissa.erp.dto.auth.RegisterRequest;
import com.elissa.erp.exception.AuthenticationException;
import com.elissa.erp.exception.DuplicateResourceException;
import com.elissa.erp.model.Employee;
import com.elissa.erp.repository.EmployeeRepository;
import com.elissa.erp.security.JwtTokenProvider;

import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee", "email", request.getEmail());
        }

        var roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add("ROLE_EMPLOYEE");
        }

        var employee = Employee.builder()
                .code(generateEmployeeCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobile(request.getMobile())
                .dateOfBirth(request.getDateOfBirth())
                .roles(roles)
                .status(Employee.EmployeeStatus.ACTIVE)
                .build();

        employeeRepository.save(employee);

        var token = jwtTokenProvider.generateToken(employee);

        return AuthResponse.builder()
                .token(token)
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .roles(employee.getRoles())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid email or password");
        }

        var employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        var token = jwtTokenProvider.generateToken(employee);

        return AuthResponse.builder()
                .token(token)
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .roles(employee.getRoles())
                .build();
    }

    private String generateEmployeeCode() {
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
