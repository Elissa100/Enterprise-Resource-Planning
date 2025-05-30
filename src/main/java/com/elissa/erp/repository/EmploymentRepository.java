package com.elissa.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elissa.erp.model.Employee;
import com.elissa.erp.model.Employment;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {

    Optional<Employment> findByCodeIgnoreCase(String code);

    List<Employment> findByEmployee(Employee employee);

    List<Employment> findByEmployeeAndStatus(Employee employee, Employment.EmploymentStatus status);

    boolean existsByCodeIgnoreCase(String code);
}
