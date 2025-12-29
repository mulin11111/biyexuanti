-- 毕业设计选题系统数据库表设计

-- 创建数据库
CREATE DATABASE IF NOT EXISTS graduation_topic_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE graduation_topic_system;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
  `role` VARCHAR(20) NOT NULL COMMENT '角色：admin（管理员）、user（普通用户）',
  `status` INT(1) NOT NULL DEFAULT 1 COMMENT '状态：1（启用）、0（禁用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 选题表
CREATE TABLE IF NOT EXISTS `topic` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '选题ID',
  `title` VARCHAR(255) NOT NULL COMMENT '课题名称',
  `description` TEXT COMMENT '课题描述',
  `teacher_name` VARCHAR(50) NOT NULL COMMENT '指导教师',
  `max_students` INT(11) NOT NULL DEFAULT 1 COMMENT '最大可选学生数',
  `current_students` INT(11) NOT NULL DEFAULT 0 COMMENT '当前已选学生数',
  `status` INT(1) NOT NULL DEFAULT 1 COMMENT '状态：1（可选题）、0（不可选题）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选题表';

-- 用户选题关联表
CREATE TABLE IF NOT EXISTS `user_topic` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `topic_id` BIGINT(20) NOT NULL COMMENT '选题ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_topic` (`user_id`, `topic_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户选题关联表';

-- 进度表
CREATE TABLE IF NOT EXISTS `progress` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `topic_id` BIGINT(20) NOT NULL COMMENT '选题ID',
  `progress_stage` VARCHAR(50) NOT NULL COMMENT '进度阶段（如：开题报告、中期检查、结题验收等）',
  `progress_content` TEXT NOT NULL COMMENT '进度内容',
  `progress_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '进度登记时间',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '进度状态（pending：待审核、approved：已通过、rejected：已驳回）',
  `teacher_comment` TEXT COMMENT '教师评语',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='进度表';

-- 插入初始数据
-- 插入管理员用户（密码：123456，已加密）
INSERT INTO `user` (`username`, `password`, `real_name`, `email`, `role`, `status`) VALUES 
('admin', '$2a$10$7T0e5rV7aZ8bY9cX0dW1eR2tY3uI4oP5aS6dF7gH8jK9lL0zX1cV2bN3m', '管理员', 'admin@example.com', 'admin', 1);

-- 插入示例选题
INSERT INTO `topic` (`title`, `description`, `teacher_name`, `max_students`, `current_students`, `status`) VALUES 
('基于Spring Boot的毕业设计选题系统', '设计并实现一个前后端分离的毕业设计选题系统，包含管理员和用户两种角色，支持选题管理、用户管理和进度跟踪。', '张老师', 5, 0, 1),
('基于Vue的校园论坛系统', '设计并实现一个基于Vue的校园论坛系统，支持用户发帖、评论、点赞等功能。', '李老师', 3, 0, 1),
('基于Java的在线考试系统', '设计并实现一个基于Java的在线考试系统，支持试卷生成、在线答题、自动评分等功能。', '王老师', 4, 0, 1);