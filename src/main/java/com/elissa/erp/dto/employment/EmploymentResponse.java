package com.elissa.erp.dto.employment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.elissa.erp.dto.employee.EmployeeResponse;
import com.elissa.erp.model.Employment;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentResponse {

    private Long id;
    private String code;
    private EmployeeResponse employee;
    private String department;
    private String position;
    private BigDecimal baseSalary;
    private Employment.EmploymentStatus status;
    private LocalDate joiningDate;

    public static EmploymentResponse fromEntity(Employment employment) {
        return EmploymentResponse.builder()
                .id(employment.getId())
                .code(employment.getCode())
                .employee(EmployeeResponse.fromEntity(employment.getEmployee()))
                .department(employment.getDepartment())
                .position(employment.getPosition())
                .baseSalary(employment.getBaseSalary())
                .status(employment.getStatus())
                .joiningDate(employment.getJoiningDate())
                .build();
    }
}