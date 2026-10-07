import { createRouter, createWebHistory } from 'vue-router'

// 与 prototype/admin-web.html A01-A12 一一对应
const routes = [
  { path: '/', redirect: '/dash' },
  { path: '/dash', component: () => import('../views/Dash.vue'), meta: { title: 'A02 总览' } },
  { path: '/job', component: () => import('../views/JobCheck.vue'), meta: { title: 'A03 岗位审核' } },
  { path: '/stu', component: () => import('../views/Stu.vue'), meta: { title: 'A04 学生管理' } },
  { path: '/apply', component: () => import('../views/Apply.vue'), meta: { title: 'A05 申请抽查' } },
  { path: '/aid', component: () => import('../views/Aid.vue'), meta: { title: 'A06 认定复审' } },
  { path: '/attend', component: () => import('../views/Attend.vue'), meta: { title: 'A07 考勤监控' } },
  { path: '/salary', component: () => import('../views/Salary.vue'), meta: { title: 'A08 工资发放' } },
  { path: '/appeal', component: () => import('../views/Appeal.vue'), meta: { title: 'A09 申诉' } },
  { path: '/pub', component: () => import('../views/Pub.vue'), meta: { title: 'A10 公示' } },
  { path: '/report', component: () => import('../views/Report.vue'), meta: { title: 'A11 统计' } },
  { path: '/sys', component: () => import('../views/Sys.vue'), meta: { title: 'A12 设置' } }
]

export default createRouter({ history: createWebHistory(), routes })
