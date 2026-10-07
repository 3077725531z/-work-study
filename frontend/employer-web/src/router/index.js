import { createRouter, createWebHistory } from 'vue-router'

// 与 prototype/employer-web.html E01-E10 对应
const routes = [
  { path: '/', redirect: '/dash' },
  { path: '/dash', component: () => import('../views/Dash.vue'), meta: { title: '部门首页' } },
  { path: '/pub', component: () => import('../views/Pub.vue'), meta: { title: '发布岗位' } },
  { path: '/jobs', component: () => import('../views/Jobs.vue'), meta: { title: '我的岗位' } },
  { path: '/review', component: () => import('../views/Review.vue'), meta: { title: 'E05 申请审核' } },
  { path: '/attend', component: () => import('../views/Attend.vue'), meta: { title: '考勤确认' } },
  { path: '/salary', component: () => import('../views/Salary.vue'), meta: { title: '工时复核' } },
  { path: '/me', component: () => import('../views/Me.vue'), meta: { title: '部门中心' } }
]
export default createRouter({ history: createWebHistory(), routes })
