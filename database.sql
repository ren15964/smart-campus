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

-- 插入更多测试用户
-- 学生用户
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES
('2024002', '$2a$10$QTdplXeJBuvJT265EXQdQekYDKCA3cEjCifgDRGSO/fW2g.tYvQOK', '李四', 'student', 1),
('2024003', '$2a$10$QTdplXeJBuvJT265EXQdQekYDKCA3cEjCifgDRGSO/fW2g.tYvQOK', '王五', 'student', 1),
('2024004', '$2a$10$QTdplXeJBuvJT265EXQdQekYDKCA3cEjCifgDRGSO/fW2g.tYvQOK', '赵六', 'student', 1);

-- 教师用户
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES
('T002', '$2a$10$QTdplXeJBuvJT265EXQdQekYDKCA3cEjCifgDRGSO/fW2g.tYvQOK', '王老师', 'teacher', 1),
('T003', '$2a$10$QTdplXeJBuvJT265EXQdQekYDKCA3cEjCifgDRGSO/fW2g.tYvQOK', '张老师', 'teacher', 1);

-- 更多管理员用户
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES
('admin2', '$2a$10$QTdplXeJBuvJT265EXQdQekYDKCA3cEjCifgDRGSO/fW2g.tYvQOK', '管理员二', 'admin', 1);

-- 插入更多测试学生信息
INSERT INTO `student` (`user_id`, `student_no`, `class_name`, `major`, `grade`, `enrollment_date`)
VALUES
(4, '2024002', '计算机1班', '计算机科学与技术', '2024', '2024-09-01'),
(5, '2024003', '软件2班', '软件工程', '2024', '2024-09-01'),
(6, '2024004', '网络3班', '网络工程', '2024', '2024-09-01');

-- 插入更多测试教师信息
INSERT INTO `teacher` (`user_id`, `teacher_no`, `title`, `department`, `office`)
VALUES
(7, 'T002', '讲师', '软件学院', 'B203'),
(8, 'T003', '教授', '网络安全学院', 'C101');

-- 插入课程数据
INSERT INTO `course` (`course_code`, `course_name`, `credit`, `hours`, `course_type`, `description`)
VALUES
('CS101', '数据结构与算法', 3.0, 48, '必修', '介绍基本数据结构和常用算法。'),
('CS102', '操作系统原理', 3.0, 48, '必修', '探讨操作系统的核心概念和原理。'),
('SE201', '软件工程导论', 2.0, 32, '必修', '软件开发过程和方法的概览。'),
('NW301', '计算机网络', 3.0, 48, '选修', '学习计算机网络的基本原理和协议。'),
('MA101', '高等数学A', 4.0, 64, '必修', '微积分和线性代数基础。');

-- 插入开课计划数据
-- 假设课程ID从1开始，教师ID从3开始 (李老师), 7 (王老师), 8 (张老师)
INSERT INTO `course_schedule` (`course_id`, `teacher_id`, `semester`, `week_day`, `start_week`, `end_week`, `start_time`, `end_time`, `classroom`, `capacity`, `selected_count`, `status`)
VALUES
(1, 3, '2024-2025-1', 1, 1, 16, '08:00', '09:40', '教1-101', 60, 0, 1), -- 数据结构与算法 by 李老师
(2, 7, '2024-2025-1', 2, 1, 16, '10:00', '11:40', '教1-203', 50, 0, 1), -- 操作系统原理 by 王老师
(3, 8, '2024-2025-1', 3, 1, 16, '14:00', '15:40', '教2-301', 45, 0, 1), -- 软件工程导论 by 张老师
(4, 3, '2024-2025-1', 4, 1, 16, '16:00', '17:40', '教1-402', 55, 0, 1), -- 计算机网络 by 李老师
(5, 7, '2024-2025-1', 5, 1, 16, '08:00', '09:40', '教3-105', 70, 0, 1); -- 高等数学A by 王老师

-- 插入选课数据
-- 假设学生ID从2开始 (张三), 4 (李四), 5 (王五), 6 (赵六)
-- 假设开课计划ID从1开始
INSERT INTO `course_selection` (`student_id`, `schedule_id`, `status`)
VALUES
(2, 1, 1), -- 张三选数据结构
(4, 1, 1), -- 李四选数据结构
(5, 2, 1), -- 王五选操作系统
(6, 3, 1), -- 赵六选软件工程
(2, 4, 1), -- 张三选计算机网络
(4, 5, 1); -- 李四选高等数学

