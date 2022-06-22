package com.zerobase.account_service.controller;

import com.zerobase.account_service.domain.TradeType;
import com.zerobase.account_service.domain.Transaction;
import com.zerobase.account_service.dto.TransactionResponse;
import com.zerobase.account_service.service.TransactionService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void balanceUse() throws Exception {
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .accountNumber(2013161003L)
                .tradeMoney(50000L)
                .transactionId(UUID.randomUUID().toString())
                .transactionResult(true)
                .tradeDateTime(LocalDateTime.now().toString())
                .build();

        when(transactionService.useMoneyTrade(anyString(), anyLong(), anyLong())).thenReturn(transactionResponse);

        mockMvc.perform(get("/transaction/balance-use?")
                        .param("userId", "nebi25")
                        .param("accountNumber", "2013161003")
                        .param("tradeMoney", "50000")
                        .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(transactionResponse)));
    }

    @Test
    void balanceCancel() throws Exception {
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .accountNumber(2013161003L)
                .tradeMoney(50000L)
                .transactionId(UUID.randomUUID().toString())
                .transactionResult(true)
                .tradeDateTime(LocalDateTime.now().toString())
                .build();

        when(transactionService.cancelMoneyTrade(anyString(), anyString(), anyLong(), anyLong())).thenReturn(transactionResponse);

        mockMvc.perform(get("/transaction/balance-cancel?")
                        .param("userId", "nebi25")
                        .param("accountNumber", "2013161003")
                        .param("tradeMoney", "50000")
                        .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(transactionResponse)));
    }

    @Test
    void balanceCheck() throws Exception {
        Transaction transaction = Transaction.builder()
                .accountNumber(2013161003L)
                .tradeMoney(50000L)
                .transactionId(UUID.randomUUID().toString())
                .result(true)
                .tradeType(TradeType.BALANCE_USE)
                .build();

        when(transactionService.getTransaction(anyString())).thenReturn(transaction);

        mockMvc.perform(get("/transaction/balance-check?")
                        .param("transactionId", UUID.randomUUID().toString())
                        .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}