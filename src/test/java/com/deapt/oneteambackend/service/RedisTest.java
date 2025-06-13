package com.deapt.oneteambackend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @author Deapt
 * @description Redis操作测试
 * @since 2025/6/7 10:34
 */

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("nameString", "deapt");
        valueOperations.set("nameInt", 1);
        valueOperations.set("nameDouble", 2.0);

        Object name = valueOperations.get("nameString");
        Assertions.assertEquals("deapt", name);

        redisTemplate.delete("nameString");
        redisTemplate.delete("nameInt");
        redisTemplate.delete("nameDouble");
    }
}
