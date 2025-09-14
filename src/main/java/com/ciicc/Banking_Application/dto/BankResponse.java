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
    private AccountInfo accountInfo;
    private Object data;

    // Static helpers to build responses

    public static BankResponse success(String message, AccountInfo accountInfo) {
        return BankResponse.builder()
                .responseCode("200")
                .responseMessage(message)
                .accountInfo(accountInfo)
                .build();
    }
    public static BankResponse success(String message, Object Data) {
        return BankResponse.builder()
                .responseCode("200")
                .responseMessage(message)
                .data(Data)
                .build();
    }

    public static BankResponse created(String message, AccountInfo accountInfo) {
        return BankResponse.builder()
                .responseCode("201")
                .responseMessage(message)
                .accountInfo(accountInfo)
                .build();
    }

    public static BankResponse conflict(String message) {
        return BankResponse.builder()
                .responseCode("409")
                .responseMessage(message)
                .build();
    }

    public static BankResponse unauthorized(String message) {
        return BankResponse.builder()
                .responseCode("401")
                .responseMessage(message)
                .build();
    }

    public static BankResponse notFound(String message) {
        return BankResponse.builder()
                .responseCode("404")
                .responseMessage(message)
                .build();
    }
}
