package com.deapt.oneteambackend.constant;

/**
 * @author Deapt
 * @description 用户相关常数
 * @since 2025/4/23 11:16
 */
public class UserConstant {
    /**
     * 盐值
     */
    public static final String SALT = "1ck12b13k1jmj1h0129h2lj";
    /**
     * 合法用户名，用户名4-20位，只能包含汉字/数字/字母和下划线
     */
    public static final String REG_EXP_ACCOUNT = "^[\\w\\u4E00-\\u9FA5]{4,20}$";
    /**
     * 合法密码，用户密码6-20位，只能包含字母/数字/下划线
     */
    public static final String REG_EXP_PASSWORD = "^\\w{6,20}$";
    /**
     * 用户登录状态
     */
    public static final String USER_LOGIN_STATE = "userLoginState";

}
