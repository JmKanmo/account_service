package com.example.account_service.domain;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class Account implements Serializable {
    private long number;

    private long money;

    private LocalDateTime localDateTime;
}
