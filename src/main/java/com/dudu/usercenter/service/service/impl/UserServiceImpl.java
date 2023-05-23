package com.dudu.usercenter.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dudu.usercenter.common.ErrorCode;
import com.dudu.usercenter.exception.BusinessException;
import com.dudu.usercenter.model.domain.User;
import com.dudu.usercenter.model.domain.request.AdminUpdateRequest;
import com.dudu.usercenter.model.domain.request.UserUpdatePasswordRequest;
import com.dudu.usercenter.model.domain.request.UserUpdateRequest;
import com.dudu.usercenter.service.service.UserService;
import com.dudu.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dudu.usercenter.contant.UserConstant.*;

/**
* @author 86198
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2022-10-06 20:23:04
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    @Resource
    private UserMapper userMapper;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "abab";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            //return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            //return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            //return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 账户不包含特殊字符
        String validPattern = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        // 账户不包含中文
        String validPattern2 = "[\u4e00-\u9fa5]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        Matcher matcher2 = Pattern.compile(validPattern2).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含了特殊字符");
        }
        if (matcher2.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户不能有中文字符");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能重复");
        }
        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUsername(DEFAULT_USERNAME);
        user.setPhone(DEFAULT_PHONE);
        user.setGender(DEFAULT_GENDER);
        user.setEmail(DEFAULT_EMAIL);
        user.setAvatarUrl(DEFAULT_AVATAR_URL);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户保存失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "有空白字符");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户小于4位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码不应该小于8");
        }
        // 账户不包含特殊字符
        String validPattern = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户不应该有特殊字符");
        }
        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 3.用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        //safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 修改密码
     *
     * @param userUpdatePasswordRequest
     * @param request
     * @return
     */
    public boolean userUpdatePassword(UserUpdatePasswordRequest userUpdatePasswordRequest, HttpServletRequest request) {
        if (userUpdatePasswordRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求为空");
        }

        //获取当前用户
        User loginUser = getLoginUser(request);
        Long currentUserId = loginUser.getId();
        if (currentUserId < 0 || currentUserId == null){
            throw new BusinessException(ErrorCode.NOT_FIND_USER,"用户不存在");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdatePasswordRequest,user);
        user.setId(loginUser.getId());
        //加密新密码
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userUpdatePasswordRequest.getNewPassword()).getBytes());

        user.setUserPassword(encryptedPassword);
        //两次输入的密码一样
        if (encryptedPassword.equals(userUpdatePasswordRequest.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "修改密码不能相同");
        }


        //更新数据
        boolean result = updateById(user);
        if (!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"更新失败");
        }
        return true;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        return currentUser;
    }

    /**
     * 用户更新自己的信息
     *
     * @param userUpdateRequest
     *
     * @return
     */
    public boolean updateMyUser(UserUpdateRequest userUpdateRequest) {

        if (userUpdateRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求为空");
        }

        //获取当前用户
        Long currentUserId = userUpdateRequest.getId();
        if (currentUserId < 0 || currentUserId == null){
            throw new BusinessException(ErrorCode.NOT_FIND_USER,"用户不存在");
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest,user);
        user.setId(currentUserId);

        user.setUsername(userUpdateRequest.getUsername());
        user.setEmail(userUpdateRequest.getEmail());
        user.setGender(userUpdateRequest.getGender());
        user.setPhone(userUpdateRequest.getPhone());
        user.setAvatarUrl(userUpdateRequest.getAvatarUrl());
        //更新数据
        boolean result = updateById(user);
        if (!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"更新失败");
        }
        return true;
    }

    /**
     * 管理员修改用户信息
     *
     * @param adminUpdateRequest
     * @return
     */
    public boolean adminUpdateUser(AdminUpdateRequest adminUpdateRequest) {

        if (adminUpdateRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求为空");
        }

        //获取当前用户
        Long currentUserId = adminUpdateRequest.getId();
        if (currentUserId < 0 || currentUserId == null){
            throw new BusinessException(ErrorCode.NOT_FIND_USER,"用户不存在");
        }
        User user = new User();
        BeanUtils.copyProperties(adminUpdateRequest,user);
        user.setId(currentUserId);

        user.setUsername(adminUpdateRequest.getUsername());
        user.setEmail(adminUpdateRequest.getEmail());
        user.setGender(adminUpdateRequest.getGender());
        user.setPhone(adminUpdateRequest.getPhone());
        user.setAvatarUrl(adminUpdateRequest.getAvatarUrl());
        user.setUserRole(adminUpdateRequest.getUserRole());
        user.setUserAccount(adminUpdateRequest.getUserAccount());
        user.setUserStatus(adminUpdateRequest.getUserStatus());

        //更新数据
        boolean result = updateById(user);
        if (!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"更新失败");
        }
        return true;
    }


    /**
     * 根据标签搜索用户
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    /*@Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        //拼接and查询
//        //like '%Java%' and like '%Python'
//        for (String tagName : tagNameList){
//            queryWrapper = queryWrapper.like("tags",tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper)
//        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());

        //1.先查所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            Set<String> tempTagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
            }.getType());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }
}

     */
}

