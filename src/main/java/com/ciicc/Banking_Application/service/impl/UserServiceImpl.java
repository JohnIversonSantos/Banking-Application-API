package com.ciicc.Banking_Application.service.impl;

import com.ciicc.Banking_Application.dto.*;
import com.ciicc.Banking_Application.entity.AuditLog;
import com.ciicc.Banking_Application.entity.SavingsAccount;
import com.ciicc.Banking_Application.entity.User;
import com.ciicc.Banking_Application.entity.Wallet;
import com.ciicc.Banking_Application.repository.*;
import com.ciicc.Banking_Application.security.JwtUtil;
import com.ciicc.Banking_Application.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SavingsAccountRepository savingsRepository;
    private final WalletAccountRepository walletRepository;
    private final AuditLogRepository auditLogRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder; // ✅ injected bean, no "new"

    /** -------------------- Account Creation -------------------- **/
    @Override
    public BankResponse createAccount(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            return BankResponse.conflict("Email already exists!");
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber()))
            return BankResponse.conflict("Phone number already exists!");

        User user = buildNewUser(request);
        userRepository.save(user);

        // Create savings account
        SavingsAccount savings = SavingsAccount.builder()
                .user(user)
                .accountNumber(user.getAccountNumber())
                .balance(BigDecimal.ZERO)
                .build();
        savingsRepository.save(savings);

        // Create wallet
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();
        walletRepository.save(wallet);

        logAudit("CREATE_ACCOUNT_SUCCESS", user.getEmail(), "Account created: " + user.getAccountNumber());

        AccountInfo accountInfo = AccountInfo.builder()
                .accountNumber(user.getAccountNumber())
                .phoneNumber(user.getPhoneNumber())
                .accountBalance(savings.getBalance())
                .accountType("SAVINGS")
                .currency(savings.getCurrency())
                .status(user.getStatus())
                .build();

        return BankResponse.created("Account created successfully!", accountInfo);
    }

    private User buildNewUser(UserRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.getPassword())) // ✅ encode with injected bean
                .accountNumber(AccountUtils.generateAccountNumber())
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
    }

    /** -------------------- Login -------------------- **/
    @Override
    public BankResponse login(LoginRequest request) {
        // Find user by email or phone
        Optional<User> userOpt = userRepository.findByEmail(request.getIdentifier());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByPhoneNumber(request.getIdentifier());
        }

        if (userOpt.isEmpty()) {
            return BankResponse.notFound("User not found with provided email/phone!");
        }

        User user = userOpt.get();

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return BankResponse.unauthorized("Invalid credentials!");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Get savings account, ensure currency and balance are not null
        SavingsAccount savings = savingsRepository.findByAccountNumber(user.getAccountNumber())
                .orElse(SavingsAccount.builder()
                        .user(user)
                        .accountNumber(user.getAccountNumber())
                        .balance(BigDecimal.ZERO)
                        .currency("PHP")
                        .build());

        // Audit log
        logAudit("LOGIN_SUCCESS", user.getEmail(), "User logged in");

        // Build AccountInfo safely
        AccountInfo accountInfo = AccountInfo.builder()
                .accountNumber(user.getAccountNumber())
                .phoneNumber(user.getPhoneNumber())
                .accountBalance(savings.getBalance() != null ? savings.getBalance() : BigDecimal.ZERO)
                .accountType("SAVINGS")
                .currency(savings.getCurrency() != null ? savings.getCurrency() : "PHP")
                .status(user.getStatus() != null ? user.getStatus() : "ACTIVE")
                .build();

        // Return login info
        return BankResponse.success("Login successful!", accountInfo);
    }

    /** -------------------- Users Retrieval -------------------- **/
    @Override
    public BankResponse getAllUsers() {
        var users = userRepository.findAll();
        if (users.isEmpty()) return BankResponse.notFound("No users found");
        return BankResponse.success("Users retrieved successfully", users);
    }

    @Override
    public BankResponse getUserByAccountNumber(String accountNumber) {
        Optional<User> userOpt = userRepository.findByAccountNumber(accountNumber);
        if (userOpt.isEmpty())
            return BankResponse.notFound("User not found with account number: " + accountNumber);

        User user = userOpt.get();
        SavingsAccount savings = savingsRepository.findByAccountNumber(accountNumber)
                .orElse(SavingsAccount.builder()
                        .user(user)
                        .accountNumber(accountNumber)
                        .balance(BigDecimal.ZERO)
                        .build());

        AccountInfo accountInfo = AccountInfo.builder()
                .accountNumber(user.getAccountNumber())
                .phoneNumber(user.getPhoneNumber())
                .accountBalance(savings.getBalance())
                .accountType("SAVINGS")
                .currency(savings.getCurrency())
                .status(user.getStatus())
                .build();

        return BankResponse.success("User retrieved successfully", accountInfo);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByIdentifier(String identifier) {
        Optional<User> userOpt = userRepository.findByEmail(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByPhoneNumber(identifier);
        }
        return userOpt;
    }

    /** -------------------- Audit Logging -------------------- **/
    private void logAudit(String action, String performedBy, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .target(null)
                .status("SUCCESS")
                .details(details)
                .build();
        auditLogRepository.save(log);
    }
}
