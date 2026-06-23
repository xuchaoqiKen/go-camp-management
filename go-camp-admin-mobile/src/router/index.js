import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/dispatch' },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { noAuth: true } },
  { path: '/dispatch', name: 'Dispatch', component: () => import('@/views/Dispatch.vue') },
  { path: '/rank-distribution', name: 'RankDistribution', component: () => import('@/views/RankDistribution.vue') },
  { path: '/leave-manage', name: 'LeaveManage', component: () => import('@/views/LeaveManage.vue') },
  { path: '/leave-detail/:id', name: 'LeaveDetail', component: () => import('@/views/LeaveDetail.vue') },
  { path: '/refund-manage', name: 'RefundManage', component: () => import('@/views/RefundManage.vue') },
  { path: '/refund-detail/:id', name: 'RefundDetail', component: () => import('@/views/RefundDetail.vue') },
  { path: '/class-manage', name: 'ClassManage', component: () => import('@/views/ClassView.vue') },
  { path: '/class-detail/:id', name: 'ClassDetail', component: () => import('@/views/ClassDetail.vue') },
  { path: '/mine', name: 'Mine', component: () => import('@/views/Mine.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.noAuth) {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
