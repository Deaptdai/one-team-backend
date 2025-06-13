package com.deapt.oneteambackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deapt.oneteambackend.model.domin.User;
import com.deapt.oneteambackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Deapt
 * @description 预热缓存
 * @since 2025/6/10 9:54
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    //重点用户
    private List<Long> mainUser = Arrays.asList(1L, 2L, 3L);
    //每天执行,预热推荐用户
    @Scheduled(cron = "59 17 21 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("deapt:precachejob:docache:lock");
        try {
            //只能有一个线程获取到锁
            if (lock.tryLock(0,-1,TimeUnit.MILLISECONDS)) {
                System.out.println("getLock"+Thread.currentThread().getId());
                for (Long userId : mainUser) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
                    String redisKey = String.format("deapt:user:recommend:%s",userId);
                    try {
                        redisTemplate.opsForValue().set(redisKey, userPage,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("缓存写入失败: {}", e.getMessage());
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
        }finally {
            //只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unLock"+Thread.currentThread().getId());
                lock.unlock();
            }
        }

    }
}
