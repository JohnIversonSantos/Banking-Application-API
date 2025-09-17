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
    private String password;

    // Account information
    private String accountNumber;       // Savings / Bank account number
    private BigDecimal walletBalance = BigDecimal.ZERO;   // Wallet (phone-to-phone) balance
    private BigDecimal savingsBalance = BigDecimal.ZERO;  // Savings / bank balance
    private String accountType = "SAVINGS";
    private String currency = "PHP";

    // Roles & status
    private String role = "CUSTOMER";   // CUSTOMER / ADMIN
    private String status = "ACTIVE";   // ACTIVE / LOCKED / SUSPENDED
    private boolean twoFactorEnabled;

    // Audit trail
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
