<template>
  <div class="page-container">
    <h2 class="page-title">班级管理</h2>
    <div class="filter-bar">
      <van-search v-model="searchKey" placeholder="搜索班级/学员" shape="round" @search="fetchList" />
      <van-dropdown-menu>
        <van-dropdown-item v-model="filterSession" :options="sessionOptions" @change="fetchList" />
      </van-dropdown-menu>
    </div>
    <div v-if="list.length === 0" class="empty-state"><van-icon name="cluster-o" /><p>暂无班级数据</p></div>
    <div v-for="item in list" :key="item.id" class="card" @click="$router.push(`/class-detail/${item.id}`)">
      <div class="flex-between">
        <span style="font-weight:600">{{ item.className }}</span>
        <van-tag type="primary">{{ item.sessionText }}</van-tag>
      </div>
      <div class="text-muted" style="font-size:13px;margin-top:4px">段位说明：{{ item.rankDesc || '-' }}</div>
      <div class="text-muted" style="font-size:13px">班主任：{{ item.headTeacher || '-' }} | 教练：{{ item.coach || '-' }}</div>
      <div class="flex-between" style="margin-top:4px">
        <span class="text-muted" style="font-size:12px">学生人数：{{ item.studentCount }}</span>
        <van-tag :type="item.status === 'ACTIVE' ? 'success' : 'default'">{{ item.status === 'ACTIVE' ? '启用' : '停用' }}</van-tag>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import adminApi from '@/api'

const searchKey = ref('')
const filterSession = ref('')
const sessionOptions = [{ text: '全部期数', value: '' }, { text: '第一期', value: '1' }, { text: '第二期', value: '2' }]
const list = ref([])

const fetchList = async () => {
  try {
    const res = await adminApi.getClassList({ keyword: searchKey.value, session: filterSession.value })
    list.value = res.data?.records || res.data || []
  } catch (e) { /* handled */ }
}

onMounted(fetchList)
</script>