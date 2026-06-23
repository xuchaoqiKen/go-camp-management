<template>
  <div class="page-container">
    <h2 class="page-title">退款管理</h2>
    <div class="filter-bar">
      <van-search v-model="searchKey" placeholder="搜索学员/退款编号" shape="round" @search="fetchList" />
      <van-dropdown-menu>
        <van-dropdown-item v-model="filterSession" :options="sessionOptions" @change="fetchList" />
        <van-dropdown-item v-model="filterStatus" :options="statusOptions" @change="fetchList" />
      </van-dropdown-menu>
    </div>
    <div v-if="list.length === 0" class="empty-state"><van-icon name="gold-coin-o" /><p>暂无退款记录</p></div>
    <div v-for="item in list" :key="item.id" class="card" @click="$router.push(`/refund-detail/${item.id}`)">
      <div class="flex-between">
        <span style="font-weight:600">{{ item.refundNo }}</span>
        <van-tag :type="statusTagType(item.status)">{{ item.statusText }}</van-tag>
      </div>
      <div class="text-muted" style="font-size:13px;margin-top:4px">学员：{{ item.studentName }} | 期数：{{ item.sessionText }}</div>
      <div class="text-muted" style="font-size:13px">退款项目：{{ item.refundItems }}</div>
      <div class="flex-between" style="margin-top:4px">
        <span style="color:#ee0a24;font-weight:600">¥{{ item.refundAmount }}</span>
        <span class="text-muted" style="font-size:12px">{{ item.createTime }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import adminApi from '@/api'

const searchKey = ref('')
const filterSession = ref('')
const filterStatus = ref('')
const sessionOptions = [{ text: '全部期数', value: '' }, { text: '第一期', value: '1' }, { text: '第二期', value: '2' }]
const statusOptions = [
  { text: '全部状态', value: '' }, { text: '待审核', value: 'PENDING' },
  { text: '已通过待退款', value: 'APPROVED' }, { text: '已退款', value: 'REFUNDED' },
  { text: '已驳回', value: 'REJECTED' }, { text: '退款失败', value: 'FAILED' }
]
const list = ref([])

const statusTagType = (s) => {
  const map = { PENDING: 'warning', APPROVED: 'primary', REFUNDED: 'success', REJECTED: 'danger', FAILED: 'danger' }
  return map[s] || 'default'
}

const fetchList = async () => {
  try {
    const res = await adminApi.getRefundList({ keyword: searchKey.value, session: filterSession.value, status: filterStatus.value })
    list.value = res.data?.records || res.data || []
  } catch (e) { /* handled */ }
}

onMounted(fetchList)
</script>