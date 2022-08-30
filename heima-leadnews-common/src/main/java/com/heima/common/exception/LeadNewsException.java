package com.heima.common.exception;

import com.heima.common.dtos.AppHttpCodeEnum;
import lombok.Getter;

import java.nio.channels.AcceptPendingException;

/**
 * 自定义业务异常类
 *   404: 服务资源找不到
 *   400： 参数类型错误或不匹配
 *   401： 权限不足
 *   500： 服务器错误
 */
@Getter// 公开getter方法
public class LeadNewsException extends RuntimeException {

    private Integer status; //状态码 400 401 等
    //private String message;//操作信息，添加成功，删除失败等等    继承的父类RuntimeException  里面有  所以不需要写


    public LeadNewsException(Integer status, String message) {

        super(message);
        this.status = status;

    }

    public LeadNewsException(AppHttpCodeEnum appHttpCodeEnum) {
        super(appHttpCodeEnum.getErrorMessage());
        this.status=appHttpCodeEnum.getCode();
    }
}
