package com.deapt.oneteambackend.once;

import com.deapt.oneteambackend.mapper.UserMapper;
import com.deapt.oneteambackend.model.domin.User;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Deapt
 * @description 插入用户数据的类
 * @since 2025/5/27 15:49
 */
//@Component
public class insertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户数据
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    public void doInsertUser(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 100;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("fakeUser");
            user.setUserAccount("fakeUser");
            user.setAvatarUrl("https://tse1-mm.cn.bing.net/th/id/OIP-C.UBjdZXtfGC3VrnzyT8wTcQHaHa?w=193&h=193&c=7&r=0&o=7&cb=iwp2&dpr=1.3&pid=1.7&rm=3");
            user.setGender(0);
            user.setUserPassword("123456");
            user.setPhone("123");
            user.setEmail("432@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setUserCode("123456");
            user.setTags("[]");
            user.setUserProfile("我是一个假用户");

            userMapper.insert(user);
        }

        stopWatch.stop();
        System.out.println("Time taken to insert users: " + stopWatch.getTime() + " ms");
    }
}
