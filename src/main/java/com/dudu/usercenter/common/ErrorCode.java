package com.dudu.usercenter.common;

/**
 * 错误码
 *
 * @author 86198
 */
public enum ErrorCode {

    // 要是很规范的话，要打好注释，生成文档什么的
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求数据为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"无权限",""),
    NOT_FIND_USER(40102,"用户不存在",""),
    OPERATION_ERROR(40200,"操作失败",""),
    SYSTEM_ERROR(50000, "系统内部异常", "");


    /**
     * 状态码
     */
    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述(详情)
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
