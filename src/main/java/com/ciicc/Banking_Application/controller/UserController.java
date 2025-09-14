package com.ciicc.Banking_Application.controller;

import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.LoginRequest;
import com.ciicc.Banking_Application.dto.UserRequest;
import com.ciicc.Banking_Application.entity.User;
import com.ciicc.Banking_Application.repository.UserRepository;
import com.ciicc.Banking_Application.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173") // or 3000 if you're using CRA
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // for testing-only endpoints

    // Register new user
    @PostMapping("/register")
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return this.userService.createAccount(userRequest);
    }

    // Login user (email or phone number + password)
    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginRequest loginRequest) {
        return this.userService.login(loginRequest);
    }

    // Get all users (testing only – don’t expose in prod)
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    // Find user by account number (testing only)
    @GetMapping("/{accountNumber}")
    public User getUserByAccountNumber(@PathVariable String accountNumber) {
        return this.userRepository.findByAccountNumber(accountNumber)
                .orElse(null);
    }
}
