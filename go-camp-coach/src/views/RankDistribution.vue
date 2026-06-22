<template>
  <div class="page-container">
    <van-nav-bar title="段位分布" left-arrow @click-left="$router.back()" fixed placeholder />
    <van-tabs v-model:active="activeTab" @change="onTabChange">
      <van-tab title="第一期" name="1" />
      <van-tab title="第二期" name="2" />
    </van-tabs>
    <div class="card" v-if="currentDate">
      <div class="section-title">{{ currentDate }} 段位分布</div>
      <div class="table-wrapper">
        <table class="data-table">
          <thead><tr><th>段位</th><th>上课人数</th></tr></thead>
          <tbody>
            <tr v-for="r in rankData" :key="r.rank" :class="{ 'highlight': r.count > 0 }">
              <td>{{ r.rank }}</td>
              <td>{{ r.count }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <van-empty v-else description="暂无段位分布数据" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { coachApi } from '@/api'

const activeTab = ref('1')
const currentDate = ref('')
const rankData = ref([
  { rank: '无基础', count: 0 },
  { rank: '10-18级', count: 0 },
  { rank: '1段', count: 0 },
  { rank: '2段', count: 0 },
  { rank: '3段', count: 0 },
  { rank: '4段', count: 0 },
  { rank: '5段', count: 0 },
  { rank: '6段+', count: 0 },
  { rank: '待确认', count: 0 }
])

const onTabChange = async (name) => {
  try {
    const res = await coachApi.getRankDistribution({ session: name })
    if (res.data) {
      currentDate.value = res.data.date || ''
      rankData.value = res.data.ranks || rankData.value
    }
  } catch (e) { /* default */ }
}

onMounted(() => onTabChange('1'))
</script>

<style scoped>
.table-wrapper { overflow-x: auto }
.data-table { width: 100%; border-collapse: collapse; font-size: 14px }
.data-table th, .data-table td { padding: 8px 12px; text-align: center; border-bottom: 1px solid #eee }
.data-table th { background: #f5f7fa; font-weight: 600 }
.highlight td { background: #e8f5e9; color: #2e7d32; font-weight: 700 }
</style>