package com.zerobase.account_service.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.util.Map;


@Data
@Builder
@RedisHash("userinfo")
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {
    @Id
    private String id;

    private String name;

    private char sex;

    private Map<Long,Account> accountMap;

    private String registrationNumber;

    private String createdTime;
}
