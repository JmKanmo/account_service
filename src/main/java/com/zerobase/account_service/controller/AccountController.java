package com.zerobase.account_service.controller;

import com.zerobase.account_service.domain.Account;
import com.zerobase.account_service.domain.dto.AccountResponse;
import com.zerobase.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(
            @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
            @RequestParam(value = "tradeMoney", defaultValue = "0", required = false) long tradeMoney) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.createAccount(userId, tradeMoney));
    }

    @GetMapping("/discard")
    public ResponseEntity<AccountResponse> discardAccount(
            @RequestParam(value = "userId", defaultValue = "", required = false) String userId,
            @RequestParam(value = "accountNumber", defaultValue = "0", required = false) long accountNumber
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.discardAccount(userId, accountNumber));
    }

    @GetMapping("/check")
    public ResponseEntity<List<Account>> getAccountInfos(@RequestParam(value = "userId", defaultValue = "", required = false) String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccountInfos(userId));
    }
}
