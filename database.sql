-- =============================================
-- 智慧校园平台数据库建表脚本
-- 数据库: smart_campus
-- 版本: v1.0
-- 日期: 2026-02-09
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS smart_campus DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smart_campus;

-- =============================================
-- 用户相关表
-- =============================================

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（学号或工号）',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `role` VARCHAR(20) NOT NULL COMMENT '角色：student-学生, teacher-教师, admin-管理员',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用, 1-启用',
  `is_first_login` TINYINT DEFAULT 1 COMMENT '是否首次登录：0-否, 1-是',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 学生表
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `student_no` VARCHAR(20) NOT NULL COMMENT '学号',
  `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级',
  `major` VARCHAR(50) DEFAULT NULL COMMENT '专业',
  `grade` VARCHAR(10) DEFAULT NULL COMMENT '年级',
  `enrollment_date` DATE DEFAULT NULL COMMENT '入学日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_class` (`class_name`),
  KEY `idx_major` (`major`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- 教师表
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `teacher_no` VARCHAR(20) NOT NULL COMMENT '工号',
  `title` VARCHAR(20) DEFAULT NULL COMMENT '职称',
  `department` VARCHAR(50) DEFAULT NULL COMMENT '院系',
  `office` VARCHAR(50) DEFAULT NULL COMMENT '办公室',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_teacher_no` (`teacher_no`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_department` (`department`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师表';

-- =============================================
-- 教务相关表
-- =============================================

-- 课程表
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_code` VARCHAR(20) NOT NULL COMMENT '课程编码',
  `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
  `credit` DECIMAL(3,1) NOT NULL COMMENT '学分',
  `hours` INT NOT NULL COMMENT '学时',
  `course_type` VARCHAR(20) DEFAULT NULL COMMENT '课程类型：必修/选修',
  `description` TEXT COMMENT '课程简介',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_code` (`course_code`),
  KEY `idx_course_name` (`course_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 开课计划表
DROP TABLE IF EXISTS `course_schedule`;
CREATE TABLE `course_schedule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `semester` VARCHAR(20) NOT NULL COMMENT '学期：如2024-2025-1',
  `week_day` TINYINT NOT NULL COMMENT '星期几：1-7',
  `start_week` TINYINT NOT NULL COMMENT '开始周次',
  `end_week` TINYINT NOT NULL COMMENT '结束周次',
  `start_time` VARCHAR(10) NOT NULL COMMENT '开始时间：如08:00',
  `end_time` VARCHAR(10) NOT NULL COMMENT '结束时间：如09:40',
  `classroom` VARCHAR(50) DEFAULT NULL COMMENT '教室',
  `capacity` INT DEFAULT 50 COMMENT '容量',
  `selected_count` INT DEFAULT 0 COMMENT '已选人数',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-关闭选课, 1-开放选课',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_semester` (`semester`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开课计划表';

-- 选课表
DROP TABLE IF EXISTS `course_selection`;
CREATE TABLE `course_selection` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '选课ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `schedule_id` BIGINT NOT NULL COMMENT '开课计划ID',
  `selection_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已退课, 1-已选课',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_schedule` (`student_id`, `schedule_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_schedule_id` (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选课表';

-- 成绩表
DROP TABLE IF EXISTS `grade`;
CREATE TABLE `grade` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `schedule_id` BIGINT NOT NULL COMMENT '开课计划ID',
  `usual_score` DECIMAL(5,2) DEFAULT NULL COMMENT '平时成绩',
  `exam_score` DECIMAL(5,2) DEFAULT NULL COMMENT '期末成绩',
  `total_score` DECIMAL(5,2) DEFAULT NULL COMMENT '总成绩',
  `gpa` DECIMAL(3,2) DEFAULT NULL COMMENT '绩点',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_schedule` (`student_id`, `schedule_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_schedule_id` (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';

-- =============================================
-- 信息发布相关表
-- =============================================

-- 通知公告表
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
  `publisher_name` VARCHAR(50) DEFAULT NULL COMMENT '发布人姓名',
  `priority` TINYINT DEFAULT 1 COMMENT '优先级：1-普通, 2-重要, 3-紧急',
  `target_role` VARCHAR(50) DEFAULT 'all' COMMENT '目标角色：all-全部, student-学生, teacher-教师',
  `attachment` VARCHAR(255) DEFAULT NULL COMMENT '附件路径',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-草稿, 1-已发布',
  `read_count` INT DEFAULT 0 COMMENT '阅读次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_publisher_id` (`publisher_id`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告表';

-- 新闻表
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '新闻ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `content` TEXT NOT NULL COMMENT '内容',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
  `publisher_id` BIGINT NOT NULL COMMENT '发布人ID',
  `publisher_name` VARCHAR(50) DEFAULT NULL COMMENT '发布人姓名',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-草稿, 1-已发布',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='新闻表';

-- 通知阅读记录表
DROP TABLE IF EXISTS `notice_read`;
CREATE TABLE `notice_read` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `notice_id` BIGINT NOT NULL COMMENT '通知ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`),
  KEY `idx_notice_id` (`notice_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知阅读记录表';

-- =============================================
-- 在线学习相关表
-- =============================================

-- 课程资源表
DROP TABLE IF EXISTS `course_resource`;
CREATE TABLE `course_resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `schedule_id` BIGINT NOT NULL COMMENT '开课计划ID',
  `resource_name` VARCHAR(200) NOT NULL COMMENT '资源名称',
  `resource_type` VARCHAR(20) NOT NULL COMMENT '资源类型：doc-文档, video-视频, ppt-课件, other-其他',
  `file_path` VARCHAR(255) NOT NULL COMMENT '文件路径',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
  `chapter` VARCHAR(100) DEFAULT NULL COMMENT '章节',
  `description` TEXT COMMENT '描述',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `uploader_id` BIGINT NOT NULL COMMENT '上传者ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_id` (`schedule_id`),
  KEY `idx_uploader_id` (`uploader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程资源表';

-- 作业表
DROP TABLE IF EXISTS `homework`;
CREATE TABLE `homework` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '作业ID',
  `schedule_id` BIGINT NOT NULL COMMENT '开课计划ID',
  `title` VARCHAR(200) NOT NULL COMMENT '作业标题',
  `content` TEXT NOT NULL COMMENT '作业内容',
  `attachment` VARCHAR(255) DEFAULT NULL COMMENT '附件路径',
  `deadline` DATETIME NOT NULL COMMENT '截止时间',
  `total_score` DECIMAL(5,2) DEFAULT 100.00 COMMENT '总分',
  `teacher_id` BIGINT NOT NULL COMMENT '发布教师ID',
  `submit_count` INT DEFAULT 0 COMMENT '提交人数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_id` (`schedule_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_deadline` (`deadline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业表';

-- 作业提交表
DROP TABLE IF EXISTS `homework_submit`;
CREATE TABLE `homework_submit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `homework_id` BIGINT NOT NULL COMMENT '作业ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `content` TEXT COMMENT '提交内容',
  `attachment` VARCHAR(255) DEFAULT NULL COMMENT '附件路径',
  `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-未提交, 1-已提交, 2-已批改',
  `is_late` TINYINT DEFAULT 0 COMMENT '是否迟交：0-否, 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否, 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_homework_student` (`homework_id`, `student_id`),
  KEY `idx_homework_id` (`homework_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业提交表';

-- 作业成绩表
DROP TABLE IF EXISTS `homework_grade`;
CREATE TABLE `homework_grade` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `submit_id` BIGINT NOT NULL COMMENT '提交ID',
  `score` DECIMAL(5,2) NOT NULL COMMENT '分数',
  `comment` TEXT COMMENT '评语',
  `grader_id` BIGINT NOT NULL COMMENT '批改教师ID',
  `grade_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '批改时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_submit_id` (`submit_id`),
  KEY `idx_grader_id` (`grader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业成绩表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入默认管理员账号
-- 用户名: admin
-- 密码: admin123 (实际使用时需要加密)
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`, `is_first_login`)
VALUES ('admin', '$2a$10$DECE6EAFHbFC5T0sj0Oh6.vvWl37UQTv1YNGSWuZ0UXeH27Q1Ry6u', '系统管理员', 'admin', 1, 0);

-- 插入测试学生账号
-- 用户名: 2024001
-- 密码: 123456
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('2024001', '$2a$10$QTdplXeJBuvJT265EXQdQekYDKCA3cEjCifgDRGSO/fW2g.tYvQOK', '张三', 'student', 1);

INSERT INTO `student` (`user_id`, `student_no`, `class_name`, `major`, `grade`, `enrollment_date`)
VALUES (2, '2024001', '计算机1班', '计算机科学与技术', '2024', '2024-09-01');

-- 插入测试教师账号
-- 用户名: T001
-- 密码: 123456
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('T001', '$2a$10$QTdplXeJBuvJT265EXQdQekYDKCA3cEjCifgDRGSO/fW2g.tYvQOK', '李老师', 'teacher', 1);

INSERT INTO `teacher` (`user_id`, `teacher_no`, `title`, `department`, `office`)
VALUES (3, 'T001', '副教授', '计算机学院', 'A301');

-- =============================================
-- 使用说明
-- =============================================
-- 1. 执行此脚本将创建数据库和所有表
-- 2. 默认管理员账号: admin / admin123
-- 3. 测试学生账号: 2024001 / 123456
-- 4. 测试教师账号: T001 / 123456
-- 5. 密码使用BCrypt加密，实际开发中请修改为真实加密密码
-- 6. 建议在生产环境中修改默认密码
-- =============================================
