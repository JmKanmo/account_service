package com.example.account_service.util;

import java.util.stream.IntStream;

public class AccountServiceUtil {
    public static final int USER_MAX_ACCOUNT_SIZE = 100000;

    public static long generateRandomNumber() {
        return (long) (Math.random() * (9999999999L - 1000000000L + 1L) + 1000000000L);
    }
}
