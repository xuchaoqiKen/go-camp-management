import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import Layout from '@/Layout.vue'

const routes = [
  // 登录页 - 独立路由，不使用布局
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  // 其他页面 - 使用布局组件嵌套
  {
    path: '/',
    component: Layout,
    redirect: '/dispatch',
    children: [
      { path: 'dispatch', name: 'Dispatch', component: () => import('@/views/Dispatch.vue') },
      { path: 'rank-distribution', name: 'RankDistribution', component: () => import('@/views/RankDistribution.vue') },
      { path: 'order-manage', name: 'OrderManage', component: () => import('@/views/OrderManage.vue') },
      { path: 'order-detail/:id', name: 'OrderDetail', component: () => import('@/views/OrderDetail.vue') },
      { path: 'approval-center', name: 'ApprovalCenter', component: () => import('@/views/ApprovalCenter.vue') },
      { path: 'student-manage', name: 'StudentManage', component: () => import('@/views/StudentManage.vue') },
      { path: 'student-detail/:id', name: 'StudentDetail', component: () => import('@/views/StudentDetail.vue') },
      { path: 'user-manage', name: 'UserManage', component: () => import('@/views/UserManage.vue') },
      { path: 'role-manage', name: 'RoleManage', component: () => import('@/views/RoleManage.vue') },
      { path: 'permission-manage', name: 'PermissionManage', component: () => import('@/views/PermissionManage.vue') },
      { path: 'department-manage', name: 'DepartmentManage', component: () => import('@/views/DepartmentManage.vue') },
      { path: 'fee-config', name: 'FeeConfig', component: () => import('@/views/FeeConfig.vue') },
      { path: 'session-config', name: 'SessionConfig', component: () => import('@/views/SessionConfig.vue') },
      { path: 'rank-config', name: 'RankConfig', component: () => import('@/views/RankConfig.vue') },
      { path: 'class-manage', name: 'ClassManage', component: () => import('@/views/ClassManage.vue') },
      { path: 'class-detail/:id', name: 'ClassDetail', component: () => import('@/views/ClassDetail.vue') },
      { path: 'room-manage', name: 'RoomManage', component: () => import('@/views/RoomManage.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 登录认证
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 如果访问登录页，有token则跳转到首页
  if (to.path === '/login') {
    if (token) {
      next('/dispatch')
    } else {
      next()
    }
    return
  }
  
  // 访问其他页面，无token则跳转到登录页
  if (!token) {
    ElMessage.warning('请先登录')
    next('/login')
    return
  }
  
  next()
})

export default router