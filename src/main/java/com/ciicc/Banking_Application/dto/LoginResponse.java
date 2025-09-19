package com.ciicc.Banking_Application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phoneNumber;
    private String accountNumber;
    private String role;
    private String status;
    private String token;
}
