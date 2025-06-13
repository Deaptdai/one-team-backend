package com.deapt.oneteambackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.deapt.oneteambackend.mapper")
@EnableScheduling
public class OneTeamBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(OneTeamBackendApplication.class, args);
    }

}
