package com.ciicc.Banking_Application.dto;

import java.math.BigDecimal;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {

    private String accountNumber;   // For savings accounts
    private String phoneNumber;     // For wallet accounts
    private BigDecimal accountBalance; // Balance for wallet/savings
    private String accountType;     // WALLET / SAVINGS
    private String currency;        // e.g., PHP, USD
    private String status;          // ACTIVE, LOCKED, SUSPENDED
}
