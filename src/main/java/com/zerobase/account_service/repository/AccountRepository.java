package com.zerobase.account_service.repository;

import com.zerobase.account_service.domain.Account;
import com.zerobase.account_service.domain.UserInfo;
import com.zerobase.account_service.dto.AccountResponse;
import com.zerobase.account_service.exception.FailMessage;
import com.zerobase.account_service.util.AccountServiceUtil;
import com.zerobase.account_service.exception.TradeFailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@Slf4j
public class AccountRepository {
    private final RedisTemplate<String, UserInfo> userInfoRedisTemplate;

    private final ValueOperations<String, UserInfo> valueOperations;

    public AccountRepository(RedisTemplate<String, UserInfo> userInfoRedisTemplate) {
        this.userInfoRedisTemplate = userInfoRedisTemplate;
        this.valueOperations = userInfoRedisTemplate.opsForValue();
    }

    public AccountResponse createAccount(String userId, long tradeMoney) {
        try {
            UserInfo userInfo = valueOperations.get(userId);

            if (userInfo == null) {
                throw new TradeFailException(FailMessage.EMPTY_USER_INFO.getName());
            }

            Map<Long, Account> accountMap = userInfo.getAccountMap();

            if (accountMap.size() >= AccountServiceUtil.USER_MAX_ACCOUNT_SIZE) {
                throw new TradeFailException(FailMessage.ALREADY_MAX_ACCOUNT.getName());
            } else if (tradeMoney < 0) {
                throw new TradeFailException(FailMessage.TRADE_MONEY_NEGATIVE.getName());
            } else if (tradeMoney > AccountServiceUtil.MAX_TRADE_MONEY) {
                throw new TradeFailException(FailMessage.TRADE_MONEY_NOT_BIGGER_THAN.getName());
            }

            Long accountNumber = AccountServiceUtil.generateRandomNumber();

            Account account = Account.builder()
                    .number(accountNumber)
                    .holder(userId)
                    .money(tradeMoney)
                    .activate(true)
                    .createdTime(LocalDateTime.now().toString())
                    .build();

            accountMap.put(accountNumber, account);

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

            return AccountResponse.builder()
                    .userId(userId)
                    .accountNumber(accountNumber)
                    .dateTime(account.getCreatedTime())
                    .build();
        } catch (Exception e) {
            throw new TradeFailException(FailMessage.ERROR_OCCUR.getName() + e.getMessage());
        }
    }

    public AccountResponse discardAccount(String userId, long accountNumber) {
        try {
            UserInfo userInfo = valueOperations.get(userId);

            if (userInfo == null) {
                throw new TradeFailException(FailMessage.EMPTY_USER_INFO.getName());
            }

            Map<Long, Account> accountMap = userInfo.getAccountMap();

            if (accountMap.containsKey(accountNumber)) {
                Account account = accountMap.get(accountNumber);

                if (!userId.equals(account.getHolder())) {
                    throw new TradeFailException(FailMessage.MISMATCH_ACCOUNT_USER.getName());
                } else if (account.getMoney() > 0) {
                    throw new TradeFailException(FailMessage.ACCOUNT_LEFT_MONEY.getName());
                } else if (!account.isActivate()) {
                    throw new TradeFailException(FailMessage.DEACTIVATE_ACCOUNT.getName());
                } else {
                    userInfoRedisTemplate.execute(new SessionCallback<Object>() {
                        @Override
                        public <K, V> Object execute(RedisOperations<K, V> operations) {
                            try {
                                operations.multi();
                                account.setActivate(false);
                                valueOperations.set(userId, userInfo);
                                return operations.exec();
                            } catch (Exception e) {
                                operations.discard();
                                throw e;
                            }
                        }
                    });
                }
                return AccountResponse.builder()
                        .userId(userId)
                        .accountNumber(accountNumber)
                        .dateTime(LocalDateTime.now().toString())
                        .build();
            } else {
                throw new TradeFailException(FailMessage.NO_COINCIDE_ACCOUNT_INFO.getName());
            }
        } catch (Exception e) {
            throw new TradeFailException(FailMessage.ERROR_OCCUR.getName() + e.getMessage());
        }
    }

