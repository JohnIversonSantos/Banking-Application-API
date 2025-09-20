package com.ciicc.Banking_Application.repository;

import com.ciicc.Banking_Application.entity.User;  // ← Add this import
import com.ciicc.Banking_Application.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletAccountRepository extends JpaRepository<Wallet, Long> {

    // Find wallet by the user's phone number
    Optional<Wallet> findByUserPhoneNumber(String phoneNumber);

    Optional<Wallet> findByUser(User user);  // ← This will now work
}