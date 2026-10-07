-- 打卡定点：给岗位加经纬度+半径（米），部门发布/编辑岗位时设定
USE work_study;

ALTER TABLE jobs
  ADD COLUMN lat DOUBLE COMMENT '岗点纬度',
  ADD COLUMN lng DOUBLE COMMENT '岗点经度',
  ADD COLUMN radius_m INT DEFAULT 50 COMMENT '打卡半径(米)';

-- 示例：图书馆一楼设一个演示坐标（请换成你岗点的真实经纬度）
-- 获取方式：高德/百度地图选点，或手机指南针App
-- UPDATE jobs SET lat = 39.9042, lng = 116.4074, radius_m = 50 WHERE code = 'J01';

-- 未设坐标的岗位：允许打卡（演示期兼容），设了坐标才强制范围校验
SELECT code, title, lat, lng, radius_m FROM jobs;
