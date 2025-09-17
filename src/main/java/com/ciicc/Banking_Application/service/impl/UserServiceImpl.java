package com.ciicc.Banking_Application.service.impl;

import com.ciicc.Banking_Application.dto.AccountInfo;
import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.LoginRequest;
import com.ciicc.Banking_Application.dto.UserRequest;
import com.ciicc.Banking_Application.entity.AuditLog;
import com.ciicc.Banking_Application.entity.User;
import com.ciicc.Banking_Application.entity.SavingsAccount;
import com.ciicc.Banking_Application.entity.Wallet;
import com.ciicc.Banking_Application.repository.*;
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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** -------------------- Account Creation -------------------- **/
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            logAudit("CREATE_ACCOUNT_FAILED", userRequest.getEmail(), "Email already exists");
            return BankResponse.conflict("Email already exists!");
        }

        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            logAudit("CREATE_ACCOUNT_FAILED", userRequest.getPhoneNumber(), "Phone number already exists");
            return BankResponse.conflict("Phone number already exists!");
        }

        User newUser = buildNewUser(userRequest);
        User savedUser = userRepository.save(newUser);

        // Create savings account
        SavingsAccount savings = new SavingsAccount();
        savings.setUser(savedUser);
        savings.setAccountNumber(savedUser.getAccountNumber());
        savings.setBalance(BigDecimal.ZERO);
        savingsRepository.save(savings);

        // Create wallet
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);

        logAudit("CREATE_ACCOUNT_SUCCESS", savedUser.getEmail(),
                "Account created successfully: " + savedUser.getAccountNumber());

        AccountInfo accountInfo = AccountInfo.builder()
                .accountNumber(savedUser.getAccountNumber())
                .phoneNumber(savedUser.getPhoneNumber())
                .accountBalance(savings.getBalance())
                .accountType("SAVINGS")
                .currency(savings.getCurrency())
                .status(savedUser.getStatus())
                .build();

        return BankResponse.created("Account created successfully!", accountInfo);
    }

    private User buildNewUser(UserRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountNumber(AccountUtils.generateAccountNumber());
        user.setRole("CUSTOMER");
        user.setStatus("ACTIVE");
        return user;
    }

    /** -------------------- Login -------------------- **/
    @Override
    public BankResponse login(LoginRequest loginRequest) {
        String identifier = loginRequest.getIdentifier();
        String password = loginRequest.getPassword();

        Optional<User> userOpt = userRepository.findByEmail(identifier);
        if (userOpt.isEmpty()) userOpt = userRepository.findByPhoneNumber(identifier);

        if (userOpt.isEmpty()) {
            logAudit("LOGIN_FAILED", identifier, "User not found");
            return BankResponse.notFound("User not found with provided email/phone!");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logAudit("LOGIN_FAILED", user.getEmail(), "Incorrect password");
            return BankResponse.unauthorized("Invalid credentials!");
        }

        // Fetch savings balance
        SavingsAccount savings = savingsRepository.findByAccountNumber(user.getAccountNumber())
                .orElseGet(() -> {
                    SavingsAccount s = new SavingsAccount();
                    s.setUser(user);
                    s.setAccountNumber(user.getAccountNumber());
                    s.setBalance(BigDecimal.ZERO);
                    return s;
                });

        logAudit("LOGIN_SUCCESS", user.getEmail(), "User logged in successfully");

        AccountInfo accountInfo = AccountInfo.builder()
                .accountNumber(user.getAccountNumber())
                .accountBalance(savings.getBalance())
                .accountType("SAVINGS")
                .currency(savings.getCurrency())
                .status(user.getStatus())
                .build();

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
        var userOpt = userRepository.findByAccountNumber(accountNumber);
        if (userOpt.isEmpty()) return BankResponse.notFound("User not found with account number: " + accountNumber);

        var user = userOpt.get();
        var savings = savingsRepository.findByAccountNumber(accountNumber)
                .orElseGet(() -> {
                    var s = new SavingsAccount();
                    s.setUser(user);
                    s.setAccountNumber(accountNumber);
                    s.setBalance(BigDecimal.ZERO);
                    return s;
                });

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
