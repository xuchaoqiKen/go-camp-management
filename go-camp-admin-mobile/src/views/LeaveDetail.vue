<template>
  <div class="page-container">
    <van-nav-bar title="请假详情" left-arrow @click-left="$router.back()" />
    <div class="card" v-if="detail">
      <div class="flex-between"><span style="font-weight:600;font-size:16px">{{ detail.studentName }}</span><van-tag :type="statusTagType(detail.status)">{{ detail.statusText }}</van-tag></div>
      <van-cell-group inset style="margin-top:12px">
        <van-cell title="期数" :value="detail.sessionText" />
        <van-cell title="请假日期" :value="detail.leaveDate" />
        <van-cell title="请假类型" :value="detail.typeText" />
        <van-cell title="来源" :value="detail.sourceText" />
        <van-cell title="提交时间" :value="detail.createTime" />
        <van-cell title="原因" :value="detail.reason || '-'" />
        <van-cell v-if="detail.approvalOpinion" title="审批意见" :value="detail.approvalOpinion" />
        <van-cell v-if="detail.approverName" title="审批人" :value="detail.approverName" />
        <van-cell v-if="detail.approveTime" title="审批时间" :value="detail.approveTime" />
      </van-cell-group>
    </div>
    <div class="card" v-if="detail && detail.status === 'PENDING'">
      <h4 style="margin-bottom:12px">审批操作</h4>
      <van-field v-model="opinion" label="审批意见" placeholder="选填" type="textarea" rows="2" />
      <div style="display:flex;gap:12px;margin-top:12px">
        <van-button type="success" block @click="approve('APPROVED')">同意</van-button>
        <van-button type="danger" block @click="approve('REJECTED')">驳回</van-button>
      </div>
    </div>
    <div v-if="!detail" class="empty-state"><van-loading /><p>加载中...</p></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import adminApi from '@/api'

const route = useRoute()
const router = useRouter()
const detail = ref(null)
const opinion = ref('')

const statusTagType = (s) => {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', REFUNDED: 'primary' }
  return map[s] || 'default'
}

const fetchDetail = async () => {
  try {
    const res = await adminApi.getLeaveDetail(route.params.id)
    detail.value = res.data
  } catch (e) { /* handled */ }
}

const approve = async (status) => {
  try {
    await adminApi.approveLeave(route.params.id, { status, opinion: opinion.value })
    showSuccessToast(status === 'APPROVED' ? '已同意' : '已驳回')
    router.back()
  } catch (e) { /* handled */ }
}

onMounted(fetchDetail)
</script>