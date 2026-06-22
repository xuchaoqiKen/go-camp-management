<template>
  <div class="page-container">
    <van-nav-bar title="请假记录" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-tabs v-model:active="activeTab">
      <van-tab title="请假记录">
        <div v-if="leaveList.length === 0" class="empty-state">
          <van-icon name="notes-o" size="48" color="#c8c9cc" />
          <div class="text-muted mt-8">暂无请假记录</div>
        </div>
        <div v-for="item in leaveList" :key="item.id" class="card">
          <div class="flex-between">
            <div>
              <div style="font-weight:500">{{ item.studentName }}</div>
              <div class="text-muted">{{ item.date }} | {{ item.typeText }}</div>
            </div>
            <span :class="['tag', item.statusClass]">{{ item.statusText }}</span>
          </div>
          <van-divider style="margin:8px 0" />
          <div class="text-muted" style="font-size:13px">
            <div>期数：{{ item.sessionName }}</div>
            <div>提交时间：{{ item.createTime }}</div>
            <div v-if="item.auditOpinion">审批意见：{{ item.auditOpinion }}</div>
          </div>
        </div>
      </van-tab>
      <van-tab title="退费记录">
        <div v-if="refundList.length === 0" class="empty-state">
          <van-icon name="gold-coin-o" size="48" color="#c8c9cc" />
          <div class="text-muted mt-8">暂无退费记录</div>
        </div>
        <div v-for="item in refundList" :key="item.id" class="card">
          <div class="flex-between">
            <div>
              <div style="font-weight:500">{{ item.studentName }}</div>
              <div class="text-muted">退款项目：{{ item.items }}</div>
            </div>
            <span :class="['tag', item.statusClass]">{{ item.statusText }}</span>
          </div>
          <van-divider style="margin:8px 0" />
          <div class="flex-between">
            <span class="text-muted">可退金额</span>
            <span class="price">¥{{ item.amount }}</span>
          </div>
        </div>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeTab = ref(0)

const leaveList = ref([
  {
    id: 1,
    studentName: '张三',
    date: '2026-07-16',
    typeText: '请假不扣课',
    statusText: '待审核',
    statusClass: 'tag-warning',
    sessionName: '第一期',
    createTime: '2026-07-15 10:30',
    auditOpinion: ''
  }
])

const refundList = ref([])
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 0;
}
</style>