import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/workbench' },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { title: '登录' } },
  { path: '/workbench', name: 'Workbench', component: () => import('@/views/Workbench.vue'), meta: { title: '工作台', requiresAuth: true } },
  { path: '/schedule', name: 'Schedule', component: () => import('@/views/Schedule.vue'), meta: { title: '排班', requiresAuth: true } },
  { path: '/meal-cancel', name: 'MealCancel', component: () => import('@/views/MealCancel.vue'), meta: { title: '撤餐申请', requiresAuth: true } },
  { path: '/mine', name: 'Mine', component: () => import('@/views/Mine.vue'), meta: { title: '我的', requiresAuth: true } },
  { path: '/student-leave', name: 'StudentLeave', component: () => import('@/views/StudentLeave.vue'), meta: { title: '学生请假', requiresAuth: true } },
  { path: '/daily-list', name: 'DailyList', component: () => import('@/views/DailyList.vue'), meta: { title: '每日名单', requiresAuth: true } },
  { path: '/rank-distribution', name: 'RankDistribution', component: () => import('@/views/RankDistribution.vue'), meta: { title: '段位分布', requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转到登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/workbench')
  } else {
    next()
  }
})

export default router