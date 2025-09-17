package com.ciicc.Banking_Application.service.impl;

import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.entity.*;
import com.ciicc.Banking_Application.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final SavingsAccountRepository savingsRepository;
    private final WalletAccountRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;

    /** -------------------- Savings Account Methods -------------------- **/
    @Override
    public BankResponse depositToSavings(String accountNumber, BigDecimal amount) {
        SavingsAccount account = savingsRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account == null) return BankResponse.notFound("Savings account not found");

        account.setBalance(account.getBalance().add(amount));
        savingsRepository.save(account);

        recordSavingsTransaction(account, TransactionType.DEPOSIT, amount, BigDecimal.ZERO,
                "Deposit to savings", null);

        return BankResponse.success("Deposit successful", account.getBalance());
    }

    @Override
    public BankResponse withdrawFromSavings(String accountNumber, BigDecimal amount) {
        SavingsAccount account = savingsRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account == null) return BankResponse.notFound("Savings account not found");

        if (account.getBalance().compareTo(amount) < 0)
            return BankResponse.conflict("Insufficient funds");

        account.setBalance(account.getBalance().subtract(amount));
        savingsRepository.save(account);

        recordSavingsTransaction(account, TransactionType.WITHDRAWAL, amount, BigDecimal.ZERO,
                "Withdrawal from savings", null);

        return BankResponse.success("Withdrawal successful", account.getBalance());
    }

    @Override
    @Transactional
    public BankResponse transferBetweenSavings(String fromAccount, String toAccount, BigDecimal amount) {
        SavingsAccount sender = savingsRepository.findByAccountNumber(fromAccount).orElse(null);
        SavingsAccount receiver = savingsRepository.findByAccountNumber(toAccount).orElse(null);

        if (sender == null || receiver == null)
            return BankResponse.notFound("One or both accounts not found");

        BigDecimal fee = BigDecimal.valueOf(15);
        BigDecimal totalDeduct = amount.add(fee);

        if (sender.getBalance().compareTo(totalDeduct) < 0)
            return BankResponse.conflict("Insufficient funds including transfer fee");

        sender.setBalance(sender.getBalance().subtract(totalDeduct));
        receiver.setBalance(receiver.getBalance().add(amount));

        savingsRepository.save(sender);
        savingsRepository.save(receiver);

        recordSavingsTransaction(sender, TransactionType.TRANSFER_OUT, amount, fee,
                "Bank to bank transfer", receiver.getAccountNumber());
        recordSavingsTransaction(receiver, TransactionType.TRANSFER_IN, amount, BigDecimal.ZERO,
                "Bank received transfer", sender.getAccountNumber());

        return BankResponse.success("Bank transfer successful (fee applied)", sender.getBalance());
    }

    /** -------------------- Wallet Methods -------------------- **/
    @Override
    public BankResponse depositToWallet(String phoneNumber, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserPhoneNumber(phoneNumber).orElse(null);
        if (wallet == null) return BankResponse.notFound("Wallet not found");

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        recordWalletTransaction(wallet, TransactionType.DEPOSIT, amount, BigDecimal.ZERO,
                "Wallet deposit", null);

        return BankResponse.success("Wallet deposit successful", wallet.getBalance());
    }

    @Override
    @Transactional
    public BankResponse transferBetweenWallets(String fromPhone, String toPhone, BigDecimal amount) {
        Wallet sender = walletRepository.findByUserPhoneNumber(fromPhone).orElse(null);
        Wallet receiver = walletRepository.findByUserPhoneNumber(toPhone).orElse(null);

        if (sender == null || receiver == null)
            return BankResponse.notFound("One or both wallets not found");
        if (sender.getBalance().compareTo(amount) < 0)
            return BankResponse.conflict("Insufficient wallet funds");

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        walletRepository.save(sender);
        walletRepository.save(receiver);

        recordWalletTransaction(sender, TransactionType.TRANSFER_WALLET, amount, BigDecimal.ZERO,
                "Wallet to wallet transfer", receiver.getUser().getPhoneNumber());
        recordWalletTransaction(receiver, TransactionType.TRANSFER_WALLET, amount, BigDecimal.ZERO,
                "Wallet received from wallet transfer", sender.getUser().getPhoneNumber());

        return BankResponse.success("Wallet transfer successful", sender.getBalance());
    }

    /** -------------------- Cross Transactions -------------------- **/
    @Override
    @Transactional
    public BankResponse transferWalletToSavings(String fromPhone, String toAccount, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserPhoneNumber(fromPhone).orElse(null);
        SavingsAccount savings = savingsRepository.findByAccountNumber(toAccount).orElse(null);

        if (wallet == null || savings == null)
            return BankResponse.notFound("Wallet or Savings account not found");
        if (wallet.getBalance().compareTo(amount) < 0)
            return BankResponse.conflict("Insufficient wallet funds");

        wallet.setBalance(wallet.getBalance().subtract(amount));
        savings.setBalance(savings.getBalance().add(amount));

        walletRepository.save(wallet);
        savingsRepository.save(savings);

        recordWalletTransaction(wallet, TransactionType.TRANSFER_WALLET_TO_BANK, amount, BigDecimal.ZERO,
                "Wallet to savings transfer", savings.getAccountNumber());
        recordSavingsTransaction(savings, TransactionType.TRANSFER_WALLET_TO_BANK, amount, BigDecimal.ZERO,
                "Received from wallet transfer", wallet.getUser().getPhoneNumber());

        return BankResponse.success("Transfer from wallet to savings successful", wallet.getBalance());
    }

    @Override
    @Transactional
    public BankResponse transferSavingsToWallet(String fromAccount, String toPhone, BigDecimal amount) {
        SavingsAccount savings = savingsRepository.findByAccountNumber(fromAccount).orElse(null);
        Wallet wallet = walletRepository.findByUserPhoneNumber(toPhone).orElse(null);

        if (savings == null || wallet == null)
            return BankResponse.notFound("Savings or Wallet not found");
        if (savings.getBalance().compareTo(amount) < 0)
            return BankResponse.conflict("Insufficient savings funds");

        savings.setBalance(savings.getBalance().subtract(amount));
        wallet.setBalance(wallet.getBalance().add(amount));

        savingsRepository.save(savings);
        walletRepository.save(wallet);

        recordSavingsTransaction(savings, TransactionType.TRANSFER_BANK_TO_WALLET, amount, BigDecimal.ZERO,
                "Savings to wallet transfer", wallet.getUser().getPhoneNumber());
        recordWalletTransaction(wallet, TransactionType.TRANSFER_BANK_TO_WALLET, amount, BigDecimal.ZERO,
                "Received from savings transfer", savings.getUser().getAccountNumber());

        return BankResponse.success("Transfer from savings to wallet successful", savings.getBalance());
    }

    /** -------------------- Transaction History -------------------- **/
    @Override
    public BankResponse getTransactionHistory(String identifier) {
        // Wallet transactions
        var walletOpt = walletRepository.findByUserPhoneNumber(identifier);
        if (walletOpt.isPresent()) {
            var transactions = transactionRepository
                    .findByWallet_User_PhoneNumberOrderByTimestampDesc(identifier)
                    .stream()
                    .map(Transaction::toSafeHistory)
                    .toList();
            return BankResponse.success("Wallet transaction history", transactions);
        }

        // Savings account transactions
        var savingsOpt = savingsRepository.findByAccountNumber(identifier);
        if (savingsOpt.isPresent()) {
            var transactions = transactionRepository.findBySavingsAccount_AccountNumberOrderByTimestampDesc(identifier)
                    .stream()
                    .map(Transaction::toSafeHistory)
                    .toList();
            return BankResponse.success("Savings transaction history", transactions);
        }

        return BankResponse.notFound("No transactions found for the provided identifier");
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
        transactionRepository.save(tx);
        createAuditLog(type.name(), account.getUser().getId().toString(),
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
        transactionRepository.save(tx);
        createAuditLog(type.name(), wallet.getUser().getPhoneNumber(),
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
        auditLogRepository.save(log);
    }
}
