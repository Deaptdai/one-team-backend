package com.deapt.oneteambackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Deapt
 * @description 用户登录请求参数
 * @since 2025/4/26 10:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 5435358749011841687L;

    /**
     * 用户名
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
}
