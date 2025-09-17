package com.ciicc.Banking_Application.component;

import com.ciicc.Banking_Application.entity.SavingsAccount;
import com.ciicc.Banking_Application.entity.Transaction;
import com.ciicc.Banking_Application.entity.TransactionType;
import com.ciicc.Banking_Application.repository.SavingsAccountRepository;
import com.ciicc.Banking_Application.repository.TransactionRepository;
import com.ciicc.Banking_Application.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SavingsInterestScheduler {

    private final SavingsAccountRepository savingsRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;

    // Runs every 5 minutes for testing
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void applyInterest() {
        List<SavingsAccount> accounts = savingsRepository.findAll();

        for (SavingsAccount account : accounts) {
            BigDecimal balance = account.getBalance();
            BigDecimal interest = balance.multiply(BigDecimal.valueOf(account.getInterestRate()).divide(BigDecimal.valueOf(100)));

            /*
            // ===== Annual Interest (commented out) =====
            // 3.5% per year, assuming 365 days
            BigDecimal annualRate = BigDecimal.valueOf(account.getInterestRate())
                    .divide(BigDecimal.valueOf(100 * 365), BigDecimal.ROUND_HALF_UP); // 3.5% per year
            BigDecimal interest = account.getBalance().multiply(annualRate);
            */

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
            auditLogRepository.save(
                    com.ciicc.Banking_Application.entity.AuditLog.builder()
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

