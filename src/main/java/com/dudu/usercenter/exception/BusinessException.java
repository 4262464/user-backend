package com.dudu.usercenter.exception;

import com.dudu.usercenter.common.ErrorCode;

/**
 * 自定义异常类
 *
 * @author 86198
 */
//定义业务异常类为了:1.相对Java的异常类，支持更多字段 2.自定义构造函数，更灵活快捷的设置字段
public class BusinessException extends RuntimeException {

    //扩充了两个字段
    private final int code; // 不需要set，就改成final

    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    //提供了构造方法，可以支持传递ErrorCode
    public BusinessException(ErrorCode errorCode) {// 造语法糖
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {// 造语法糖
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
