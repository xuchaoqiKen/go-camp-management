<template>
  <div class="order-page">
    <!-- 可折叠查询区域 -->
    <div class="filter-bar">
      <div class="filter-summary" @click="filterExpanded = !filterExpanded">
        <span class="filter-label">筛选：订单号 / 学员姓名 / 期数 / 支付状态 / 房间匹配状态 / 报名时间</span>
        <el-button type="primary" class="query-btn" @click.stop="fetchData">查询</el-button>
        <el-icon class="expand-icon" :class="{ expanded: filterExpanded }"><ArrowDown /></el-icon>
      </div>
      <div class="filter-content" :class="{ expanded: filterExpanded }">
        <el-form :inline="true" :model="query">
          <el-form-item label="订单号">
            <el-input v-model="query.orderNo" placeholder="请输入订单号" clearable />
          </el-form-item>
          <el-form-item label="学员姓名">
            <el-input v-model="query.studentName" placeholder="请输入学员姓名" clearable />
          </el-form-item>
          <el-form-item label="期数">
            <el-select v-model="query.sessionId" clearable placeholder="请选择">
              <el-option label="第一期" :value="1" />
              <el-option label="第二期" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="支付状态">
            <el-select v-model="query.paymentStatus" clearable placeholder="请选择">
              <el-option label="已支付" value="paid" />
              <el-option label="待支付" value="unpaid" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
          </el-form-item>
          <el-form-item label="房间匹配状态">
            <el-select v-model="query.roomMatchStatus" clearable placeholder="请选择">
              <el-option label="待匹配" value="pending" />
              <el-option label="已匹配" value="matched" />
              <el-option label="已确认" value="confirmed" />
              <el-option label="匹配失败" value="match_failed" />
            </el-select>
          </el-form-item>
          <el-form-item label="报名时间">
            <el-date-picker v-model="query.dateRange" type="daterange" range-separator="至"
              start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 订单表格 -->
    <el-table :data="list" class="order-table">
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="studentName" label="学员" />
      <el-table-column prop="sessionName" label="期数" />
      <el-table-column prop="rankCategory" label="段位分类" />
      <el-table-column prop="accommodationType" label="住宿类型">
        <template #default="{ row }">
          <el-tag :type="getAccommodationTagType(row.accommodationType)" size="small">
            {{ formatAccommodationType(row.accommodationType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roomMatchStatus" label="房间状态">
        <template #default="{ row }">
          <el-tag :type="getRoomStatusTagType(row.roomMatchStatus)" size="small">
            {{ formatRoomMatchStatus(row.roomMatchStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" align="right">
        <template #default="{ row }">
          ¥{{ row.amount || 0 }}
        </template>
      </el-table-column>
      <el-table-column prop="paymentStatus" label="状态">
        <template #default="{ row }">
          <el-tag :type="getPaymentStatusTagType(row.paymentStatus)" size="small">
            {{ formatPayStatus(row.paymentStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="showDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="学员姓名">{{ detailData.studentName }}</el-descriptions-item>
        <el-descriptions-item label="期数">{{ detailData.sessionName }}</el-descriptions-item>
        <el-descriptions-item label="段位分类">{{ detailData.rankCategory }}</el-descriptions-item>
        <el-descriptions-item label="住宿类型">{{ formatAccommodationType(detailData.accommodationType) }}</el-descriptions-item>
        <el-descriptions-item label="房间匹配状态">{{ formatRoomMatchStatus(detailData.roomMatchStatus) }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag :type="getPaymentStatusTagType(detailData.paymentStatus)" size="small">
            {{ formatPayStatus(detailData.paymentStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ detailData.amount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ formatTime(detailData.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 订单详情关键区块 -->
    <div class="detail-section">
      <h3 class="section-title">订单详情关键区块</h3>
      <div class="detail-grid">
        <div class="detail-card">
          <div class="card-title">学生信息</div>
          <div class="card-desc">姓名、年龄、性别、填报段位、系统段位分类；线下班级仅作可选备注。</div>
        </div>
        <div class="detail-card">
          <div class="card-title">费用明细</div>
          <div class="card-desc">早餐/午餐/晚餐、全托管、陪住标间/大床；全托管含住宿早餐不重复收费。</div>
        </div>
        <div class="detail-card">
          <div class="card-title">房间匹配</div>
          <div class="card-desc">住宿类型、匹配状态、房间号、床位号、线下调整记录。</div>
        </div>
        <div class="detail-card">
          <div class="card-title">请假关联</div>
          <div class="card-desc">请假记录、退款/退款影响；管理员审核，教练和工作人员可见。</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'
import { getOrderList, getOrderDetail } from '@/api'

const filterExpanded = ref(false)
const query = reactive({
  studentName: '',
  orderNo: '',
  sessionId: '',
  paymentStatus: '',
  roomMatchStatus: '',
  dateRange: []
})
const list = ref([])
const detailVisible = ref(false)
const detailData = ref({})

async function fetchData() {
  const params = {
    ...query,
    page: 1,
    size: 10,
    startDate: query.dateRange?.[0] || '',
    endDate: query.dateRange?.[1] || ''
  }
  const res = await getOrderList(params)
  list.value = res.data?.records || []
}

async function showDetail(id) {
  const res = await getOrderDetail(id)
  detailData.value = res.data || {}
  detailVisible.value = true
}

function formatPayStatus(status) {
  const statusMap = { paid: '已支付', unpaid: '待支付', cancelled: '已取消', refunding: '退款中', refunded: '已退款' }
  return statusMap[status] || status || '-'
}

function getPaymentStatusTagType(status) {
  const typeMap = { paid: 'success', unpaid: 'warning', cancelled: 'info', refunding: 'primary', refunded: 'danger' }
  return typeMap[status] || ''
}

function formatRoomMatchStatus(status) {
  const statusMap = { pending: '待匹配', matched: '已匹配', confirmed: '已确认', match_failed: '匹配失败', manual_adjusted: '已调整' }
  return statusMap[status] || status || '-'
}

function getRoomStatusTagType(status) {
  const typeMap = { pending: 'warning', matched: 'primary', confirmed: 'success', match_failed: 'danger', manual_adjusted: 'info' }
  return typeMap[status] || ''
}

function formatAccommodationType(type) {
  const typeMap = { no_stay: '不住宿', full_board: '全托管', accompany_standard: '陪住标间', accompany_king: '陪住大床' }
  return typeMap[type] || type || '-'
}

function getAccommodationTagType(type) {
  const typeMap = { no_stay: 'info', full_board: 'success', accompany_standard: 'warning', accompany_king: 'primary' }
  return typeMap[type] || ''
}

function formatTime(time) {
  if (!time) return '-'
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

onMounted(fetchData)
</script>

<style scoped>
.order-page {
  padding: 0;
}

/* 筛选栏 */
.filter-bar {
  margin-bottom: 24px;
}
.filter-summary {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #f3f4f6;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}
.filter-label {
  flex: 1;
  font-size: 14px;
  color: #6b7280;
}
.query-btn {
  border-radius: 20px;
  padding: 8px 24px;
  background: #059669;
  border-color: #059669;
}
.query-btn:hover {
  background: #047857 !important;
  border-color: #047857 !important;
}
.expand-icon {
  margin-left: 12px;
  color: #6b7280;
  transition: transform 0.3s;
}
.expand-icon.expanded {
  transform: rotate(180deg);
}

.filter-content {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
}
.filter-content.expanded {
  max-height: 200px;
  padding-top: 16px;
}
.filter-content :deep(.el-form-item) {
  margin-bottom: 12px;
}
.filter-content :deep(.el-input),
.filter-content :deep(.el-select),
.filter-content :deep(.el-date-editor) {
  width: 160px;
}

/* 订单表格 */
.order-table {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}
.order-table :deep(.el-table__header-wrapper) {
  background: #f0fdf4;
}
.order-table :deep(.el-table__header th) {
  background: #f0fdf4;
  color: #065f46;
  font-weight: 600;
}
.order-table :deep(.el-table__row:hover) {
  background: #f9fafb;
}

/* 详情区块 */
.detail-section {
  margin-top: 24px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 24px;
}
.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 20px 0;
}
.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}
.detail-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px 20px;
}
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 6px;
}
.card-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
}
</style>