-- 插入成绩数据
INSERT INTO `grade` (`student_id`, `schedule_id`, `usual_score`, `exam_score`, `total_score`, `gpa`, `remark`)
VALUES
(2, 1, 85.00, 90.00, 88.00, 3.8, '表现优秀'),
(4, 1, 70.00, 75.00, 72.00, 2.5, '中等偏上'),
(5, 2, 90.00, 88.00, 89.00, 3.9, '非常棒！'),
(6, 3, 60.00, 65.00, 62.00, 1.5, '需要努力'),
(2, 4, 78.00, 82.00, 80.00, 3.0, '良好');

-- 插入通知公告数据
INSERT INTO `notice` (`title`, `content`, `publisher_id`, `publisher_name`, `priority`, `target_role`, `publish_time`, `status`, `read_count`)
VALUES
('关于2024-2025学年第一学期期末考试安排的通知', '请各位同学及时关注考试时间地点。', 1, '系统管理员', 3, 'all', '2026-01-15 09:00:00', 1, 120),
('图书馆闭馆通知', '图书馆将于2026年2月10日至2月17日进行维护。', 1, '系统管理员', 2, 'all', '2026-02-08 15:30:00', 1, 80),
('面向教师的教学评估系统培训通知', '请所有教师参加。', 3, '李老师', 1, 'teacher', '2026-02-05 10:00:00', 1, 30);

-- 插入新闻数据
INSERT INTO `news` (`title`, `cover_image`, `content`, `category`, `publisher_id`, `publisher_name`, `view_count`, `publish_time`, `status`)
VALUES
('我校在全国大学生创新创业大赛中喜获佳绩', 'http://example.com/news1_cover.jpg', '我校代表队在本次大赛中表现突出，获得一等奖一项，二等奖两项。', '校园新闻', 1, '系统管理员', 200, '2026-01-20 10:00:00', 1),
('计算机学院成功举办“AI前沿技术”专题讲座', 'http://example.com/news2_cover.jpg', '本次讲座邀请了知名专家，吸引了大量师生参与。', '学术动态', 1, '系统管理员', 150, '2026-02-01 14:00:00', 1);

-- 插入课程资源数据
-- 假设开课计划ID从1开始
INSERT INTO `course_resource` (`schedule_id`, `resource_name`, `resource_type`, `file_path`, `file_size`, `chapter`, `description`, `uploader_id`)
VALUES
(1, '数据结构第一章PPT', 'ppt', '/resources/cs101_chapter1.ppt', 102400, '第一章 绪论', '数据结构课程第一章的课件。', 3),
(1, '数据结构与算法习题集', 'doc', '/resources/cs101_exercises.pdf', 512000, '全部章节', '数据结构课程配套习题。', 3),
(2, '操作系统原理视频教程', 'video', '/resources/os_video.mp4', 102400000, '第一章 操作系统概述', '操作系统原理入门视频。', 7);

-- 插入作业数据
-- 假设开课计划ID从1开始，教师ID从3开始
INSERT INTO `homework` (`schedule_id`, `title`, `content`, `deadline`, `total_score`, `teacher_id`)
VALUES
(1, '数据结构第一次作业：链表实现', '请使用C++或Java实现单链表的基本操作。', '2026-03-01 23:59:59', 100.00, 3),
(2, '操作系统原理实验一：进程管理', '完成进程创建与销毁的实验报告。', '2026-03-15 23:59:59', 100.00, 7);

-- 插入作业提交数据
-- 假设作业ID从1开始，学生ID从2开始
INSERT INTO `homework_submit` (`homework_id`, `student_id`, `content`, `submit_time`, `status`, `is_late`)
VALUES
(1, 2, '已完成链表实现，代码见附件。', '2026-02-28 18:00:00', 1, 0),
(1, 4, '代码已上传，希望没有bug。', '2026-03-02 10:00:00', 1, 1), -- 迟交
(2, 5, '实验报告已提交。', '2026-03-10 16:00:00', 1, 0);

-- 插入作业成绩数据
-- 假设提交ID从1开始，教师ID从3开始
INSERT INTO `homework_grade` (`submit_id`, `score`, `comment`, `grader_id`, `grade_time`)
VALUES
(1, 95.00, '代码规范，功能完善。', 3, '2026-03-05 10:00:00'),
(3, 88.00, '报告内容详实，思考深入。', 7, '2026-03-12 14:00:00');

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
