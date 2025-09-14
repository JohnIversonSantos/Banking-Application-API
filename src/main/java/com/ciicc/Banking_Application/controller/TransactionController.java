package com.ciicc.Banking_Application.controller;

import java.math.BigDecimal;

import com.ciicc.Banking_Application.service.impl.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ciicc.Banking_Application.dto.BankResponse;

import lombok.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public BankResponse deposit(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        return this.transactionService.deposit(accountNumber, amount);
    }

    @PostMapping("/withdraw")
    public BankResponse withdraw(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        return this.transactionService.withdraw(accountNumber, amount);
    }

    @PostMapping("/transfer")
    public BankResponse transfer(@RequestParam String fromAccount,
                                 @RequestParam String toAccount,
                                 @RequestParam BigDecimal amount) {
        return this.transactionService.transfer(fromAccount, toAccount, amount);
    }

    // @GetMapping("/history/{accountNumber}")
    // public BankResponse getHistory(@PathVariable String accountNumber) {
    //     return this.transactionService.getTransactionHistory(accountNumber);
    // }
}

