package com.deapt.oneteambackend.service;

import com.deapt.oneteambackend.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Deapt
 * @description 算法工具类测试
 * @since 2025/6/25 19:50
 */
public class AlgorithmUtilsTest {
    @Test
    void minDistanceTest() {
        String str1 = "kitten";
        String str2 = "sitting";
        int distance = AlgorithmUtils.minDistance(str1, str2);
        System.out.println("最小编辑距离: " + distance); // 输出: 最小编辑距离: 3
    }

    @Test
    void testCompareTest() {
        List<String> list1 = Arrays.asList("java", "大一");
        List<String> list2 = Arrays.asList("java", "大二");
        List<String> list3 = Arrays.asList("python", "大二");
        int distance1 = AlgorithmUtils.minDistance(list1, list2);
        int distance2 = AlgorithmUtils.minDistance(list1, list3);
        System.out.println("最小编辑距离: " + distance1 + " " + distance2); // 输出: 最小编辑距离: 3
    }

}
