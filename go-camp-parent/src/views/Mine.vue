<template>
  <div class="page-container">
    <div class="page-title">我的</div>

    <!-- 个人信息 -->
    <div class="card">
      <div class="section-title">个人信息</div>
      <van-cell-group inset>
        <van-cell title="姓名" :value="profile.name || '未完善'" />
        <van-cell title="手机号" :value="profile.phone || '未绑定'" />
        <van-cell title="城市" :value="profile.city || '未填写'" />
      </van-cell-group>
    </div>

    <!-- 关联学员 -->
    <div class="card mt-12">
      <div class="section-title">关联学员</div>
      <div v-for="student in students" :key="student.id" class="student-card">
        <div class="flex-between">
          <div>
            <div class="student-name">{{ student.name }}</div>
            <div class="text-muted">段位：{{ student.rank || '待确认' }}</div>
          </div>
          <span :class="['tag', student.statusClass]">{{ student.statusText }}</span>
        </div>
        <van-divider style="margin:8px 0" />
        <div class="text-muted" style="font-size:13px;line-height:1.8">
          <div>期数：{{ student.sessions || '-' }}</div>
          <div>班级：{{ student.className || '待分配' }}</div>
          <div>住宿：{{ student.accommodation || '不住宿' }}</div>
          <div>房间：{{ student.roomInfo || '待系统匹配/待老师调整' }}</div>
          <div>早餐来源：{{ student.breakfastSource || '-' }}</div>
        </div>
      </div>
    </div>

    <!-- 请假记录 -->
    <div class="card mt-12">
      <div class="section-title flex-between">
        <span>请假记录</span>
        <span class="text-muted" style="font-size:13px" @click="$router.push('/leave-refund')">查看全部</span>
      </div>
      <div v-if="leaveRecords.length === 0" class="text-muted" style="text-align:center;padding:16px 0">暂无记录</div>
      <div v-for="item in leaveRecords" :key="item.id" class="leave-item flex-between">
        <div>
          <div>{{ item.studentName }} - {{ item.typeText }}</div>
          <div class="text-muted">{{ item.date }}</div>
        </div>
        <span :class="['tag', item.statusClass]">{{ item.statusText }}</span>
      </div>
    </div>

    <!-- 成绩信息（二期） -->
    <div class="card mt-12">
      <div class="section-title">成绩信息</div>
      <div class="placeholder-card">
        <van-icon name="underway-o" size="32" color="#c8c9cc" />
        <div class="text-muted mt-8">二期功能开发中，敬请期待</div>
      </div>
    </div>

    <!-- 退出登录 -->
    <van-button block plain type="danger" @click="handleLogout" class="mt-16">退出登录</van-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog } from 'vant'
import { mineApi } from '@/api'

const router = useRouter()

const profile = ref({ name: '张家长', phone: '138****1234', city: '北京' })

const students = ref([
  {
    id: 1,
    name: '张三',
    rank: '3段',
    statusText: '在营',
    statusClass: 'tag-success',
    sessions: '第一期',
    className: '段位A班',
    accommodation: '全托管',
    roomInfo: 'A栋301-1床',
    breakfastSource: '托管包含'
  }
])

const leaveRecords = ref([
  { id: 1, studentName: '张三', date: '2026-07-16', typeText: '请假不扣课', statusText: '待审核', statusClass: 'tag-warning' }
])

onMounted(async () => {
  try {
    const res = await mineApi.getProfile()
    if (res.data) {
      profile.value = res.data.profile || profile.value
      students.value = res.data.students || students.value
      leaveRecords.value = res.data.leaveRecords || leaveRecords.value
    }
  } catch (e) { /* 使用模拟数据 */ }
})

function handleLogout() {
  showConfirmDialog({ title: '提示', message: '确定退出登录？' }).then(() => {
    localStorage.removeItem('token')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.student-card {
  background: #f7f8fa;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 8px;
}
.student-name {
  font-size: 15px;
  font-weight: 600;
}
.leave-item {
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}
.leave-item:last-child { border-bottom: none; }
.placeholder-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 0;
}
</style>