-- ============================================================
-- 勤工助学岗位管理系统 · 数据库设计（MySQL 8.0 / utf8mb4）
-- 设计原则：字典驱动枚举 + 配置表 + 软删除 + ext JSON 扩展
-- 新功能优先插入字典/配置，不动旧表；业务新字段先进 ext，稳定再转正列
-- ============================================================
CREATE DATABASE IF NOT EXISTS work_study DEFAULT CHARACTER SET utf8mb4;
USE work_study;

DROP TABLE IF EXISTS admin_logs, reviews, notices, read_receipts, publicities,
 aid_applications, appeals, salaries, leaves, attendances, timetables,
 application_logs, applications, jobs, departments, sys_config, sys_dict, users;

-- ---------- 1. 基础与字典 ----------
-- 字典表：学院 / 角色 / 岗位类型 / 各状态 / 档位，全走这里，加枚举只插行
CREATE TABLE sys_dict (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dict_type VARCHAR(64) NOT NULL COMMENT 'college/job_status/apply_status/role/aid_level/pay_tier',
  dict_code VARCHAR(64) NOT NULL COMMENT '编码',
  dict_label VARCHAR(128) NOT NULL COMMENT '显示名',
  sort_no INT DEFAULT 0,
  enabled TINYINT DEFAULT 1,
  UNIQUE KEY uk_type_code (dict_type, dict_code)
) COMMENT='字典表';

-- 系统配置：薪资档、考勤阈值、同步开关，改功能不改代码
CREATE TABLE sys_config (
  ckey VARCHAR(64) PRIMARY KEY,
  cval VARCHAR(512) NOT NULL,
  remark VARCHAR(128)
) COMMENT='系统配置';

-- ---------- 2. 用户与组织 ----------
CREATE TABLE departments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128) NOT NULL COMMENT '部门名',
  contact VARCHAR(64) COMMENT '联系人',
  tel VARCHAR(32) COMMENT '电话',
  place VARCHAR(128) COMMENT '工作地点',
  capacity INT DEFAULT 10 COMMENT '可容纳人数',
  ext JSON COMMENT '扩展',
  deleted TINYINT DEFAULT 0
) COMMENT='用工部门';

-- 三端统一：学生 / 部门账号 / 管理员，用 role 区分，加角色不用新表
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  sno VARCHAR(32) UNIQUE COMMENT '学号/工号，唯一',
  password_hash VARCHAR(128) NOT NULL COMMENT '密码(hash)',
  role VARCHAR(16) NOT NULL COMMENT 'student/employer/admin',
  name VARCHAR(64) NOT NULL,
  college VARCHAR(64) COMMENT '学院(字典或文本)',
  grade VARCHAR(16) COMMENT '年级',
  phone VARCHAR(16),
  bank_card VARCHAR(32) COMMENT '银行卡号',
  dept_id BIGINT COMMENT '部门账号所属部门',
  status VARCHAR(16) DEFAULT '待审核' COMMENT '正常/待审核/黑名单',
  ext JSON COMMENT '扩展：头像/证书等',
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_role (role), INDEX idx_dept (dept_id)
) COMMENT='用户';

-- ---------- 3. 岗位 ----------
-- pay_tier 只存档位编码，金额走 sys_config；pay_amount 是发布时快照，调薪不影响历史
CREATE TABLE jobs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(32) UNIQUE COMMENT 'J01',
  title VARCHAR(64) NOT NULL,
  dept_id BIGINT NOT NULL,
  headcount INT NOT NULL COMMENT '名额',
  pay_tier VARCHAR(16) COMMENT 'A/B/C 档',
  pay_amount DECIMAL(8,2) COMMENT '发布时快照元/时',
  work_time VARCHAR(128) COMMENT '班次时间',
  place VARCHAR(128),
  content TEXT COMMENT '工作内容',
  requirement TEXT COMMENT '要求',
  status VARCHAR(16) DEFAULT '待审核' COMMENT '待审核/招募中/已截止/驳回',
  reject_reason VARCHAR(256),
  view_count INT DEFAULT 0,
  ext JSON,
  deleted TINYINT DEFAULT 0,
  create_by BIGINT, create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_dept_status (dept_id,status)
) COMMENT='勤工助学岗位';

-- ---------- 4. 申请（课表闭环拦截点） ----------
CREATE TABLE applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(32) UNIQUE COMMENT 'A1001',
  student_id BIGINT NOT NULL, job_id BIGINT NOT NULL,
  slots JSON COMMENT '["周一晚","周二晚"] 可到岗时段',
  remark VARCHAR(200), attach_url VARCHAR(256),
  status VARCHAR(16) DEFAULT '待审核' COMMENT '待审核/已通过/未通过/已撤回',
  dept_comment VARCHAR(256),
  ext JSON,
  deleted TINYINT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_stu_job (student_id, job_id),
  INDEX idx_job_status (job_id,status)
) COMMENT='岗位申请';

