<template>
  <div class="page-container">
    <van-nav-bar title="撤餐申请" left-arrow @click-left="$router.back()" fixed placeholder />
    <div class="card" v-for="item in mealList" :key="item.id">
      <div class="fw-bold">{{ item.date }} {{ item.mealType === 'lunch' ? '工作午餐' : '工作晚餐' }}</div>
      <div class="text-muted mt-12">开餐时间：{{ item.mealTime }}</div>
      <div class="mt-12">
        <van-button v-if="item.canCancel" type="danger" size="small" @click="handleCancel(item)">申请撤餐</van-button>
        <span v-else class="tag tag-default">已取消/不可撤</span>
      </div>
    </div>
    <van-empty v-if="!mealList.length" description="暂无排班餐食记录" />

    <van-dialog v-model:show="showOverdue" title="逾期确认" message="已逾期，未就餐将由教练个人承担餐费损耗" show-cancel-button confirm-button-text="确认提交" @confirm="confirmOverdue" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import { coachApi } from '@/api'

const mealList = ref([])
const showOverdue = ref(false)
const currentCancel = ref(null)

onMounted(async () => {
  try {
    const res = await coachApi.getMealCancelList()
    if (res.data) mealList.value = res.data
  } catch (e) { /* default empty */ }
})

const handleCancel = async (item) => {
  if (item.isOverdue) {
    currentCancel.value = item
    showOverdue.value = true
    return
  }
  await doCancel(item)
}

const confirmOverdue = async () => {
  showOverdue.value = false
  if (currentCancel.value) await doCancel(currentCancel.value, true)
}

const doCancel = async (item, overdue = false) => {
  try {
    await coachApi.submitMealCancel({ id: item.id, overdue })
    showToast('撤餐申请已提交')
    mealList.value = mealList.value.map(m => m.id === item.id ? { ...m, canCancel: false } : m)
  } catch (e) { /* error handled */ }
}
</script>