package com.deapt.oneteambackend.exception;

import com.deapt.oneteambackend.common.result.StatusCode;
import lombok.Getter;

/**
 * @author Deapt
 * @description 自定义基础异常
 * @since 2025/4/28 13:11
 */

@Getter
public class BaseException extends RuntimeException{
    private final int code;
    private final String description;
    public BaseException(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public BaseException(String msg, int code, String description) {
        super(msg);
        this.code = code;
        this.description = description;
    }
    public BaseException(StatusCode statusCode,String description) {
        super(statusCode.getMessage());
        this.code = statusCode.getCode();
        this.description = description;
    }
}
