package com.ciicc.Banking_Application.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users") // maps this class to the "users" table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment primary key
    private Long id;

    // Basic personal info
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private LocalDate dateOfBirth;

    // Contact details
    private String address; // expand later with the address API from JSON
    private String email;
    private String password; // should be stored as a hashed value

    private String phoneNumber;
    private String alternativePhoneNumber;

    // Account information
    @Column(unique = true, nullable = false) // must be unique per user
    private String accountNumber;

    private BigDecimal accountBalance; // using BigDecimal for money accuracy

    private String accountType;
    private String currency;

    // Security and authentication
    private String role;  // e.g., "CUSTOMER", "ADMIN"
    private String status; // e.g., "ACTIVE", "LOCKED", "SUSPENDED"
    private int failedLoginAttempts;
    private LocalDateTime lastLoginAt;
    private boolean twoFactorEnabled; // true if user has 2FA enabled

    // Audit trail
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
