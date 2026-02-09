package com.smartcampus.common;

import lombok.Getter;

/**
 * 返回码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(400, "操作失败"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    // 用户相关
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),
    OLD_PASSWORD_ERROR(1004, "原密码错误"),
    TEACHER_NOT_FOUND(1005, "教师不存在"),

    // 课程相关
    COURSE_NOT_FOUND(2001, "课程不存在"),
    COURSE_ALREADY_EXISTS(2002, "课程已存在"),
    COURSE_SCHEDULE_NOT_FOUND(2003, "开课计划不存在"),

    // 选课相关
    COURSE_FULL(3001, "课程已满"),
    COURSE_TIME_CONFLICT(3002, "课程时间冲突"),
    ALREADY_SELECTED(3003, "已选择该课程"),
    NOT_IN_SELECTION_TIME(3004, "不在选课时间内"),
    COURSE_SELECTION_NOT_FOUND(3005, "选课记录不存在"),

    // 成绩相关
    GRADE_NOT_FOUND(3101, "成绩不存在"),
    GRADE_ALREADY_EXISTS(3102, "成绩已存在"),

    // 文件相关
    FILE_UPLOAD_ERROR(4001, "文件上传失败"),
    FILE_TYPE_ERROR(4002, "文件类型不支持"),
    FILE_SIZE_ERROR(4003, "文件大小超出限制"),
    COURSE_RESOURCE_NOT_FOUND(4004, "课程资源不存在"),

    // 作业相关
    HOMEWORK_NOT_FOUND(5001, "作业不存在"),
    HOMEWORK_SUBMIT_NOT_FOUND(5002, "作业提交记录不存在"),
    HOMEWORK_ALREADY_SUBMITTED(5003, "作业已提交，请勿重复提交"),
    HOMEWORK_PAST_DEADLINE(5004, "已过作业截止时间"),
    HOMEWORK_GRADE_NOT_FOUND(5005, "作业成绩不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
