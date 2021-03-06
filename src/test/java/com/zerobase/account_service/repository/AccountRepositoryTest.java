package com.zerobase.account_service.repository;

import com.zerobase.account_service.domain.Account;
import com.zerobase.account_service.domain.UserInfo;
import com.zerobase.account_service.dto.AccountResponse;
import com.zerobase.account_service.exception.FailMessage;
import com.zerobase.account_service.util.AccountServiceUtil;
import com.zerobase.account_service.exception.TradeFailException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    public void clearCache() {
        Cursor<?> cursor = accountRepository.getUserInfoRedisTemplate().scan(ScanOptions.scanOptions().count(30).build());

        while (cursor.hasNext()) {
            String key = (String) cursor.next();
            UserInfo userInfo = accountRepository.getValueOperations().get(key);
            if (userInfo != null) {
                userInfo.getAccountMap().clear();
                accountRepository.getValueOperations().set(key, userInfo);
            }
        }
    }

    @Test
    @Order(1)
    public void initTest() {
        assertNotNull(accountRepository);
        assertNotNull(accountRepository.getValueOperations().get("nebi25"));
    }

    @Test
    @Order(2)
    public void getUserInfoTest() {
        assertEquals(accountRepository.getUserInfo("nebi25").isPresent(), true);
    }

    @Test
    @Order(3)
    public void createAccountTest() {
        try {
            assertThrows(TradeFailException.class, () -> accountRepository.createAccount(UUID.randomUUID().toString(), 100000L));
            int accountSize = accountRepository.getUserInfo("nebi25").get().getAccountMap().size();
            assertNotNull(accountRepository.createAccount("nebi25", 100000L));
            assertEquals(accountRepository.getUserInfo("nebi25").get().getAccountMap().size(), accountSize + 1);

            for (int i = 1; i < AccountServiceUtil.USER_MAX_ACCOUNT_SIZE; i++) {
                accountRepository.createAccount("nebi25", 100000L);
            }
            assertThrows(TradeFailException.class, () -> accountRepository.createAccount("nebi25", 500000L), FailMessage.ALREADY_MAX_ACCOUNT.getName());

            assertThrows(TradeFailException.class, () -> accountRepository.createAccount("akxk25", 0L));
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    public void discardAccountTest() {
        try {
            // ????????? ????????? ?????? ??????
            long accountNumber = accountRepository.createAccount("nebi25", 0L).getAccountNumber();

            long finalAccountNumber = accountNumber;
            assertEquals(accountRepository.discardAccount("nebi25", accountNumber).getAccountNumber() == accountNumber, true);

            // ?????? ???????????? ??????
            assertFalse(accountRepository.getUserInfo("nebi25").get().getAccountMap().get(accountNumber).isActivate());

            // ????????? ?????? ?????? ??????
            long finalAccountNumber1 = finalAccountNumber;
            assertThrows(TradeFailException.class, () -> accountRepository.discardAccount("nebi25", finalAccountNumber1), FailMessage.DEACTIVATE_ACCOUNT.getName());


            // ????????? ?????? ??????
            accountNumber = accountRepository.createAccount("sm949", 0L).getAccountNumber();

            // ?????? ?????? id
            finalAccountNumber = accountNumber;
            long finalAccountNumber2 = finalAccountNumber;
            assertThrows(TradeFailException.class, () -> accountRepository.discardAccount("km95", finalAccountNumber2), FailMessage.EMPTY_USER_INFO.getName());

            accountNumber = accountRepository.createAccount("sainlove", 10000L).getAccountNumber();
            // ????????? ???????????? ??????
            long finalAccountNumber3 = accountNumber;
            assertThrows(TradeFailException.class, () -> accountRepository.discardAccount("sainlove", finalAccountNumber3), FailMessage.ACCOUNT_LEFT_MONEY.getName());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(5)
    public void getAccountListTest() {
        try {
            for (int i = 0; i < AccountServiceUtil.USER_MAX_ACCOUNT_SIZE; i++)
                accountRepository.createAccount("nebi25", 1000L);

            assertThrows(TradeFailException.class, () -> accountRepository.getAccountInfos("nebisss25"), FailMessage.EMPTY_USER_INFO.getName());

            List<Account> accounts = accountRepository.getAccountInfos("nebi25");
            assertEquals(accounts != null && accounts.size() == AccountServiceUtil.USER_MAX_ACCOUNT_SIZE, true);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(6)
    public void createMultiAccountTest() {
        try {
            new Thread(() -> {
                for (int i = 0; i < AccountServiceUtil.USER_MAX_ACCOUNT_SIZE; i++)
                    accountRepository.createAccount("sainlove", 1000L);
            }).start();

            new Thread(() -> {
                for (int i = 0; i < AccountServiceUtil.USER_MAX_ACCOUNT_SIZE; i++)
                    accountRepository.createAccount("answjddus9543", 10000000L);
            }).start();

            new Thread(() -> {
                for (int i = 0; i < AccountServiceUtil.USER_MAX_ACCOUNT_SIZE; i++)
                    accountRepository.createAccount("fmfmfmkorea59", 10000L);
            }).start();

            new Thread(() -> {
                for (int i = 0; i < AccountServiceUtil.USER_MAX_ACCOUNT_SIZE; i++)
                    accountRepository.createAccount("narajang11", 10000L);
            }).start();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                fail();
                throw new RuntimeException(e);
            }

            assertEquals(accountRepository.getUserInfo("sainlove").get().getAccountMap().size(), AccountServiceUtil.USER_MAX_ACCOUNT_SIZE);
            assertEquals(accountRepository.getUserInfo("answjddus9543").get().getAccountMap().size(), AccountServiceUtil.USER_MAX_ACCOUNT_SIZE);
            assertEquals(accountRepository.getUserInfo("fmfmfmkorea59").get().getAccountMap().size(), AccountServiceUtil.USER_MAX_ACCOUNT_SIZE);
            assertEquals(accountRepository.getUserInfo("narajang11").get().getAccountMap().size(), AccountServiceUtil.USER_MAX_ACCOUNT_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(7)
    public void useMoneyTest() {
        try {
            AccountResponse accountResponse = accountRepository.createAccount("nebi25", 50000);
            Account account = accountRepository.useMoney("nebi25", accountResponse.getAccountNumber(), 50000);
            assertEquals(account.getMoney(), 0);
            assertThrows(TradeFailException.class, () -> accountRepository.useMoney("nebi25", accountResponse.getAccountNumber(), -255L), "?????? ????????? ???????????? ??? ??? ????????????.");
            assertThrows(TradeFailException.class, () -> accountRepository.useMoney("nebi25", accountResponse.getAccountNumber(), AccountServiceUtil.MAX_TRADE_MONEY + 100L), "?????? ????????? ?????? ?????? ??????: "
                    + AccountServiceUtil.MAX_TRADE_MONEY + "??? ?????? ??? ????????????.");
            assertThrows(TradeFailException.class, () -> accountRepository.useMoney("nebidsds25", accountResponse.getAccountNumber(), 50000), FailMessage.EMPTY_USER_INFO.getName());
            assertThrows(TradeFailException.class, () -> accountRepository.useMoney("nebi25", accountResponse.getAccountNumber(), 100000), "?????? ????????? ???????????? ?????????.");

            accountRepository.discardAccount("nebi25", account.getNumber());
            assertThrows(TradeFailException.class, () -> accountRepository.useMoney("nebi25", accountResponse.getAccountNumber(), 100000), FailMessage.DEACTIVATE_ACCOUNT.getName());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(8)
    public void cancelUsageMoneyTest() {
        try {
            AccountResponse accountResponse = accountRepository.createAccount("nebi25", 50000);
            accountRepository.useMoney("nebi25", accountResponse.getAccountNumber(), 50000);
            Account account = accountRepository.cancelUsageMoney("nebi25", accountResponse.getAccountNumber(), 50000);
            assertEquals(account.getMoney(), 50000);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}