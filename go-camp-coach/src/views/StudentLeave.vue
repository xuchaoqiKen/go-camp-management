<template>
  <div class="page-container">
    <van-nav-bar title="学生请假状态" left-arrow @click-left="$router.back()" fixed placeholder />
    <van-search v-model="searchKey" placeholder="搜索学员姓名" shape="round" />
    <div class="card" v-for="item in filteredList" :key="item.id">
      <div class="fw-bold">{{ item.studentName }} <span class="text-muted">({{ item.sessionName }})</span></div>
      <div class="text-muted mt-12">请假日期：{{ item.leaveDate }} | 类型：{{ item.typeText }}</div>
      <div class="mt-12">
        <span :class="['tag', statusClass(item.status)]">{{ item.statusText }}</span>
        <span class="text-muted ml-8" v-if="item.reason">原因：{{ item.reason }}</span>
      </div>
    </div>
    <van-empty v-if="!filteredList.length" description="暂无请假记录" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { coachApi } from '@/api'

const searchKey = ref('')
const leaveList = ref([])

const filteredList = computed(() => {
  if (!searchKey.value) return leaveList.value
  return leaveList.value.filter(i => i.studentName.includes(searchKey.value))
})

const statusClass = (s) => {
  const map = { pending: 'tag-warning', approved: 'tag-success', rejected: 'tag-danger', refunded: 'tag-info' }
  return map[s] || 'tag-default'
}

onMounted(async () => {
  try {
    const res = await coachApi.getStudentLeaveList()
    if (res.data) leaveList.value = res.data
  } catch (e) { /* default */ }
})
</script>