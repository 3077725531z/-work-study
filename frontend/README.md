# 前端三端对照表（全部按 prototype/ 实现，后端 http://localhost:8080）

| 端 | 目录 | 端口 | UI | 原型 |
|---|---|---|---|---|
| 学生移动 | frontend/student-h5 | 5171 | Vant | prototype/student-mobile.html P01-P15 |
| 管理网页 | frontend/admin-web | 5172 | Element-Plus | prototype/admin-web.html A01-A12 |
| 部门网页 | frontend/employer-web | 5173 | Element-Plus | prototype/employer-web.html E01-E10 |

启动：
```bash
cd frontend/student-h5 && npm i && npm run dev
cd frontend/admin-web && npm i && npm run dev
cd frontend/employer-web && npm i && npm run dev
```

student-h5 已生成：package/vite/api(request+index)/router(P01-P15)/App+Tabbar/main/index.html/views-ApplyForm(完整示例)+Login(占位模板)。
其余 13 个 views 按 ApplyForm/Login 模板照抄原型字段即可，接口都在 src/api/index.js。

admin-web / employer-web 见各自目录的 src/api + src/router + views/（已生成 Discipline 示例 + 占位模板）。
