package com.dudu.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

//管理员修改用户信息
@Data
public class AdminUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    private String username;

    private String userAccount;

    private String avatarUrl;

    private Integer gender;

    private String email;

    private Integer userStatus;

    private String phone;

    private Integer userRole;

}
