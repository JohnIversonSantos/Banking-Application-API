package com.ciicc.Banking_Application.utils;

import com.ciicc.Banking_Application.entity.*;
import com.ciicc.Banking_Application.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionUtils {

    private TransactionUtils() {} // private constructor to prevent instantiation

    public static void recordTransaction(TransactionRepository transactionRepository,
                                         SavingsAccount savingsAccount,
                                         Wallet wallet,
                                         TransactionType type,
                                         BigDecimal amount,
                                         BigDecimal fee,
                                         String description,
                                         String targetAccountNumber) {

        Transaction tx = Transaction.builder()
                .savingsAccount(savingsAccount)   // null if wallet only
                .wallet(wallet)                   // null if savings only
                .type(type)
                .amount(amount)
                .fee(fee != null ? fee : BigDecimal.ZERO)
                .description(description)
                .targetAccountNumber(targetAccountNumber)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);
    }

    public static boolean hasSufficientBalance(BigDecimal balance, BigDecimal amount, BigDecimal fee) {
        BigDecimal total = amount.add(fee != null ? fee : BigDecimal.ZERO);
        return balance.compareTo(total) >= 0;
    }

}
