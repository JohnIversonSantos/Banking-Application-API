package com.ciicc.Banking_Application.service.impl;

import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.LoginRequest;
import com.ciicc.Banking_Application.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse login(LoginRequest loginRequest);
}
