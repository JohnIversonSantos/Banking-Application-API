package com.ciicc.Banking_Application.repository;

import com.ciicc.Banking_Application.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByPerformedByOrderByCreatedAtDesc(String performedBy);

    List<AuditLog> findByTargetOrderByCreatedAtDesc(String target);
}
