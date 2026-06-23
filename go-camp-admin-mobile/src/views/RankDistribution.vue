<template>
  <div class="page-container">
    <h2 class="page-title">每日教学段位分布</h2>
    <van-tabs v-model:active="activeTab" @change="fetchData">
      <van-tab title="第一期" name="1" />
      <van-tab title="第二期" name="2" />
    </van-tabs>
    <van-cell-group inset style="margin-top:12px">
      <van-field v-model="selectedDate" label="日期" readonly is-link @click="showDatePicker = true" />
    </van-cell-group>
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker v-model="currentDate" title="选择日期" @confirm="onDateConfirm" @cancel="showDatePicker = false" />
    </van-popup>
    <div class="card" style="margin-top:16px; overflow-x:auto">
      <table class="rank-table">
        <thead>
          <tr>
            <th>日期</th>
            <th>无基础</th>
            <th>10-18级</th>
            <th>1段</th>
            <th>2段</th>
            <th>3段</th>
            <th>4段</th>
            <th>5段</th>
            <th>6段+</th>
            <th>待确认</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in tableData" :key="row.date">
            <td>{{ row.date }}</td>
            <td :class="{ 'highlight-green': row.levelNone > 0 }">{{ row.levelNone || 0 }}</td>
            <td :class="{ 'highlight-green': row.level10to18 > 0 }">{{ row.level10to18 || 0 }}</td>
            <td :class="{ 'highlight-green': row.dan1 > 0 }">{{ row.dan1 || 0 }}</td>
            <td :class="{ 'highlight-green': row.dan2 > 0 }">{{ row.dan2 || 0 }}</td>
            <td :class="{ 'highlight-green': row.dan3 > 0 }">{{ row.dan3 || 0 }}</td>
            <td :class="{ 'highlight-green': row.dan4 > 0 }">{{ row.dan4 || 0 }}</td>
            <td :class="{ 'highlight-green': row.dan5 > 0 }">{{ row.dan5 || 0 }}</td>
            <td :class="{ 'highlight-green': row.dan6plus > 0 }">{{ row.dan6plus || 0 }}</td>
            <td :class="{ 'text-warning': row.pending > 0 }">{{ row.pending || 0 }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="tableData.length === 0" class="empty-state"><van-icon name="bar-chart-o" /><p>暂无段位分布数据</p></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import adminApi from '@/api'

const activeTab = ref('1')
const selectedDate = ref(new Date().toISOString().slice(0, 10))
const showDatePicker = ref(false)
const currentDate = ref([new Date().getFullYear(), new Date().getMonth() + 1, new Date().getDate()])
const tableData = ref([])

const fetchData = async () => {
  try {
    const res = await adminApi.getRankDistribution({ session: activeTab.value, date: selectedDate.value })
    tableData.value = res.data || []
  } catch (e) { /* handled */ }
}

const onDateConfirm = ({ selectedValues }) => {
  selectedDate.value = `${selectedValues[0]}-${String(selectedValues[1]).padStart(2,'0')}-${String(selectedValues[2]).padStart(2,'0')}`
  showDatePicker.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.rank-table { width:100%; border-collapse:collapse; font-size:12px; min-width:700px }
.rank-table th,.rank-table td { padding:6px 4px; border:1px solid #ebedf0; text-align:center }
.rank-table th { background:#f7f8fa; font-weight:600 }
</style>