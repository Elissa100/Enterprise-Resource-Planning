package com.bruce.erp.repository;

import com.bruce.erp.model.entity.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {

    Optional<Deduction> findByCodeIgnoreCase(String code);

    Optional<Deduction> findByDeductionNameIgnoreCase(String deductionName);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByDeductionNameIgnoreCase(String deductionName);
}
