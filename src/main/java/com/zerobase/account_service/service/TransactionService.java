package com.zerobase.account_service.service;

import com.zerobase.account_service.domain.TradeType;
import com.zerobase.account_service.domain.Transaction;
import com.zerobase.account_service.domain.dto.TransactionResponse;
import com.zerobase.account_service.repository.TransactionRepository;
import com.zerobase.account_service.repository.AccountRepository;
import com.zerobase.account_service.util.AccountServiceUtil;
import com.zerobase.account_service.util.TradeFailException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;


    public TransactionResponse useMoneyTrade(String userId, long accountNumber, long tradeMoney) {
        try {
            accountRepository.useMoney(userId, accountNumber, tradeMoney);

            String tid = UUID.randomUUID().toString();

            transactionRepository.save(Transaction.builder()
                    .transactionId(tid)
                    .tradeType(TradeType.BALANCE_USE)
                    .result(true)
                    .accountNumber(accountNumber)
                    .tradeMoney(tradeMoney)
                    .build());

            return TransactionResponse.builder()
                    .accountNumber(accountNumber)
                    .transactionResult(true)
                    .transactionId(tid)
                    .tradeMoney(tradeMoney)
                    .tradeDateTime(LocalDateTime.now().toString())
                    .build();
        } catch (Exception e) {
            String failedTid = UUID.randomUUID().toString();
            transactionRepository.save(Transaction.builder()
                    .transactionId(failedTid)
                    .tradeType(TradeType.BALANCE_USE)
                    .result(false)
                    .accountNumber(accountNumber)
                    .tradeMoney(tradeMoney)
                    .build());

            throw new TradeFailException("[실패 트랜잭션 ID:" + failedTid + "], " + e.getMessage());
        }
    }

    public TransactionResponse cancelMoneyTrade(String transactionId, String userId, long accountNumber, long tradeMoney) {
        try {
            Transaction transaction = transactionRepository.findByTransactionId(transactionId);

            if (transaction == null) {
                throw new TradeFailException("트랜잭션 id에 해당하는 거래 내역이 없습니다.");
            } else if (transaction.isResult() == false) {
                throw new TradeFailException("해당 트랜잭션은 실패한 거래입니다.");
            } else if (transaction.getTradeType() == TradeType.BALANCE_USE_CANCEL) {
                throw new TradeFailException("동일한 거래 취소 유형의 트랜잭션은 중복 취소할 수 없습니다.");
            } else if (transaction.getAccountNumber() != accountNumber) {
                throw new TradeFailException("트랜잭션이 해당 계좌의 거래가 아닙니다.");
            } else if (transaction.getTradeMoney() != tradeMoney) {
                throw new TradeFailException("원거래 금액과 취소 금액이 다릅니다.");
            }

            String newTid = UUID.randomUUID().toString();

            accountRepository.cancelUsageMoney(userId, accountNumber, tradeMoney);

            transactionRepository.save(Transaction.builder()
                    .transactionId(newTid)
                    .tradeType(TradeType.BALANCE_USE_CANCEL)
                    .result(true)
                    .accountNumber(accountNumber)
                    .tradeMoney(tradeMoney)
                    .build());

            return TransactionResponse.builder()
                    .accountNumber(accountNumber)
                    .transactionResult(true)
                    .transactionId(newTid)
                    .tradeMoney(tradeMoney)
                    .tradeDateTime(LocalDateTime.now().toString())
                    .build();
        } catch (Exception e) {
            String failedTid = UUID.randomUUID().toString();
            transactionRepository.save(Transaction.builder()
                    .transactionId(failedTid)
                    .tradeType(TradeType.BALANCE_USE_CANCEL)
                    .result(false)
                    .accountNumber(accountNumber)
                    .tradeMoney(tradeMoney)
                    .build());

            throw new TradeFailException("[실패 트랜잭션 ID:" + failedTid + "], " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Transaction getTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId);

        if (transaction == null) {
            throw new TradeFailException("트랜잭션 id에 대한 거래 내역이 없습니다.");
        }
        return transaction;
    }
}
