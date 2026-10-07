-- 勤工助学演示数据：一键导入，重复执行不报错
USE work_study;

-- 字典
INSERT IGNORE INTO sys_dict (dict_type, dict_code, dict_label) VALUES
('role','student','学生'),
('role','employer','用工部门'),
('role','admin','管理员'),
('job_status','待审核','待审核'),
('job_status','招募中','招募中'),
('job_status','已截止','已截止'),
('apply_status','待审核','待审核'),
('apply_status','已通过','已通过'),
('apply_status','未通过','未通过'),
('pay_tier','A','25元/h'),
('pay_tier','B','28元/h'),
('pay_tier','C','30元/h'),
('aid_level','一档','一档'),
('aid_level','二档','二档'),
('aid_level','三档','三档');

-- 配置
INSERT INTO sys_config (ckey, cval, remark) VALUES
('pay.A','25','A档 元/时'),
('pay.B','28','B档'),
('pay.C','30','C档'),
('attend.radius_m','50','打卡半径(米)'),
('attend.late_min','15','迟到阈值(分钟)'),
('attend.absent_max','3','缺勤取消资格红线'),
('timetable.require_sync','1','无课表不可申请岗位')
ON DUPLICATE KEY UPDATE cval = VALUES(cval);

-- 部门
INSERT IGNORE INTO departments (id, name, contact, tel, place) VALUES
(1,'图书馆流通部','王老师','62201111','图书馆一楼'),
(2,'教务处','赵老师','62202222','行政楼302'),
(3,'实验中心','李老师','62203333','实验楼B区');

-- 账号：密码全部 123456，status=正常 可直接登录
INSERT IGNORE INTO users (sno, password_hash, role, name, college, grade, phone, status) VALUES
('2023307120','123456','student','张奇峰','电子信息工程学院','大三','18046783883','正常'),
('2022001234','123456','student','张晓','信息学院','大二','13800001111','正常'),
('2022002002','123456','student','李晨','商学院','大一','13800002222','正常');
INSERT IGNORE INTO users (sno, password_hash, role, name, dept_id, status) VALUES
('T001','123456','employer','王老师',1,'正常');
INSERT IGNORE INTO users (sno, password_hash, role, name, status) VALUES
('A001','123456','admin','学工处','正常');

-- 岗位
INSERT IGNORE INTO jobs (code, title, dept_id, headcount, pay_tier, pay_amount, work_time, place, content, requirement, status) VALUES
('J01','图书馆流通岗',1,10,'A',25.00,'周一至五 18:00-21:00','图书馆一楼','借还办理、上架理架、晚间值班','责任心强，新生可报，需参加1h培训','招募中'),
('J02','学院办公室助理',2,5,'B',28.00,'工作日 14:00-17:00','行政楼302','收发文件、整理表格、接待来访','熟练Office，保密意识强','招募中'),
('J03','实验室助管',3,4,'C',30.00,'按实验排班','实验楼B区','实验准备、器材管理、安全值班','理工科优先，需安全培训','招募中');

-- 课表：张奇峰(按学号查id)第6周有课，用于演示冲突拦截
INSERT IGNORE INTO timetables (student_id, semester, weekday, slot, course, source)
SELECT id, '2026-秋', 1, '1-2节', '高等数学', 'jw' FROM users WHERE sno = '2023307120';
INSERT IGNORE INTO timetables (student_id, semester, weekday, slot, course, source)
SELECT id, '2026-秋', 3, '晚', '数据库', 'jw' FROM users WHERE sno = '2023307120';

-- 工资示例：张晓9月 32h×25=800
INSERT IGNORE INTO salaries (student_id, job_id, month, hours, rate, gross, deduct, net, bank_tail, status)
SELECT u.id, 1, '2026-09', 32, 25.00, 800.00, 0, 800.00, '6632', '待确认'
FROM users u WHERE u.sno = '2022001234';

-- 公示示例
INSERT IGNORE INTO publicities (id, title, period, content, status) VALUES
(1,'第12期录用公示','9.25-9.27','{"jobs":[{"title":"图书馆流通岗","names":"张*晓、李*晨等6人"}]}','公示中');

-- 通知示例
INSERT IGNORE INTO notices (title, body, scope) VALUES
('考勤提醒','今晚18:00班次，请提前10分钟到岗','all'),
('工资发放','8月工资已发放，请查收银行卡','all');
