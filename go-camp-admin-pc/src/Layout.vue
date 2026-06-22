<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo-wrap">
        <div class="logo-icon">Go</div>
        <div class="logo-text">
          <div class="logo-title">围棋集训营</div>
          <div class="logo-subtitle">教育与后勤管理系统</div>
        </div>
      </div>
      <el-menu :default-active="activeMenu" router class="sidebar-menu" background-color="#ffffff" text-color="#1f2937" active-text-color="#10b981">
        <el-menu-item index="/dispatch" class="menu-item-active">
          <el-icon><Calendar /></el-icon>
          <span>每日调度</span>
        </el-menu-item>
<el-menu-item index="/rank-distribution" class="menu-item-with-desc">
          <el-icon><Grid /></el-icon>
          <span class="menu-text-wrap">
            <span class="menu-title">每日教学段位分布</span>
            <span class="menu-desc">(排课依据)</span>
          </span>
        </el-menu-item>
        <el-menu-item index="/order-manage">
          <el-icon><Document /></el-icon>
          <span>报名订单管理</span>
        </el-menu-item>
<el-menu-item index="/approval-center" class="menu-item-with-desc">
          <el-icon><Checked /></el-icon>
          <span class="menu-text-wrap">
            <span class="menu-title">审批中心</span>
            <span class="menu-desc">(请假/退款/撤餐)</span>
          </span>
        </el-menu-item>
        <el-menu-item index="/room-manage">
          <el-icon><OfficeBuilding /></el-icon>
          <span>房态库存管理</span>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/student-manage">学员管理</el-menu-item>
          <el-menu-item index="/user-manage">用户管理</el-menu-item>
          <el-menu-item index="/role-manage">角色管理</el-menu-item>
          <el-menu-item index="/permission-manage">权限管理</el-menu-item>
          <el-menu-item index="/department-manage">部门管理</el-menu-item>
          <el-menu-item index="/fee-config">费用配置</el-menu-item>
          <el-menu-item index="/session-config">营期配置</el-menu-item>
          <el-menu-item index="/rank-config">段位分类配置</el-menu-item>
          <el-menu-item index="/class-manage">班级管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container class="main-container">
      <el-header class="header">
        <span class="page-title">{{ currentPageTitle }}</span>
        <div class="header-right">
          <span class="date-display">
            {{ currentDate }}
            <el-icon><Calendar /></el-icon>
          </span>
           <el-dropdown>
             <div class="user-info">
               <span class="user-avatar">{{ realName.charAt(0) }}</span>
               <span class="user-name">{{ realName }}</span>
               <el-icon><ArrowDown /></el-icon>
             </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Calendar, Grid, Document, Checked, OfficeBuilding, Setting, ArrowDown } from '@element-plus/icons-vue'
import { logout as logoutApi } from '@/api'

const route = useRoute()
const router = useRouter()
const activeMenu = computed(() => route.path)
const username = computed(() => localStorage.getItem('username') || '管理员')
const realName = computed(() => localStorage.getItem('realName') || localStorage.getItem('username') || '管理员')
const currentDate = ref('')

// 页面标题映射
const pageTitleMap = {
  '/dispatch': '每日调度',
  '/rank-distribution': '每日教学段位分布',
  '/order-manage': '报名订单管理',
  '/order-detail': '订单详情',
  '/approval-center': '审批中心',
  '/room-manage': '房态库存管理',
  '/student-manage': '学员管理',
  '/student-detail': '学员详情',
  '/user-manage': '用户管理',
  '/role-manage': '角色管理',
  '/permission-manage': '权限管理',
  '/department-manage': '部门管理',
  '/fee-config': '费用配置',
  '/session-config': '营期配置',
  '/rank-config': '段位分类配置',
  '/class-manage': '班级管理',
  '/class-detail': '班级详情'
}

const currentPageTitle = computed(() => {
  const path = route.path
  // 匹配带参数的路由
  if (path.startsWith('/order-detail/')) return pageTitleMap['/order-detail']
  if (path.startsWith('/student-detail/')) return pageTitleMap['/student-detail']
  if (path.startsWith('/class-detail/')) return pageTitleMap['/class-detail']
  return pageTitleMap[path] || '首页'
})

function updateDate() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekday = weekdays[now.getDay()]
  currentDate.value = `${year}年${month}月${day}日 ${weekday}`
}

let timer = null
onMounted(() => {
  updateDate()
  timer = setInterval(updateDate, 60000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

async function logout() {
  try {
    await logoutApi()
  } catch (e) {
  }
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('realName')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.sidebar { 
  background: #ffffff; 
  overflow-y: auto; 
  border-right: 1px solid #e5e7eb;
}
.logo-wrap {
  display: flex;
  align-items: center;
  padding: 20px 16px;
  gap: 10px;
  border-bottom: 1px solid #e5e7eb;
}
.logo-icon {
  width: 36px;
  height: 36px;
  background: #10b981;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: bold;
  font-size: 14px;
}
.logo-text {
  flex: 1;
}
.logo-title {
  font-size: 18px;
  font-weight: bold;
  color: #1f2937;
  line-height: 1.2;
}
.logo-subtitle {
  font-size: 11px;
  color: #6b7280;
  margin-top: 2px;
}
.sidebar-menu {
  border-right: none !important;
  padding: 8px 0;
}
.sidebar-menu .el-menu-item {
  height: auto;
  padding: 12px 20px;
  line-height: 1.4;
  margin: 0 8px;
  border-radius: 8px;
  color: #6b7280;
}
.sidebar-menu .el-menu-item.is-active {
  background: #10b981 !important;
  color: #ffffff !important;
}
.sidebar-menu .el-menu-item .el-icon {
  margin-right: 10px;
}
.menu-item-with-desc {
  display: flex;
  align-items: center;
  padding: 12px 20px !important;
}
.menu-item-with-desc .el-icon {
  margin-right: 10px;
  flex-shrink: 0;
}
.menu-text-wrap {
  display: flex;
  flex-direction: column;
  text-align: left;
  flex: 1;
}
.menu-title {
  font-size: 14px;
  line-height: 1.3;
}
.menu-desc {
  display: block;
  font-size: 12px;
  text-align: left;
  opacity: 0.8;
  line-height: 1.3;
  margin-top: 2px;
}
.sidebar-menu .el-sub-menu__title {
  padding: 12px 20px;
  margin: 0 8px;
  border-radius: 8px;
}
.header { 
  background: #fff; 
  display: flex; 
  align-items: center; 
  justify-content: space-between; 
  border-bottom: 1px solid #e5e7eb; 
  padding: 0 24px;
  height: 60px;
}
.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}
.date-display {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6b7280;
}
.user-info { 
  cursor: pointer; 
  display: flex; 
  align-items: center; 
  gap: 8px; 
}
.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #60a5fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 16px;
}
.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}
.main-container { background: #f9fafb; }
</style>