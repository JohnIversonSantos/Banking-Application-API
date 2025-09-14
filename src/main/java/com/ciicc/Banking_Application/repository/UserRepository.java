package com.ciicc.Banking_Application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ciicc.Banking_Application.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByAccountNumber(String accountNumber);

    // Combine search by email OR phone
    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

}
