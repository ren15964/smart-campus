package com.smartcampus.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 注册DTO
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "角色不能为空")
    private String role; // student, teacher

    private String email;

    private String phone;
}

