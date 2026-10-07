-- 岗位可到岗时段：部门发布时勾选，不再写死周一到周五晚
USE work_study;

ALTER TABLE jobs
  ADD COLUMN recruit_slots VARCHAR(255) DEFAULT '周一晚,周二晚,周三晚,周四晚,周五晚' COMMENT '可到岗时段,逗号分隔';

UPDATE jobs SET recruit_slots = '周一晚,周二晚,周三晚,周四晚,周五晚' WHERE recruit_slots IS NULL OR recruit_slots = '';

SELECT code, title, recruit_slots FROM jobs;
