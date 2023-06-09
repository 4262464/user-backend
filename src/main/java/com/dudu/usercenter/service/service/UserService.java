package com.dudu.usercenter.service.service;

import com.dudu.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dudu.usercenter.model.domain.request.AdminUpdateRequest;
import com.dudu.usercenter.model.domain.request.UserUpdatePasswordRequest;
import com.dudu.usercenter.model.domain.request.UserUpdateRequest;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * 用户服务
 *
* @author 86198
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2022-10-06 20:23:04
 *
 *
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     * @param request
     * @return
     */
     int userLogout(HttpServletRequest request);

    /**
     *修改密码
     *
     * @param userUpdatePasswordRequest
     * @param request
     * @return
     */
     boolean userUpdatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest,HttpServletRequest request);

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
     User getLoginUser(HttpServletRequest request);

    /**
     * 用户修改自己的信息
     *
     * @param userUpdateRequest
     *
     * @return
     */
     boolean updateMyUser(UserUpdateRequest userUpdateRequest);

    /**
     * 管理员修改用户的信息
     *
     * @param adminUpdateRequest
     * @param request
     * @return
     */
     boolean adminUpdateUser(AdminUpdateRequest adminUpdateRequest);


    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    //List<User> searchUsersByTags(List<String> tagNameList);
}
