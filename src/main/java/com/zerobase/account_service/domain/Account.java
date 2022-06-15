package com.zerobase.account_service.domain;


import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class Account implements Serializable {
    private final long number;

    private final String holder;

    private long money;

    private final String createdTime;

    private boolean activate;
}
