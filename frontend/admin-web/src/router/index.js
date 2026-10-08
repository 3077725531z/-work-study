import { createRouter, createWebHistory } from 'vue-router'

// 与 prototype/admin-web.html A01-A12 一一对应
const routes = [
  { path: '/', redirect: '/dash' },
  { path: '/login', component: () => import('../views/Login.vue'), meta: { title: '登录', public: true } },
  { path: '/dash', component: () => import('../views/Dash.vue'), meta: { title: '总览' } },
  { path: '/job', component: () => import('../views/JobCheck.vue'), meta: { title: '岗位审核' } },
  { path: '/jobs', component: () => import('../views/JobList.vue'), meta: { title: '岗位管理' } },
  { path: '/stu', component: () => import('../views/Stu.vue'), meta: { title: '学生管理' } },
  { path: '/apply', component: () => import('../views/Apply.vue'), meta: { title: '申请抽查' } },
  { path: '/aid', component: () => import('../views/Aid.vue'), meta: { title: '认定复审' } },
  { path: '/attend', component: () => import('../views/Attend.vue'), meta: { title: '考勤监控' } },
  { path: '/salary', component: () => import('../views/Salary.vue'), meta: { title: '工资发放' } },
  { path: '/appeal', component: () => import('../views/Appeal.vue'), meta: { title: '申诉工单' } },
  { path: '/pub', component: () => import('../views/Pub.vue'), meta: { title: '公示管理' } },
  { path: '/report', component: () => import('../views/Report.vue'), meta: { title: '统计报表' } },
  { path: '/sys', component: () => import('../views/Sys.vue'), meta: { title: '系统设置' } }
]

const router = createRouter({ history: createWebHistory(), routes })

// 未登录拦截
router.beforeEach((to) => {
  if (!to.meta.public && !localStorage.getItem('admin-token')) {
    return '/login'
  }
  document.title = `${to.meta.title || ''} · 勤工助学管理`
  return true
})

export default router
