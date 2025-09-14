package com.ciicc.Banking_Application.repository;

import com.ciicc.Banking_Application.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // List<Transaction> findByAccountNumberOrderByTimeStamp(String accountNumber);
     List<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber);
}

