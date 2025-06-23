package com.deapt.oneteambackend.common.result;

import lombok.Getter;

/**
 * @author Deapt
 * @description 错误码
 * @since 2025/4/28 12:35
 */
@Getter
public enum StatusCode {
    SUCCESS(200, "成功"),
    ERROR(500, "失败"),
    USER_NOT_LOGIN(401, "用户未登录"),
    REQUEST_IS_NULL(400, "请求数据为空"),
    PARAMETER_ERROR(400, "请求参数错误"),
    USER_NO_AUTH(403, "用户权限不足"),
    SYSTEM_ERROR(500, "系统异常"),
    ACCOUNT_EXIST_ERROR(400, "账号已存在"),
    FORBIDDEN(403, "禁止访问");


    /**
     * 状态码
     */
    private final int code;
    /**
     * 状态信息
     */
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
