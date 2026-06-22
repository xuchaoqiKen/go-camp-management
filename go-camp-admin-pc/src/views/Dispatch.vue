<template>
  <div class="dispatch-page">
    <div class="stat-row">
      <div class="stat-card card-green">
        <div class="card-label">参训学员</div>
        <div class="card-value">{{ stats.studentCount || 0 }} <span class="unit">人</span></div>
      </div>
      <div class="stat-card card-blue">
        <div class="card-label">在岗教练</div>
        <div class="card-value">{{ stats.coachCount || 0 }} <span class="unit">人</span></div>
      </div>
      <div class="stat-card card-orange">
        <div class="card-label">早餐</div>
        <div class="card-value">{{ stats.breakfastCount || 0 }} <span class="unit">份</span></div>
      </div>
      <div class="stat-card card-purple">
        <div class="card-label">总餐次</div>
        <div class="card-value">{{ stats.totalMeals || 0 }} <span class="unit">次</span></div>
      </div>
      <div class="stat-card card-green-light">
        <div class="card-label">占用床位</div>
        <div class="card-value">{{ stats.occupiedBeds || 0 }} <span class="unit">张</span></div>
      </div>
    </div>

    <div class="table-section">
      <div class="section-header">
        <div class="section-title">
          <span class="title-dot"></span>
          <span>每日调度大屏</span>
        </div>
        <div class="session-tabs">
          <span 
            class="tab-item" 
            :class="{ active: activeSession === '1' }"
            @click="activeSession = '1'; fetchData()"
          >第一期</span>
          <span 
            class="tab-item"
            :class="{ active: activeSession === '2' }"
            @click="activeSession = '2'; fetchData()"
          >第二期</span>
        </div>
      </div>
      
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>日期</th>
              <th>学员数</th>
              <th>教练数</th>
              <th>早餐</th>
              <th>午餐</th>
              <th>晚餐</th>
              <th>总餐次</th>
              <th>总床位</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in list" :key="idx" :class="{ 'highlight-row': idx === 1 }">
              <td>{{ item.date }}</td>
              <td><span class="text-green">{{ item.studentCount }}</span></td>
              <td><span class="text-blue">{{ item.coachCount }}</span></td>
              <td><span class="text-green">{{ item.breakfastCount }}</span></td>
              <td>{{ item.lunchCount }}</td>
              <td>{{ item.dinnerCount }}</td>
              <td><span class="text-orange">{{ item.totalMeals }}</span></td>
              <td><span class="text-green">{{ item.occupiedBeds }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div class="table-note">
        总餐次 = 早餐 + 午餐 + 晚餐（师+生），统计口径与报名排期/教练排班一致
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getDispatchStats, getDispatchList } from '@/api'

const activeSession = ref('1')
const stats = reactive({ 
  studentCount: 0, 
  coachCount: 0, 
  breakfastCount: 0,
  totalMeals: 0, 
  occupiedBeds: 0 
})
const list = ref([])

async function fetchData() {
  const params = { sessionId: activeSession.value }
  try {
    const res1 = await getDispatchStats(params)
    Object.assign(stats, res1.data || {})
    const res2 = await getDispatchList(params)
    list.value = res2.data?.records || []
  } catch (e) {
    // 使用模拟数据
    list.value = [
      { date: '7月10日', studentCount: 72, coachCount: 10, breakfastCount: 75, lunchCount: 36, dinnerCount: 39, totalMeals: 150, occupiedBeds: 48 },
      { date: '7月11日', studentCount: 86, coachCount: 12, breakfastCount: 92, lunchCount: 59, dinnerCount: 59, totalMeals: 210, occupiedBeds: 56 },
      { date: '7月12日', studentCount: 83, coachCount: 12, breakfastCount: 89, lunchCount: 58, dinnerCount: 58, totalMeals: 205, occupiedBeds: 55 },
      { date: '7月13日', studentCount: 80, coachCount: 11, breakfastCount: 86, lunchCount: 56, dinnerCount: 56, totalMeals: 198, occupiedBeds: 53 },
      { date: '7月14日', studentCount: 84, coachCount: 12, breakfastCount: 90, lunchCount: 61, dinnerCount: 61, totalMeals: 212, occupiedBeds: 56 },
      { date: '7月15日', studentCount: 86, coachCount: 12, breakfastCount: 92, lunchCount: 61, dinnerCount: 61, totalMeals: 214, occupiedBeds: 56 },
    ]
    stats.studentCount = 86
    stats.coachCount = 12
    stats.breakfastCount = 92
    stats.totalMeals = 302
    stats.occupiedBeds = 56
  }
}

onMounted(fetchData)
</script>

<style scoped>
.dispatch-page {
  padding: 0;
}
.stat-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}
.stat-card {
  flex: 1;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}
.card-green {
  background: #059669;
  border-color: #059669;
}
.card-blue {
  background: #2563eb;
  border-color: #2563eb;
}
.card-orange {
  background: #ffffff;
  border-color: #e5e7eb;
}
.card-purple {
  background: #ffffff;
  border-color: #e5e7eb;
}
.card-green-light {
  background: #ffffff;
  border-color: #e5e7eb;
}
.card-label {
  font-size: 14px;
  color: #fff;
  margin-bottom: 8px;
  opacity: 0.9;
}
.card-orange .card-label,
.card-purple .card-label,
.card-green-light .card-label {
  color: #6b7280;
}
.card-value {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  line-height: 1;
}
.card-value .unit {
  font-size: 18px;
  margin-left: 4px;
}
.card-orange .card-value {
  color: #f59e0b;
}
.card-purple .card-value {
  color: #8b5cf6;
}
.card-green-light .card-value {
  color: #10b981;
}
.table-section {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  padding: 24px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}
.title-dot {
  width: 14px;
  height: 14px;
  background: #3b82f6;
  border-radius: 4px;
}
.session-tabs {
  display: flex;
  background: #f3f4f6;
  border-radius: 20px;
  padding: 4px;
}
.tab-item {
  padding: 8px 20px;
  border-radius: 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}
.tab-item.active {
  background: #10b981;
  color: #fff;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}
.table-wrap {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
}
.data-table th {
  background: #f8fafc;
  padding: 14px 16px;
  text-align: center;
  font-weight: 600;
  font-size: 14px;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
}
.data-table th:first-child {
  text-align: left;
}
.data-table td {
  padding: 14px 16px;
  text-align: center;
  font-size: 14px;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
}
.data-table td:first-child {
  text-align: left;
}
.data-table tbody tr:hover {
  background: #f9fafb;
}
.highlight-row {
  background: #f0fdf4 !important;
}
.text-green {
  color: #10b981;
  font-weight: 600;
}
.text-blue {
  color: #2563eb;
  font-weight: 600;
}
.text-orange {
  color: #f59e0b;
  font-weight: 600;
}
.table-note {
  margin-top: 16px;
  font-size: 12px;
  color: #9ca3af;
  padding-left: 16px;
}
</style>