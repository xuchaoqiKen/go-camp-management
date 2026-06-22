<template>
  <div class="page-container">
    <van-nav-bar title="我的" fixed placeholder />
    <div class="card">
      <van-cell-group inset>
        <van-cell title="姓名" :value="info.name" />
        <van-cell title="手机号" :value="info.phone" />
        <van-cell title="所属部门" :value="info.department" />
        <van-cell title="授课日期" :value="info.teachDate || '待排班'" />
        <van-cell title="到岗状态" :value="info.statusText || '未排班'" />
      </van-cell-group>
    </div>

    <div class="card">
      <div class="section-title">功能入口</div>
      <van-cell-group inset>
        <van-cell title="撤餐记录" is-link to="/meal-cancel" />
        <van-cell title="学生请假查看" is-link to="/student-leave" />
        <van-cell title="每日教学名单" is-link to="/daily-list" />
      </van-cell-group>
    </div>

    <div class="card">
      <div class="section-title">撤餐记录</div>
      <div v-if="mealRecords.length">
        <div v-for="r in mealRecords" :key="r.id" class="card" style="background:#f9f9f9">
          <div class="fw-bold">{{ r.date }} {{ r.mealType === 'lunch' ? '午餐' : '晚餐' }}</div>
          <div class="text-muted">提交时间：{{ r.submitTime }}</div>
          <div class="mt-12">
            <span :class="['tag', statusClass(r.status)]">{{ r.statusText }}</span>
            <span class="tag tag-danger ml-8" v-if="r.isOverdue">逾期</span>
          </div>
        </div>
      </div>
      <van-empty v-else description="暂无撤餐记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { coachApi } from '@/api'

const info = ref({ name: '', phone: '', department: '', teachDate: '', statusText: '' })
const mealRecords = ref([])

const statusClass = (s) => {
  const map = { pending: 'tag-warning', approved: 'tag-success', rejected: 'tag-danger' }
  return map[s] || 'tag-default'
}

onMounted(async () => {
  try {
    const [infoRes, recordRes] = await Promise.all([coachApi.getMineInfo(), coachApi.getMealCancelRecords()])
    if (infoRes.data) info.value = infoRes.data
    if (recordRes.data) mealRecords.value = recordRes.data
  } catch (e) { /* default */ }
})
</script>