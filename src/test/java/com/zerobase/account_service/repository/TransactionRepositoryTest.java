package com.zerobase.account_service.repository;

import com.zerobase.account_service.domain.TradeType;
import com.zerobase.account_service.domain.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void insertAndFindTransactionTest() {
        try {
            String tid = UUID.randomUUID().toString();
            transactionRepository.save(transactionRepository.save(Transaction.builder()
                    .transactionId(tid)
                    .tradeType(TradeType.BALANCE_USE)
                    .result(true)
                    .accountNumber(2013161003L)
                    .tradeMoney(50000)
                    .build()));

            assertEquals(transactionRepository.count(), 1L);

            // 저장 되지 않은 tid 로 탐색
            assertNull(transactionRepository.findByTransactionId(tid + "<<undefined>>"));

            // save tid 로 탐색
            Transaction transaction = transactionRepository.findByTransactionId(tid);

            assertNotNull(transaction);
            assertTrue(transaction.getTransactionId().equals(tid)
                    && transaction.getTradeType() == TradeType.BALANCE_USE
                    && transaction.isResult() == true
                    && transaction.getAccountNumber() == 2013161003L
                    && transaction.getTradeMoney() == 50000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}