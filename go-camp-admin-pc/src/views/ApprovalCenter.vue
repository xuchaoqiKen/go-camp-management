<template>
  <div class="approval-page">
    <!-- Tab按钮 -->
    <div class="tab-container">
      <div class="tab-buttons">
        <button 
          v-for="tab in tabs" 
          :key="tab.name"
          class="tab-btn" 
          :class="{ active: activeTab === tab.name }"
          @click="switchTab(tab.name)"
        >
          {{ tab.label }}
          <span v-if="tab.count > 0" class="tab-count">{{ tab.count }}</span>
        </button>
      </div>
      <el-button v-if="activeTab === 'leave'" type="success" class="manual-add-btn" @click="showManualAdd">
        手动新增请假
      </el-button>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tags">
      <span>筛选：学员张三  期数 第一期  日期 7月  状态 待审核  来源 全部</span>
    </div>

    <!-- 表格 -->
    <el-table :data="list" class="approval-table" v-loading="loading">
      <el-table-column prop="requestNo" label="申请编号" />
      <el-table-column prop="studentId" label="学员ID" />
      <el-table-column prop="leaveDate" label="请假日期" />
      <el-table-column label="类型">
        <template #default="{ row }">
          {{ getApplyType(row) }}
        </template>
      </el-table-column>
      <el-table-column label="来源">
        <template #default="{ row }">
          {{ formatSource(row.source) }}
        </template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)" size="small">
            {{ formatStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button v-if="row.status === 'pending'" type="primary" size="small" link @click="handleApprove(row)">审核</el-button>
          <el-button v-else size="small" link @click="viewDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 手动新增请假弹窗 -->
    <el-dialog v-model="manualAddVisible" title="手动新增请假" width="900px">
      <el-form :model="form" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="学员姓名/手机号">
              <el-select v-model="form.studentId" filterable placeholder="请选择学员" style="width: 100%">
                <el-option
                  v-for="item in students"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="期数">
              <el-select v-model="form.sessionId" placeholder="请选择期数" style="width: 100%">
                <el-option label="第一期" value="1" />
                <el-option label="第二期" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="请假日期">
              <el-date-picker v-model="form.leaveDate" type="date" placeholder="请选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="请假类型">
              <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
                <el-option label="请假不扣课" value="NO_DEDUCTION" />
                <el-option label="请假扣课" value="DEDUCTION" />
                <el-option label="退午餐" value="MEAL_CANCEL" />
                <el-option label="退住宿" value="ACCOMMODATION_CANCEL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否退餐">
              <el-switch v-model="form.refundMeal" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否退住宿">
              <el-switch v-model="form.refundAccommodation" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="原因说明">
              <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请输入原因说明" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="manualAddVisible = false">取消</el-button>
        <el-button type="primary" @click="submitLeave" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>

    <!-- 审核弹窗 -->
    <el-dialog v-model="approveVisible" title="审核" width="500px">
      <el-form label-width="100px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="approveForm.approved">
            <el-radio :label="true">通过</el-radio>
            <el-radio :label="false">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" v-if="!approveForm.approved">
          <el-input v-model="approveForm.reason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApprove" :loading="approveLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="600px">
      <el-descriptions :column="1" border v-if="detailData">
        <el-descriptions-item label="申请编号">{{ detailData.requestNo }}</el-descriptions-item>
        <el-descriptions-item label="学员ID">{{ detailData.studentId }}</el-descriptions-item>
        <el-descriptions-item label="请假日期">{{ detailData.leaveDate }}</el-descriptions-item>
        <el-descriptions-item label="请假类型">{{ getApplyType(detailData) }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{ formatSource(detailData.source) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(detailData.status)">{{ formatStatus(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="原因" v-if="detailData.reason">{{ detailData.reason }}</el-descriptions-item>
        <el-descriptions-item label="审核意见" v-if="detailData.reviewOpinion">{{ detailData.reviewOpinion }}</el-descriptions-item>
        <el-descriptions-item label="提交时间" v-if="detailData.submitTime">{{ detailData.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="detailData.reviewTime">{{ detailData.reviewTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getApprovalList, approveLeave, addManualLeave, getStudentList, getLeaveDetail } from '@/api'

const activeTab = ref('leave')
const loading = ref(false)
const submitting = ref(false)
const approveLoading = ref(false)
const tabs = computed(() => [
  { name: 'leave', label: '请假审批', count: 0 },
  { name: 'refund', label: '退款审批', count: 0 },
  { name: 'mealCancel', label: '撤餐审批', count: 0 }
])

const list = ref([])
const students = ref([])
const detailVisible = ref(false)
const detailData = ref(null)
const manualAddVisible = ref(false)
const approveVisible = ref(false)
const currentApproveRow = ref(null)

const form = reactive({
  studentId: '',
  sessionId: '',
  leaveDate: '',
  type: '',
  refundMeal: false,
  refundAccommodation: false,
  reason: ''
})

const approveForm = reactive({
  approved: true,
  reason: ''
})

async function loadList() {
  loading.value = true
  try {
    const res = await getApprovalList({
      type: activeTab.value,
      page: 1,
      size: 100
    })
    list.value = res.data?.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function switchTab(tab) {
  activeTab.value = tab
  loadList()
}

function getApplyType(row) {
  const type = row.leaveType || row.type
  const map = {
    'NO_DEDUCTION': '请假不扣课',
    'DEDUCTION': '请假扣课',
    'MEAL_CANCEL': '退午餐',
    'ACCOMMODATION_CANCEL': '退住宿',
    'no_deduction': '请假不扣课',
    'deduction': '请假扣课',
    'meal_cancel': '退午餐',
    'accommodation_cancel': '退住宿'
  }
  return map[type] || '请假'
}

function formatStatus(status) {
  const map = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已驳回',
    'refunded': '已退款',
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已驳回',
    'REFUNDED': '已退款'
  }
  return map[status] || status || '-'
}

function getStatusTagType(status) {
  const map = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger',
    'refunded': 'success',
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'REFUNDED': 'success'
  }
  return map[status] || 'info'
}

function formatSource(source) {
  const map = { parent: '家长提交', coach: '教练提交', admin: '后台新增', manual: '后台新增' }
  return map[source] || source || '-'
}

function showManualAdd() {
  manualAddVisible.value = true
}

async function submitLeave() {
  if (!form.studentId) {
    ElMessage.warning('请选择学员')
    return
  }
  if (!form.sessionId) {
    ElMessage.warning('请选择期数')
    return
  }
  if (!form.leaveDate) {
    ElMessage.warning('请选择请假日期')
    return
  }
  if (!form.type) {
    ElMessage.warning('请选择请假类型')
    return
  }
  
  submitting.value = true
  try {
    await addManualLeave(form)
    ElMessage.success('新增请假成功')
    manualAddVisible.value = false
    loadList()
    
    // 重置表单
    Object.assign(form, {
      studentId: '',
      sessionId: '',
      leaveDate: '',
      type: '',
      refundMeal: false,
      refundAccommodation: false,
      reason: ''
    })
  } catch (e) {
    ElMessage.error('新增失败')
  } finally {
    submitting.value = false
  }
}

function handleApprove(row) {
  currentApproveRow.value = row
  approveForm.approved = true
  approveForm.reason = ''
  approveVisible.value = true
}

async function submitApprove() {
  if (!approveForm.approved && !approveForm.reason) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  
  approveLoading.value = true
  try {
    await approveLeave({
      id: currentApproveRow.value.id,
      approved: approveForm.approved,
      reason: approveForm.reason
    })
    ElMessage.success(approveForm.approved ? '审核通过' : '已驳回')
    approveVisible.value = false
    loadList()
  } catch (e) {
    ElMessage.error('审核失败')
  } finally {
    approveLoading.value = false
  }
}

async function viewDetail(row) {
  try {
    const res = await getLeaveDetail(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (e) {
    console.error(e)
  }
}

onMounted(async () => {
  loadList()
  try {
    const res = await getStudentList({ page: 1, size: 1000 })
    students.value = res.data?.records || []
  } catch (e) {}
})
</script>

<style scoped>
.approval-page {
  padding: 0;
}

/* Tab容器 */
.tab-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.tab-buttons {
  display: flex;
  gap: 8px;
  background: #e5e7eb;
  padding: 4px;
  border-radius: 24px;
}
.tab-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  background: transparent;
  color: #4b5563;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}
.tab-btn.active {
  background: #059669;
  color: white;
}
.tab-count {
  background: rgba(255,255,255,0.2);
  padding: 0 6px;
  border-radius: 10px;
  font-size: 12px;
}
.manual-add-btn {
  border-radius: 20px;
  padding: 8px 24px;
  background: #059669;
  border-color: #059669;
}
.manual-add-btn:hover {
  background: #047857 !important;
  border-color: #047857 !important;
}

/* 筛选标签 */
.filter-tags {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 20px;
  padding: 12px 16px;
  margin-bottom: 20px;
}
.filter-tags span {
  font-size: 14px;
  color: #6b7280;
}

/* 表格 */
.approval-table {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}
.approval-table :deep(.el-table__header-wrapper) {
  background: #dcfce7;
}
.approval-table :deep(.el-table__header th) {
  background: #dcfce7;
  color: #166534;
  font-weight: 600;
}
</style>
