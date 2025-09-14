package com.ciicc.Banking_Application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private Long id;

    // Basic personal info
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private LocalDate dateOfBirth;

    // Contact details
    private String address;
    private String email;
    private String phoneNumber;
    private String alternativePhoneNumber;

    // Security
    private String password;   // 🔹 now included for registration

    // Account information (system-generated mostly, but kept here for flexibility)
    private String accountNumber;
    private BigDecimal accountBalance;
    private String accountType;
    private String currency;

    private String role;
    private String status;
    private boolean twoFactorEnabled;

    // Audit trail
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
