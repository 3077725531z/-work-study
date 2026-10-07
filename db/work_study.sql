/*
 Navicat Premium Data Transfer

 Source Server         : 1
 Source Server Type    : MySQL
 Source Server Version : 80403
 Source Host           : localhost:3306
 Source Schema         : work_study

 Target Server Type    : MySQL
 Target Server Version : 80403
 File Encoding         : 65001

 Date: 08/10/2026 00:13:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_logs
-- ----------------------------
DROP TABLE IF EXISTS `admin_logs`;
CREATE TABLE `admin_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operator_id` bigint NULL DEFAULT NULL,
  `action` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `target` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员操作审计' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_logs
-- ----------------------------

-- ----------------------------
-- Table structure for aid_applications
-- ----------------------------
DROP TABLE IF EXISTS `aid_applications`;
CREATE TABLE `aid_applications`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NULL DEFAULT NULL,
  `level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '一档/二档/三档',
  `income` decimal(10, 2) NULL DEFAULT NULL COMMENT '家庭人均年收入',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `files` json NULL COMMENT '[\"身份证\",\"贫困证明\",\"承诺书\"]',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审核',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '困难认定' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of aid_applications
-- ----------------------------
INSERT INTO `aid_applications` VALUES (1, 2, '二档', 1111.00, 'q\'we1233333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333', '[\"/files/006d391eb5814840bed1caaaa99c8d38.png\"]', '待审核', '2026-10-07 23:50:46', '2026-10-07 23:50:46');

-- ----------------------------
-- Table structure for appeals
-- ----------------------------
DROP TABLE IF EXISTS `appeals`;
CREATE TABLE `appeals`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'S1001',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'salary/publicity/attendance',
  `ref_id` bigint NULL DEFAULT NULL,
  `student_id` bigint NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待处理',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `deadline` date NULL DEFAULT NULL COMMENT 'SLA 截止',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '申诉工单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appeals
-- ----------------------------

-- ----------------------------
-- Table structure for application_logs
-- ----------------------------
DROP TABLE IF EXISTS `application_logs`;
CREATE TABLE `application_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` bigint NOT NULL,
  `action` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'submit/dept_approve/dept_reject/publicity/onboard',
  `operator_id` bigint NULL DEFAULT NULL,
  `operator_role` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `comment` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_app`(`app_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '申请流程日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of application_logs
-- ----------------------------

-- ----------------------------
-- Table structure for applications
-- ----------------------------
DROP TABLE IF EXISTS `applications`;
CREATE TABLE `applications`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'A1001',
  `student_id` bigint NOT NULL,
  `job_id` bigint NOT NULL,
  `slots` json NULL COMMENT '[\"周一晚\",\"周二晚\"] 可到岗时段',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `attach_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审核' COMMENT '待审核/已通过/未通过/已撤回',
  `dept_comment` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ext` json NULL,
  `deleted` tinyint NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_stu_job`(`student_id` ASC, `job_id` ASC) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `idx_job_status`(`job_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位申请' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of applications
-- ----------------------------
INSERT INTO `applications` VALUES (1, 'A1791385987522622', 3, 1, '[\"周一晚\", \"周五晚\"]', '', NULL, '待审核', NULL, NULL, 0, '2026-10-07 23:13:07', '2026-10-07 23:13:07');

-- ----------------------------
-- Table structure for attendances
-- ----------------------------
DROP TABLE IF EXISTS `attendances`;
CREATE TABLE `attendances`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NULL DEFAULT NULL,
  `job_id` bigint NULL DEFAULT NULL,
  `work_date` date NULL DEFAULT NULL,
  `clock_in` datetime NULL DEFAULT NULL,
  `clock_out` datetime NULL DEFAULT NULL,
  `hours` decimal(4, 1) NULL DEFAULT NULL COMMENT '工时',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '正常/迟到/缺勤/请假',
  `lat` double NULL DEFAULT NULL,
  `lng` double NULL DEFAULT NULL COMMENT '打卡定位',
  `confirm_by` bigint NULL DEFAULT NULL,
  `confirmed` tinyint NULL DEFAULT 0 COMMENT '部门确认后锁定',
  `ext` json NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sj_d`(`student_id` ASC, `job_id` ASC, `work_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考勤记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attendances
-- ----------------------------

-- ----------------------------
-- Table structure for departments
-- ----------------------------
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名',
  `contact` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `tel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `place` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '工作地点',
  `capacity` int NULL DEFAULT 10 COMMENT '可容纳人数',
  `ext` json NULL COMMENT '扩展',
  `deleted` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用工部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of departments
-- ----------------------------
INSERT INTO `departments` VALUES (1, '图书馆流通部', '王老师', '62201111', '图书馆一楼', 10, NULL, 0);
INSERT INTO `departments` VALUES (2, '教务处', '赵老师', '62202222', '行政楼302', 10, NULL, 0);
INSERT INTO `departments` VALUES (3, '实验中心', '李老师', '62203333', '实验楼B区', 10, NULL, 0);

-- ----------------------------
-- Table structure for jobs
-- ----------------------------
DROP TABLE IF EXISTS `jobs`;
CREATE TABLE `jobs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'J01',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `dept_id` bigint NOT NULL,
  `headcount` int NOT NULL COMMENT '名额',
  `pay_tier` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'A/B/C 档',
  `pay_amount` decimal(8, 2) NULL DEFAULT NULL COMMENT '发布时快照元/时',
  `work_time` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '班次时间',
  `place` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '工作内容',
  `requirement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '要求',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审核' COMMENT '待审核/招募中/已截止/驳回',
  `reject_reason` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `view_count` int NULL DEFAULT 0,
  `ext` json NULL,
  `deleted` tinyint NULL DEFAULT 0,
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lat` double NULL DEFAULT NULL COMMENT '岗点纬度',
  `lng` double NULL DEFAULT NULL COMMENT '岗点经度',
  `radius_m` int NULL DEFAULT 50 COMMENT '打卡半径(米)',
  `recruit_slots` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '周一晚,周二晚,周三晚,周四晚,周五晚' COMMENT '可到岗时段,逗号分隔',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `idx_dept_status`(`dept_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '勤工助学岗位' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of jobs
-- ----------------------------
INSERT INTO `jobs` VALUES (1, 'J01', '图书馆流通岗', 1, 10, 'A', 25.00, '周一至五 18:00-21:00', '图书馆一楼', '借还办理、上架理架、晚间值班', '责任心强，新生可报，需参加1h培训', '招募中', NULL, 45, NULL, 0, NULL, '2026-10-07 20:20:48', '2026-10-07 20:20:48', NULL, NULL, 50, '周一晚,周二晚,周三晚,周四晚,周五晚');
INSERT INTO `jobs` VALUES (2, 'J02', '学院办公室助理', 2, 5, 'B', 28.00, '工作日 14:00-17:00', '行政楼302', '收发文件、整理表格、接待来访', '熟练Office，保密意识强', '招募中', NULL, 5, NULL, 0, NULL, '2026-10-07 20:20:48', '2026-10-07 20:20:48', NULL, NULL, 50, '周一晚,周二晚,周三晚,周四晚,周五晚');
INSERT INTO `jobs` VALUES (3, 'J03', '实验室助管', 3, 4, 'C', 30.00, '按实验排班', '实验楼B区', '实验准备、器材管理、安全值班', '理工科优先，需安全培训', '招募中', NULL, 13, NULL, 0, NULL, '2026-10-07 20:20:48', '2026-10-07 20:20:48', NULL, NULL, 50, '周一晚,周二晚,周三晚,周四晚,周五晚');

-- ----------------------------
-- Table structure for leaves
-- ----------------------------
DROP TABLE IF EXISTS `leaves`;
CREATE TABLE `leaves`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NULL DEFAULT NULL,
  `job_id` bigint NULL DEFAULT NULL,
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '事假/病假/考试周',
  `start_date` date NULL DEFAULT NULL,
  `end_date` date NULL DEFAULT NULL,
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `proof_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审批',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '请假' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of leaves
-- ----------------------------

-- ----------------------------
-- Table structure for notices
-- ----------------------------
DROP TABLE IF EXISTS `notices`;
CREATE TABLE `notices`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `scope` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'all',
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notices
-- ----------------------------
INSERT INTO `notices` VALUES (1, '考勤提醒', '今晚18:00班次，请提前10分钟到岗', 'all', NULL, '2026-10-07 20:20:48');
INSERT INTO `notices` VALUES (2, '工资发放', '8月工资已发放，请查收银行卡', 'all', NULL, '2026-10-07 20:20:48');

-- ----------------------------
-- Table structure for publicities
-- ----------------------------
DROP TABLE IF EXISTS `publicities`;
CREATE TABLE `publicities`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `period` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公示期',
  `content` json NULL COMMENT '脱敏名单等',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '公示中',
  `create_by` bigint NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公示' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of publicities
-- ----------------------------
INSERT INTO `publicities` VALUES (1, '第12期录用公示', '9.25-9.27', '{\"jobs\": [{\"names\": \"张*晓、李*晨等6人\", \"title\": \"图书馆流通岗\"}]}', '公示中', NULL, '2026-10-07 20:20:48');

-- ----------------------------
-- Table structure for read_receipts
-- ----------------------------
DROP TABLE IF EXISTS `read_receipts`;
CREATE TABLE `read_receipts`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `publicity_id` bigint NULL DEFAULT NULL,
  `student_id` bigint NULL DEFAULT NULL,
  `read_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_p_s`(`publicity_id` ASC, `student_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公示已读回执' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of read_receipts
-- ----------------------------

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NULL DEFAULT NULL,
  `job_id` bigint NULL DEFAULT NULL,
  `dept_id` bigint NULL DEFAULT NULL,
  `stars` tinyint NULL DEFAULT NULL,
  `tags` json NULL,
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门评价学生' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reviews
-- ----------------------------

-- ----------------------------
-- Table structure for salaries
-- ----------------------------
DROP TABLE IF EXISTS `salaries`;
CREATE TABLE `salaries`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NULL DEFAULT NULL,
  `job_id` bigint NULL DEFAULT NULL,
  `month` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '2026-09',
  `hours` decimal(6, 1) NULL DEFAULT NULL,
  `rate` decimal(8, 2) NULL DEFAULT NULL,
  `gross` decimal(10, 2) NULL DEFAULT NULL,
  `deduct` decimal(10, 2) NULL DEFAULT 0.00,
  `net` decimal(10, 2) NULL DEFAULT NULL,
  `bank_tail` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '银行卡尾号',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待确认',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sj_m`(`student_id` ASC, `job_id` ASC, `month` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工资表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of salaries
-- ----------------------------
INSERT INTO `salaries` VALUES (1, 3, 1, '2026-09', 32.0, 25.00, 800.00, 0.00, 800.00, '6632', '待确认');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `ckey` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cval` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ckey`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('attend.absent_max', '3', '缺勤取消资格红线');
INSERT INTO `sys_config` VALUES ('attend.late_min', '15', '迟到阈值(分钟)');
INSERT INTO `sys_config` VALUES ('attend.radius_m', '50', '打卡半径(米)');
INSERT INTO `sys_config` VALUES ('pay.A', '25', 'A档 元/时');
INSERT INTO `sys_config` VALUES ('pay.B', '28', 'B档');
INSERT INTO `sys_config` VALUES ('pay.C', '30', 'C档');
INSERT INTO `sys_config` VALUES ('timetable.require_sync', '1', '无课表不可申请岗位');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dict_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'college/job_status/apply_status/role/aid_level/pay_tier',
  `dict_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '编码',
  `dict_label` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '显示名',
  `sort_no` int NULL DEFAULT 0,
  `enabled` tinyint NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type_code`(`dict_type` ASC, `dict_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, 'role', 'student', '学生', 0, 1);
INSERT INTO `sys_dict` VALUES (2, 'role', 'employer', '用工部门', 0, 1);
INSERT INTO `sys_dict` VALUES (3, 'role', 'admin', '管理员', 0, 1);
INSERT INTO `sys_dict` VALUES (4, 'job_status', '待审核', '待审核', 0, 1);
INSERT INTO `sys_dict` VALUES (5, 'job_status', '招募中', '招募中', 0, 1);
INSERT INTO `sys_dict` VALUES (6, 'job_status', '已截止', '已截止', 0, 1);
INSERT INTO `sys_dict` VALUES (7, 'apply_status', '待审核', '待审核', 0, 1);
INSERT INTO `sys_dict` VALUES (8, 'apply_status', '已通过', '已通过', 0, 1);
INSERT INTO `sys_dict` VALUES (9, 'apply_status', '未通过', '未通过', 0, 1);
INSERT INTO `sys_dict` VALUES (10, 'pay_tier', 'A', '25元/h', 0, 1);
INSERT INTO `sys_dict` VALUES (11, 'pay_tier', 'B', '28元/h', 0, 1);
INSERT INTO `sys_dict` VALUES (12, 'pay_tier', 'C', '30元/h', 0, 1);
INSERT INTO `sys_dict` VALUES (13, 'aid_level', '一档', '一档', 0, 1);
INSERT INTO `sys_dict` VALUES (14, 'aid_level', '二档', '二档', 0, 1);
INSERT INTO `sys_dict` VALUES (15, 'aid_level', '三档', '三档', 0, 1);

-- ----------------------------
-- Table structure for timetables
-- ----------------------------
DROP TABLE IF EXISTS `timetables`;
CREATE TABLE `timetables`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `semester` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '2026-秋',
  `weekday` tinyint NULL DEFAULT NULL COMMENT '1=周一..7=周日',
  `slot` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '1-2节/3-4节/晚',
  `course` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程名',
  `source` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'manual' COMMENT 'jw教务/manual手动/import导入',
  `weeks` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1-16周' COMMENT '如 1-8周/单周/1-16周',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sem_day_slot`(`student_id` ASC, `semester` ASC, `weekday` ASC, `slot` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生课表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of timetables
-- ----------------------------
INSERT INTO `timetables` VALUES (1, 2, '2026-秋', 1, '1-2节', '高等数学', 'jw', '1-16周');
INSERT INTO `timetables` VALUES (2, 2, '2026-秋', 3, '晚', '数据库', 'jw', '1-16周');
INSERT INTO `timetables` VALUES (3, 2, '2026-秋', 1, '3-4节', 'js', 'jw', '1-16周');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sno` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号/工号，唯一',
  `password_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(hash)',
  `role` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'student/employer/admin',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `college` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学院(字典或文本)',
  `grade` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `bank_card` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '银行卡号',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门账号所属部门',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审核' COMMENT '正常/待审核/黑名单',
  `ext` json NULL COMMENT '扩展：头像/证书等',
  `deleted` tinyint NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `sno`(`sno` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_dept`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (2, '2023307120', '123456', 'student', '张奇峰', '电子信息工程学院', '大三', '18046783883', NULL, NULL, '正常', NULL, 0, '2026-10-07 20:20:48', '2026-10-07 20:20:48');
INSERT INTO `users` VALUES (3, '2022001234', '123456', 'student', '张晓', '信息学院', '大二', '13800001111', NULL, NULL, '正常', NULL, 0, '2026-10-07 20:20:48', '2026-10-07 20:20:48');
INSERT INTO `users` VALUES (4, '2022002002', '123456', 'student', '李晨', '商学院', '大一', '13800002222', NULL, NULL, '正常', NULL, 0, '2026-10-07 20:20:48', '2026-10-07 20:20:48');
INSERT INTO `users` VALUES (5, 'T001', '123456', 'employer', '王老师', NULL, NULL, NULL, NULL, 1, '正常', NULL, 0, '2026-10-07 20:20:48', '2026-10-07 20:20:48');
INSERT INTO `users` VALUES (6, 'A001', '123456', 'admin', '学工处', NULL, NULL, NULL, NULL, NULL, '正常', NULL, 0, '2026-10-07 20:20:48', '2026-10-07 20:20:48');

SET FOREIGN_KEY_CHECKS = 1;
