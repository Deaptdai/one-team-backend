package com.deapt.oneteambackend.service;

import com.deapt.oneteambackend.model.domin.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Deapt
 * @description 测试Mybatis-Plus
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
        user.setUserAccount("deapt");
        user.setAvatarUrl("https://avatars.githubusercontent.com/u/120431493?s=48&v=4");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("123456");
        user.setEmail("123456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "dea";
        String userPassword = "123456";
        String checkPassword = "123456";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,result);
    }
}