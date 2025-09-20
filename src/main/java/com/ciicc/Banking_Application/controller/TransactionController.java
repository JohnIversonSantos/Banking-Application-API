package com.ciicc.Banking_Application.controller;

import com.ciicc.Banking_Application.dto.BankResponse;
import com.ciicc.Banking_Application.dto.TransactionRequest;
import com.ciicc.Banking_Application.service.impl.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        exposedHeaders = "*",
        allowCredentials = "true"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    /** -------------------- WALLET -------------------- **/

    @PostMapping("/wallet/deposit")
    public BankResponse depositToWallet(@RequestBody TransactionRequest request) {
        return transactionService.depositToWallet(request.getPhoneNumber(), request.getAmount());
    }

    @PostMapping("/wallet/transfer")
    public BankResponse transferBetweenWallets(@RequestBody TransactionRequest request) {
        return transactionService.transferBetweenWallets(
                request.getFromPhone(),
                request.getToPhone(),
                request.getAmount()
        );
    }

    @PostMapping("/wallet/transfer-to-bank")
    public BankResponse transferWalletToSavings(@RequestBody TransactionRequest request) {
        return transactionService.transferWalletToSavings(
                request.getFromPhone(),
                request.getToAccount(),
                request.getAmount()
        );
    }

    @GetMapping("/wallet/history/{phoneNumber}")
    public BankResponse walletTransactionHistory(@PathVariable String phoneNumber) {
        return transactionService.getTransactionHistory(phoneNumber);
    }

    /** -------------------- SAVINGS -------------------- **/

    @PostMapping("/savings/deposit")
    public BankResponse depositToSavings(@RequestBody TransactionRequest request) {
        return transactionService.depositToSavings(request.getAccountNumber(), request.getAmount());
    }

    @PostMapping("/savings/withdraw")
    public BankResponse withdrawFromSavings(@RequestBody TransactionRequest request) {
        return transactionService.withdrawFromSavings(request.getAccountNumber(), request.getAmount());
    }

    @PostMapping("/savings/transfer")
    public BankResponse transferBetweenSavings(@RequestBody TransactionRequest request) {
        return transactionService.transferBetweenSavings(
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount()
        );
    }

    @PostMapping("/savings/transfer-to-wallet")
    public BankResponse transferSavingsToWallet(@RequestBody TransactionRequest request) {
        return transactionService.transferSavingsToWallet(
                request.getFromAccount(),
                request.getToPhone(),
                request.getAmount()
        );
    }

    @GetMapping("/savings/history/{accountNumber}")
    public BankResponse savingsTransactionHistory(@PathVariable String accountNumber) {
        return transactionService.getTransactionHistory(accountNumber);
    }
}
