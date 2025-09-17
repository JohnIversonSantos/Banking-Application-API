package com.ciicc.Banking_Application.service.impl;

import com.ciicc.Banking_Application.dto.BankResponse;
import java.math.BigDecimal;

public interface TransactionService {

    /** ------------------- SAVINGS ACCOUNT TRANSACTIONS ------------------- */
    BankResponse depositToSavings(String accountNumber, BigDecimal amount);

    BankResponse withdrawFromSavings(String accountNumber, BigDecimal amount);

    BankResponse transferBetweenSavings(String fromAccount, String toAccount, BigDecimal amount);

    /** ------------------- WALLET TRANSACTIONS ------------------- */
    BankResponse depositToWallet(String phoneNumber, BigDecimal amount);

    BankResponse transferBetweenWallets(String fromPhone, String toPhone, BigDecimal amount);

    /** ------------------- CROSS TRANSACTIONS ------------------- */
    BankResponse transferWalletToSavings(String fromPhone, String toAccount, BigDecimal amount);

    BankResponse transferSavingsToWallet(String fromAccount, String toPhone, BigDecimal amount);

    /** ------------------- TRANSACTION HISTORY ------------------- */
    /**
     * Retrieves transaction history for a user, either by savings account number or phone number for wallet.
     * The implementation will detect the type and return the appropriate transactions.
     */
    BankResponse getTransactionHistory(String identifier);
}