    public List<Account> getAccountInfos(String userId) {
        UserInfo userInfo = valueOperations.get(userId);

        if (userInfo == null) {
            throw new TradeFailException(FailMessage.EMPTY_USER_INFO.getName());
        }

        return new ArrayList<>(userInfo.getAccountMap().values());
    }

    public Account useMoney(String userId, long accountNumber, long tradeMoney) {
        try {
            UserInfo userInfo = valueOperations.get(userId);

            if (userInfo == null) {
                throw new TradeFailException(FailMessage.EMPTY_USER_INFO.getName());
            }

            Map<Long, Account> accountMap = userInfo.getAccountMap();

            if (accountMap.containsKey(accountNumber)) {
                Account account = accountMap.get(accountNumber);

                if (!account.getHolder().equals(userId)) {
                    throw new TradeFailException(FailMessage.MISMATCH_ACCOUNT_USER.getName());
                } else if (!account.isActivate()) {
                    throw new TradeFailException(FailMessage.DEACTIVATE_ACCOUNT.getName());
                } else if (account.getMoney() < tradeMoney) {
                    throw new TradeFailException("거래 금액이 잔액보다 큽니다.");
                } else if (tradeMoney < 0 || tradeMoney > AccountServiceUtil.MAX_TRADE_MONEY) {
                    if (tradeMoney < 0) {
                        throw new TradeFailException("거래 금액은 음수값이 올 수 없습니다.");
                    } else if (tradeMoney > AccountServiceUtil.MAX_TRADE_MONEY) {
                        throw new TradeFailException("거래 금액이 최대 거래 금액: "
                                + AccountServiceUtil.MAX_TRADE_MONEY + "을 넘을 수 없습니다.");
                    }
                }
                userInfoRedisTemplate.execute(new SessionCallback<Object>() {
                    @Override
                    public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                        try {
                            operations.multi();
                            long balance = account.getMoney();
                            account.setMoney(balance - tradeMoney);
                            valueOperations.set(userId, userInfo);
                            return operations.exec();
                        } catch (Exception e) {
                            operations.discard();
                            throw e;
                        }
                    }
                });
                return valueOperations.get(userId).getAccountMap().get(accountNumber);
            } else {
                throw new TradeFailException(FailMessage.NO_COINCIDE_ACCOUNT_INFO.getName());
            }
        } catch (Exception e) {
            throw new TradeFailException(FailMessage.ERROR_OCCUR.getName() + e.getMessage());
        }
    }

    public Account cancelUsageMoney(String userId, long accountNumber, long tradeMoney) {
        try {
            UserInfo userInfo = valueOperations.get(userId);

            if (userInfo == null) {
                throw new TradeFailException(FailMessage.EMPTY_USER_INFO.getName());
            }

            Map<Long, Account> accountMap = userInfo.getAccountMap();

            if (accountMap.containsKey(accountNumber)) {
                Account account = accountMap.get(accountNumber);

                if (!account.getHolder().equals(userId)) {
                    throw new TradeFailException(FailMessage.MISMATCH_ACCOUNT_USER.getName());
                } else if (!account.isActivate()) {
                    throw new TradeFailException(FailMessage.DEACTIVATE_ACCOUNT.getName());
                }
                userInfoRedisTemplate.execute(new SessionCallback<Object>() {
                    @Override
                    public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                        try {
                            operations.multi();
                            long balance = account.getMoney();
                            account.setMoney(balance + tradeMoney);
                            valueOperations.set(userId, userInfo);
                            return operations.exec();
                        } catch (Exception e) {
                            operations.discard();
                            throw e;
                        }
                    }
                });
                return valueOperations.get(userId).getAccountMap().get(accountNumber);
            } else {
                throw new TradeFailException(FailMessage.NO_COINCIDE_ACCOUNT_INFO.getName());
            }
        } catch (Exception e) {
            throw new TradeFailException(FailMessage.ERROR_OCCUR.getName() + e.getMessage());
        }
    }

    public RedisTemplate<String, UserInfo> getUserInfoRedisTemplate() {
        return userInfoRedisTemplate;
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
        try {
            AccountServiceUtil.initializeUserInfos(this);
        } catch (Exception e) {
            log.info(FailMessage.INITIALIZE_FAIL.getName(), e);
        }
    }
}