-- 时间线：提交→部门初审→公示→上岗，每节点留痕
CREATE TABLE application_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  app_id BIGINT NOT NULL,
  action VARCHAR(32) COMMENT 'submit/dept_approve/dept_reject/publicity/onboard',
  operator_id BIGINT, operator_role VARCHAR(16),
  comment VARCHAR(256),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_app (app_id)
) COMMENT='申请流程日志';

-- ---------- 5. 课表闭环核心 ----------
CREATE TABLE timetables (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT NOT NULL,
  semester VARCHAR(16) COMMENT '2026-秋',
  weekday TINYINT COMMENT '1=周一..7=周日',
  slot VARCHAR(16) COMMENT '1-2节/3-4节/晚',
  course VARCHAR(64) COMMENT '课程名',
  source VARCHAR(16) DEFAULT 'manual' COMMENT 'jw教务/manual手动/import导入',
  UNIQUE KEY uk_sem_day_slot (student_id,semester,weekday,slot)
) COMMENT='学生课表';

-- ---------- 6. 考勤与请假 ----------
CREATE TABLE attendances (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT, job_id BIGINT, work_date DATE,
  clock_in DATETIME, clock_out DATETIME,
  hours DECIMAL(4,1) COMMENT '工时',
  status VARCHAR(16) COMMENT '正常/迟到/缺勤/请假',
  lat DOUBLE, lng DOUBLE COMMENT '打卡定位',
  confirm_by BIGINT, confirmed TINYINT DEFAULT 0 COMMENT '部门确认后锁定',
  ext JSON,
  UNIQUE KEY uk_sj_d (student_id,job_id,work_date)
) COMMENT='考勤记录';

CREATE TABLE leaves (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT, job_id BIGINT,
  type VARCHAR(16) COMMENT '事假/病假/考试周',
  start_date DATE, end_date DATE,
  reason VARCHAR(500), proof_url VARCHAR(256),
  status VARCHAR(16) DEFAULT '待审批',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='请假';

-- ---------- 7. 工资 ----------
-- 公式 net = hours*rate - deduct；状态机：待确认→已复核→已发放(锁死)/冲正
CREATE TABLE salaries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT, job_id BIGINT,
  month VARCHAR(8) COMMENT '2026-09',
  hours DECIMAL(6,1), rate DECIMAL(8,2),
  gross DECIMAL(10,2), deduct DECIMAL(10,2) DEFAULT 0, net DECIMAL(10,2),
  bank_tail VARCHAR(8) COMMENT '银行卡尾号',
  status VARCHAR(16) DEFAULT '待确认',
  UNIQUE KEY uk_sj_m (student_id,job_id,month)
) COMMENT='工资表';

-- 工单统一：工资申诉/公示异议/考勤申诉全走这里，type 区分，后续加类型只加字典
CREATE TABLE appeals (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(32) UNIQUE COMMENT 'S1001',
  type VARCHAR(32) COMMENT 'salary/publicity/attendance',
  ref_id BIGINT, student_id BIGINT,
  content TEXT, status VARCHAR(16) DEFAULT '待处理', reply TEXT,
  deadline DATE COMMENT 'SLA 截止', create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='申诉工单';

-- ---------- 8. 困难认定 ----------
CREATE TABLE aid_applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT, level VARCHAR(16) COMMENT '一档/二档/三档',
  income DECIMAL(10,2) COMMENT '家庭人均年收入',
  reason VARCHAR(500), files JSON COMMENT '["身份证","贫困证明","承诺书"]',
  status VARCHAR(16) DEFAULT '待审核',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='困难认定';

-- ---------- 9. 公示与已读回执 ----------
CREATE TABLE publicities (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(128), period VARCHAR(64) COMMENT '公示期',
  content JSON COMMENT '脱敏名单等',
  status VARCHAR(16) DEFAULT '公示中',
  create_by BIGINT, create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='公示';

-- 回执：必须已读才可上岗打卡
CREATE TABLE read_receipts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  publicity_id BIGINT, student_id BIGINT,
  read_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_p_s (publicity_id,student_id)
) COMMENT='公示已读回执';

-- ---------- 10. 评价 / 通知 / 审计 ----------
CREATE TABLE reviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT, job_id BIGINT, dept_id BIGINT,
  stars TINYINT, tags JSON, comment VARCHAR(500),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='部门评价学生';

CREATE TABLE notices (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(128), body TEXT, scope VARCHAR(16) DEFAULT 'all',
  create_by BIGINT, create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='通知公告';

CREATE TABLE admin_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  operator_id BIGINT, action VARCHAR(64),
  target VARCHAR(128), ip VARCHAR(32),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='管理员操作审计';

-- ---------- 种子：配置 ----------
INSERT INTO sys_config(ckey,cval,remark) VALUES
('pay.A','25','A档 元/时'),
('pay.B','28','B档'),
('pay.C','30','C档'),
('attend.radius_m','50','打卡半径(米)'),
('attend.late_min','15','迟到阈值(分钟)'),
('attend.absent_max','3','缺勤取消资格红线'),
('timetable.require_sync','1','无课表不可申请岗位');