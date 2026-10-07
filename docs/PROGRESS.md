# 勤工助学岗位管理系统 · 进度备忘（2026-10-08）

> 下次开工先读本文件，从「下次从哪开始」继续。

---

## 1. 项目全景

| 层 | 位置 | 端口/说明 |
|---|---|---|
| 后端 Spring Boot 3.2 + MyBatis-Plus 3.5.7 + MySQL | `backend/` | 8080，Swagger `http://localhost:8080/swagger-ui.html` |
| 学生移动端 Vue3 + Vant | `frontend/student-h5/` | 5171，15 页 P01–P15 全联调 |
| 管理网页 Vue3 + Element-Plus | `frontend/admin-web/` | 5172，11 页 A02–A12 |
| 部门网页 Vue3 + Element-Plus | `frontend/employer-web/` | 5173，7 页全联调 |
| 原型 | `prototype/` | 三端 HTML 原型 + `tokens.css` |
| 数据库脚本 | `db/` | `schema.sql` + `seed.sql` + 3 个 migrate |
| 设计规范 | `.agents/skills/hallmark/SKILL.md` | Hallmark；轮换记录 `.hallmark/log.json` |

一键三端：根目录 `npm run dev`（concurrently）。数据库 `work_study`，账号密码见 §6。

---

## 2. 后端已做（控制器/服务 Witt：全部真实接口，无桩）

- 认证 `AuthController/AuthService`：登录返回 `token/uid/sno/role/name`；注册收 `password→passwordHash`，学号 6–20 位字母数字校验、重复学号 400；`UserController` 用户分页。
- 岗位 `JobController`：发布/分页/详情浏览+1/通过上架/驳回（必填原因）；字段含 `lat/lng/radius_m`（打卡定点）、`recruitSlots`（可到岗时段，部门发布时勾选日期×时段笛卡尔生成）。
- 申请 `ApplicationController/ApplyService/ApplyQueryController`：提交强制≥2 时段、无课表 400、先查重（studentId+jobId）再课表冲突 403、编号 `A+毫秒+3位随机`、`slots` 存合法 JSON 数组（曾因逗号串报 Invalid JSON，已修 `toJsonArray`）；审核通过自动排班（`[在岗]岗位名` source=work 写入课表）；撤回走 audit(false)。
- 课表 `TimetableController/TimetableDeleteController/TimetableService`：5 段口径（1-2/3-4/5-6/7-8/晚），周次 `expand`（1-16/单双/1-8/9-16/逗号列举），`findConflicts` 按星期+节次+周次重叠判（含在岗）；`hasCourses` 无课表不可申请；在岗排班不可删。
- 考勤 `AttendanceController/AttendanceQueryController/LeaveController`：打卡三重门（已通过申请＋岗点半径 haversine＋定位必填），`confirm` 锁定；列表接口支持 `studentId` 过滤；请假真写库。
- 工资 `SalaryController/SalaryQueryController/SalaryService`：`net=hours×rate−deduct` 核算、复核、发放状态机；列表支持 `studentId`。
- 认定 `AidController/AidQueryController`：申请定档、列表支持 `studentId`。
- 申诉 `AppealController/AppealSubmitController`：提交/列表（支持 `studentId`）/回复；实体 `notice/entity/Appeal` + `AppealMapper`（曾缺失致编译挂，已补）。
- 公示 `PublicityController`：发布/列表/已读回执。
- 评价 `ReviewController/ReviewQueryController`：写入 + 学生只读 `/reviews/mine`。
- 系统 `SysConfigController`：配置读写 + 字典（新增 `spring-boot-starter-jdbc`）。
- 文件 `FileController/FileStore/WebConfig`：`POST /api/files/upload`（jpg/png/pdf≤5MB）存启动目录 `uploads/`（相对路径转 `user.dir` 绝对路径），`/files/**` 静态映射。
- 通用：`Result/BizException/GlobalExceptionHandler`（异常打印堆栈并回显类名，不再吞成光秃“系统繁忙”）；`application.yml` 修 characterEncoding=utf8（utf8mb4 驱动不认）。

---

## 3. 学生端已做（15 页，Hallmark Cobalt/Workbench，全 token 化）

