package com.ciicc.Banking_Application.service.impl;

import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.LoginRequest;
import com.ciicc.Banking_Application.dto.UserRequest;

public interface UserService {

    /** -------------------- Account Management -------------------- **/
    BankResponse createAccount(UserRequest userRequest);

    BankResponse login(LoginRequest loginRequest);

    /** -------------------- User Queries -------------------- **/
    BankResponse getAllUsers(); // Admin/testing only

    BankResponse getUserByAccountNumber(String accountNumber);
}
