package com.ciicc.Banking_Application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private LocalDate dateOfBirth;        // Changed from String to LocalDate
    private String address;
    private String email;
    private String phoneNumber;
    private String accountNumber;
    private BigDecimal walletBalance;
    private BigDecimal savingsBalance;
    private String role;
    private String status;
    private LocalDateTime createdAt;      // Changed from String to LocalDateTime
    private LocalDateTime updatedAt;      // Changed from String to LocalDateTime
}