<template>
  <div class="rank-page">
    <div class="content-card">
      <div class="card-header">
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
              <th>无基础</th>
              <th>10-1级</th>
              <th>1段</th>
              <th>2段</th>
              <th>3段</th>
              <th>4段</th>
              <th>5段</th>
              <th>6段+</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in list" :key="idx">
              <td>{{ item.date }}</td>
              <td>{{ item.noneBase || 0 }}</td>
              <td>{{ item.level10_18 || 0 }}</td>
              <td>{{ item.dan1 || 0 }}</td>
              <td>{{ item.dan2 || 0 }}</td>
              <td>{{ item.dan3 || 0 }}</td>
              <td>{{ item.dan4 || 0 }}</td>
              <td>{{ item.dan5 || 0 }}</td>
              <td>{{ item.dan6Plus || 0 }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRankDistribution } from '@/api'

const activeSession = ref('1')
const list = ref([])

async function fetchData() {
  const params = { sessionId: activeSession.value }
  try {
    const res = await getRankDistribution(params)
    list.value = res.data || []
  } catch (e) {
    // 模拟数据
    list.value = [
      { date: '7月5日', noneBase: 12, level10_18: 45, dan1: 38, dan2: 27, dan3: 18, dan4: 11, dan5: 6, dan6Plus: 2 },
      { date: '7月6日', noneBase: 11, level10_18: 47, dan1: 40, dan2: 29, dan3: 19, dan4: 12, dan5: 6, dan6Plus: 2 },
      { date: '7月7日', noneBase: 13, level10_18: 46, dan1: 41, dan2: 30, dan3: 20, dan4: 12, dan5: 7, dan6Plus: 2 },
      { date: '7月8日', noneBase: 14, level10_18: 48, dan1: 43, dan2: 31, dan3: 20, dan4: 13, dan5: 7, dan6Plus: 3 },
      { date: '7月9日', noneBase: 12, level10_18: 46, dan1: 39, dan2: 28, dan3: 19, dan4: 12, dan5: 6, dan6Plus: 2 },
      { date: '7月10日', noneBase: 11, level10_18: 44, dan1: 37, dan2: 27, dan3: 18, dan4: 11, dan5: 6, dan6Plus: 2 },
      { date: '7月11日', noneBase: 12, level10_18: 45, dan1: 38, dan2: 27, dan3: 18, dan4: 11, dan5: 6, dan6Plus: 2 },
      { date: '7月12日', noneBase: 13, level10_18: 47, dan1: 40, dan2: 30, dan3: 19, dan4: 12, dan5: 7, dan6Plus: 2 },
      { date: '7月13日', noneBase: 12, level10_18: 46, dan1: 39, dan2: 28, dan3: 19, dan4: 11, dan5: 6, dan6Plus: 2 },
      { date: '7月14日', noneBase: 11, level10_18: 45, dan1: 38, dan2: 27, dan3: 18, dan4: 11, dan5: 6, dan6Plus: 2 },
      { date: '7月15日', noneBase: 12, level10_18: 46, dan1: 39, dan2: 28, dan3: 19, dan4: 11, dan5: 6, dan6Plus: 2 },
    ]
  }
}

onMounted(fetchData)
</script>

<style scoped>
.rank-page {
  padding: 0;
}
.content-card {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  padding: 24px;
}
.card-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
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
  table-layout: fixed;
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
</style>