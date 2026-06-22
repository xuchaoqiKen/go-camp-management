<template>
  <div class="page-container">
    <van-nav-bar title="报名结果" left-arrow @click-left="$router.back()" fixed placeholder />

    <div v-for="student in students" :key="student.id" class="card">
      <div class="section-title">{{ student.name }}</div>
      <van-cell-group inset>
        <van-cell title="段位分类" :value="student.rankCategory || '待确认'" />
        <van-cell title="所属班级" :value="student.className || '待分配'" />
        <van-cell title="住宿类型" :value="student.accommodationType || '-'" />
        <van-cell title="房间号" :value="student.roomNumber || '待系统匹配/待老师调整'" />
        <van-cell title="床位号" :value="student.bedNumber || '-'" />
        <van-cell title="早餐来源" :value="student.breakfastSource || '-'" />
      </van-cell-group>
    </div>

    <div class="card mt-12">
      <div class="section-title">订单信息</div>
      <van-cell-group inset>
        <van-cell title="订单编号" :value="orderInfo.orderNo || '-'" />
        <van-cell title="支付编号" :value="orderInfo.paymentNo || '-'" />
        <van-cell title="后勤费合计" :value="'¥' + (orderInfo.totalFee || 0)" />
        <van-cell title="支付状态" value="已支付">
          <template #value><span class="tag tag-success">已支付</span></template>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { mineApi } from '@/api'

const route = useRoute()

const students = ref([
  {
    id: 1,
    name: '张三',
    rankCategory: '段位组',
    className: '段位A班',
    accommodationType: '全托管',
    roomNumber: 'A栋301',
    bedNumber: '1床',
    breakfastSource: '托管包含'
  }
])

const orderInfo = ref({
  orderNo: 'GO20260715001',
  paymentNo: 'PAY20260715001',
  totalFee: 3270
})

onMounted(async () => {
  try {
    const res = await mineApi.getRegistrationResult()
    if (res.data) {
      students.value = res.data.students || students.value
      orderInfo.value = res.data.orderInfo || orderInfo.value
    }
  } catch (e) { /* 使用模拟数据 */ }
})
</script>