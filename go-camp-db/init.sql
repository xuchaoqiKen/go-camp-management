-- ============================================
-- 围棋训练营管理系统 - 数据库初始化脚本
-- 数据库: go_camp | 字符集: utf8mb4
-- ============================================

CREATE DATABASE IF NOT EXISTS `go_camp` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `go_camp`;

-- ============================================
-- 1. 系统管理模块
-- ============================================

-- 部门表
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `dept_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
    `dept_code` VARCHAR(50) NOT NULL COMMENT '部门编码',
    `leader` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0停用 1启用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_code` (`dept_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(200) NOT NULL COMMENT '密码(BCrypt加密)',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0未知 1男 2女',
    `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0停用 1启用',
    `open_id` VARCHAR(100) DEFAULT NULL COMMENT '微信OpenID',
    `union_id` VARCHAR(100) DEFAULT NULL COMMENT '微信UnionID',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色标识',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0停用 1启用',
    `is_system` TINYINT DEFAULT 0 COMMENT '是否系统内置 0否 1是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表(菜单/按钮)
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID 0为顶级',
    `perm_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `perm_code` VARCHAR(100) NOT NULL COMMENT '权限标识',
    `perm_type` TINYINT NOT NULL COMMENT '类型 1目录 2菜单 3按钮',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    `component` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0停用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_perm_code` (`perm_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户-角色关联
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色-权限关联
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL,
    `permission_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_perm_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 操作日志表
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT DEFAULT NULL,
    `username` VARCHAR(50) DEFAULT NULL,
    `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
    `action` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
    `target_id` VARCHAR(100) DEFAULT NULL COMMENT '目标ID',
    `before_data` TEXT DEFAULT NULL COMMENT '变更前JSON',
    `after_data` TEXT DEFAULT NULL COMMENT '变更后JSON',
    `remark` VARCHAR(500) DEFAULT NULL,
    `ip` VARCHAR(50) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================
-- 2. 营期与配置模块
-- ============================================

-- 营期表
DROP TABLE IF EXISTS `camp_session`;
CREATE TABLE `camp_session` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `session_name` VARCHAR(50) NOT NULL COMMENT '期数名称',
    `session_code` VARCHAR(20) NOT NULL COMMENT '期数编码',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `check_in_date` DATE NOT NULL COMMENT '报到日',
    `check_out_date` DATE NOT NULL COMMENT '退房日',
    `status` TINYINT DEFAULT 1 COMMENT '0停用 1启用',
    `remark` VARCHAR(500) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_code` (`session_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营期配置表';

-- 营期每日规则表
DROP TABLE IF EXISTS `camp_daily_rule`;
CREATE TABLE `camp_daily_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `session_id` BIGINT NOT NULL COMMENT '营期ID',
    `rule_date` DATE NOT NULL COMMENT '日期',
    `has_class` TINYINT DEFAULT 1 COMMENT '是否上课 0否 1是',
    `breakfast_available` TINYINT DEFAULT 1 COMMENT '早餐可选',
    `lunch_available` TINYINT DEFAULT 1 COMMENT '午餐可选',
    `dinner_available` TINYINT DEFAULT 1 COMMENT '晚餐可选',
    `accommodation_available` TINYINT DEFAULT 1 COMMENT '可住宿',
    `is_check_in_day` TINYINT DEFAULT 0 COMMENT '报到日',
    `is_check_out_day` TINYINT DEFAULT 0 COMMENT '退房日',
    `remark` VARCHAR(500) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_date` (`session_id`, `rule_date`),
    KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营期每日规则表';

-- 费用配置表
DROP TABLE IF EXISTS `fee_config`;
CREATE TABLE `fee_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `fee_code` VARCHAR(50) NOT NULL COMMENT '费用编码',
    `fee_name` VARCHAR(50) NOT NULL COMMENT '费用名称(早餐/午餐/晚餐/全托管/陪住标间/陪住大床/不住宿)',
    `fee_type` VARCHAR(30) NOT NULL COMMENT '费用类型 meal/accommodation/package',
    `unit_price` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '单价(元)',
    `charge_unit` VARCHAR(20) NOT NULL COMMENT '计费单位(份/晚/间/天)',
    `session_id` BIGINT DEFAULT NULL COMMENT '适用期数ID NULL=通用',
    `start_date` DATE DEFAULT NULL COMMENT '生效开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '生效结束日期',
    `status` TINYINT DEFAULT 1 COMMENT '0停用 1启用',
    `remark` VARCHAR(500) DEFAULT NULL,
    `create_by` VARCHAR(50) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_fee_code` (`fee_code`),
    KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用配置表';

-- 费用配置历史表
DROP TABLE IF EXISTS `fee_config_history`;
CREATE TABLE `fee_config_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `fee_config_id` BIGINT NOT NULL,
    `unit_price_old` DECIMAL(10,2) DEFAULT NULL,
    `unit_price_new` DECIMAL(10,2) DEFAULT NULL,
    `change_reason` VARCHAR(500) DEFAULT NULL,
    `operator` VARCHAR(50) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_fee_config_id` (`fee_config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用配置变更历史';

-- 段位配置表
DROP TABLE IF EXISTS `rank_config`;
CREATE TABLE `rank_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `rank_code` VARCHAR(30) NOT NULL COMMENT '段位编码',
    `rank_name` VARCHAR(50) NOT NULL COMMENT '段位名称',
    `rank_category` VARCHAR(30) DEFAULT NULL COMMENT '段位分类(无基础组/级位初级组/级位进阶组/段位组/待确认)',
    `min_level` INT DEFAULT NULL COMMENT '段位范围-最小值',
    `max_level` INT DEFAULT NULL COMMENT '段位范围-最大值',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '0停用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rank_code` (`rank_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='段位配置表';

-- ============================================
-- 3. 学员与家长模块
-- ============================================

-- 学员表
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '学员ID',
    `student_no` VARCHAR(30) NOT NULL COMMENT '学员编号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` TINYINT NOT NULL COMMENT '性别 1男 2女',
    `age` INT NOT NULL COMMENT '年龄',
    `rank_code` VARCHAR(30) DEFAULT NULL COMMENT '段位编码',
    `rank_category` VARCHAR(30) DEFAULT NULL COMMENT '段位分类',
    `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
    `parent_user_id` BIGINT NOT NULL COMMENT '关联家长用户ID',
    `accompany_person` VARCHAR(50) DEFAULT NULL COMMENT '陪同人员',
    `camp_status` VARCHAR(20) DEFAULT 'registered' COMMENT '在营状态 registered/checked_in/checked_out/cancelled',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    KEY `idx_parent_user_id` (`parent_user_id`),
    KEY `idx_rank_code` (`rank_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员表';

-- ============================================
-- 4. 报名订单模块
-- ============================================

-- 报名订单表
DROP TABLE IF EXISTS `registration_order`;
CREATE TABLE `registration_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(30) NOT NULL COMMENT '学员订单编号',
    `payment_no` VARCHAR(30) DEFAULT NULL COMMENT '关联支付编号(合并支付时多个订单同一支付编号)',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `session_id` BIGINT NOT NULL COMMENT '营期ID',
    `is_continuous` TINYINT DEFAULT 0 COMMENT '是否连报 0单期 1连报',
    `session_ids` VARCHAR(50) DEFAULT NULL COMMENT '连报时包含的期数ID(逗号分隔)',
    `rank_category` VARCHAR(30) DEFAULT NULL COMMENT '段位分类(报名时记录)',
    `accommodation_type` VARCHAR(30) DEFAULT NULL COMMENT '住宿类型 no_stay/full_board/accompany_standard/accompany_king',
    `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '后勤订单金额',
    `payment_status` VARCHAR(20) DEFAULT 'unpaid' COMMENT '支付状态 unpaid/paid/refunding/partial_refund',
    `order_status` VARCHAR(20) DEFAULT 'pending' COMMENT '订单状态 pending/confirmed/cancelled',
    `room_match_status` VARCHAR(20) DEFAULT 'pending' COMMENT '房间匹配状态 pending/matched/confirmed/manual_adjusted',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_payment_no` (`payment_no`),
    KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报名订单表';

-- 支付记录表
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `payment_no` VARCHAR(30) NOT NULL COMMENT '支付编号',
    `parent_user_id` BIGINT NOT NULL COMMENT '支付家长ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '支付总金额',
    `paid_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '已退款金额',
    `payment_status` VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/success/failed/closed',
    `wx_transaction_id` VARCHAR(64) DEFAULT NULL COMMENT '微信支付交易号',
    `wx_out_trade_no` VARCHAR(64) DEFAULT NULL COMMENT '微信商户订单号',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_parent_user_id` (`parent_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 每日排期表
DROP TABLE IF EXISTS `daily_schedule`;
CREATE TABLE `daily_schedule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `session_id` BIGINT NOT NULL COMMENT '营期ID',
    `schedule_date` DATE NOT NULL COMMENT '排期日期',
    `has_class` TINYINT DEFAULT 1 COMMENT '是否上课',
    `has_breakfast` TINYINT DEFAULT 0 COMMENT '早餐勾选',
    `breakfast_source` VARCHAR(30) DEFAULT NULL COMMENT '早餐来源 boarding_included/accommodation_included/day_purchase',
    `breakfast_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '早餐金额',
    `breakfast_editable` TINYINT DEFAULT 1 COMMENT '早餐是否可编辑',
    `has_lunch` TINYINT DEFAULT 0 COMMENT '午餐',
    `lunch_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '午餐金额',
    `has_dinner` TINYINT DEFAULT 0 COMMENT '晚餐',
    `dinner_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '晚餐金额',
    `accommodation_type` VARCHAR(30) DEFAULT NULL COMMENT '当日住宿类型',
    `accommodation_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '住宿金额',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_date` (`order_id`, `schedule_date`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_session_date` (`session_id`, `schedule_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日排期表';

-- ============================================
-- 5. 班级与分房模块
-- ============================================

-- 班级表
DROP TABLE IF EXISTS `camp_class`;
CREATE TABLE `camp_class` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `class_name` VARCHAR(50) NOT NULL COMMENT '班级名称',
    `session_id` BIGINT NOT NULL COMMENT '所属期数ID',
    `rank_description` VARCHAR(200) DEFAULT NULL COMMENT '段位说明',
    `max_students` INT DEFAULT 0 COMMENT '人数上限',
    `head_teacher_id` BIGINT DEFAULT NULL COMMENT '教务(班主任)用户ID',
    `coach_id` BIGINT DEFAULT NULL COMMENT '授课教练用户ID',
    `student_count` INT DEFAULT 0 COMMENT '当前学生人数',
    `status` TINYINT DEFAULT 1 COMMENT '0停用 1启用',
    `remark` VARCHAR(500) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_session` (`class_name`, `session_id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_head_teacher` (`head_teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 班级-学员关联表
DROP TABLE IF EXISTS `class_student`;
CREATE TABLE `class_student` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `session_id` BIGINT NOT NULL COMMENT '期数ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_session` (`student_id`, `session_id`),
    KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级学员关联表';

-- 房间表
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `room_no` VARCHAR(20) NOT NULL COMMENT '房号',
    `room_name` VARCHAR(50) DEFAULT NULL COMMENT '房间名称',
    `room_type` VARCHAR(30) NOT NULL COMMENT '房型 standard/king',
    `building` VARCHAR(50) DEFAULT NULL COMMENT '楼栋',
    `floor` INT DEFAULT NULL COMMENT '楼层',
    `total_beds` INT NOT NULL DEFAULT 0 COMMENT '总床位数',
    `occupied_beds` INT DEFAULT 0 COMMENT '已占用床位',
    `session_id` BIGINT NOT NULL COMMENT '所属期数',
    `gender_limit` TINYINT DEFAULT 0 COMMENT '性别限制 0不限 1男 2女',
    `status` VARCHAR(20) DEFAULT 'available' COMMENT '状态 available/partial/occupied/maintenance',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room_no` (`room_no`),
    KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

-- 房间入住记录表
DROP TABLE IF EXISTS `room_occupancy`;
CREATE TABLE `room_occupancy` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `room_id` BIGINT NOT NULL COMMENT '房间ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `session_id` BIGINT NOT NULL COMMENT '期数ID',
    `bed_no` VARCHAR(10) DEFAULT NULL COMMENT '床位号',
    `check_in_date` DATE NOT NULL COMMENT '入住日期',
    `check_out_date` DATE NOT NULL COMMENT '退房日期',
    `occupancy_source` VARCHAR(30) DEFAULT 'system' COMMENT '占用来源 system/manual',
    `match_basis` VARCHAR(200) DEFAULT NULL COMMENT '匹配依据',
    `status` VARCHAR(20) DEFAULT 'occupied' COMMENT 'occupied/released',
    `release_time` DATETIME DEFAULT NULL COMMENT '释放时间',
    `release_reason` VARCHAR(500) DEFAULT NULL COMMENT '释放原因',
    `release_operator` VARCHAR(50) DEFAULT NULL COMMENT '释放操作人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_session_date` (`session_id`, `check_in_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间入住记录表';

-- ============================================
-- 6. 请假与退款模块
-- ============================================

-- 请假申请表
DROP TABLE IF EXISTS `leave_request`;
CREATE TABLE `leave_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `request_no` VARCHAR(30) NOT NULL COMMENT '申请编号',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `session_id` BIGINT NOT NULL COMMENT '期数ID',
    `leave_date` DATE NOT NULL COMMENT '请假日期',
    `leave_type` VARCHAR(30) NOT NULL COMMENT '请假类型 no_deduction/meal_refund_only',
    `source` VARCHAR(20) DEFAULT 'parent' COMMENT '来源 parent/manual',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '原因',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/approved/rejected/refunded/refund_failed',
    `refund_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '退款金额',
    `reviewer_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `review_opinion` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    `review_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `is_overdue` TINYINT DEFAULT 0 COMMENT '是否逾期',
    `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人(手动新增时)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_request_no` (`request_no`),
    UNIQUE KEY `uk_student_date_type` (`student_id`, `leave_date`, `leave_type`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';

-- 退款申请表
DROP TABLE IF EXISTS `refund_request`;
CREATE TABLE `refund_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `refund_no` VARCHAR(30) NOT NULL COMMENT '退款申请编号',
    `order_id` BIGINT NOT NULL COMMENT '关联订单ID',
    `student_id` BIGINT NOT NULL COMMENT '学员ID',
    `leave_request_id` BIGINT DEFAULT NULL COMMENT '关联请假申请ID',
    `refund_type` VARCHAR(30) NOT NULL COMMENT '退款类型 breakfast/lunch/dinner/accommodation',
    `refund_items` VARCHAR(500) DEFAULT NULL COMMENT '退款项目明细JSON',
    `refund_amount` DECIMAL(10,2) NOT NULL COMMENT '可退金额',
    `actual_refund_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '实际退款金额',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/approved/pending_refund/refunded/rejected/refund_failed',
    `source` VARCHAR(20) DEFAULT 'parent' COMMENT '来源 parent/manual',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '退款原因',
    `reviewer_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `review_opinion` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    `review_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `refund_executor_id` BIGINT DEFAULT NULL COMMENT '退款执行人ID',
    `refund_time` DATETIME DEFAULT NULL COMMENT '退款执行时间',
    `wx_refund_id` VARCHAR(64) DEFAULT NULL COMMENT '微信退款单号',
    `refund_fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '退款失败原因',
    `retry_count` INT DEFAULT 0 COMMENT '重试次数',
    `last_retry_time` DATETIME DEFAULT NULL COMMENT '最后重试时间',
    `is_overdue` TINYINT DEFAULT 0 COMMENT '是否逾期',
    `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人(手动新增时)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请表';

-- ============================================
-- 7. 教练模块
-- ============================================

-- 教练排班表
DROP TABLE IF EXISTS `coach_schedule`;
CREATE TABLE `coach_schedule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `coach_id` BIGINT NOT NULL COMMENT '教练用户ID',
    `session_id` BIGINT NOT NULL COMMENT '期数ID',
    `schedule_date` DATE NOT NULL COMMENT '排班日期',
    `has_class` TINYINT DEFAULT 0 COMMENT '到岗上课',
    `has_work_lunch` TINYINT DEFAULT 0 COMMENT '工作午餐',
    `has_work_dinner` TINYINT DEFAULT 0 COMMENT '工作晚餐',
    `need_accommodation` TINYINT DEFAULT 0 COMMENT '需要住宿',
    `lunch_cost` DECIMAL(10,2) DEFAULT 0 COMMENT '午餐成本',
    `dinner_cost` DECIMAL(10,2) DEFAULT 0 COMMENT '晚餐成本',
    `total_cost` DECIMAL(10,2) DEFAULT 0 COMMENT '学校承担后勤成本',
    `status` VARCHAR(20) DEFAULT 'submitted' COMMENT 'submitted/cancelled',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_coach_date` (`coach_id`, `schedule_date`),
    KEY `idx_session_date` (`session_id`, `schedule_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练排班表';

-- 撤餐申请表
DROP TABLE IF EXISTS `meal_cancel_request`;
CREATE TABLE `meal_cancel_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `request_no` VARCHAR(30) NOT NULL COMMENT '申请编号',
    `coach_id` BIGINT NOT NULL COMMENT '教练用户ID',
    `schedule_id` BIGINT NOT NULL COMMENT '排班记录ID',
    `cancel_date` DATE NOT NULL COMMENT '撤餐日期',
    `meal_type` VARCHAR(20) NOT NULL COMMENT '餐别 lunch/dinner',
    `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `is_overdue` TINYINT DEFAULT 0 COMMENT '是否逾期',
    `overdue_confirmed` TINYINT DEFAULT 0 COMMENT '逾期确认 0未确认 1已确认',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/approved/rejected/overdue_violation',
    `reviewer_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `review_opinion` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    `review_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_request_no` (`request_no`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_schedule_id` (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='撤餐申请表';

-- ============================================
-- 8. 初始化数据
-- ============================================

-- 初始化部门
INSERT INTO `sys_department` (`dept_name`, `dept_code`, `leader`, `sort_order`) VALUES
('教务部', 'DEAN_DEPT', NULL, 1),
('教练部', 'COACH_DEPT', NULL, 2);

-- 初始化角色
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `is_system`) VALUES
('管理员', 'ADMIN', '系统管理员，拥有全部权限', 1),
('教务管理', 'DEAN_ADMIN', '教务管理，可审批请假/退款/执行退款', 1),
('教务(班主任)', 'HEAD_TEACHER', '班主任，仅查看负责班级学员', 1),
('教练', 'COACH', '教练，查看上课名单/排班/撤餐', 1),
('家长', 'PARENT', '家长，报名/请假/查看信息', 1);

-- 初始化管理员账号 (密码: admin123 BCrypt加密)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `dept_id`, `status`) VALUES
('admin', '$2a$10$Zk0NAO5weAZfFTd2aRehCOBrFrGRzO40unq/P9DyrYBswyTmk/R.W', '系统管理员', '13800000000', 1, 1);

-- 管理员绑定角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 初始化段位配置
INSERT INTO `rank_config` (`rank_code`, `rank_name`, `rank_category`, `min_level`, `max_level`, `sort_order`) VALUES
('NO_BASIC', '无基础', '无基础组', NULL, NULL, 1),
('LEVEL_18_10', '18级-10级', '级位初级组', 10, 18, 2),
('LEVEL_9_1', '9级-1级', '级位进阶组', 1, 9, 3),
('DAN_1', '1段', '段位组', NULL, NULL, 4),
('DAN_2', '2段', '段位组', NULL, NULL, 5),
('DAN_3', '3段', '段位组', NULL, NULL, 6),
('DAN_4', '4段', '段位组', NULL, NULL, 7),
('DAN_5', '5段', '段位组', NULL, NULL, 8),
('DAN_6', '6段+', '段位组', NULL, NULL, 9),
('UNKNOWN', '待确认', '待确认', NULL, NULL, 99);

-- 初始化费用配置
INSERT INTO `fee_config` (`fee_code`, `fee_name`, `fee_type`, `unit_price`, `charge_unit`, `status`) VALUES
('BREAKFAST', '早餐', 'meal', 15.00, '份', 1),
('LUNCH', '午餐', 'meal', 30.00, '份', 1),
('DINNER', '晚餐', 'meal', 30.00, '份', 1),
('FULL_BOARD', '全托管', 'package', 200.00, '晚', 1),
('ACCOMPANY_STANDARD', '陪住标间', 'accommodation', 300.00, '间/晚', 1),
('ACCOMPANY_KING', '陪住大床', 'accommodation', 400.00, '间/晚', 1),
('NO_STAY', '不住宿', 'accommodation', 0.00, '天', 1);

-- 初始化测试家长账号 (密码: admin123)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `dept_id`, `status`) VALUES
('parent1', '$2a$10$Zk0NAO5weAZfFTd2aRehCOBrFrGRzO40unq/P9DyrYBswyTmk/R.W', '张家长', '13800000001', 1, 1);

-- 初始化测试教练账号 (密码: admin123)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `dept_id`, `status`) VALUES
('coach1', '$2a$10$Zk0NAO5weAZfFTd2aRehCOBrFrGRzO40unq/P9DyrYBswyTmk/R.W', '李教练', '13800000002', 2, 1);

-- 初始化测试教务管理账号 (密码: admin123)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `dept_id`, `status`) VALUES
('dean1', '$2a$10$Zk0NAO5weAZfFTd2aRehCOBrFrGRzO40unq/P9DyrYBswyTmk/R.W', '王教务', '13800000003', 1, 1);

-- 绑定用户角色 (admin=1, parent1=2, coach1=3, dean1=4)
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2, 5);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (3, 4);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (4, 2);

-- 初始化测试学员
INSERT INTO `student` (`student_no`, `name`, `gender`, `age`, `rank_code`, `city`, `parent_user_id`, `camp_status`) VALUES
('STU001', '张三', 1, 12, 'DAN_3', '北京', 2, 'in_camp'),
('STU002', '李四', 2, 10, 'LEVEL_9_1', '上海', 2, 'in_camp');

-- 初始化营期配置
INSERT INTO `camp_session` (`session_name`, `session_code`, `start_date`, `end_date`, `check_in_date`, `check_out_date`, `status`) VALUES
('第一期', 'SESSION_1', '2026-07-15', '2026-07-24', '2026-07-15', '2026-07-24', 1),
('第二期', 'SESSION_2', '2026-07-26', '2026-08-04', '2026-07-26', '2026-08-04', 1);
