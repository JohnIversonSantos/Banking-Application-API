package com.ciicc.Banking_Application.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;        // e.g., LOGIN, TRANSFER
    private String performedBy;   // userId or phone/email
    private String target;        // affected account/phone/transaction
    private String status;        // SUCCESS / FAILED
    private String details;       // optional JSON or message

    @CreationTimestamp
    private LocalDateTime createdAt;
}
