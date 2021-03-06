package com.zerobase.account_service.service;

import com.zerobase.account_service.domain.Account;
import com.zerobase.account_service.domain.TradeType;
import com.zerobase.account_service.domain.Transaction;
import com.zerobase.account_service.dto.TransactionResponse;
import com.zerobase.account_service.repository.AccountRepository;
import com.zerobase.account_service.repository.TransactionRepository;
import com.zerobase.account_service.exception.TradeFailException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
class TransactionServiceTest {
    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Test
    public void useMoneyTrade() {
        try {
            when(accountRepository.useMoney(anyString(), anyLong(), anyLong())).thenReturn(
                    Account.builder()
                            .number(2013161003L)
                            .money(50000)
                            .activate(true)
                            .createdTime(LocalDateTime.now().toString())
                            .holder("nebi25")
                            .build());

            Transaction transaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .tradeType(TradeType.BALANCE_USE)
                    .result(true)
                    .accountNumber(2013161003)
                    .tradeMoney(50000)
                    .build();

            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

            TransactionResponse transactionResponse = transactionService.useMoneyTrade("nebi25", 2013161003L, 50000);
            assertTrue(transactionResponse != null
                    && transactionResponse.getTransactionId() != null
                    && transactionResponse.getAccountNumber() == 2013161003L
                    && transaction.getTradeType() == TradeType.BALANCE_USE
                    && transactionResponse.getTradeMoney() == 50000
                    && transactionResponse.isTransactionResult() == true);

            verify(accountRepository, times(1)).useMoney(anyString(), anyLong(), anyLong());
            verify(transactionRepository, times(1)).save(any());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void cancelMoneyTradeTest() {
        try {
            when(accountRepository.cancelUsageMoney(anyString(), anyLong(), anyLong())).thenReturn(
                    Account.builder()
                            .number(2013161003L)
                            .money(50000)
                            .activate(true)
                            .createdTime(LocalDateTime.now().toString())
                            .holder("nebi25")
                            .build()
            );

            when(transactionRepository.save(any())).thenReturn(Transaction.builder().build());

            // ???????????? ????????? CANCEL??? ??????
            when(transactionRepository.findByTransactionId(anyString())).thenReturn(Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .tradeType(TradeType.BALANCE_USE_CANCEL)
                    .tradeMoney(50000L)
                    .accountNumber(2013161003L)
                    .result(true)
                    .build());

            Assertions.assertThrows(TradeFailException.class, () -> transactionService.cancelMoneyTrade(UUID.randomUUID().toString(), "nebi25", 2013161003L, 50000L), "????????? ?????? ?????? ????????? ??????????????? ?????? ????????? ??? ????????????.");

            // ???????????? id??? ???????????? ?????? ????????? ?????? ??????
            when(transactionRepository.findByTransactionId(anyString())).thenReturn(null);
            Assertions.assertThrows(TradeFailException.class, () -> transactionService.cancelMoneyTrade(UUID.randomUUID().toString(), "nebi25", 2013161003L, 50000L), "???????????? id??? ???????????? ?????? ????????? ????????????.");

            // ??????????????? ?????? ????????? ????????? ?????? ??????
            when(transactionRepository.findByTransactionId(anyString())).thenReturn(Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .tradeType(TradeType.BALANCE_USE)
                    .tradeMoney(50000L)
                    .accountNumber(20131610033L)
                    .result(true)
                    .build());
            assertThrows(TradeFailException.class, () -> transactionService.cancelMoneyTrade(UUID.randomUUID().toString(), "nebi25", 2013161003L, 50000L), "??????????????? ?????? ????????? ????????? ????????????.");


            // ????????? ????????? ?????? ????????? ?????? ??????
            when(transactionRepository.findByTransactionId(anyString())).thenReturn(Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .tradeType(TradeType.BALANCE_USE)
                    .tradeMoney(500000L)
                    .accountNumber(2013161003L)
                    .result(true)
                    .build());
            assertThrows(TradeFailException.class, () -> transactionService.cancelMoneyTrade(UUID.randomUUID().toString(), "nebi25", 2013161003L, 50000L), "????????? ????????? ?????? ????????? ????????????.");


            // ?????? ?????? ??????
            when(transactionRepository.findByTransactionId(anyString())).thenReturn(Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .tradeType(TradeType.BALANCE_USE)
                    .tradeMoney(50000L)
                    .accountNumber(2013161003L)
                    .result(true)
                    .build());
            transactionService.cancelMoneyTrade(UUID.randomUUID().toString(), "nebi25", 2013161003L, 50000L);

            verify(transactionRepository, atLeast(1)).findByTransactionId(any());
            verify(accountRepository, atLeast(1)).cancelUsageMoney(anyString(), anyLong(), anyLong());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void getTransactionTest() {
        try {
            when(transactionRepository.findByTransactionId(anyString())).thenReturn(null);
            assertThrows(TradeFailException.class, () -> transactionService.getTransaction(anyString()), "?????? ???????????? id??? ?????? ???????????? ????????? ?????? ??? ????????????.");

            String tid = UUID.randomUUID().toString();
            Transaction transaction = Transaction.builder()
                    .id(1L)
                    .transactionId(tid)
                    .tradeType(TradeType.BALANCE_USE)
                    .result(true)
                    .accountNumber(2013161003L)
                    .tradeMoney(50000L)
                    .build();
            when(transactionRepository.findByTransactionId(anyString())).thenReturn(transaction);
            assertEquals(transactionService.getTransaction(tid) == transaction, true);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}