package com.deapt.oneteambackend.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Deapt
 * @description Redisson本地配置
 * @since 2025/6/10 17:16
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String host;
    private String port;
    @Bean
    public RedissonClient redissonClient() {
        //Create config object.
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(redisAddress).setDatabase(2);

        //2.Create Redisson instance
        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }
}
