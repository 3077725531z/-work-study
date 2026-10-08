# 勤工助学岗位管理系统 · 进度备忘（2026-10-08 更新）

> 下次开工先读本文件，从「下次从哪开始」继续。

---

## 1. 项目全景

| 层 | 位置 | 端口/说明 |
|---|---|---|
| 后端 Spring Boot 3.2 + MyBatis-Plus 3.5.7 + MySQL | `backend/` | 8080，Swagger `http://localhost:8080/swagger-ui.html` |
| 学生移动端 Vue3 + Vant | `frontend/student-h5/` | 5171，15 页，Hallmark 化 |
| 管理网页 Vue3 + Element-Plus | `frontend/admin-web/` | 5172，12 页，Hallmark 化 + 命令面板 |
| 部门网页 Vue3 + Element-Plus | `frontend/employer-web/` | 5173，7 页全联调 |
| 原型 | `prototype/` | 三端 HTML 原型 + `tokens.css` |
| 数据库脚本 | `db/` | `schema.sql` + `seed.sql` + 3 个 migrate |
| 设计规范 | `.agents/skills/hallmark/SKILL.md` | Hallmark；轮换记录 `.hallmark/log.json` |

一键三端：根目录 `npm run dev`（concurrently）。数据库 `work_study`，账号密码见 §6。

---

## 2. 后端已做

### 2.1 核心模块（全部真实接口，无桩）

- 认证 `AuthController/AuthService`：登录返回 `token/uid/sno/role/name`；注册校验；`UserController` 用户分页 + 改密接口 `PUT /api/users/{id}/password`。
- 岗位 `JobController`：发布/分页（支持 key/status/sort）/详情浏览+1/通过上架/驳回/下架 `PUT /api/jobs/{id}/offline`；详情含 `onboardCount`（在岗人数）。
- 申请 `ApplicationController/ApplyService/ApplyQueryController`：提交≥2 时段、无课表 400、查重+课表冲突 403、编号 `A+毫秒+3位随机`、`slots` JSON 数组；审核通过自动排班；撤回。
- 课表 `TimetableController/TimetableDeleteController/TimetableService`：5 段口径、周次 expand、冲突检测（含在岗）、在岗排班不可删。
- 考勤 `AttendanceController/AttendanceQueryController/LeaveController`：打卡三重门（申请通过+haversine 半径+GPS 定位）、confirm 锁定、请假。
- 工资 `SalaryController/SalaryQueryController/SalaryService`：`net=hours×rate−deduct`、复核/发放状态机。
- 认定 `AidController/AidQueryController`：申请定档、复审。
- 申诉 `AppealController/AppealSubmitController`：提交/列表/回复。
- 公示 `PublicityController`：发布/列表/已读回执。
- 评价 `ReviewController/ReviewQueryController`：写入 + 学生只读。
- 系统 `SysConfigController`：配置读写。
- 文件 `FileController`：上传（jpg/png/pdf≤5MB）、`/files/**` 静态映射。
- 管理 `AdminController`：`/api/admin/overview`（KPI+待办+预警）、`/api/admin/stats`（月发放趋势）。

### 2.2 安全改造（本次完成）

- `LoginUser`（ThreadLocal）+ `JwtInterceptor` + `WebConfig` 注册拦截器。
- 所有学生端列表/提交/操作接口**从 JWT token 取 uid**，不再信任前端传参，杜绝越权。
- 操作类接口（撤回/改密/确认打卡等）校验本人数据。
- 放行路径：`/api/auth/**`、`/api/jobs/**`、`/api/publicities`、`/api/files/upload`、`/api/sys-config/**`。

---

## 3. 学生端已做（15 页，Hallmark Cobalt/Workbench）

- 全局：`tokens.css`（OKLCH）+ `theme.css`（Vant 变量映射）+ `request.js`（Result 非 200 抛错、401 跳 `/login` history 模式）+ 路由守卫（token+uid 双检）。
- 登录/注册：Hallmark 全屏页，密码+确认、手机号正则。
- 大厅 JobHall：搜索+分类 chips+排序（推荐/最新/高薪，后端支持 sort）+下拉刷新。
- 详情 JobDetail：hero+三卡+悬浮申请条。
- 申请 ApplyForm：`recruitSlots` 动态选项+课表冲突预检+防连点。
- 进度 MyApplies：统计头+状态筛选+三节点时间线+撤回/重申。
- 考勤 Attend：当月统计+今日班次+双打卡（GPS）+显示定位坐标。
- 请假 Leave：类型/岗位/日期/≥10 字。
- 工资 Salary：汇总 hero+公式卡+三 Tab+两步确认。
- 认定 Aid：档位+≥30 字+附件上传（≤3 份）。
- 通知 Notices：已读回执（调后端 pubRead）+未读筛选+红点。
- 公示详情 PubDetail：名单解析+确认/异议。
- 课表 Timetable：5×7 网格+周次批量+教务同步按钮。
- 我的 Me：真实学号（取 `localStorage.sno`）+统计+修改密码弹窗（调后端接口）。
- 404 页面：路由通配符兜底。

