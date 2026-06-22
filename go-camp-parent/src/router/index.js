import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/workbench'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/workbench',
    name: 'Workbench',
    component: () => import('@/views/Workbench.vue'),
    meta: { title: '工作台', requiresAuth: true }
  },
  {
    path: '/registration',
    name: 'Registration',
    component: () => import('@/views/Registration.vue'),
    meta: { title: '学员报名', requiresAuth: true }
  },
  {
    path: '/registration/:studentId/schedule',
    name: 'DailySchedule',
    component: () => import('@/views/DailySchedule.vue'),
    meta: { title: '每日排期', requiresAuth: true }
  },
  {
    path: '/settlement',
    name: 'Settlement',
    component: () => import('@/views/Settlement.vue'),
    meta: { title: '费用结算', requiresAuth: true }
  },
  {
    path: '/leave-refund',
    name: 'LeaveRefund',
    component: () => import('@/views/LeaveRefund.vue'),
    meta: { title: '请假退费', requiresAuth: true }
  },
  {
    path: '/leave-refund/apply',
    name: 'LeaveApply',
    component: () => import('@/views/LeaveApply.vue'),
    meta: { title: '请假申请', requiresAuth: true }
  },
  {
    path: '/leave-refund/refund',
    name: 'RefundApply',
    component: () => import('@/views/RefundApply.vue'),
    meta: { title: '退费申请', requiresAuth: true }
  },
  {
    path: '/mine',
    name: 'Mine',
    component: () => import('@/views/Mine.vue'),
    meta: { title: '我的', requiresAuth: true }
  },
  {
    path: '/mine/leave-records',
    name: 'LeaveRecords',
    component: () => import('@/views/LeaveRecords.vue'),
    meta: { title: '请假记录', requiresAuth: true }
  },
  {
    path: '/mine/registration-result',
    name: 'RegistrationResult',
    component: () => import('@/views/RegistrationResult.vue'),
    meta: { title: '报名结果', requiresAuth: true }
  }
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
