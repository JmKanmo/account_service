package com.zerobase.account_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse{
    private final String userId;
    private final long accountNumber;
    private final String dateTime;
}
