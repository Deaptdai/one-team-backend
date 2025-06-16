package com.deapt.oneteambackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Deapt
 * @description 用户注册请求参数
 * @since 2025/4/26 10:33
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 9025488541195099317L;

    /**
     * 用户名
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 确认密码
     */
    private String checkPassword;
}
