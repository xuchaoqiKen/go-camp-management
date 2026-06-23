<template>
  <div class="page-container">
    <van-nav-bar title="退款详情" left-arrow @click-left="$router.back()" />
    <div class="card" v-if="detail">
      <div class="flex-between"><span style="font-weight:600;font-size:16px">{{ detail.refundNo }}</span><van-tag :type="statusTagType(detail.status)">{{ detail.statusText }}</van-tag></div>
      <van-cell-group inset style="margin-top:12px">
        <van-cell title="学员" :value="detail.studentName" />
        <van-cell title="关联订单" :value="detail.orderNo" />
        <van-cell title="期数" :value="detail.sessionText" />
        <van-cell title="退款项目" :value="detail.refundItems" />
        <van-cell title="可退金额"><span style="color:#ee0a24;font-weight:600">¥{{ detail.refundAmount }}</span></van-cell>
        <van-cell title="提交时间" :value="detail.createTime" />
        <van-cell title="原因" :value="detail.reason || '-'" />
        <van-cell v-if="detail.approvalOpinion" title="审批意见" :value="detail.approvalOpinion" />
        <van-cell v-if="detail.approverName" title="审批人" :value="detail.approverName" />
        <van-cell v-if="detail.approveTime" title="审批时间" :value="detail.approveTime" />
        <van-cell v-if="detail.wxRefundNo" title="微信退款流水号" :value="detail.wxRefundNo" />
        <van-cell v-if="detail.failReason" title="失败原因" :value="detail.failReason" />
        <van-cell v-if="detail.retryCount" title="重试次数" :value="detail.retryCount" />
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
    <div class="card" v-if="detail && detail.status === 'APPROVED'">
      <h4 style="margin-bottom:12px">退款执行</h4>
      <van-button type="primary" block @click="executeRefund">执行微信原路退款</van-button>
    </div>
    <div class="card" v-if="detail && detail.status === 'FAILED'">
      <h4 style="margin-bottom:12px">退款重试</h4>
      <van-button type="warning" block @click="executeRefund">重新发起退款</van-button>
    </div>
    <div v-if="!detail" class="empty-state"><van-loading /><p>加载中...</p></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showSuccessToast, showConfirmDialog } from 'vant'
import adminApi from '@/api'

const route = useRoute()
const router = useRouter()
const detail = ref(null)
const opinion = ref('')

const statusTagType = (s) => {
  const map = { PENDING: 'warning', APPROVED: 'primary', REFUNDED: 'success', REJECTED: 'danger', FAILED: 'danger' }
  return map[s] || 'default'
}

const fetchDetail = async () => {
  try {
    const res = await adminApi.getRefundDetail(route.params.id)
    detail.value = res.data
  } catch (e) { /* handled */ }
}

const approve = async (status) => {
  try {
    await adminApi.approveRefund(route.params.id, { status, opinion: opinion.value })
    showSuccessToast(status === 'APPROVED' ? '已同意' : '已驳回')
    router.back()
  } catch (e) { /* handled */ }
}

const executeRefund = async () => {
  try {
    await showConfirmDialog({ title: '确认退款', message: '确认执行微信原路退款？' })
    await adminApi.executeRefund(route.params.id)
    showSuccessToast('退款已发起')
    fetchDetail()
  } catch (e) { /* handled */ }
}

onMounted(fetchDetail)
</script>