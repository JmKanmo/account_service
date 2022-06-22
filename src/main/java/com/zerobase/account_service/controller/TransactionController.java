package com.zerobase.account_service.controller;

import com.zerobase.account_service.domain.Transaction;
import com.zerobase.account_service.dto.TransactionResponse;
import com.zerobase.account_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("balance-use")
    public ResponseEntity<TransactionResponse> balanceUse(
            @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
            @RequestParam(value = "accountNumber", defaultValue = "0", required = false) long accountNumber,
            @RequestParam(value = "tradeMoney", defaultValue = "0", required = false) long tradeMoney
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.useMoneyTrade(userId, accountNumber, tradeMoney));
    }

    @GetMapping("/balance-cancel")
    public ResponseEntity<TransactionResponse> balanceCancel(
            @RequestParam(value = "transactionId", defaultValue = "", required = false) String transactionId,
            @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
            @RequestParam(value = "accountNumber", defaultValue = "0", required = false) long accountNumber,
            @RequestParam(value = "tradeMoney", defaultValue = "0", required = false) long tradeMoney
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.cancelMoneyTrade(transactionId, userId, accountNumber, tradeMoney));
    }

    @GetMapping("/balance-check")
    public ResponseEntity<Transaction> balanceCheck(
            @RequestParam(value = "transactionId", defaultValue = "", required = false) String transactionId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransaction(transactionId));
    }
}
