package com.zerobase.account_service.config;

import com.zerobase.account_service.domain.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;




import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class EmbeddedRedisTemplateTest {
    @Autowired
    private RedisTemplate<String, UserInfo> userInfoRedisTemplate;

    @Test
    public void userInfoInitTest() {
        try {
            ValueOperations<String, UserInfo> valueOperations = userInfoRedisTemplate.opsForValue();
            assertNotNull(valueOperations);
        } catch (Exception e) {
            fail();
        }
    }
}
