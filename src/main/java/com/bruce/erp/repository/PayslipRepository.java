package com.bruce.erp.repository;

import com.bruce.erp.model.Employee;
import com.bruce.erp.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    
    List<Payslip> findByEmployee(Employee employee);
    
    List<Payslip> findByEmployeeAndStatus(Employee employee, Payslip.PayslipStatus status);
    
    List<Payslip> findByMonthAndYear(Integer month, Integer year);
    
    List<Payslip> findByMonthAndYearAndStatus(Integer month, Integer year, Payslip.PayslipStatus status);
    
    Optional<Payslip> findByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
    
    boolean existsByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
}