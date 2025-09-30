package com.ciicc.Banking_Application.repository;

import com.ciicc.Banking_Application.entity.Transaction;
import com.ciicc.Banking_Application.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // All transactions for a phone number
    List<Transaction> findByWallet_User_PhoneNumberOrderByTimestampDesc(String phoneNumber);

    // Filtered by transaction type(s)
    List<Transaction> findByWallet_User_PhoneNumberAndTypeInOrderByTimestampDesc(
            String phoneNumber, List<TransactionType> types
    );

    // All transactions for savings account
    List<Transaction> findBySavingsAccount_AccountNumberOrderByTimestampDesc(String accountNumber);

    // Filtered by transaction type(s) for savings account
    List<Transaction> findBySavingsAccount_AccountNumberAndTypeInOrderByTimestampDesc(
            String accountNumber, List<TransactionType> types
    );

    // Sum of Today's transfer for daily limit check
//    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
//            "FROM Transaction t " +
//            "WHERE t.savingsAccount.accountNumber = :accountNumber " +
//            "AND t.timestamp BETWEEN :startOfDay AND :endOfDay " +
//            "AND t.type = com.ciicc.Banking_Application.entity.TransactionType.TRANSFER_OUT")
//    BigDecimal sumDailySavingsTransfers(
//            @Param("accountNumber") String accountNumber,
//            @Param("startOfDay") LocalDateTime startOfDay,
//            @Param("endOfDay") LocalDateTime endOfDay
//    );
}
