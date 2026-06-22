<template>
  <div class="page-container">
    <van-nav-bar title="教练工作台" fixed placeholder />

    <div class="reminder-bar" v-if="reminders.length">
      <van-icon name="warning-o" />
      <span>{{ reminders.join('；') }}</span>
    </div>

    <div class="grid-2">
      <div class="stat-card" v-for="item in stats" :key="item.label">
        <div class="stat-value">{{ item.value }}</div>
        <div class="stat-label">{{ item.label }}</div>
      </div>
    </div>

    <div class="card mt-12">
      <div class="section-title">功能入口</div>
      <van-cell-group inset>
        <van-cell title="每日上课名单" icon="friends-o" is-link to="/daily-list">
          <template #right-icon><van-badge :content="dailyBadge" v-if="dailyBadge" /></template>
        </van-cell>
        <van-cell title="学生请假状态" icon="notes-o" is-link to="/student-leave">
          <template #right-icon><van-badge :content="leaveBadge" v-if="leaveBadge" /></template>
        </van-cell>
        <van-cell title="段位分布" icon="chart-trending-o" is-link to="/rank-distribution" />
        <van-cell title="排班" icon="calendar-o" is-link to="/schedule">
          <template #right-icon><van-badge dot v-if="!scheduleSubmitted" /></template>
        </van-cell>
        <van-cell title="撤餐申请" icon="delete-o" is-link to="/meal-cancel">
          <template #right-icon><van-badge :content="mealCancelBadge" v-if="mealCancelBadge" /></template>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { coachApi } from '@/api'

const stats = ref([
  { label: '今日上课学员', value: 0 },
  { label: '今日请假学员', value: 0 },
  { label: '待处理撤餐', value: 0 },
  { label: '排班状态', value: '未提交' }
])

const reminders = ref([])
const dailyBadge = ref('')
const leaveBadge = ref('')
const mealCancelBadge = ref('')
const scheduleSubmitted = ref(false)

onMounted(async () => {
  try {
    const res = await coachApi.getMineInfo()
    if (res.data) {
      const d = res.data
      stats.value = [
        { label: '今日上课学员', value: d.todayStudentCount || 0 },
        { label: '今日请假学员', value: d.todayLeaveCount || 0 },
        { label: '待处理撤餐', value: d.pendingMealCancel || 0 },
        { label: '排班状态', value: d.scheduleSubmitted ? '已提交' : '未提交' }
      ]
      scheduleSubmitted.value = d.scheduleSubmitted
      if (d.reminders) reminders.value = d.reminders
      if (d.todayStudentCount) dailyBadge.value = String(d.todayStudentCount)
      if (d.todayLeaveCount) leaveBadge.value = String(d.todayLeaveCount)
      if (d.pendingMealCancel) mealCancelBadge.value = String(d.pendingMealCancel)
    }
  } catch (e) { /* 使用默认数据 */ }
})
</script>