<template>
  <div class="page-container">
    <van-nav-bar title="费用结算" left-arrow @click-left="$router.back()" fixed placeholder />

    <!-- 学费锚定展示 -->
    <div class="card tuition-card">
      <div class="flex-between">
        <span>学费参考（400元/天）</span>
        <span class="text-muted">仅作价值参考，不计入支付</span>
      </div>
    </div>

    <!-- 学员费用明细 -->
    <div class="card" v-for="(order, idx) in feeItems" :key="idx">
      <div class="section-title">{{ order.studentName }} - 后勤费用明细</div>

      <div class="fee-row flex-between" v-if="order.breakfastCount > 0">
        <span>早餐 × {{ order.breakfastCount }}份</span>
        <span class="price">¥{{ order.breakfastFee }}</span>
      </div>
      <div class="fee-row flex-between" v-if="order.lunchCount > 0">
        <span>午餐 × {{ order.lunchCount }}份</span>
        <span class="price">¥{{ order.lunchFee }}</span>
      </div>
      <div class="fee-row flex-between" v-if="order.dinnerCount > 0">
        <span>晚餐 × {{ order.dinnerCount }}份</span>
        <span class="price">¥{{ order.dinnerFee }}</span>
      </div>
      <div class="fee-row flex-between" v-if="order.accommodationFee > 0">
        <span>{{ order.accommodationType }} × {{ order.accommodationNights }}晚</span>
        <span class="price">¥{{ order.accommodationFee }}</span>
      </div>

      <van-divider />
      <div class="flex-between">
        <span class="section-title" style="border:none;margin:0;padding:0">小计</span>
        <span class="price" style="font-size:18px">¥{{ order.totalFee }}</span>
      </div>
    </div>

    <!-- 合计 -->
    <div class="card">
      <div class="flex-between">
        <span style="font-size:16px;font-weight:600">后勤费合计</span>
        <span class="price" style="font-size:20px">¥{{ totalFee }}</span>
      </div>
      <div class="text-muted mt-8">多学员合并支付，支付成功后生成各学员独立订单</div>
    </div>

    <!-- 支付按钮 -->
    <van-button block type="primary" size="large" @click="handlePay" :loading="paying" class="mt-16">
      支付后勤费 ¥{{ totalFee }}
    </van-button>

    <!-- 微信支付弹窗模拟 -->
    <van-dialog
      v-model:show="showPayDialog"
      title="微信支付"
      message="模拟调起微信支付..."
      show-cancel-button
      @confirm="confirmPay"
    >
      <div class="pay-amount">¥{{ totalFee }}</div>
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { useRegistrationStore } from '@/stores/registration'

const router = useRouter()
const store = useRegistrationStore()

const paying = ref(false)
const showPayDialog = ref(false)

// 模拟费用数据
const feeItems = ref([
  {
    studentName: '张三',
    breakfastCount: 8,
    breakfastFee: 120,
    lunchCount: 9,
    lunchFee: 225,
    dinnerCount: 9,
    dinnerFee: 225,
    accommodationType: '全托管',
    accommodationNights: 9,
    accommodationFee: 2700,
    totalFee: 3270
  }
])

const totalFee = computed(() => feeItems.value.reduce((sum, item) => sum + item.totalFee, 0))

onMounted(async () => {
  // 实际项目中从store获取或调用API计算
  if (store.students.length > 0) {
    try {
      const result = await store.calculateFee()
      if (result) {
        feeItems.value = result.map(r => ({
          studentName: r.studentName,
          breakfastCount: r.breakfastCount || 0,
          breakfastFee: r.breakfastFee || 0,
          lunchCount: r.lunchCount || 0,
          lunchFee: r.lunchFee || 0,
          dinnerCount: r.dinnerCount || 0,
          dinnerFee: r.dinnerFee || 0,
          accommodationType: r.accommodationType || '不住宿',
          accommodationNights: r.accommodationNights || 0,
          accommodationFee: r.accommodationFee || 0,
          totalFee: r.totalFee || 0
        }))
      }
    } catch (e) {
      // 使用模拟数据
    }
  }
})

function handlePay() {
  showPayDialog.value = true
}

async function confirmPay() {
  paying.value = true
  try {
    // 提交订单
    await store.submitOrder()
    // 创建支付
    const orderIds = store.currentOrders.map(o => o.id)
    await store.createPayment(orderIds)

    showPayDialog.value = false
    showSuccessToast('支付成功')
    setTimeout(() => {
      router.push('/mine')
    }, 1500)
  } catch (e) {
    showToast('支付失败，请重试')
  } finally {
    paying.value = false
  }
}
</script>

<style scoped>
.tuition-card {
  background: #fffbe8;
  border: 1px solid #ffe58f;
}
.fee-row {
  padding: 8px 0;
  font-size: 14px;
}
.pay-amount {
  text-align: center;
  font-size: 24px;
  color: #ee0a24;
  font-weight: 600;
  padding: 16px 0;
}
</style>