- 全局：`shared/tokens.css`（OKLCH）+ `src/theme.css`（Vant 变量映射 + 8 状态）+ `request.js`（Result 非 200 统一抛错、401 清 token 跳登录、断网 toast）+ 路由守卫（无 token 回登录、无 uid 回登录）+ 学号规则 6–20 位字母数字。
- 登录/注册：Hallmark 全屏页（无 Tabbar，`App.vue` 按 `meta.public` 隐藏），密码+确认、手机号正则、忘记密码指引。
- 大厅 JobHall：欢迎 hero + 搜索 + 分类 chips + 推荐/最新/高薪排序 + 招募中筛选 + 下拉刷新 + 岗位卡。
- 详情 JobDetail：hero（编号胶囊+薪资大字）+ 内容/要求/须知三卡 + 底部悬浮申请条（非招募中禁用）。
- 申请 ApplyForm：申请人自动带入、选项来自岗位 `recruitSlots`（动态）、卡片多选 + 计数、进页即拉课表做红色重叠预检（红框+课程名）、选中红色本地拦截、后端 403 仍为最终防线、防连点双锁、备注 50 字。
- 进度 MyApplies：统计头 + 状态筛选 + 岗位名回填 + 三节点时间线（待审 active=1、通过 active=2，配“非公示”小字说明）+ 撤回/重申/去打卡。
- 考勤 Attend：当月统计来自打卡记录（按 `workDate` 前缀过滤）+ 今日班次取自在岗岗+ 岗位选择器（仅已通过）+ 在岗列表（真名+班次，点击选中）+ 审核中分区 + 双打卡（GPS 上传）+ 记录确认态。
- 请假 Leave：类型/岗位（仅已通过真名）/起止日期/≥10 字校验。
- 工资 Salary：汇总 hero + 公式解释卡 + 账单/核算器/申诉三 Tab；确认改为两步（详情+4 条款+勾选才可点），调真接口改状态；申诉 `studentId: uid()`。
- 认定 Aid：档位选择器 + ≥30 字 + 附件上传（≤3 份，即传即存，至少 1 份才可提交）+ 历史（理由两行截断+展开、附件份数+新窗口查看，走 8080 绝对地址；`/files` 已加 vite 代理）。
- 通知 Notices：未读数 + 全部/未读筛选 + 红点 + 内容 JSON 转人话摘要 + 下拉刷新；修过 `readIds.value.has` 的 ref 解包 bug。
- 公示详情 PubDetail：名单解析渲染（JSON→逐行岗位名单）+ 规则卡 + 确认/异议双按钮（异议跳申诉并预选公示类型）。
- 课表 Timetable：5 段×7 天网格（红=有课/蓝=在岗/灰=可排）+ 点格加课 + 周次批量（全选/单双/1-8/9-16）+ 列表+删除（在岗不可删）+ 移动端弹窗适配（88vh 滚动、周次 4 列）。
- 我的 Me：SVG 卡通头像 + 数据库档案（姓名学号学院年级手机）+ 申请/考勤/累计工资统计 + 五入口 + 修改密码/退出（评价已移出本页）。

---

## 4. 管理/部门端已做

- 管理：总览、岗位审核、学生管理、申请抽查、认定复审、考勤监控、工资发放、申诉回复、公示发布、ECharts 统计、系统设置——全部真实接口。
- 部门：首页、发布（名称/人数/地点/薪资档联动单价/可到岗日×段勾选+组合预览/内容/要求/重置）、我的岗位、审核（含冲突禁用）、考勤确认、工时复核、中心。

---

## 5. 数据库迁移（按顺序执行过，未执行先执行）

1. `db/schema.sql` → `db/seed.sql`（3 部门/3 学生+T001/A001，密码全 123456；3 岗；在岗示例课表；工资；公示；通知）
2. `db/migrate_attend_geo.sql`（jobs 加 lat/lng/radius_m；给 J01 更新真实坐标）
3. `db/migrate_timetable_weeks.sql`（timetables 加 weeks，旧行回填 1-16 周）
4. `db/migrate_job_slots.sql`（jobs 加 recruit_slots，老岗默认周一到周五晚）
5. 脏数据修复：`applications.slots` 非 JSON 行包成数组；验证 `NOT JSON_VALID(slots)` 为 0。

---

## 6. 演示账号与主流程

学生 `2023307120/123456`、管理 `A001/123456`、部门 `T001/123456`。
流程：填课表→大厅→详情→申请（冲突红字）→部门审核→公示确认→打卡（定点半径）→工资确认（条款）→发放。

---

## 7. 下次从哪开始（优先级排序）

1. **部门发布页补岗点坐标输入**（lat/lng/radius_m 表单项）：定点打卡的最后一块拼图，现在只能靠 SQL 更新坐标。
2. **管理端 Hallmark 化**：学生端已 Cobalt/Workbench，管理须换 macrostructure+主题轴（记入 `.hallmark/log.json`），页面多为裸表。
3. **后端按 token 取 uid**：列表接口现靠前端传 `studentId`，正式应从 JWT 解析（加拦截器 + `LoginUser` 上下文），否则可越权看别人数据。
4. **申请 `slots` 历史兼容**：`roster` 已容忍旧逗号串；若还有老脏行跑 §5 第 5 条 SQL。
5. **教务对接预留**：`TimetableService.syncFromJw` 仍是桩，论文写“预留接口”即可；真要接需学校 VPN+账号。
6. **附件管理页**（可选）：管理端查看认定附件（`/files/**` 直链已通）。
7. **Excel 导入导出**（可选）：花名册/工资条 POI 导出，论文加分项。

---

## 8. 已知坑（别再踩）

- `characterEncoding=utf8mb4` 驱动不认，用 `utf8`；库字符集保持 utf8mb4。
- Vant Picker 列必须对象数组 `{text,value}`，字符串数组炸 `children`。
- `ref(new Set())` 在 computed 里用 `.value`，模板自动解包。
- `slots` 列是 JSON，必须存 `["..."]`。
- 申请编号用毫秒全量+随机，`%100000` 易撞唯一键。
- `uploads` 用 `user.dir` 绝对化；前端看附件走 8080 绝对地址；vite 加 `/files` 代理（改代理要重启 dev）。
- `weeks` 解析：`expand` 支持 `1-8周/单周/9-16周/1,3,5周`。
