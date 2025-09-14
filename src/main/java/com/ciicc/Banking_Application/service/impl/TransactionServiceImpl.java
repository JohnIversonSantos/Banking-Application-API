package com.ciicc.Banking_Application.service.impl;

import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.entity.Transaction;
import com.ciicc.Banking_Application.entity.User;
import com.ciicc.Banking_Application.repository.TransactionRepository;
import com.ciicc.Banking_Application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Override
    public BankResponse deposit(String accountNumber, BigDecimal amount) {
        User user = this.userRepository.findByAccountNumber(accountNumber).orElse(null);
        if (user == null) return BankResponse.notFound("Account not found");

        user.setAccountBalance(user.getAccountBalance().add(amount));
        this.userRepository.save(user);

        Transaction transaction = Transaction.builder()
                .accountNumber(accountNumber)
                .type("DEPOSIT")
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .description("Deposit")
                .build();
        this.transactionRepository.save(transaction);

        return BankResponse.success("Deposit successful", user.getAccountBalance());
    }

    @Override
    public BankResponse withdraw(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BankResponse.conflict("Amount must be greater than zero");
        }

        User user = this.userRepository.findByAccountNumber(accountNumber).orElse(null);
        if (user == null) {
            return BankResponse.notFound("Account not found");
        }
        if (user.getAccountBalance().compareTo(amount) < 0) {
            return BankResponse.conflict("Insufficient funds");
        }

        user.setAccountBalance(user.getAccountBalance().subtract(amount));
        this.userRepository.save(user);

        Transaction transaction = Transaction.builder()
                .accountNumber(accountNumber)
                .type("WITHDRAWAL")
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .description("Withdrawal")
                .build();
        this.transactionRepository.save(transaction);

        return BankResponse.success("Withdrawal successful", user.getAccountBalance());
    }

    @Override
    @Transactional
    public BankResponse transfer(String fromAccount, String toAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BankResponse.conflict("Amount must be greater than zero");
        }

        User sender = this.userRepository.findByAccountNumber(fromAccount).orElse(null);
        User receiver = this.userRepository.findByAccountNumber(toAccount).orElse(null);

        if (sender == null || receiver == null) {
            return BankResponse.notFound("One or both accounts not found");
        }
        if (sender.getAccountBalance().compareTo(amount) < 0) {
            return BankResponse.conflict("Insufficient funds in sender's account");
        }
        
        // Deduct from sender
        sender.setAccountBalance(sender.getAccountBalance().subtract(amount));
        this.userRepository.save(sender);

        // Add to receiver
        receiver.setAccountBalance(receiver.getAccountBalance().add(amount));
        this.userRepository.save(receiver);

        // Record sender's transaction
        Transaction senderTransaction = Transaction.builder()
                .accountNumber(fromAccount)
                .type("TRANSFER_OUT")
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .description("Transfer to " + toAccount)
                .targetAccountNumber(toAccount)
                .build();
            this.transactionRepository.save(senderTransaction);

            return BankResponse.success("Transfer successful", sender.getAccountBalance());
    }

    // public List<Transaction> getTransactions(String accountNumber) {
    //     return this.transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
    // }

     @Override
     public BankResponse getTransactionHistory(String accountNumber) {
         List<Transaction> transactions = this.transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
         if (transactions.isEmpty()) {
             return BankResponse.notFound("No transactions found for this account");
         }
         return BankResponse.success("Transaction history retrieved", transactions);
     }

}
