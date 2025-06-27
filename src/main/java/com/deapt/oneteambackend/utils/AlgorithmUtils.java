package com.deapt.oneteambackend.utils;

import java.util.List;
import java.util.Objects;

/**
 * @author Deapt
 * @description 算法工具类
 * @since 2025/6/25 19:50
 */
public class AlgorithmUtils {
    /**
     * 距离变价算法（用于计算最相似的两组标签）
     * @param tagList1 标签列表1
     * @param tagList2 标签列表2
     * @return 最小编辑距离
     */
    public static int minDistance(List<String> tagList1, List<String> tagList2) {
        int m = tagList1.size();
        int n = tagList2.size();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (Objects.equals(tagList1.get(i - 1), tagList2.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1])) + 1;
                }
            }
        }
        return dp[m][n];
    }

    /**
     * 距离变价算法（用于计算最相似的两个字符串）
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 最小编辑距离
     */
    public static int minDistance(String str1, String str2) {
        int m = str1.length();
        int n = str2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1])) + 1;
                }
            }
        }
        return dp[m][n];
    }
}
