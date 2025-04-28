package com.deapt.oneteambackend.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Deapt
 * @description 后端统一返回结果
 * @since 2025/4/26 12:59
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; // 返回信息
    private T data; //数据
    private String description; //描述

    /**
     * 成功返回
     * @param statusCode 状态码
     * @return
     * @param <T>
     */
    public static <T> Result<T> success(StatusCode statusCode) {
        Result<T> result = new Result<T>();
        result.msg = statusCode.getMessage();
        result.code = statusCode.getCode();
        return result;
    }

    /**
     * 成功返回
     * @param object 数据
     * @param statusCode 状态码
     * @return
     * @param <T>
     */
    public static <T> Result<T> success(T object, StatusCode statusCode) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.msg = statusCode.getMessage();
        result.code = statusCode.getCode();
        return result;
    }

    /**
     * 失败返回
     * @param statusCode 状态码
     * @return
     * @param <T>
     */
    public static <T> Result<T> error(StatusCode statusCode) {
        Result<T> result = new Result<T>();
        result.msg = statusCode.getMessage();
        result.code = statusCode.getCode();
        return result;
    }

    /**
     * 失败返回
     * @param statusCode 状态码
     * @param msg 返回信息
     * @return
     * @param <T>
     */
    public static <T> Result<T> error(StatusCode statusCode, String msg) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = statusCode.getCode();
        return result;
    }

    public static <T> Result<T> error(int code, String msg, String description) {
        Result<T> result = new Result<T>();
        result.msg = msg;
        result.code = code;
        result.description = description;
        return result;
    }
}
