package com.deapt.oneteambackend.service;

import com.deapt.oneteambackend.model.domin.User;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**f
 * @author Deapt
 * @description 插入用户测试
 * @since 2025/5/27 19:00
 */
@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserService userService;

    //CPU密集型任务，分配核心线程数 = CPU - 1
    //IO密集型任务， 分配核心线程数可以大于 CPU 核心数量
    private ExecutorService executorService = new ThreadPoolExecutor(60, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户测试
     */
    @Test
    public void doInsertUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = getUser();
            userList.add(user);
        }
        //20 秒左右 10 万条
        userService.saveBatch(userList, 10000);

        stopWatch.stop();
        System.out.println("Time taken to insert users: " + stopWatch.getTime() + " ms");
    }

    /**
     * 并发批量插入用户测试
     * 由于并发插入会导致 MySQL 锁表，可能会导致插入失败，所以需要在 MySQL 中设置 innodb_lock_wait_timeout=1000
     */
    @Test
    public void doConcurrencyInsertUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 100000;
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = getUser();
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("ThreadName:" + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
                System.out.println("ThreadName1:" + Thread.currentThread().getName());
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        stopWatch.stop();
        System.out.println("Time taken to insert users: " + stopWatch.getTime() + " ms");
    }

    private static User getUser() {
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
        return user;
    }
}
