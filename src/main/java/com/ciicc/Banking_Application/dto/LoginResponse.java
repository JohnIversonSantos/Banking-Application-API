package com.ciicc.Banking_Application.dto;

import lombok.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private List<AccountInfo> accounts; // wallet + savings
    private String token;               // JWT or session token
}
