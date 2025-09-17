package com.ciicc.Banking_Application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    // Wallet
    private String phoneNumber;
    private String fromPhone;
    private String toPhone;

    // Savings
    private String accountNumber;
    private String fromAccount;
    private String toAccount;

    // Cross
    private String toAccountForWallet; // optional if needed

    // Amount
    private BigDecimal amount;
}
