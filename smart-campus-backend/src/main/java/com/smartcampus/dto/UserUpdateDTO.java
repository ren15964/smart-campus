package com.smartcampus.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 用户更新DTO
 */
@Data
public class UserUpdateDTO {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    private String realName;

    private String email;

    private String phone;

    private String avatar;

    private Integer status;
}
