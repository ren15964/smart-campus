package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartcampus.common.PageResult;
import com.smartcampus.common.ResultCode;
import com.smartcampus.dto.LoginDTO;
import com.smartcampus.dto.PasswordUpdateDTO;
import com.smartcampus.dto.RegisterDTO;
import com.smartcampus.dto.UserAddDTO;
import com.smartcampus.dto.UserUpdateDTO;
import com.smartcampus.entity.User;
import com.smartcampus.exception.BusinessException;
import com.smartcampus.mapper.UserMapper;
import com.smartcampus.service.UserService;
import com.smartcampus.utils.JwtUtil;
import com.smartcampus.utils.PasswordUtil;
import com.smartcampus.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getRealName());

        // 构造返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        result.put("userInfo", userVO);

        return result;
    }

    @Override
    public Map<String, Object> register(RegisterDTO registerDTO) {
        if (!StringUtils.hasText(registerDTO.getRole())) {
            throw new BusinessException("角色不能为空");
        }
        // 仅允许学生/教师自助注册，管理员账号需后台添加
        if (!("student".equals(registerDTO.getRole()) || "teacher".equals(registerDTO.getRole()))) {
            throw new BusinessException("角色不合法");
        }
        // 密码二次确认
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, registerDTO.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(PasswordUtil.encode(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setRole(registerDTO.getRole());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1);
        user.setIsFirstLogin(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);

        // 注册后直接签发token（相当于自动登录）
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), user.getRealName());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        result.put("userInfo", userVO);
        return result;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public PageResult<UserVO> pageUser(Integer current, Integer size, String role, String keyword) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword).or().like(User::getRealName, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = userMapper.selectPage(page, wrapper);

        List<UserVO> userVOList = userPage.getRecords().stream()
                .map(user -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(user, userVO);
                    return userVO;
                })
                .collect(Collectors.toList());

        return new PageResult<>(userVOList, userPage.getTotal(), userPage.getSize(), userPage.getCurrent(), userPage.getPages());
    }

    @Override
    public void addUser(UserAddDTO userAddDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userAddDTO.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(userAddDTO, user);
        user.setPassword(PasswordUtil.encode(userAddDTO.getPassword())); // 密码加密
        user.setStatus(1); // 默认启用
        user.setIsFirstLogin(1); // 默认首次登录
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        User user = userMapper.selectById(userUpdateDTO.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        BeanUtils.copyProperties(userUpdateDTO, user);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 逻辑删除
        user.setIsDeleted(1);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void updatePassword(Long userId, PasswordUpdateDTO passwordUpdateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证旧密码
        if (!PasswordUtil.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }

        // 更新新密码
        user.setPassword(PasswordUtil.encode(passwordUpdateDTO.getNewPassword()));
        user.setIsFirstLogin(0); // 修改密码后将不再是首次登录
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void updateProfile(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 允许更新的字段
        user.setRealName(userUpdateDTO.getRealName());
        user.setEmail(userUpdateDTO.getEmail());
        user.setPhone(userUpdateDTO.getPhone());
        user.setAvatar(userUpdateDTO.getAvatar());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}
