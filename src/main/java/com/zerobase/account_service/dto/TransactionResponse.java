package com.zerobase.account_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private final long accountNumber;
    private final boolean transactionResult;
    private final String transactionId;
    private final long tradeMoney;
    private final String tradeDateTime;
}
