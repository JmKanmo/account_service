package com.example.account_service.repository;

import com.example.account_service.domain.Account;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserInfoRepositoryTest {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Test
    @Order(1)
    public void initTest() {
        assertNotNull(userInfoRepository);
        assertNotNull(userInfoRepository.getValueOperations().get("nebi25"));
    }

    @Test
    @Order(2)
    public void getUserInfoTest() {
        assertEquals(userInfoRepository.getUserInfo("nebi25").isPresent(), true);
    }

    @Test
    @Order(3)
    public void createAccountTest() {
        try {
            int accountSize = userInfoRepository.getUserInfo("nebi25").get().getAccountMap().size();
            assertNotNull(userInfoRepository.createAccount("nebi25", 100000L));
            assertEquals(userInfoRepository.createAccount(UUID.randomUUID().toString(), 100000L).isPresent(), false);
            assertEquals(userInfoRepository.getUserInfo("nebi25").get().getAccountMap().size(), accountSize + 1);
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    public void discardAccountTest() {
        try {
            Account account = userInfoRepository.createAccount("nebi25", 100000L).get();
            long accountNumber = account.getNumber();
            assertEquals(userInfoRepository.discardAccount("nebi25", accountNumber).isPresent(), true);
            assertNull(userInfoRepository.getUserInfo("nebi25").get().getAccountMap().get(account.getNumber()));

            // 등록 안된 id
            assertEquals(userInfoRepository.discardAccount("akxk25", accountNumber).isPresent(), false);

            // 등록 안된 계좌번호
            account = userInfoRepository.createAccount("nebi25", 100000L).get();
            accountNumber = account.getNumber() + 1;
            assertEquals(userInfoRepository.discardAccount("nebi25", accountNumber).isPresent(), false);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(5)
    public void getAccountListTest() {
        try {
            int accountListSize = userInfoRepository.getAccountList("nebi25").get().size();
            for (int i = 0; i < 1000; i++)
                userInfoRepository.createAccount("nebi25", 1000L);

            assertEquals(userInfoRepository.getUserInfo("nebisss25").isPresent(), false);

            List<Account> accounts = userInfoRepository.getAccountList("nebi25").get();
            assertEquals(accounts != null && accounts.size() == (accountListSize + 1000), true);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(6)
    public void createMultiAccountTest() {
        new Thread(() -> {
            for (int i = 0; i < 500; i++)
                userInfoRepository.createAccount("sainlove", 1000L);
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 500; i++)
                userInfoRepository.createAccount("answjddus9543", 10000000L);
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 500; i++)
                userInfoRepository.createAccount("fmfmfmkorea59", 10000L);
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 500; i++)
                userInfoRepository.createAccount("narajang11", 10000L);
        }).start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            fail();
            throw new RuntimeException(e);
        }

        assertEquals(userInfoRepository.getUserInfo("sainlove").get().getAccountMap().size(), 500);
        assertEquals(userInfoRepository.getUserInfo("answjddus9543").get().getAccountMap().size(), 500);
        assertEquals(userInfoRepository.getUserInfo("fmfmfmkorea59").get().getAccountMap().size(), 500);
        assertEquals(userInfoRepository.getUserInfo("narajang11").get().getAccountMap().size(), 500);
    }
}