package com.ciicc.Banking_Application.controller;

import com.ciicc.Banking_Application.dto.AccountInfo;
import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.UserRequest;
import com.ciicc.Banking_Application.dto.UserResponse;
import com.ciicc.Banking_Application.entity.User;
import com.ciicc.Banking_Application.entity.Wallet;
import com.ciicc.Banking_Application.repository.SavingsAccountRepository;
import com.ciicc.Banking_Application.repository.WalletAccountRepository;
import com.ciicc.Banking_Application.security.JwtUtil;
import com.ciicc.Banking_Application.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Optional;

@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        exposedHeaders = "*",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final SavingsAccountRepository savingsRepository;
    private final WalletAccountRepository walletRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    /** -------------------- Register New User -------------------- **/
    @PostMapping("/register")
    public BankResponse registerUser(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    /** -------------------- Get All Users (Admin / Testing Only) -------------------- **/
    @GetMapping("/all")
    public BankResponse getAllUsers() {
        return userService.getAllUsers();
    }

    /** -------------------- Get User by Account Number -------------------- **/
    @GetMapping("/{accountNumber}")
    public BankResponse getUserByAccountNumber(@PathVariable String accountNumber) {
        return userService.getUserByAccountNumber(accountNumber);
    }

    /** -------------------- Get Current User (Updated Version) -------------------- **/
    @GetMapping("/me")
    public BankResponse getCurrentUser() {
        try {
            // Method 1: Use Spring Security Context (Recommended)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            System.out.println("🔍 Getting user for email from Security Context: " + email);

            // Use the service method you created
            return userService.getCurrentUser(email);

        } catch (Exception e) {
            System.err.println("❌ Error in /users/me: " + e.getMessage());
            e.printStackTrace();
            return BankResponse.builder()
                    .responseCode("500")
                    .responseMessage("Failed to retrieve user: " + e.getMessage())
                    .build();
        }
    }

    /** -------------------- Alternative /me endpoint using manual token extraction -------------------- **/
    @GetMapping("/me-alt")
    public BankResponse getCurrentUserAlt(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return BankResponse.unauthorized("Missing token");
            }

            if (!jwtUtil.validateToken(token)) {
                return BankResponse.unauthorized("Invalid or expired token");
            }

            String email = jwtUtil.extractEmail(token);
            System.out.println("🔍 Getting user for email from JWT: " + email);

            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return BankResponse.notFound("User not found");
            }

            User user = userOpt.get();

            // Get wallet and savings balances
            Optional<Wallet> walletOpt = walletRepository.findByUserPhoneNumber(user.getPhoneNumber());
            var savingsOpt = savingsRepository.findByAccountNumber(user.getAccountNumber());

            BigDecimal walletBalance = walletOpt.map(Wallet::getBalance).orElse(BigDecimal.ZERO);
            BigDecimal savingsBalance = savingsOpt.map(savings -> savings.getBalance()).orElse(BigDecimal.ZERO);

            // Build full user response with all the data
            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .middleName(user.getMiddleName())
                    .gender(user.getGender())
                    .dateOfBirth(user.getDateOfBirth())        // Direct assignment
                    .address(user.getAddress())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .accountNumber(user.getAccountNumber())
                    .walletBalance(walletBalance)
                    .savingsBalance(savingsBalance)
                    .role(user.getRole())
                    .status(user.getStatus())
                    .createdAt(user.getCreatedAt())            // Direct assignment
                    .updatedAt(user.getUpdatedAt())            // Direct assignment
                    .build();

            return BankResponse.success("User retrieved successfully", userResponse);

        } catch (Exception e) {
            System.err.println("❌ Error in /users/me-alt: " + e.getMessage());
            e.printStackTrace();
            return BankResponse.builder()
                    .responseCode("500")
                    .responseMessage("Failed to retrieve user: " + e.getMessage())
                    .build();
        }
    }

    /** -------------------- Original /me endpoint (for backward compatibility) -------------------- **/
    @GetMapping("/me-original")
    public BankResponse getCurrentUserOriginal(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return BankResponse.unauthorized("Missing token");
        }

        if (!jwtUtil.validateToken(token)) {
            return BankResponse.unauthorized("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isEmpty()) {
            return BankResponse.notFound("User not found");
        }

        User user = userOpt.get();

        // Get savings account info
        var savingsOpt = savingsRepository.findByAccountNumber(user.getAccountNumber());
        AccountInfo accountInfo = savingsOpt.map(savings -> AccountInfo.builder()
                .accountNumber(user.getAccountNumber())
                .phoneNumber(user.getPhoneNumber())
                .accountBalance(savings.getBalance() != null ? savings.getBalance() : BigDecimal.ZERO)
                .accountType("SAVINGS")
                .currency(savings.getCurrency() != null ? savings.getCurrency() : "PHP")
                .status(user.getStatus() != null ? user.getStatus() : "ACTIVE")
                .build()).orElse(null);

        // Return BankResponse containing both account info and full user data
        return BankResponse.builder()
                .responseCode("200")
                .responseMessage("User retrieved successfully")
                .accountInfo(accountInfo)
                .data(user) // full user object
                .build();
    }

    /** -------------------- Helper Method -------------------- **/
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}