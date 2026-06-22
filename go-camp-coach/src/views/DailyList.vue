<template>
  <div class="page-container">
    <van-nav-bar title="每日教学名单" left-arrow @click-left="$router.back()" fixed placeholder />
    <van-search v-model="searchKey" placeholder="搜索学员姓名" shape="round" />
    <div class="card" v-for="item in filteredList" :key="item.studentId">
      <div class="fw-bold">{{ item.studentName }} <span class="text-muted">({{ item.rank }})</span></div>
      <div class="text-muted mt-12">段位分类：{{ item.rankCategory || '待确认' }}</div>
      <div class="text-muted mt-12">住宿：{{ item.accommodationText || '不住宿' }}</div>
      <div class="mt-12">
        <span :class="['tag', item.isLeave ? 'tag-warning' : 'tag-success']">{{ item.isLeave ? '请假' : '在课' }}</span>
        <span class="text-muted ml-8" v-if="item.className">班级：{{ item.className }}</span>
      </div>
    </div>
    <van-empty v-if="!filteredList.length" description="暂无今日上课学员" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { coachApi } from '@/api'

const searchKey = ref('')
const studentList = ref([])

const filteredList = computed(() => {
  if (!searchKey.value) return studentList.value
  return studentList.value.filter(i => i.studentName.includes(searchKey.value))
})

onMounted(async () => {
  try {
    const res = await coachApi.getDailyList()
    if (res.data) studentList.value = res.data
  } catch (e) { /* default */ }
})
</script>