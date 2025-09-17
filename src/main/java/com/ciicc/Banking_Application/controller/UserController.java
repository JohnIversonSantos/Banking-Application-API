package com.ciicc.Banking_Application.controller;

import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.LoginRequest;
import com.ciicc.Banking_Application.dto.UserRequest;
import com.ciicc.Banking_Application.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173") // adjust if needed
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /** -------------------- Register New User -------------------- **/
    @PostMapping("/register")
    public BankResponse registerUser(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    /** -------------------- Login User -------------------- **/
    @PostMapping("/login")
    public BankResponse loginUser(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    /** -------------------- Get All Users (Admin / Testing Only) -------------------- **/
    @GetMapping("/all")
    public BankResponse getAllUsers() {
        return userService.getAllUsers();
    }

<<<<<<< HEAD
    /** -------------------- Get User by Account Number -------------------- **/
    @GetMapping("/{accountNumber}")
    public BankResponse getUserByAccountNumber(@PathVariable String accountNumber) {
        return userService.getUserByAccountNumber(accountNumber);
=======
    // Find user by account number (testing only)
    @GetMapping("/{accountNumber}")
    public User getUserByAccountNumber(@PathVariable String accountNumber) {
        return this.userRepository.findByAccountNumber(accountNumber)
                .orElse(null);
>>>>>>> parent of 98bc79d (omment A rest function)
    }
}
