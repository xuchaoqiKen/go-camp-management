<template>
  <div class="student-page">
    <!-- 可折叠查询区域 -->
    <div class="filter-bar">
      <div class="filter-summary" @click="filterExpanded = !filterExpanded">
        <span class="filter-label">筛选：学员编号 / 学员姓名 / 段位 / 班型/班级 / 在营状态</span>
        <el-button type="primary" class="query-btn" @click.stop="fetchData">查询</el-button>
        <el-button class="reset-btn" @click.stop="resetQuery">重置</el-button>
        <el-icon class="expand-icon" :class="{ expanded: filterExpanded }"><ArrowDown /></el-icon>
      </div>
      <div class="filter-content" :class="{ expanded: filterExpanded }">
        <el-form :inline="true" :model="query">
          <el-form-item label="学员编号">
            <el-input v-model="query.studentNo" placeholder="请输入学员编号" clearable />
          </el-form-item>
          <el-form-item label="学员姓名">
            <el-input v-model="query.name" placeholder="请输入学员姓名" clearable />
          </el-form-item>
          <el-form-item label="段位">
            <el-select v-model="query.rank" clearable placeholder="请选择段位">
              <el-option v-for="r in ranks" :key="r" :label="r" :value="r" />
            </el-select>
          </el-form-item>
          <el-form-item label="班型/班级">
            <el-select v-model="query.classId" clearable placeholder="请选择班型/班级">
              <el-option v-for="c in classList" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="在营状态">
            <el-select v-model="query.campStatus" clearable placeholder="请选择在营状态">
              <el-option label="在营" value="ACTIVE" />
              <el-option label="未入住" value="NOT_CHECKED_IN" />
              <el-option label="已离营" value="CHECKED_OUT" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 学员表格 -->
    <el-table :data="list" class="student-table">
      <el-table-column prop="studentNo" label="学员编号" />
      <el-table-column prop="name" label="学员姓名" />
      <el-table-column prop="gender" label="性别" align="center">
        <template #default="{ row }">
          {{ formatGender(row.gender) }}
        </template>
      </el-table-column>
      <el-table-column prop="age" label="年龄" align="center" />
      <el-table-column prop="rank" label="段位" align="center">
        <template #default="{ row }">
          {{ getRankName(row.rank || row.rankCode) }}
        </template>
      </el-table-column>
      <el-table-column prop="className" label="班型/班级" align="center">
        <template #default="{ row }">
          {{ getClassName(row.classId || row.classNameId) || row.className || row.classTitle || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="联系方式" align="center">
        <template #default="{ row }">
          {{ formatPhone(row.phone) }}
        </template>
      </el-table-column>
      <el-table-column prop="campStatus" label="在营状态" align="center" width="120">
        <template #default="{ row }">
          <el-select 
            :model-value="row.campStatus" 
            size="small" 
            @change="handleCampStatusChange(row, $event)"
            style="width: 100px"
          >
            <el-option label="在营" value="ACTIVE" />
            <el-option label="未入住" value="NOT_CHECKED_IN" />
            <el-option label="已离营" value="CHECKED_OUT" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="100">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="showDetail(row.id)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <span class="total-text">共 {{ total }} 条</span>
      <el-pagination 
        v-model:current-page="page" 
        :total="total" 
        :page-size="pageSize"
        @current-change="fetchData" 
        @size-change="handleSizeChange"
        layout="prev, pager, next, jumper, ->, sizes"
      />
      <div class="jump-wrap">
        <span>跳至</span>
        <el-input-number v-model="jumpPage" :min="1" :max="Math.max(1, Math.ceil(total/pageSize))" controls-position="right" @change="handleJump" />
        <span>页</span>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="学员详情" width="780px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="学员编号" :span="1">{{ detail.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="学员姓名" :span="1">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="性别" :span="1">{{ detail.gender }}</el-descriptions-item>
        <el-descriptions-item label="年龄" :span="1">{{ detail.age }}岁</el-descriptions-item>
        <el-descriptions-item label="段位" :span="1">{{ detail.rank }}</el-descriptions-item>
        <el-descriptions-item label="段位分类" :span="1">{{ detail.rankCategory || '-' }}</el-descriptions-item>
        <el-descriptions-item label="城市" :span="1">{{ detail.city }}</el-descriptions-item>
        <el-descriptions-item label="家长" :span="1">{{ detail.parentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话" :span="2">{{ detail.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属班级" :span="1">{{ detail.className || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="报名期数" :span="1">{{ detail.sessionName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="住宿类型" :span="1">{{ detail.accommodationType }}</el-descriptions-item>
        <el-descriptions-item label="房间号" :span="1">{{ detail.roomNo || '待匹配' }}</el-descriptions-item>
        <el-descriptions-item label="床位号" :span="1">{{ detail.bedNo || '待匹配' }}</el-descriptions-item>
        <el-descriptions-item label="在营状态" :span="1">
          <el-tag :type="getCampStatusType(detail.campStatus)" size="small">
            {{ formatCampStatus(detail.campStatus) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-tabs v-model="activeDetailTab" class="detail-tabs" type="border-card">
        <el-tab-pane label="每日排期" name="schedule">
          <el-table :data="detail.schedule || []" border size="small">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="hasClass" label="上课" width="80" align="center">
              <template #default="{ row }">
                <el-icon v-if="row.hasClass" color="#52c41a"><Check /></el-icon>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="breakfast" label="早餐" width="80" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.breakfast" size="small" type="success">含</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="lunch" label="午餐" width="80" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.lunch" size="small" type="success">含</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="dinner" label="晚餐" width="80" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.dinner" size="small" type="success">含</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="accommodation" label="住宿" width="80" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.accommodation" size="small" type="primary">住</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="请假记录" name="leave">
          <el-table :data="detail.leaveRecords || []" border size="small">
            <el-table-column prop="leaveDate" label="请假日期" width="120" />
            <el-table-column prop="leaveType" label="请假类型" width="120" />
            <el-table-column prop="reason" label="原因" min-width="150" />
            <el-table-column prop="status" label="状态" width="100" align="center" />
            <el-table-column prop="createTime" label="申请时间" width="160" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="退款记录" name="refund">
          <el-table :data="detail.refundRecords || []" border size="small">
            <el-table-column prop="requestNo" label="申请编号" width="160" />
            <el-table-column prop="refundType" label="退款类型" width="120" />
            <el-table-column prop="amount" label="金额" width="100" align="right">
              <template #default="{ row }">¥{{ row.amount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100" align="center" />
            <el-table-column prop="createTime" label="申请时间" width="160" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ArrowDown, Check } from '@element-plus/icons-vue'
import { getStudentList, getStudentDetail, getClassList, updateStudentCampStatus } from '@/api'

const filterExpanded = ref(false)
const ranks = ['无基础','18级','17级','16级','15级','14级','13级','12级','11级','10级','9级','8级','7级','6级','5级','4级','3级','2级','1级','1段','2段','3段','4段','5段','6段+']
const query = reactive({ 
  name: '', 
  studentNo: '', 
  rank: '', 
  classId: '',
  campStatus: ''
})
const list = ref([])
const classList = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const jumpPage = ref(1)
const detailVisible = ref(false)
const detail = ref(null)
const activeDetailTab = ref('schedule')

async function fetchData() {
  const res = await getStudentList({ ...query, page: page.value, size: pageSize.value })
  list.value = res.data?.records || []
  total.value = res.data?.total || 0
}

async function showDetail(id) {
  const res = await getStudentDetail(id)
  detail.value = res.data
  detailVisible.value = true
}

function resetQuery() {
  Object.assign(query, { 
    name: '', 
    studentNo: '', 
    rank: '', 
    classId: '',
    campStatus: ''
  })
  page.value = 1
  fetchData()
}

function formatCampStatus(status) {
  const map = { ACTIVE: '在营', NOT_CHECKED_IN: '未入住', CHECKED_OUT: '已离营' }
  return map[status] || status || '-'
}

function formatGender(gender) {
  const map = { 1: '男', 0: '女', 2: '女', '1': '男', '0': '女', '2': '女', M: '男', F: '女', male: '男', female: '女' }
  return map[gender] || gender || '-'
}

function getRankName(rankCode) {
  if (!rankCode) return '-'
  // 如果已经是名称直接返回
  if (ranks.includes(rankCode)) return rankCode
  // 否则尝试从ranks数组匹配
  const found = ranks.find(r => r.includes(rankCode) || rankCode.includes(r))
  return found || rankCode || '-'
}

function getClassName(classId) {
  if (!classId) return ''
  const found = classList.value.find(c => c.id == classId)
  return found ? found.name : ''
}

function handleCampStatusChange(row, newStatus) {
  ElMessageBox.confirm(
    `确认将学员"${row.name}"的在营状态修改为"${formatCampStatus(newStatus)}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await updateStudentCampStatus(row.id, newStatus)
      row.campStatus = newStatus
      ElMessage.success('状态更新成功')
    } catch (e) {
      ElMessage.error('状态更新失败')
    }
  }).catch(() => {})
}

function getCampStatusClass(status) {
  return status === 'ACTIVE' ? 'active' : status === 'CHECKED_OUT' ? 'checked-out' : ''
}

function getCampStatusType(status) {
  const map = { ACTIVE: 'success', NOT_CHECKED_IN: 'warning', CHECKED_OUT: 'info' }
  return map[status] || 'info'
}

function formatPhone(phone) {
  if (!phone) return '-'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

function handleSizeChange(val) {
  pageSize.value = val
  page.value = 1
  fetchData()
}

function handleJump(val) {
  page.value = val
  fetchData()
}

onMounted(async () => {
  fetchData()
  try {
    const res = await getClassList({ page: 1, size: 1000 })
    classList.value = res.data?.records || []
  } catch (e) {}
})
</script>

<style scoped>
.student-page {
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
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}
.filter-label {
  flex: 1;
  font-size: 14px;
  color: #6b7280;
}
.query-btn {
  border-radius: 8px;
  padding: 8px 24px;
  background: #059669;
  border-color: #059669;
}
.query-btn:hover {
  background: #047857 !important;
  border-color: #047857 !important;
}
.reset-btn {
  border-radius: 8px;
  padding: 8px 24px;
  margin-left: 12px;
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
.filter-content :deep(.el-select) {
  width: 200px;
}

/* 学员表格 */
.student-table {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}
.student-table :deep(.el-table__header-wrapper) {
  background: #f9fafb;
}
.student-table :deep(.el-table__header th) {
  background: #f9fafb;
  color: #374151;
  font-weight: 600;
}
.student-table :deep(.el-table__row:hover) {
  background: #f9fafb;
}
.camp-status-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  background: #dcfce7;
  color: #166534;
}
.camp-status-tag.checked-out {
  background: #f3f4f6;
  color: #6b7280;
}

/* 分页 */
.pagination-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 0;
}
.total-text {
  font-size: 14px;
  color: #6b7280;
}
.jump-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6b7280;
}
.jump-wrap :deep(.el-input-number) {
  width: 100px;
}

.detail-tabs {
  margin-top: 20px;
}
</style>
