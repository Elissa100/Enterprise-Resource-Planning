package com.elissa.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elissa.erp.model.Deduction;

import java.util.Optional;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {

    Optional<Deduction> findByCodeIgnoreCase(String code);

    Optional<Deduction> findByDeductionNameIgnoreCase(String deductionName);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByDeductionNameIgnoreCase(String deductionName);
}
