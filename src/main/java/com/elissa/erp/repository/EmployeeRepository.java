package com.elissa.erp.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elissa.erp.model.Employee;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmailIgnoreCase(String email);

    Optional<Employee> findByCodeIgnoreCase(String code);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email);

    Optional<Employee> findByEmail(@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email);
}
