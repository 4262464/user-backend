package com.dudu.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dudu.usercenter.common.BaseResponse;
import com.dudu.usercenter.common.ErrorCode;
import com.dudu.usercenter.common.ResultUtils;
import com.dudu.usercenter.exception.BusinessException;
import com.dudu.usercenter.model.domain.User;
import com.dudu.usercenter.model.domain.request.*;
import com.dudu.usercenter.service.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.dudu.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.dudu.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author 86198
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            //return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能有空白字符");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能有空白字符");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
            //return null;
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    //我把参数username 改成了 useraccount
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String useraccount, HttpServletRequest request){
        // 仅管理员可查询
        if(!isAdmin(request)){
            //return new ArrayList<>();
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNoneBlank(useraccount)){
            queryWrapper.like("useraccount",useraccount);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }
    @GetMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
        // 仅管理员可查询
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无权限");
        }

        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @PostMapping("/delete2")
    public BaseResponse<Boolean> deleteUser(@RequestBody UserDeleteRequest deleteRequest,HttpServletRequest request){
        //是否为管理员
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无权限");
        }

        if(deleteRequest == null || deleteRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean removeUser = userService.removeById(deleteRequest.getId());

        return ResultUtils.success(removeUser);
    }

    /**
     * 创建用户
     *
     * @param createUserRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody CreateUserRequest createUserRequest,HttpServletRequest request){
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        if (createUserRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User user = new User();
        BeanUtils.copyProperties(createUserRequest, user);
        System.out.println("新增用户===="+user);
        boolean result = userService.save(user);
        //ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        if (!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    @GetMapping("/search2")
    public BaseResponse<List<User>> searchUsers(UserSearchRequest searchRequest, HttpServletRequest request) {
        // 管理员校验
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        String username = searchRequest.getUsername();
        String userAccount = searchRequest.getUserAccount();
        Integer gender = searchRequest.getGender();
        String phone = searchRequest.getPhone();
        String email = searchRequest.getEmail();
        Integer userStatus = searchRequest.getUserStatus();
        Integer userRole = searchRequest.getUserRole();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //Date updateTime = searchRequest.getUpdateTime();
        Date createTime = searchRequest.getCreateTime();
        // username
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        // userAccount
        if (StringUtils.isNotBlank(userAccount)) {
            queryWrapper.like("userAccount", userAccount);
        }
        // gender
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.eq("gender", gender);
        }
        // phone
        if (StringUtils.isNotBlank(phone)) {
            queryWrapper.like("phone", phone);
        }
        // email
        if (StringUtils.isNotBlank(email)) {
            queryWrapper.like("email", email);
        }
        // userStatus
        if (userStatus != null) {
            queryWrapper.eq("userStatus", userStatus);
        }

        if (userRole != null) {
            queryWrapper.eq("userRole", userRole);
        }

//        if (updateTime != null) {
//            queryWrapper.like("updateTime", updateTime);
//        }
        if (createTime != null) {
            queryWrapper.like("createTime", createTime);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> users = userList.stream().map(userService::getSafetyUser).collect(Collectors.toList());
        return ResultUtils.success(users);
    }

    /**
     * 修改密码
     *
     * @param updatePasswordRequest
     * @param request
     * @return
     */
    @PostMapping("/update/password")
    public BaseResponse<Boolean> updateUserPassword(@RequestBody UserUpdatePasswordRequest updatePasswordRequest,
                                                    HttpServletRequest request) {

        boolean updateUserPassword = userService.userUpdatePassword(updatePasswordRequest, request);
        if (updateUserPassword) {
            return ResultUtils.success(true);
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR,"修改密码失败");
        }
    }

    @PostMapping("/update/myUser")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateRequest userUpdateRequest){

        boolean updateMyUser = userService.updateMyUser(userUpdateRequest);

        if (updateMyUser) {
            return ResultUtils.success(true);
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR,"修改信息失败");
        }
    }

    @PostMapping("/update/admin")
    public BaseResponse<Boolean> adminUpdateUser(@RequestBody AdminUpdateRequest adminUpdateRequest, HttpServletRequest request){

        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }

        boolean adminUpdateUser = userService.adminUpdateUser(adminUpdateRequest);

        if (adminUpdateUser) {
            return ResultUtils.success(true);
        } else {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR,"修改信息失败");
        }
    }



    /**
         * 是否有管理员
         *
         * @param request
         * @return
         */
    private boolean isAdmin(HttpServletRequest request){
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == 0;//ADMIN_ROLE;
    }
}
