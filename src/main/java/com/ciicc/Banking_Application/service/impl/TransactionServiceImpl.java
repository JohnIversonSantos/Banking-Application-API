package com.ciicc.Banking_Application.service.impl;

import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.TransactionHistory;
import com.ciicc.Banking_Application.entity.*;
import com.ciicc.Banking_Application.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final SavingsAccountRepository savingsRepository;
    private final WalletAccountRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;

//    private static final BigDecimal PER_TRANSACTION_LIMIT = new BigDecimal("50000");
//    private static final BigDecimal DAILY_LIMIT = new BigDecimal("100000");


    /** -------------------- Savings Account Methods -------------------- **/
    @Override
    public BankResponse depositToSavings(String accountNumber, BigDecimal amount) {
        SavingsAccount account = this.savingsRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account == null) return BankResponse.notFound("Savings account not found");

        account.setBalance(account.getBalance().add(amount));
        this.savingsRepository.save(account);

        this.recordSavingsTransaction(account, TransactionType.DEPOSIT, amount, BigDecimal.ZERO,
                "Deposit to savings", null);

        return BankResponse.success("Deposit successful", account.getBalance());
    }

    @Override
    public BankResponse withdrawFromSavings(String accountNumber, BigDecimal amount) {
        SavingsAccount account = this.savingsRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account == null) return BankResponse.notFound("Savings account not found");

        if (account.getBalance().compareTo(amount) < 0)
            return BankResponse.conflict("Insufficient funds");

        account.setBalance(account.getBalance().subtract(amount));
        this.savingsRepository.save(account);

        this.recordSavingsTransaction(account, TransactionType.WITHDRAWAL, amount, BigDecimal.ZERO,
                "Withdrawal from savings", null);

        return BankResponse.success("Withdrawal successful", account.getBalance());
    }

    @Override
    @Transactional
    public BankResponse transferBetweenSavings(String fromAccount, String toAccount, BigDecimal amount) {
        SavingsAccount sender = this.savingsRepository.findByAccountNumber(fromAccount).orElse(null);
        SavingsAccount receiver = this.savingsRepository.findByAccountNumber(toAccount).orElse(null);

        if (sender == null || receiver == null)
            return BankResponse.notFound("One or both accounts not found");

        BigDecimal fee = BigDecimal.valueOf(15);
        BigDecimal totalDeduct = amount.add(fee);

        if (sender.getBalance().compareTo(totalDeduct) < 0)
            return BankResponse.conflict("Insufficient funds including transfer fee");

        sender.setBalance(sender.getBalance().subtract(totalDeduct));
        receiver.setBalance(receiver.getBalance().add(amount));

        this.savingsRepository.save(sender);
        this.savingsRepository.save(receiver);

        // Bank-to-bank transfer (directional)
        this.recordSavingsTransaction(sender, TransactionType.TRANSFER_OUT, amount, fee,
                "Sent money to another bank account", receiver.getAccountNumber());
        this.recordSavingsTransaction(receiver, TransactionType.TRANSFER_IN, amount, BigDecimal.ZERO,
                "Received money from another bank account", sender.getAccountNumber());

        return BankResponse.success("Bank transfer successful (fee applied)", sender.getBalance());
    }

    /** -------------------- Wallet Methods -------------------- **/
    @Override
    public BankResponse depositToWallet(String phoneNumber, BigDecimal amount) {
        Wallet wallet = this.walletRepository.findByUserPhoneNumber(phoneNumber).orElse(null);
        if (wallet == null) return BankResponse.notFound("Wallet not found");

        wallet.setBalance(wallet.getBalance().add(amount));
        this.walletRepository.save(wallet);

        this.recordWalletTransaction(wallet, TransactionType.DEPOSIT, amount, BigDecimal.ZERO,
                "Wallet deposit", null);

        return BankResponse.success("Wallet deposit successful", wallet.getBalance());
    }

    @Override
    @Transactional
    public BankResponse transferBetweenWallets(String fromPhone, String toPhone, BigDecimal amount) {
        Wallet sender = this.walletRepository.findByUserPhoneNumber(fromPhone).orElse(null);
        Wallet receiver = this.walletRepository.findByUserPhoneNumber(toPhone).orElse(null);

        if (sender == null || receiver == null)
            return BankResponse.notFound("One or both wallets not found");
        if (sender.getBalance().compareTo(amount) < 0)
            return BankResponse.conflict("Insufficient wallet funds");

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        this.walletRepository.save(sender);
        this.walletRepository.save(receiver);

        // Wallet-to-wallet transfer (directional)
        this.recordWalletTransaction(sender, TransactionType.TRANSFER_OUT, amount, BigDecimal.ZERO,
                "Sent money to wallet", receiver.getUser().getPhoneNumber());
        this.recordWalletTransaction(receiver, TransactionType.TRANSFER_IN, amount, BigDecimal.ZERO,
                "Received money from wallet", sender.getUser().getPhoneNumber());

        return BankResponse.success("Wallet transfer successful", sender.getBalance());
    }

    /** -------------------- Cross Transactions -------------------- **/
    @Override
    @Transactional
    public BankResponse transferWalletToSavings(String fromPhone, String toAccount, BigDecimal amount) {
        Wallet wallet = this.walletRepository.findByUserPhoneNumber(fromPhone).orElse(null);
        SavingsAccount savings = this.savingsRepository.findByAccountNumber(toAccount).orElse(null);

        if (wallet == null || savings == null)
            return BankResponse.notFound("Wallet or Savings account not found");
        if (wallet.getBalance().compareTo(amount) < 0)
            return BankResponse.conflict("Insufficient wallet funds");

        wallet.setBalance(wallet.getBalance().subtract(amount));
        savings.setBalance(savings.getBalance().add(amount));

        this.walletRepository.save(wallet);
        this.savingsRepository.save(savings);

        // Wallet → Savings
        this.recordWalletTransaction(wallet, TransactionType.TRANSFER_WALLET_TO_BANK, amount, BigDecimal.ZERO,
                "Wallet to savings transfer", savings.getAccountNumber());
        this.recordSavingsTransaction(savings, TransactionType.TRANSFER_WALLET_TO_BANK, amount, BigDecimal.ZERO,
                "Received from wallet transfer", wallet.getUser().getPhoneNumber());

        return BankResponse.success("Transfer from wallet to savings successful", wallet.getBalance());
    }

    @Override
    @Transactional
    public BankResponse transferSavingsToWallet(String fromAccount, String toPhone, BigDecimal amount) {
        SavingsAccount savings = this.savingsRepository.findByAccountNumber(fromAccount).orElse(null);
        Wallet wallet = this.walletRepository.findByUserPhoneNumber(toPhone).orElse(null);

        if (savings == null || wallet == null)
            return BankResponse.notFound("Savings or Wallet not found");
        if (savings.getBalance().compareTo(amount) < 0)
            return BankResponse.conflict("Insufficient savings funds");

        savings.setBalance(savings.getBalance().subtract(amount));
        wallet.setBalance(wallet.getBalance().add(amount));

        this.savingsRepository.save(savings);
        this.walletRepository.save(wallet);

        // Savings → Wallet
        this.recordSavingsTransaction(savings, TransactionType.TRANSFER_BANK_TO_WALLET, amount, BigDecimal.ZERO,
                "Savings to wallet transfer", wallet.getUser().getPhoneNumber());
        this.recordWalletTransaction(wallet, TransactionType.TRANSFER_BANK_TO_WALLET, amount, BigDecimal.ZERO,
                "Received from savings transfer", savings.getUser().getAccountNumber());

        return BankResponse.success("Transfer from savings to wallet successful", savings.getBalance());
    }

    /** -------------------- Transaction History -------------------- **/
    @Override
    public BankResponse getTransactionHistory(String identifier) {
        // Wallet transactions
        var walletOpt = this.walletRepository.findByUserPhoneNumber(identifier);
        if (walletOpt.isPresent()) {
            var transactions = this.transactionRepository
                    .findByWallet_User_PhoneNumberOrderByTimestampDesc(identifier)
                    .stream()
                    .map(Transaction::toSafeHistory)
                    .toList();
            return BankResponse.success("Wallet transaction history", transactions);
        }

        // Savings account transactions
        var savingsOpt = this.savingsRepository.findByAccountNumber(identifier);
        if (savingsOpt.isPresent()) {
            var transactions = this.transactionRepository
                    .findBySavingsAccount_AccountNumberOrderByTimestampDesc(identifier)
                    .stream()
                    .map(Transaction::toSafeHistory)
                    .toList();
            return BankResponse.success("Savings transaction history", transactions);
        }

        return BankResponse.notFound("No transactions found for the provided identifier");
    }

    @Override
    public BankResponse getWalletTransactionHistory(String phoneNumber, boolean isSender) {
        List<TransactionHistory> transactions = this.transactionRepository
                .findByWallet_User_PhoneNumberOrderByTimestampDesc(phoneNumber)
                .stream()
                .filter(tx -> {
                    if (isSender) {
                        // Only show deposits and money sent
                        return tx.getType() == TransactionType.DEPOSIT
                                || tx.getType() == TransactionType.TRANSFER_OUT
                                || tx.getType() == TransactionType.TRANSFER_WALLET_TO_BANK;
                    } else {
                        // Only show received money
                        return tx.getType() == TransactionType.TRANSFER_IN
                                || tx.getType() == TransactionType.TRANSFER_BANK_TO_WALLET
                                || tx.getType() == TransactionType.TRANSFER_WALLET_TO_BANK;
                    }
                })
                .map(tx -> new TransactionHistory(
                        tx.getType(),
                        tx.getAmount(),
                        tx.getFee(),
                        tx.getDescription(),
                        tx.getTargetAccountNumber(),
                        tx.getTimestamp(),
                        tx.getStatus()
                ))
                .toList();

        return BankResponse.success("Wallet transaction history retrieved", transactions);
    }

    @Override
    public BankResponse getSavingsTransactionHistory(String accountNumber, boolean isSender) {
        List<TransactionHistory> transactions = this.transactionRepository
                .findBySavingsAccount_AccountNumberOrderByTimestampDesc(accountNumber)
                .stream()
                .filter(tx -> {
                    if (isSender) {
                        // Only show deposits and money sent
                        return tx.getType() == TransactionType.DEPOSIT
                                || tx.getType() == TransactionType.TRANSFER_OUT
                                || tx.getType() == TransactionType.TRANSFER_BANK_TO_WALLET;
                    } else {
                        // Only show received money
                        return tx.getType() == TransactionType.TRANSFER_IN
                                || tx.getType() == TransactionType.TRANSFER_WALLET_TO_BANK;
                    }
                })
                .map(tx -> {
                    BigDecimal displayAmount = tx.getAmount();

                    // Adjust sign from user's perspective
                    if ((tx.getType() == TransactionType.TRANSFER_WALLET_TO_BANK && !isSender) ||  // receiving from wallet
                            tx.getType() == TransactionType.TRANSFER_IN ||  // incoming transfer
                            tx.getType() == TransactionType.DEPOSIT ||      // deposit
                            tx.getType() == TransactionType.INTEREST ||     // interest earned
                            (tx.getType() == TransactionType.TRANSFER_BANK_TO_WALLET && !isSender)) { // receiving from savings
                        // positive amount
                    } else {
                        displayAmount = displayAmount.negate(); // negative amount
                    }

                    return new TransactionHistory(
                            tx.getType(),
                            displayAmount,  // adjusted sign
                            tx.getFee(),
                            tx.getDescription(),
                            tx.getTargetAccountNumber(),
                            tx.getTimestamp(),
                            tx.getStatus()
                    );
                })
                .toList();

        return BankResponse.success("Savings transaction history retrieved", transactions);
    }

    /** -------------------- Helper Methods -------------------- **/
    private void recordSavingsTransaction(SavingsAccount account, TransactionType type,
                                          BigDecimal amount, BigDecimal fee,
                                          String description, String targetAccountNumber) {
        Transaction tx = Transaction.builder()
                .savingsAccount(account)
                .wallet(null)
                .type(type)
                .amount(amount)
                .fee(fee)
                .description(description)
                .targetAccountNumber(targetAccountNumber)
                .status("SUCCESS")
                .build();
        this.transactionRepository.save(tx);
        this.createAuditLog(type.name(), account.getUser().getId().toString(),
                targetAccountNumber, "SUCCESS", description);
    }

    private void recordWalletTransaction(Wallet wallet, TransactionType type,
                                         BigDecimal amount, BigDecimal fee,
                                         String description, String targetPhone) {
        Transaction tx = Transaction.builder()
                .savingsAccount(null)
                .wallet(wallet)
                .type(type)
                .amount(amount)
                .fee(fee)
                .description(description)
                .targetAccountNumber(targetPhone)
                .status("SUCCESS")
                .build();
        this.transactionRepository.save(tx);
        this.createAuditLog(type.name(), wallet.getUser().getPhoneNumber(),
                targetPhone, "SUCCESS", description);
    }

    private void createAuditLog(String action, String performedBy, String target,
                                String status, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .target(target)
                .status(status)
                .details(details)
                .build();
        this.auditLogRepository.save(log);
    }
}
