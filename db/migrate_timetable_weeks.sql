-- 课表升级：周次 + 排班来源
USE work_study;

ALTER TABLE timetables
  ADD COLUMN weeks VARCHAR(64) DEFAULT '1-16周' COMMENT '如 1-8周/单周/1-16周';

-- 历史数据回填
UPDATE timetables SET weeks = '1-16周' WHERE weeks IS NULL OR weeks = '';

SELECT id, student_id, weekday, slot, course, weeks, source FROM timetables LIMIT 5;
