package com.dudu.usercenter.exception;

import com.dudu.usercenter.common.BaseResponse;
import com.dudu.usercenter.common.ErrorCode;
import com.dudu.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器
 *
 * @author 86198
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /*
    作用:1.捕获代码中所有的异常，内部消化，集中处理，让前端得到更详细的业务报错/信息
        2.同时屏蔽掉项目框架本身的异常(不暴露服务器内部状态)
    实现:Spring AOP:在调用方法前后进行额外的处理
     */

    @ExceptionHandler(BusinessException.class) //对什么类的异常进行什么样的处理
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException"+e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException",e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }

}
