package com.ciicc.Banking_Application.dto;

import java.math.BigDecimal;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {

    private String accountNumber;   // unique account identifier
    private BigDecimal accountBalance; // money stored
    private String accountType;     // SAVINGS / CHECKING
    private String currency;        // e.g., PHP, USD
    private String status;          // ACTIVE, LOCKED, SUSPENDED
}
