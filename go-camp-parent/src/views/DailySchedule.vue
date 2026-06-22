<template>
  <div class="page-container">
    <van-nav-bar title="每日排期" left-arrow @click-left="$router.back()" fixed placeholder />

    <!-- 学员切换 -->
    <van-tabs v-model:active="activeStudent" class="mb-12">
      <van-tab v-for="(student, idx) in store.students" :key="student.id" :title="student.name">
        <!-- 期数切换 -->
        <van-tabs v-model:active="activeSession" type="card" class="mt-8">
          <van-tab v-for="s in store.sessions" :key="s.id" :title="s.name">
            <div class="schedule-content">
              <div class="section-title mt-12">{{ s.name }} - 每日排期配置</div>

              <div v-for="day in getSessionDays(s)" :key="day.date" class="day-card">
                <div class="day-header flex-between">
                  <span class="day-date">{{ day.date }}</span>
                  <span class="day-label">{{ day.label }}</span>
                </div>

                <van-cell-group inset>
                  <!-- 上课 -->
                  <van-cell title="安排上课" center>
                    <template #right-icon>
                      <van-switch
                        v-model="day.schedule.hasClass"
                        :disabled="day.isCheckinDay"
                        @change="onScheduleChange(student.id, activeSession, day)"
                      />
                    </template>
                  </van-cell>

                  <!-- 早餐 -->
                  <van-cell title="早餐" center>
                    <template #right-icon>
                      <van-switch
                        v-model="day.schedule.hasBreakfast"
                        :disabled="day.isCheckinDay || isBreakfastAuto(day)"
                        @change="onScheduleChange(student.id, activeSession, day)"
                      />
                    </template>
                    <template #label>
                      <span class="text-muted" v-if="isBreakfastAuto(day)">住宿自动含早</span>
                    </template>
                  </van-cell>

                  <!-- 午餐 -->
                  <van-cell title="午餐" center>
                    <template #right-icon>
                      <van-switch
                        v-model="day.schedule.hasLunch"
                        :disabled="day.isCheckinDay"
                        @change="onScheduleChange(student.id, activeSession, day)"
                      />
                    </template>
                  </van-cell>

                  <!-- 晚餐 -->
                  <van-cell title="晚餐" center>
                    <template #right-icon>
                      <van-switch
                        v-model="day.schedule.hasDinner"
                        :disabled="day.isCheckoutDay"
                        @change="onScheduleChange(student.id, activeSession, day)"
                      />
                    </template>
                  </van-cell>

                  <!-- 住宿模式 -->
                  <van-cell title="住宿模式" :value="day.schedule.accommodation || '不住宿'" is-link @click="showAccommodationPicker(student.id, activeSession, day)" />
                </van-cell-group>
              </div>
            </div>
          </van-tab>
        </van-tabs>
      </van-tab>
    </van-tabs>

    <!-- 底部操作 -->
    <div class="bottom-bar">
      <van-button block type="primary" @click="goToSettlement">下一步：费用结算</van-button>
    </div>

    <!-- 住宿选择器 -->
    <van-popup v-model:show="showAccommodation" position="bottom" round>
      <van-picker
        :columns="accommodationOptions"
        @confirm="onAccommodationConfirm"
        @cancel="showAccommodation = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { useRegistrationStore } from '@/stores/registration'

const router = useRouter()
const store = useRegistrationStore()

const activeStudent = ref(0)
const activeSession = ref(0)
const showAccommodation = ref(false)
const currentDayRef = ref(null)

const accommodationOptions = ['不住宿', '全托管', '陪住标间', '陪住大床']

// 模拟生成每日排期数据
function getSessionDays(session) {
  const days = []
  const start = new Date(session.startDate || '2026-07-15')
  const end = new Date(session.endDate || '2026-07-24')
  const checkinDate = new Date(session.checkinDate || '2026-07-15')
  const checkoutDate = new Date(session.checkoutDate || '2026-07-24')

  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    const dateStr = d.toISOString().split('T')[0]
    const isCheckinDay = dateStr === checkinDate.toISOString().split('T')[0]
    const isCheckoutDay = dateStr === checkoutDate.toISOString().split('T')[0]

    days.push({
      date: dateStr,
      label: isCheckinDay ? '报到日' : isCheckoutDay ? '退房日' : '',
      isCheckinDay,
      isCheckoutDay,
      schedule: reactive({
        hasClass: !isCheckinDay,
        hasBreakfast: false,
        hasLunch: !isCheckinDay,
        hasDinner: !isCheckoutDay,
        accommodation: '不住宿'
      })
    })
  }
  return days
}

function isBreakfastAuto(day) {
  return ['全托管', '陪住标间', '陪住大床'].includes(day.schedule.accommodation)
}

function onScheduleChange(studentId, sessionIdx, day) {
  // 住宿联动早餐
  if (isBreakfastAuto(day)) {
    day.schedule.hasBreakfast = true
  }
  store.setStudentSchedule(studentId, sessionIdx, day.date, { ...day.schedule })
}

function showAccommodationPicker(studentId, sessionIdx, day) {
  currentDayRef.value = { studentId, sessionIdx, day }
  showAccommodation.value = true
}

function onAccommodationConfirm({ selectedOptions }) {
  const acc = selectedOptions[0]?.text || '不住宿'
  if (currentDayRef.value) {
    const { studentId, sessionIdx, day } = currentDayRef.value
    day.schedule.accommodation = acc
    if (isBreakfastAuto(day)) {
      day.schedule.hasBreakfast = true
    }
    store.setStudentSchedule(studentId, sessionIdx, day.date, { ...day.schedule })
  }
  showAccommodation.value = false
}

function goToSettlement() {
  router.push('/settlement')
}
</script>

<style scoped>
.schedule-content {
  padding: 0 12px;
}
.day-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  padding: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.day-header {
  margin-bottom: 8px;
}
.day-date {
  font-size: 15px;
  font-weight: 600;
}
.day-label {
  font-size: 12px;
  color: #1989fa;
  background: #e8f4ff;
  padding: 2px 8px;
  border-radius: 4px;
}
.bottom-bar {
  position: fixed;
  bottom: 50px;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.05);
}
</style>