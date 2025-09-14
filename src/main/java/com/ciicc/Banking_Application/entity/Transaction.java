package com.ciicc.Banking_Application.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table (name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    private Long transactionId;

    private String accountNumber;
    private String phoneNumber;
    private String type;            
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private String targetAccountNumber;
}

