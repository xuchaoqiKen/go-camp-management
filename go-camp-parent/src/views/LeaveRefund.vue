<template>
  <div class="page-container">
    <van-nav-bar title="请假退费" left-arrow @click-left="$router.back()" fixed placeholder />

    <!-- 功能入口 -->
    <div class="card">
      <van-cell title="请假申请" icon="notes-o" is-link to="/leave-refund/apply" label="选择学员、期数和日期提交请假" />
      <van-cell title="退费申请" icon="gold-coin-o" is-link to="/leave-refund/refund" label="申请早餐/午餐/晚餐/住宿退费" />
    </div>

    <!-- 近期申请记录 -->
    <div class="card mt-12">
      <div class="section-title">近期申请</div>
      <div v-if="recentList.length === 0" class="text-muted" style="text-align:center;padding:20px 0">暂无申请记录</div>
      <div v-for="item in recentList" :key="item.id" class="leave-item" @click="viewDetail(item)">
        <div class="flex-between">
          <div>
            <div class="leave-student">{{ item.studentName }}</div>
            <div class="text-muted">{{ item.date }} | {{ item.typeText }}</div>
          </div>
          <div class="flex-center">
            <span :class="['tag', item.statusClass]">{{ item.statusText }}</span>
            <van-icon name="arrow" color="#969799" class="ml-8" />
          </div>
        </div>
      </div>
    </div>

    <!-- 规则说明 -->
    <div class="card mt-12">
      <div class="section-title">规则说明</div>
      <div class="text-muted" style="line-height:1.8">
        <p>1. 请假/退费需提前12小时申请</p>
        <p>2. 低于规定时间红线即为逾期，不允许特批</p>
        <p>3. 退款只针对后勤费用，不涉及学费</p>
        <p>4. 已消费项目不可退款</p>
        <p>5. 托管/住宿已包含的早餐不可重复退款</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const recentList = ref([
  {
    id: 1,
    studentName: '张三',
    date: '2026-07-16',
    typeText: '请假不扣课',
    statusText: '待审核',
    statusClass: 'tag-warning'
  }
])

function viewDetail(item) {
  // 跳转详情
}
</script>

<style scoped>
.leave-item {
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}
.leave-item:last-child {
  border-bottom: none;
}
.leave-student {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 4px;
}
.ml-8 {
  margin-left: 8px;
}
</style>