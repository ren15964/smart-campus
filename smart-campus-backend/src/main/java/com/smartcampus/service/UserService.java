package com.smartcampus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartcampus.dto.LoginDTO;
import com.smartcampus.dto.PasswordUpdateDTO;
import com.smartcampus.dto.RegisterDTO;
import com.smartcampus.dto.UserAddDTO;
import com.smartcampus.dto.UserUpdateDTO;
import com.smartcampus.entity.User;
import com.smartcampus.common.PageResult;
import com.smartcampus.vo.UserVO;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     */
    Map<String, Object> login(LoginDTO loginDTO);

    /**
     * 用户注册
     */
    Map<String, Object> register(RegisterDTO registerDTO);

    /**
     * 获取用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 分页查询用户列表
     */
    PageResult<UserVO> pageUser(Integer current, Integer size, String role, String keyword);

    /**
     * 添加用户
     */
    void addUser(UserAddDTO userAddDTO);

    /**
     * 更新用户信息
     */
    void updateUser(UserUpdateDTO userUpdateDTO);

    /**
     * 删除用户（逻辑删除）
     */
    void deleteUser(Long id);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, PasswordUpdateDTO passwordUpdateDTO);

    /**
     * 更新个人信息
     */
    void updateProfile(Long userId, UserUpdateDTO userUpdateDTO);
}
