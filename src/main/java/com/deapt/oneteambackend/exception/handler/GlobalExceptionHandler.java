package com.deapt.oneteambackend.exception.handler;

import com.deapt.oneteambackend.common.result.Result;
import com.deapt.oneteambackend.common.result.StatusCode;
import com.deapt.oneteambackend.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Deapt
 * @description 全局异常处理类
 * @since 2025/4/28 15:08
 */

@Slf4j
@RestControllerAdvice //Spring AOP: 在调用方法前后进行额外的处理
 // 隐藏该类，使其不会出现在生成的 OpenAPI 文档中
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)// 捕获自定义基础异常
    public Result baseExceptionHandler(BaseException e) {
        log.error("runtimeException:" + e.getMessage(),e);
        return Result.error(e.getCode(), e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)// 捕获运行时异常异常
    public Result runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException:" ,e);
        return Result.error(StatusCode.SYSTEM_ERROR, e.getMessage());
    }
}
