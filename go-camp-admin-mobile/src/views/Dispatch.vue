<template>
  <div class="page-container">
    <h2 class="page-title">每日调度大屏</h2>
    <van-tabs v-model:active="activeTab" @change="onTabChange">
      <van-tab title="第一期" name="1" />
      <van-tab title="第二期" name="2" />
    </van-tabs>
    <van-cell-group inset style="margin-top:12px">
      <van-field v-model="selectedDate" label="日期" readonly is-link @click="showDatePicker = true" />
    </van-cell-group>
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker v-model="currentDate" title="选择日期" @confirm="onDateConfirm" @cancel="showDatePicker = false" />
    </van-popup>
    <div class="stat-cards" style="margin-top:16px">
      <div class="stat-card"><div class="stat-value">{{ stats.studentCount }}</div><div class="stat-label">参训学员</div></div>
      <div class="stat-card"><div class="stat-value">{{ stats.coachCount }}</div><div class="stat-label">在岗教练</div></div>
      <div class="stat-card"><div class="stat-value">{{ stats.totalMeals }}</div><div class="stat-label">总餐次</div></div>
      <div class="stat-card"><div class="stat-value">{{ stats.occupiedBeds }}</div><div class="stat-label">占用床位</div></div>
    </div>
    <div class="card" style="overflow-x:auto">
      <h4 style="margin-bottom:10px">调度明细表</h4>
      <table class="dispatch-table">
        <thead><tr><th>日期</th><th>学员</th><th>教练</th><th>早餐</th><th>午餐</th><th>晚餐</th><th>总餐次</th><th>床位</th></tr></thead>
        <tbody>
          <tr v-for="row in tableData" :key="row.date">
            <td>{{ row.date }}</td><td>{{ row.studentCount }}</td><td>{{ row.coachCount }}</td>
            <td>{{ row.breakfast }}</td><td>{{ row.lunch }}</td><td>{{ row.dinner }}</td>
            <td>{{ row.totalMeals }}</td><td>{{ row.occupiedBeds }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="tableData.length === 0" class="empty-state"><van-icon name="notes-o" /><p>暂无调度数据</p></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { showToast } from 'vant'
import adminApi from '@/api'

const activeTab = ref('1')
const selectedDate = ref(new Date().toISOString().slice(0, 10))
const showDatePicker = ref(false)
const currentDate = ref([new Date().getFullYear(), new Date().getMonth() + 1, new Date().getDate()])

const stats = reactive({ studentCount: 0, coachCount: 0, totalMeals: 0, occupiedBeds: 0 })
const tableData = ref([])

const fetchData = async () => {
  try {
    const params = { session: activeTab.value, date: selectedDate.value }
    const res = await adminApi.getDispatchStats(params)
    if (res.data) {
      stats.studentCount = res.data.studentCount || 0
      stats.coachCount = res.data.coachCount || 0
      stats.totalMeals = res.data.totalMeals || 0
      stats.occupiedBeds = res.data.occupiedBeds || 0
    }
    const tableRes = await adminApi.getDispatchTable(params)
    tableData.value = tableRes.data || []
  } catch (e) { /* handled by interceptor */ }
}

const onTabChange = () => fetchData()
const onDateConfirm = ({ selectedValues }) => {
  selectedDate.value = `${selectedValues[0]}-${String(selectedValues[1]).padStart(2,'0')}-${String(selectedValues[2]).padStart(2,'0')}`
  showDatePicker.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.dispatch-table { width:100%; border-collapse:collapse; font-size:12px; min-width:600px }
.dispatch-table th,.dispatch-table td { padding:6px 4px; border:1px solid #ebedf0; text-align:center }
.dispatch-table th { background:#f7f8fa; font-weight:600 }
</style>