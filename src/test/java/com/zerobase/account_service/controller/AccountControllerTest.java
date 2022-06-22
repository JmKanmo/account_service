package com.zerobase.account_service.controller;

import com.zerobase.account_service.domain.Account;
import com.zerobase.account_service.dto.AccountResponse;
import com.zerobase.account_service.service.AccountService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
    @MockBean
    private AccountService accountService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createAccountTest() throws Exception {
        AccountResponse accountResponse = AccountResponse.builder()
                .accountNumber(2013161003L)
                .userId("nebi25")
                .dateTime(LocalDateTime.now().toString())
                .build();

        when(accountService.createAccount(anyString(), anyLong())).thenReturn(accountResponse);

        mockMvc.perform(get("/account/create?")
                        .param("userId", "nebi25")
                        .param("tradeMoney", "50000").accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(accountResponse)));
    }

    @Test
    public void discardAccountTest() throws Exception {
        AccountResponse accountResponse = AccountResponse.builder()
                .accountNumber(2013161003L)
                .userId("nebi25")
                .dateTime(LocalDateTime.now().toString())
                .build();

        when(accountService.discardAccount(anyString(), anyLong())).thenReturn(accountResponse);

        mockMvc.perform(get("/account/discard?")
                        .param("userId", "nebi25")
                        .param("accountNumber", "2013161003").accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(accountResponse)));
    }

    @Test
    public void checkAccountTest() throws Exception {
        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .activate(true)
                        .createdTime(LocalDateTime.now().toString())
                        .money(50000)
                        .number(2013161003)
                        .holder("nebi25")
                        .build(),

                Account.builder()
                        .activate(true)
                        .createdTime(LocalDateTime.now().toString())
                        .money(50000)
                        .number(2013161003)
                        .holder("nebi25")
                        .build(),

                Account.builder()
                        .activate(true)
                        .createdTime(LocalDateTime.now().toString())
                        .money(50000)
                        .number(2013161003)
                        .holder("nebi25")
                        .build()
        );

        when(accountService.getAccountInfos(anyString())).thenReturn(accounts);

        mockMvc.perform(get("/account/check?")
                        .param("userId", "nebi25"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(accounts)));
    }
}