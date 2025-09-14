package com.ciicc.Banking_Application.service.impl;
import java.math.BigDecimal;
import java.util.List;

import com.ciicc.Banking_Application.entity.Transaction;

import com.ciicc.Banking_Application.dto.BankResponse;

public interface TransactionService {

    BankResponse deposit(String accountNumber, BigDecimal amount);

    BankResponse withdraw(String accountNumber, BigDecimal amount);

    BankResponse transfer(String fromAccount, String toAccount, BigDecimal amount);

//    List<Transaction> getTransactions(String accountNumber);

    BankResponse getTransactionHistory(String accountNumber);
    
}
