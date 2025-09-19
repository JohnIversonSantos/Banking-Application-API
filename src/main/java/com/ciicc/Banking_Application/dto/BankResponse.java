package com.ciicc.Banking_Application.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankResponse {

    private String responseCode;
    private String responseMessage;
    private AccountInfo accountInfo;  // Info about the affected account
    private Object data;              // Any extra payload (transactions, lists, etc.)

    // ✅ Success with Account Info
    public static BankResponse success(String message, AccountInfo accountInfo) {
        return BankResponse.builder()
                .responseCode("200")
                .responseMessage(message)
                .accountInfo(accountInfo)
                .build();
    }

    // ✅ Success with custom data
    public static BankResponse success(String message, Object data) {
        return BankResponse.builder()
                .responseCode("200")
                .responseMessage(message)
                .data(data)
                .build();
    }

    // ✅ Success with Account Info + Data
    public static BankResponse success(String message, AccountInfo accountInfo, Object data) {
        return BankResponse.builder()
                .responseCode("200")
                .responseMessage(message)
                .accountInfo(accountInfo)
                .data(data)
                .build();
    }

    // ✅ Created
    public static BankResponse created(String message, AccountInfo accountInfo) {
        return BankResponse.builder()
                .responseCode("201")
                .responseMessage(message)
                .accountInfo(accountInfo)
                .build();
    }

    // ✅ Conflict
    public static BankResponse conflict(String message) {
        return BankResponse.builder()
                .responseCode("409")
                .responseMessage(message)
                .build();
    }

    // ✅ Unauthorized
    public static BankResponse unauthorized(String message) {
        return BankResponse.builder()
                .responseCode("401")
                .responseMessage(message)
                .build();
    }

    // ✅ Not Found
    public static BankResponse notFound(String message) {
        return BankResponse.builder()
                .responseCode("404")
                .responseMessage(message)
                .build();
    }
}
