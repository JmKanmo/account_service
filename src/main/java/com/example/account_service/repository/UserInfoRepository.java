package com.example.account_service.repository;

import com.example.account_service.domain.Account;
import com.example.account_service.domain.UserInfo;
import com.example.account_service.util.AccountServiceUtil;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

@Repository
public class UserInfoRepository {
    private final RedisTemplate<String, UserInfo> userInfoRedisTemplate;

    private ValueOperations<String, UserInfo> valueOperations;

    public UserInfoRepository(RedisTemplate<String, UserInfo> userInfoRedisTemplate) {
        this.userInfoRedisTemplate = userInfoRedisTemplate;
        this.valueOperations = userInfoRedisTemplate.opsForValue();
    }

    public Optional<Account> createAccount(String userId, long money) {
        UserInfo userInfo = valueOperations.get(userId);

        if (userInfo == null) {
            return Optional.empty();
        }

        Map<Long, Account> accountMap = userInfo.getAccountMap();
        Long accountNumber;

        if (accountMap.size() >= AccountServiceUtil.USER_MAX_ACCOUNT_SIZE) {
            return Optional.empty();
        }

        while (true) {
            accountNumber = AccountServiceUtil.generateRandomNumber();
            if (!accountMap.containsKey(accountNumber)) {
                break;
            }
        }

        Account account = Account.builder()
                .number(accountNumber)
                .money(money)
                .localDateTime(LocalDateTime.now())
                .build();

        accountMap.put(accountNumber, account);

        try {
            userInfoRedisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) {
                    try {
                        operations.multi();
                        valueOperations.set(userId, userInfo);
                        return operations.exec();
                    } catch (Exception e) {
                        operations.discard();
                        throw e;
                    }
                }
            });
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.of(account);
    }

    public Optional<Long> discardAccount(String userId, long accountNumber) {
        UserInfo userInfo = valueOperations.get(userId);

        if (userInfo == null) {
            return Optional.empty();
        }

        Map<Long, Account> accountMap = userInfo.getAccountMap();

        if (accountMap.containsKey(accountNumber)) {
            try {
                userInfoRedisTemplate.execute(new SessionCallback<Object>() {
                    @Override
                    public <K, V> Object execute(RedisOperations<K, V> operations) {
                        try {
                            operations.multi();
                            accountMap.remove(accountNumber);
                            valueOperations.set(userId, userInfo);
                            return operations.exec();
                        } catch (Exception e) {
                            operations.discard();
                            throw e;
                        }
                    }
                });
            } catch (Exception e) {
                return Optional.empty();
            }
            return Optional.of(accountNumber);
        }
        return Optional.empty();
    }

    public Optional<List<Account>> getAccountList(String userId) {
        UserInfo userInfo = valueOperations.get(userId);

        if (userInfo == null) {
            return Optional.empty();
        }

        return Optional.of(new ArrayList<>(userInfo.getAccountMap().values()));
    }

    public Optional<UserInfo> getUserInfo(String userId) {
        return Optional.ofNullable(valueOperations.get(userId));
    }

    public ValueOperations<String, UserInfo> getValueOperations() {
        return valueOperations;
    }

    @PostConstruct
    public void initialize() {
        // 사용자 정보 초기화
        valueOperations.set("nebi25", UserInfo.builder().
                name("junmo kang")
                .id("nebi25")
                .registrationNumber("960503-1492678")
                .sex('M')
                .accountMap(new HashMap<>())
                .createdTime(LocalDateTime.now())
                .build());

        valueOperations.set("kanna94", UserInfo.builder().
                name("yang sun mu")
                .id("sm949")
                .registrationNumber("740315-1193483")
                .sex('F')
                .accountMap(new HashMap<>())
                .createdTime(LocalDateTime.of(2012, Month.AUGUST, 30, 18, 35))
                .build());

        valueOperations.set("sainlove", UserInfo.builder().
                name("양세인")
                .id("sainlove")
                .registrationNumber("020701-2192818")
                .sex('F')
                .accountMap(new HashMap<>())
                .createdTime(LocalDateTime.of(2019, Month.JANUARY, 18, 13, 55))
                .build());

        valueOperations.set("danna54494", UserInfo.builder().
                name("김단나")
                .id("danna54494")
                .registrationNumber("990328-1892812")
                .sex('M')
                .accountMap(new HashMap<>())
                .createdTime(LocalDateTime.now())
                .build());

        valueOperations.set("moo342", UserInfo.builder().
                name("염무성")
                .id("moo342")
                .registrationNumber("840701-2396815")
                .sex('M')
                .accountMap(new HashMap<>())
                .createdTime(LocalDateTime.of(2009, Month.APRIL, 05, 05, 46))
                .build());

        valueOperations.set("tjdtnwld", UserInfo.builder().
                name("장성수")
                .id("tjdtnwld")
                .registrationNumber("0601228-2532745")
                .sex('M')
                .accountMap(new HashMap<>())
                .createdTime(LocalDateTime.of(2021, Month.SEPTEMBER, 11, 12, 46))
                .build());

        valueOperations.set("ndmsndnfds", UserInfo.builder().
                name("Han yeung ho")
                .id("ndmsndnfds")
                .registrationNumber("670319-1152365")
                .sex('M')
                .accountMap(new HashMap<>())
                .createdTime(LocalDateTime.of(2022, Month.JANUARY, 05, 15, 12))
                .build());

        valueOperations.set("answjddus9543", UserInfo.builder().
                name("문정연")
                .id("answjddus9543")
                .registrationNumber("950403-1298432")
                .sex('F')
                .accountMap(new HashMap<>())
                .createdTime(LocalDateTime.of(2020, Month.MAY, 16, 10, 03))
                .build());

        valueOperations.set("fmfmfmkorea59", UserInfo.builder().
                name("장판수")
                .id("fmfmfmkorea59")
                .registrationNumber("980915-1393456")
                .sex('M')
                .accountMap(new HashMap<>())
                .createdTime(LocalDateTime.of(2015, Month.FEBRUARY, 25, 18, 30))
                .build());

        valueOperations.set("narajang11", UserInfo.builder().
                name("장나라")
                .id("narajang11")
                .registrationNumber("900915-1393456")
                .sex('F')
                .accountMap(new HashMap<>())
                .createdTime(LocalDateTime.of(2008, Month.DECEMBER, 29, 12, 24))
                .build());
    }
}