---

## 4. 管理端已做（12 页，Hallmark 化 + 命令面板）

### 4.1 基础设施

- `theme.css`：Element Plus 变量映射到 Hallmark token，KPI 卡/hero/card/tag 等组件类。
- `App.vue`：深色侧边栏+退出登录+**命令面板**（Ctrl+K，实时搜索+高亮+键盘导航）。
- `Login.vue`：管理员登录页（校验 role=admin）+ 路由守卫。
- `request.js`：Result code 判断，失败正确抛错。

### 4.2 页面

- **总览 Dash**：4 KPI 卡（在招/在岗/待办/本月应发）+ 待办清单（带跳转）+ 考勤预警（出勤率+缺勤≥2次学生）+ 月发放趋势 ECharts。
- **岗位审核 JobCheck**：只显示"待审核"岗位，通过/驳回。
- **岗位管理 JobList**（新增）：全岗位列表+搜索+状态筛选+详情弹窗（含在岗人数）+上下架+**发布岗位**（表单含定位获取坐标、星期×时段勾选生成 recruitSlots）。
- **学生管理 Stu**：搜索+列表。
- **申请抽查 Apply**：确认通过/打回。
- **认定复审 Aid**：定档/降档。
- **考勤监控 Attend**：打卡记录+确认状态。
- **工资发放 Salary**：复核/发放状态机。
- **申诉工单 Appeal**：回复弹窗。
- **公示管理 Pub**：发布表单+历史列表。
- **统计报表 Report**：ECharts 柱状图。
- **系统设置 Sys**：配置读写弹窗。

---

## 5. 部门端已做

首页、发布（名称/人数/地点/薪资档联动/可到岗日×段勾选+组合预览/内容/要求）、我的岗位、审核（含冲突禁用）、考勤确认、工时复核、中心。

---

## 6. 数据库迁移（按顺序执行过）

1. `db/schema.sql` → `db/seed.sql`（3 部门/3 学生+T001/A001，密码全 123456；3 岗）
2. `db/migrate_attend_geo.sql`（jobs 加 lat/lng/radius_m）
3. `db/migrate_timetable_weeks.sql`（timetables 加 weeks）
4. `db/migrate_job_slots.sql`（jobs 加 recruit_slots）
5. 脏数据修复：`applications.slots` 非 JSON 行包成数组。

---

## 7. 演示账号与主流程

学生 `2023307120/123456`、管理 `A001/123456`、部门 `T001/123456`。
流程：填课表→大厅→详情→申请（冲突红字）→部门/管理审核→公示确认→打卡（定点半径）→工资确认→发放。

---

## 8. 下次从哪开始（优先级排序）

1. **部门发布页补岗点坐标输入**（lat/lng/radius_m）：管理端已用浏览器定位，部门端仍靠 SQL。
2. **教务对接预留**：`TimetableService.syncFromJw` 仍是桩，论文写"预留接口"。
3. **附件管理页**（可选）：管理端查看认定附件（`/files/**` 直链已通）。
4. **Excel 导入导出**（可选）：花名册/工资条 POI 导出，论文加分项。
5. **部门端 Hallmark 化**（可选）：管理端已做，部门端仍是裸表。

---

## 9. 已知坑（别再踩）

- `characterEncoding=utf8mb4` 驱动不认，用 `utf8`；库字符集保持 utf8mb4。
- Vant Picker 列必须对象数组 `{text,value}`。
- `ref(new Set())` computed 里用 `.value`，模板自动解包。
- `slots`/`recruitSlots` 列是 JSON，必须存 `["..."]`。
- 申请编号用毫秒全量+随机，`%100000` 易撞唯一键。
- `uploads` 用 `user.dir` 绝对化；前端附件走 8080 绝对地址；vite 加 `/files` 代理。
- `weeks` 解析：`expand` 支持 `1-8周/单周/9-16周/1,3,5周`。
- MySQL 表名：考勤表是 `attendances`（复数），不是 `attendance`。
- `jobs.dept_id` 不允许空，管理员发布需默认设值。
- JDK 17+（Spring Boot 3.2 要求），本机装 JDK 21 可用。
- MySQL root 密码重置：`mysqld --defaults-file="C:\ProgramData\MySQL\MySQL Server 8.4\my.ini" --skip-grant-tables`。
