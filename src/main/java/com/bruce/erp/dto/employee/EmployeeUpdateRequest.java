package com.bruce.erp.dto.employee;

import com.bruce.erp.model.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdateRequest {

    private String firstName;

    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    @Pattern(
        regexp = "^(07[2389]\\d{7}|\\+2507[2389]\\d{7})$",
        message = "Mobile number must be a valid Rwanda number: 07[2389]XXXXXXX (local) or +2507[2389]XXXXXXX (international)"
    )
    private String mobile;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Employee.EmployeeStatus status;

    private Set<String> roles;
}
