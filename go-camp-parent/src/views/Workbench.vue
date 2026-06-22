<template>
  <div class="page-container">
    <div class="page-title">工作台</div>

    <!-- 状态提醒 -->
    <div class="card" v-if="reminders.length > 0">
      <div class="section-title">待处理提醒</div>
      <div
        v-for="item in reminders"
        :key="item.id"
        class="reminder-item flex-between"
        @click="handleReminderClick(item)"
      >
        <div class="flex-center">
          <van-icon :name="item.icon" :color="item.color" size="18" />
          <span class="reminder-text">{{ item.text }}</span>
        </div>
        <van-icon name="arrow" color="#969799" />
      </div>
    </div>

    <!-- 功能入口 -->
    <div class="card">
      <div class="section-title">功能入口</div>
      <van-grid :column-num="3" :border="false">
        <van-grid-item
          v-for="item in menuItems"
          :key="item.key"
          :icon="item.icon"
          :text="item.text"
          :badge="item.badge"
          @click="handleMenuClick(item)"
        />
      </van-grid>
    </div>

    <!-- 学员概览 -->
    <div class="card" v-if="students.length > 0">
      <div class="section-title">我的学员</div>
      <div
        v-for="student in students"
        :key="student.id"
        class="student-item flex-between"
      >
        <div>
          <div class="student-name">{{ student.name }}</div>
          <div class="text-muted">
            段位：{{ student.rank || '待确认' }} | 状态：{{ student.statusText }}
          </div>
        </div>
        <span :class="['tag', student.statusClass]">{{ student.statusText }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { mineApi } from '@/api'

const router = useRouter()

const reminders = ref([
  { id: 1, icon: 'bill', color: '#ee0a24', text: '待支付后勤费：¥1,200', type: 'payment' },
  { id: 2, icon: 'notes', color: '#ff976a', text: '资料未完善，请补全学员信息', type: 'profile' }
])

const menuItems = ref([
  { key: 'registration', icon: 'add-square', text: '学员报名', badge: '' },
  { key: 'leave-refund', icon: 'logistics', text: '请假退费', badge: '' },
  { key: 'mine', icon: 'user', text: '我的', badge: '' }
])

const students = ref([])

onMounted(async () => {
  try {
    const res = await mineApi.getProfile()
    if (res.data?.students) {
      students.value = res.data.students.map(s => ({
        ...s,
        statusText: s.status === 1 ? '在营' : s.status === 0 ? '待确认' : '已离营',
        statusClass: s.status === 1 ? 'tag-success' : s.status === 0 ? 'tag-warning' : 'tag-muted'
      }))
    }
  } catch (e) {
    // 未登录或数据为空
  }
})

function handleReminderClick(item) {
  if (item.type === 'payment') {
    router.push('/settlement')
  } else if (item.type === 'profile') {
    router.push('/registration')
  }
}

function handleMenuClick(item) {
  router.push(`/${item.key}`)
}
</script>

<style scoped>
.reminder-item {
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}
.reminder-item:last-child {
  border-bottom: none;
}
.reminder-text {
  margin-left: 8px;
  font-size: 14px;
  color: #323233;
}
.student-item {
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}
.student-item:last-child {
  border-bottom: none;
}
.student-name {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 4px;
}
</style>