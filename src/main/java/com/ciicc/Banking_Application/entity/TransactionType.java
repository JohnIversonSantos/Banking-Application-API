package com.ciicc.Banking_Application.entity;

public enum TransactionType {
    DEPOSIT,                // Deposit to savings or wallet
    WITHDRAWAL,             // Withdrawal from savings
    TRANSFER_OUT,           // Outgoing transfer from savings
    TRANSFER_IN,            // Incoming transfer to savings
    TRANSFER_WALLET,        // Wallet to wallet transfer
    TRANSFER_WALLET_TO_BANK,// Wallet to savings transfer
    TRANSFER_BANK_TO_WALLET, // Savings to wallet transfer
    INTEREST
}
