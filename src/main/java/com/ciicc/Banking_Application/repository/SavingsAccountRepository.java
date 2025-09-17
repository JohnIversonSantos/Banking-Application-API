package com.ciicc.Banking_Application.repository;

import com.ciicc.Banking_Application.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    Optional<SavingsAccount> findByAccountNumber(String accountNumber);
    Optional<SavingsAccount> findByUser_Id(Long userId);
}
