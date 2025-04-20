package com.deapt.oneteambackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")  // 确保加载 application-dev.yml
class OneTeamBackendApplicationTests {

    @Test
    public void testSelect() {

    }

}

