package com.deapt.oneteambackend.service;

import com.deapt.oneteambackend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Acer
 * @description 测试
 * @since 2025/4/22 10:20
 */

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;
    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("deapt");
        user.setUserAccount("123");
        user.setAvatarUrl("https://avatars.githubusercontent.com/u/120431493?s=48&v=4");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("123");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }
}