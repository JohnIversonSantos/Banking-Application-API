package com.ciicc.Banking_Application.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    
    private String identifier;  // can be email OR phone
    private String password;
}
