package com.zerobase.account_service.service;

import com.zerobase.account_service.domain.Account;
import com.zerobase.account_service.dto.AccountResponse;
import com.zerobase.account_service.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
class AccountServiceTest {
    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Test
    void createAccount() {
        try {
            when(accountRepository.createAccount(anyString(), anyLong())).thenReturn(
                    AccountResponse.builder()
                            .accountNumber(2013161003L)
                            .userId("nebi25")
                            .dateTime(LocalDateTime.now().toString())
                            .build());

            AccountResponse accountResponse = accountService.createAccount(anyString(), anyLong());
            assertTrue(accountResponse != null
                    && accountResponse.getUserId().equals("nebi25")
                    && accountResponse.getAccountNumber() == 2013161003L);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void discardAccount() {
        try {
            when(accountRepository.discardAccount(anyString(), anyLong())).thenReturn(
                    AccountResponse.builder()
                            .accountNumber(2013161003L)
                            .userId("nebi25")
                            .dateTime(LocalDateTime.now().toString())
                            .build());

            AccountResponse accountResponse = accountService.discardAccount(anyString(), anyLong());
            assertTrue(accountResponse != null
                    && accountResponse.getUserId().equals("nebi25")
                    && accountResponse.getAccountNumber() == 2013161003L);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void getAccountInfo() {
        try {
            when(accountRepository.getAccountInfos(anyString())).thenReturn(
                    Arrays.asList(Account.builder().build()
                            , Account.builder().build()));

            assertEquals(accountService.getAccountInfos(anyString()).size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}