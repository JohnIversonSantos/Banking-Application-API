package com.ciicc.Banking_Application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistory(
        com.ciicc.Banking_Application.entity.TransactionType type,
        BigDecimal amount,
        BigDecimal fee,
        String description,
        String targetAccountOrPhone,
        LocalDateTime timestamp,
        String status
) {}
