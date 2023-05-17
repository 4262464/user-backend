package com.dudu.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author 86198
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3557511145491756587L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

}
