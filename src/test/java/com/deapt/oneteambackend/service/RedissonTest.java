package com.deapt.oneteambackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deapt.oneteambackend.config.RedissonConfig;
import com.deapt.oneteambackend.model.domin.User;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Deapt
 * @description Redisson测试类
 * @since 2025/6/10 17:29
 */
@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;
    @Test
    void test(){
        //list使用
        //java中：数据存储在JVM本地
        List<String> list = new ArrayList<>();
        list.add("deapt");
        System.out.println("list:"+ list.get(0));
        list.remove(0);

        //redisson中：数据存储在redis的内存中
        RList<String> rList = redissonClient.getList("testList");
        rList.add("deapt");
        System.out.println("rList:"+ rList.get(0));
        rList.remove(0);
    }

    @Test
    void testWatchDog(){
        RLock lock = redissonClient.getLock("deapt:precachejob:docache:lock");
        try {
            //只能有一个线程获取到锁
            if (lock.tryLock(0,-1, TimeUnit.MILLISECONDS)) {
                //实际要执行的代码，这里使用sleep模拟
                Thread.sleep(300000);
                System.out.println("getLock"+Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }finally {
            //只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unLock"+Thread.currentThread().getId());
                lock.unlock();
            }
        }

    }
}
