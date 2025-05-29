package com.bruce.erp.service;

import com.bruce.erp.dto.auth.AuthRequest;
import com.bruce.erp.dto.auth.AuthResponse;
import com.bruce.erp.dto.auth.RegisterRequest;
import com.bruce.erp.exception.AuthenticationException;
import com.bruce.erp.exception.DuplicateResourceException;
import com.bruce.erp.model.Employee;
import com.bruce.erp.repository.EmployeeRepository;
import com.bruce.erp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
