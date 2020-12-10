package com.whoiszxl.core.exception;

import com.whoiszxl.core.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常捕获
 */
@Slf4j
@ControllerAdvice
public class ExceptionCatchAdvice {


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<String> exception(Exception exception){
        //记录日志
        log.error("全局异常捕捉:{}",exception.getMessage());
        return Result.buildError(exception.getMessage());
    }

    @ExceptionHandler(SystemException.class)
    @ResponseBody
    public Result<String> systemException(SystemException exception){
        //记录日志
        log.error("全局system异常捕捉:{}",exception.getMessage());
        return Result.buildError(exception.getMessage());
    }
}
