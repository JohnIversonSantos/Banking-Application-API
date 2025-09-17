package com.ciicc.Banking_Application.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.ciicc.Banking_Application.dto.TransactionHistory;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "savings_account_id", nullable = true)
    private SavingsAccount savingsAccount;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = true)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;

    private BigDecimal fee = BigDecimal.ZERO;

    private String description;

    private String targetAccountNumber;

    @CreationTimestamp
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String status;

    // ---------------- Safe DTO Method ----------------
    public TransactionHistory toSafeHistory() {
        return new TransactionHistory(
                this.type,
                this.amount,
                this.fee,
                this.description,
                this.targetAccountNumber,
                this.timestamp,
                this.status
        );
    }
}
