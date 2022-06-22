package com.zerobase.account_service.util;

import com.zerobase.account_service.domain.UserInfo;
import com.zerobase.account_service.repository.AccountRepository;
import com.google.common.collect.ConcurrentHashMultiset;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;

public class AccountServiceUtil {
    public static final long MAX_TRADE_MONEY = 99999999999L;
    public static final int USER_MAX_ACCOUNT_SIZE = 10;
    private static final ConcurrentHashMultiset<Long> accountNumberSet = ConcurrentHashMultiset.create();

    private AccountServiceUtil() {
    }

    public static long generateRandomNumber() {
        long accountNumber = 0L;

        while (true) {
            accountNumber = (long) (Math.random() * (9999999999L - 1000000000L + 1L) + 1000000000L);

            if (!accountNumberSet.contains(accountNumber)) {
                break;
            } else {
                accountNumberSet.add(accountNumber);
            }
        }
        return accountNumber;
    }

    public static void initializeUserInfos(AccountRepository accountRepository) {
        try {
            accountRepository.getValueOperations().set("nebi25", UserInfo.builder().
                    name("junmo kang")
                    .id("nebi25")
                    .registrationNumber("960503-1492678")
                    .sex('M')
                    .accountMap(new HashMap<>())
                    .createdTime(LocalDateTime.now().toString())
                    .build());

            accountRepository.getValueOperations().set("sm949", UserInfo.builder().
                    name("yang sun mu")
                    .id("sm949")
                    .registrationNumber("740315-1193483")
                    .sex('F')
                    .accountMap(new HashMap<>())
                    .createdTime(LocalDateTime.of(2012, Month.AUGUST, 30, 18, 35).toString())
                    .build());

            accountRepository.getValueOperations().set("sainlove", UserInfo.builder().
                    name("양세인")
                    .id("sainlove")
                    .registrationNumber("020701-2192818")
                    .sex('F')
                    .accountMap(new HashMap<>())
                    .createdTime(LocalDateTime.of(2019, Month.JANUARY, 18, 13, 55).toString())
                    .build());

            accountRepository.getValueOperations().set("danna54494", UserInfo.builder().
                    name("김단나")
                    .id("danna54494")
                    .registrationNumber("990328-1892812")
                    .sex('M')
                    .accountMap(new HashMap<>())
                    .createdTime(LocalDateTime.now().toString())
                    .build());

            accountRepository.getValueOperations().set("moo342", UserInfo.builder().
                    name("염무성")
                    .id("moo342")
                    .registrationNumber("840701-2396815")
                    .sex('M')
                    .accountMap(new HashMap<>())
                    .createdTime(LocalDateTime.of(2009, Month.APRIL, 05, 05, 46).toString())
                    .build());

            accountRepository.getValueOperations().set("tjdtnwld", UserInfo.builder().
                    name("장성수")
                    .id("tjdtnwld")
                    .registrationNumber("0601228-2532745")
                    .sex('M')
                    .accountMap(new HashMap<>())
                    .createdTime(LocalDateTime.of(2021, Month.SEPTEMBER, 11, 12, 46).toString())
                    .build());

            accountRepository.getValueOperations().set("ndmsndnfds", UserInfo.builder().
                    name("Han yeung ho")
                    .id("ndmsndnfds")
                    .registrationNumber("670319-1152365")
                    .sex('M')
                    .accountMap(new HashMap<>())
                    .createdTime(LocalDateTime.of(2022, Month.JANUARY, 05, 15, 12).toString())
                    .build());

            accountRepository.getValueOperations().set("answjddus9543", UserInfo.builder().
                    name("문정연")
                    .id("answjddus9543")
                    .registrationNumber("950403-1298432")
                    .sex('F')
                    .accountMap(new HashMap<>())
                    .createdTime(LocalDateTime.of(2020, Month.MAY, 16, 10, 03).toString())
                    .build());

            accountRepository.getValueOperations().set("fmfmfmkorea59", UserInfo.builder().
                    name("장판수")
                    .id("fmfmfmkorea59")
                    .registrationNumber("980915-1393456")
                    .sex('M')
                    .accountMap(new HashMap<>())
                    .createdTime(LocalDateTime.of(2015, Month.FEBRUARY, 25, 18, 30).toString())
                    .build());

            accountRepository.getValueOperations().set("narajang11", UserInfo.builder().
                    name("장나라")
                    .id("narajang11")
                    .registrationNumber("900915-1393456")
                    .sex('F')
                    .accountMap(new HashMap<>())
                    .createdTime(LocalDateTime.of(2008, Month.DECEMBER, 29, 12, 24).toString())
                    .build());
        } catch (Exception e) {
            throw e;
        }
    }
}
