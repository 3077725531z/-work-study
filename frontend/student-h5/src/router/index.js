import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/jobs' },
  { path: '/login', component: () => import('../views/Login.vue'), meta: { title: 'P01 登录', public: true } },
  { path: '/register', component: () => import('../views/Register.vue'), meta: { title: 'P02 注册', public: true } },
  { path: '/jobs', component: () => import('../views/JobHall.vue'), meta: { title: 'P03 岗位大厅' } },
  { path: '/jobs/:id', component: () => import('../views/JobDetail.vue'), meta: { title: 'P04 岗位详情' } },
  { path: '/apply', component: () => import('../views/ApplyForm.vue'), meta: { title: 'P05 提交申请' } },
  { path: '/applies', component: () => import('../views/MyApplies.vue'), meta: { title: 'P06 我的申请' } },
  { path: '/attend', component: () => import('../views/Attend.vue'), meta: { title: 'P07 考勤' } },
  { path: '/leave', component: () => import('../views/Leave.vue'), meta: { title: 'P08 请假' } },
  { path: '/salary', component: () => import('../views/Salary.vue'), meta: { title: 'P09 工资' } },
  { path: '/appeal', component: () => import('../views/Appeal.vue'), meta: { title: 'P10 申诉' } },
  { path: '/aid', component: () => import('../views/Aid.vue'), meta: { title: 'P11 认定' } },
  { path: '/me', component: () => import('../views/Me.vue'), meta: { title: 'P12 我的' } },
  { path: '/notices', component: () => import('../views/Notices.vue'), meta: { title: 'P13 消息' } },
  { path: '/pubs/:id', component: () => import('../views/PubDetail.vue'), meta: { title: 'P14 公示详情' } },
  { path: '/timetable', component: () => import('../views/Timetable.vue'), meta: { title: 'P15 课表' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 未登录拦截：无 token 一律回登录页
router.beforeEach((to) => {
  if (!to.meta.public && !localStorage.getItem('token')) {
    return '/login'
  }
  document.title = `${to.meta.title || ''} · 勤工助学`
  return true
})

export default router
