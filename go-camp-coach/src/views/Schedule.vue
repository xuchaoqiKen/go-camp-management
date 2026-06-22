<template>
  <div class="page-container">
    <van-nav-bar title="排班" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-tabs v-model:active="activeTab" @change="onTabChange">
      <van-tab title="第一期" name="1" />
      <van-tab title="第二期" name="2" />
    </van-tabs>

    <div class="card mt-12" v-for="day in scheduleDays" :key="day.date">
      <div class="section-title">{{ day.date }} {{ day.dayOfWeek }}</div>
      <van-cell-group inset>
        <van-cell title="到岗上课" center>
          <template #right-icon><van-switch v-model="day.attendClass" :disabled="day.isCheckInDay" size="22" /></template>
        </van-cell>
        <van-cell title="工作午餐" center>
          <template #right-icon><van-switch v-model="day.workLunch" :disabled="day.isCheckInDay" size="22" /></template>
        </van-cell>
        <van-cell title="工作晚餐" center>
          <template #right-icon><van-switch v-model="day.workDinner" size="22" /></template>
        </van-cell>
        <van-cell title="需要住宿" center>
          <template #right-icon><van-switch v-model="day.needAccommodation" size="22" /></template>
        </van-cell>
      </van-cell-group>
      <div class="text-muted mt-12" v-if="day.isCheckInDay">报到日，无课无午餐</div>
    </div>

    <div class="card mt-12">
      <div class="section-title">成本核算</div>
      <div class="fw-bold color-primary" style="font-size:18px">学校承担后勤成本：¥{{ totalCost }}</div>
    </div>

    <div style="padding: 12px 0 24px">
      <van-button type="primary" block round @click="submitSchedule" :loading="submitting">仅提交排期</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast, showConfirmDialog } from 'vant'
import { coachApi } from '@/api'

const activeTab = ref('1')
const submitting = ref(false)

const scheduleDays = ref([
  { date: '2026-07-15', dayOfWeek: '周三', attendClass: false, workLunch: false, workDinner: true, needAccommodation: true, isCheckInDay: true },
  { date: '2026-07-16', dayOfWeek: '周四', attendClass: true, workLunch: true, workDinner: true, needAccommodation: true, isCheckInDay: false },
  { date: '2026-07-17', dayOfWeek: '周五', attendClass: true, workLunch: true, workDinner: true, needAccommodation: true, isCheckInDay: false }
])

const totalCost = computed(() => {
  let cost = 0
  scheduleDays.value.forEach(d => {
    if (d.workLunch) cost += 25
    if (d.workDinner) cost += 25
    if (d.needAccommodation) cost += 100
  })
  return cost
})

const onTabChange = async (name) => {
  try {
    const res = await coachApi.getSchedule({ session: name })
    if (res.data) scheduleDays.value = res.data
  } catch (e) { /* 使用默认数据 */ }
}

const submitSchedule = async () => {
  const emptyDays = scheduleDays.value.filter(d => !d.attendClass && !d.workLunch && !d.workDinner && !d.needAccommodation && !d.isCheckInDay)
  if (emptyDays.length > 0) {
    showToast('存在未配置的日期，请完善排班')
    return
  }
  try {
    await showConfirmDialog({ title: '确认提交', message: '提交后排班将生效，确认提交？' })
  } catch { return }
  submitting.value = true
  try {
    await coachApi.submitSchedule({ session: activeTab.value, days: scheduleDays.value })
    showToast('排班提交成功')
  } catch (e) { /* error handled by interceptor */ }
  finally { submitting.value = false }
}

onMounted(() => onTabChange('1'))
</script>