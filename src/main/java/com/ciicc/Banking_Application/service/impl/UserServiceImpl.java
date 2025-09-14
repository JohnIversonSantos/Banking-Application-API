package com.ciicc.Banking_Application.service.impl;

import com.ciicc.Banking_Application.dto.AccountInfo;
import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.UserRequest;
import com.ciicc.Banking_Application.dto.LoginRequest;
import com.ciicc.Banking_Application.entity.User;
import com.ciicc.Banking_Application.repository.UserRepository;
import com.ciicc.Banking_Application.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (this.userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.conflict("Email already exists!");
        }

        if (this.userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            return BankResponse.conflict("Phone number already exists!");
        }

        User newUser = new User();
        newUser.setFirstName(userRequest.getFirstName());
        newUser.setLastName(userRequest.getLastName());
        newUser.setMiddleName(userRequest.getMiddleName());
        newUser.setEmail(userRequest.getEmail());
        newUser.setPhoneNumber(userRequest.getPhoneNumber());
        newUser.setGender(userRequest.getGender());
        newUser.setDateOfBirth(userRequest.getDateOfBirth());
        newUser.setAddress(userRequest.getAddress());

        // Hash password
        String rawPassword = userRequest.getPassword();
        String hashedPassword;
        try {
            hashedPassword = this.passwordEncoder.encode(rawPassword);
        } catch (Exception e) {
            hashedPassword = rawPassword;
        }
        newUser.setPassword(hashedPassword);

        // Account number
        String accountNumber = AccountUtils.generateAccountNumber();
        newUser.setAccountNumber(accountNumber);

        // Defaults
        newUser.setAccountBalance(BigDecimal.ZERO);
        newUser.setAccountType("SAVINGS");
        newUser.setCurrency("PHP");
        newUser.setRole("CUSTOMER");
        newUser.setStatus("ACTIVE");

        User savedUser = this.userRepository.save(newUser);

        AccountInfo accountInfo = AccountInfo.builder()
                .accountNumber(savedUser.getAccountNumber())
                .accountBalance(savedUser.getAccountBalance())
                .accountType(savedUser.getAccountType())
                .currency(savedUser.getCurrency())
                .status(savedUser.getStatus())
                .build();

        return BankResponse.created("Account created successfully!", accountInfo);
    }

    // Login method
    public BankResponse login(LoginRequest loginRequest) {
        String identifier = loginRequest.getIdentifier();
        String password = loginRequest.getPassword();

        Optional<User> userOpt = this.userRepository.findByEmail(identifier);
        if (userOpt.isEmpty()) {
            userOpt = this.userRepository.findByPhoneNumber(identifier);
        }

        if (userOpt.isEmpty()) {
            return BankResponse.notFound("User not found with provided email/phone!");
        }

        User user = userOpt.get();

        if (!this.passwordEncoder.matches(password, user.getPassword())) {
            return BankResponse.unauthorized("Invalid credentials!");
        }

        AccountInfo accountInfo = AccountInfo.builder()
                .accountNumber(user.getAccountNumber())
                .accountBalance(user.getAccountBalance())
                .accountType(user.getAccountType())
                .currency(user.getCurrency())
                .status(user.getStatus())
                .build();

        return BankResponse.success("Login successful!", accountInfo);
    }

}
