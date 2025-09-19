package com.ciicc.Banking_Application.controller;

import com.ciicc.Banking_Application.dto.AccountInfo;
import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.LoginRequest;
import com.ciicc.Banking_Application.dto.UserRequest;
import com.ciicc.Banking_Application.entity.User;
import com.ciicc.Banking_Application.repository.SavingsAccountRepository;
import com.ciicc.Banking_Application.security.JwtUtil;
import com.ciicc.Banking_Application.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final SavingsAccountRepository savingsRepository;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    /** -------------------- Register New User -------------------- **/
    @PostMapping("/register")
    public BankResponse registerUser(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    /** -------------------- Login User -------------------- **/
//    @PostMapping("/login")
//    public BankResponse loginUser(@RequestBody LoginRequest loginRequest) {
//        return userService.login(loginRequest);
//    }

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

    @GetMapping("/me")
    public BankResponse getCurrentUser(HttpServletRequest request) {
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
        var savingsOpt = savingsRepository.findByAccountNumber(user.getAccountNumber());
        AccountInfo accountInfo = savingsOpt.map(savings -> AccountInfo.builder()
                .accountNumber(user.getAccountNumber())
                .phoneNumber(user.getPhoneNumber())
                .accountBalance(savings.getBalance())
                .accountType("SAVINGS")
                .currency(savings.getCurrency())
                .status(user.getStatus())
                .build()).orElse(null);

        return BankResponse.success("User retrieved successfully", accountInfo, user);
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
