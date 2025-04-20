package com.deapt.oneteambackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.deapt.oneteambackend.mapper")
public class OneTeamBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(OneTeamBackendApplication.class, args);
    }

}
