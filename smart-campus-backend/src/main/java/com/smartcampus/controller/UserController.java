package com.smartcampus.controller;

import com.smartcampus.common.PageResult;
import com.smartcampus.common.Result;
import com.smartcampus.dto.LoginDTO;
import com.smartcampus.dto.PasswordUpdateDTO;
import com.smartcampus.dto.RegisterDTO;
import com.smartcampus.dto.UserAddDTO;
import com.smartcampus.dto.UserUpdateDTO;
import com.smartcampus.service.UserService;
import com.smartcampus.utils.JwtUtil;
import com.smartcampus.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = userService.login(loginDTO);
        return Result.success("登录成功", result);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Validated @RequestBody RegisterDTO registerDTO) {
        Map<String, Object> result = userService.register(registerDTO);
        return Result.success("注册成功", result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    /**
     * 用户列表（管理员）
     */
    @GetMapping("/list")
    public Result<PageResult<UserVO>> pageUser(@RequestParam(defaultValue = "1") Integer current,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 @RequestParam(required = false) String role,
                                                 @RequestParam(required = false) String keyword) {
        PageResult<UserVO> pageResult = userService.pageUser(current, size, role, keyword);
        return Result.success(pageResult);
    }

    /**
     * 添加用户（管理员）
     */
    @PostMapping("/add")
    public Result<String> addUser(@Validated @RequestBody UserAddDTO userAddDTO) {
        userService.addUser(userAddDTO);
        return Result.success("添加成功");
    }

    /**
     * 更新用户（管理员）
     */
    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable Long id, @Validated @RequestBody UserUpdateDTO userUpdateDTO) {
        userUpdateDTO.setId(id);
        userService.updateUser(userUpdateDTO);
        return Result.success("更新成功");
    }

    /**
     * 删除用户（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> updatePassword(HttpServletRequest request, @Validated @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        userService.updatePassword(userId, passwordUpdateDTO);
        return Result.success("密码修改成功");
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(HttpServletRequest request, @Validated @RequestBody UserUpdateDTO userUpdateDTO) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        userService.updateProfile(userId, userUpdateDTO);
        return Result.success("更新成功");
    }

}
