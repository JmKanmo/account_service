package com.zerobase.account_service.service;

import com.zerobase.account_service.domain.Account;
import com.zerobase.account_service.domain.dto.AccountResponse;
import com.zerobase.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountResponse createAccount(String userId, long tradeMoney) {
        return accountRepository.createAccount(userId, tradeMoney);
    }

    public AccountResponse discardAccount(String userId, long accountNumber) {
        return accountRepository.discardAccount(userId, accountNumber);
    }

    public List<Account> getAccountInfos(String userId) {
        return accountRepository.getAccountInfos(userId);
    }
}
