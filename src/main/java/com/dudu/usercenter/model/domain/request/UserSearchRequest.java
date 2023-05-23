package com.dudu.usercenter.model.domain.request;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dudu.usercenter.model.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserSearchRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    private String username;

    private String userAccount;

    private Integer gender;

    private String phone;

    private String email;

    private Integer userStatus;

    private Integer userRole;

    //private Date updateTime;

    private Date createTime;

}
