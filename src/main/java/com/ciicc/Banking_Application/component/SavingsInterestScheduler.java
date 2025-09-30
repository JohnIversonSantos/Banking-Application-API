package com.ciicc.Banking_Application.component;

import com.ciicc.Banking_Application.entity.SavingsAccount;
import com.ciicc.Banking_Application.entity.Transaction;
import com.ciicc.Banking_Application.entity.TransactionType;
import com.ciicc.Banking_Application.entity.AuditLog;
import com.ciicc.Banking_Application.repository.SavingsAccountRepository;
import com.ciicc.Banking_Application.repository.TransactionRepository;
import com.ciicc.Banking_Application.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SavingsInterestScheduler {

    private final SavingsAccountRepository savingsRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;

    /**
     * Apply interest to all savings accounts.
     *
     * Testing: runs every 5 minute with 3.5% per day
     * Production: replace with daily cron for annual interest
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 1 minute interval for testing
    @Transactional
    public void applyInterest() {

        // Fetch all savings accounts
        List<SavingsAccount> accounts = savingsRepository.findAll();

        for (SavingsAccount account : accounts) {

            BigDecimal balance = account.getBalance(); // Current balance

            // Skip accounts with zero or negative balance
            if (balance.compareTo(BigDecimal.ZERO) <= 0) continue;

            // ==============================
            // DAILY INTEREST (3.5% per day) for testing
            // ==============================
            BigDecimal dailyRate = BigDecimal.valueOf(3.5)
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            BigDecimal interest = balance.multiply(dailyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            /*
            // ==============================
            // ANNUAL INTEREST (3.5% per year)
            // ==============================
            // If you want to apply annual interest once per day (at midnight):
            // Divide annual rate by 365 to get daily portion
            BigDecimal annualRate = BigDecimal.valueOf(3.5) // 3.5% per year
                                             .divide(BigDecimal.valueOf(100 * 365), 10, RoundingMode.HALF_UP);
            BigDecimal interest = balance.multiply(annualRate)
                                         .setScale(2, RoundingMode.HALF_UP);
            // Scheduler for production: run once per day at midnight
            // @Scheduled(cron = "0 0 0 * * ?") // every day at 00:00
            */

            // Debug output (optional)
            System.out.println("Account: " + account.getAccountNumber());
            System.out.println("Balance: " + balance);
            System.out.println("Interest: " + interest);

            // Update balance
            account.setBalance(balance.add(interest));
            savingsRepository.save(account);

            // Record interest as transaction
            Transaction tx = Transaction.builder()
                    .savingsAccount(account)
                    .wallet(null)
                    .type(TransactionType.INTEREST)
                    .amount(interest)
                    .fee(BigDecimal.ZERO)
                    .description("Automatic interest applied")
                    .targetAccountNumber(null)
                    .status("SUCCESS")
                    .build();
            transactionRepository.save(tx);

            // Optional audit log
            auditLogRepository.save(AuditLog.builder()
                    .action("INTEREST_APPLIED")
                    .performedBy("SYSTEM")
                    .target(account.getAccountNumber())
                    .status("SUCCESS")
                    .details("Interest of " + interest + " applied")
                    .build()
            );
        }
    }
}
