package com.heima.common.exception;

import com.heima.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常拦截器
 */
@RestControllerAdvice  // @RestControllerAdvice = @ControllerAdvice+@ResponseBody
public class GlobalExceptionHandler {


    /**
     * 处理业务异常（自定义异常）
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = LeadNewsException.class)
    public ResponseResult handlerLeadNewsException(LeadNewsException e) {

        return ResponseResult.errorResult(e.getStatus(), e.getMessage());
    }

    /**
     * 处理系统异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseResult handlerException(Exception e) {
        return ResponseResult.errorResult(500, "系统异常，稍后重试" + e.getMessage());
    }
}
