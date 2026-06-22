<template>
  <div class="page-container">
    <!-- 库存统计卡片 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="6"><el-card><el-statistic title="总房间数" :value="stats.totalRooms" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="总入住人数" :value="stats.totalOccupied" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="空余床位" :value="stats.availableBeds" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="待确认房间" :value="stats.pendingConfirm" /></el-card></el-col>
    </el-row>

    <el-form :inline="true" :model="query">
      <el-form-item label="期数"><el-select v-model="query.sessionId" clearable><el-option label="第一期" :value="1" /><el-option label="第二期" :value="2" /></el-select></el-form-item>
      <el-form-item label="房型"><el-select v-model="query.roomType" clearable><el-option label="标间" value="STANDARD" /><el-option label="大床房" value="KING" /><el-option label="多人间" value="DORM" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="query.status" clearable><el-option label="未入住" value="EMPTY" /><el-option label="待匹配" value="PENDING_MATCH" /><el-option label="待确认" value="PENDING_CONFIRM" /><el-option label="已入住" value="OCCUPIED" /><el-option label="部分入住" value="PARTIAL" /></el-select></el-form-item>
      <el-form-item label="房号"><el-input v-model="query.roomNo" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="fetchData">查询</el-button><el-button @click="exportData">导出</el-button></el-form-item>
    </el-form>

    <el-table :data="list" border stripe>
      <el-table-column prop="roomNo" label="房号" width="100" />
      <el-table-column prop="roomName" label="房间名称" width="120" />
      <el-table-column prop="roomType" label="房型" width="80" />
      <el-table-column prop="totalBeds" label="床位数" width="70" />
      <el-table-column prop="occupiedBeds" label="已入住" width="70" />
      <el-table-column prop="availableBeds" label="剩余床位" width="80" />
      <el-table-column prop="matchStatus" label="匹配状态" width="100"><template #default="{row}"><el-tag :type="statusType(row.matchStatus)">{{ row.matchStatus }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="showDetail(row)">详情</el-button>
          <el-button size="small" type="warning" @click="showAdjust(row)">调房</el-button>
          <el-button size="small" type="danger" @click="handleRelease(row)">释放</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 房间详情弹窗 -->
    <el-dialog v-model="detailVisible" title="房间详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="房号">{{ detail.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="房型">{{ detail.roomType }}</el-descriptions-item>
        <el-descriptions-item label="总床位">{{ detail.totalBeds }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.matchStatus }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail.occupants" border stripe style="margin-top:16px">
        <el-table-column prop="name" label="姓名" width="80" />
        <el-table-column prop="type" label="类型" width="80"><template #default="{row}"><el-tag :type="row.type==='STUDENT'?'primary':'warning'">{{ row.type==='STUDENT'?'学员':'家长' }}</el-tag></template></el-table-column>
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="checkInDate" label="入住日期" width="120" />
        <el-table-column prop="checkOutDate" label="退房日期" width="120" />
        <el-table-column prop="source" label="来源" min-width="120" />
      </el-table>
      <h4 style="margin-top:12px">调整记录</h4>
      <el-table :data="detail.adjustLogs" border stripe>
        <el-table-column prop="operator" label="操作人" width="80" />
        <el-table-column prop="action" label="操作" min-width="150" />
        <el-table-column prop="reason" label="原因" min-width="120" />
        <el-table-column prop="createTime" label="时间" width="160" />
      </el-table>
    </el-dialog>

    <!-- 调房弹窗 -->
    <el-dialog v-model="adjustVisible" title="手动调房" width="500px">
      <el-form :model="adjustForm" label-width="80px">
        <el-form-item label="当前房间">{{ adjustForm.currentRoom }}</el-form-item>
        <el-form-item label="目标房间"><el-select v-model="adjustForm.targetRoomId" filterable><el-option v-for="r in availableRooms" :key="r.id" :label="`${r.roomNo} (剩余${r.availableBeds}床)`" :value="r.id" /></el-select></el-form-item>
        <el-form-item label="调房原因"><el-input v-model="adjustForm.reason" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="adjustVisible=false">取消</el-button><el-button type="primary" @click="submitAdjust">确认调房</el-button></template>
    </el-dialog>

    <!-- 释放确认弹窗 -->
    <el-dialog v-model="releaseVisible" title="释放房间" width="500px">
      <el-form :model="releaseForm" label-width="80px">
        <el-form-item label="房间">{{ releaseForm.roomNo }}</el-form-item>
        <el-form-item label="释放原因"><el-input v-model="releaseForm.reason" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="releaseVisible=false">取消</el-button><el-button type="danger" @click="submitRelease">确认释放</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoomList, getRoomStats, getRoomDetail, adjustRoom, releaseRoom, exportRooms, getAvailableRooms } from '@/api'

const query = reactive({ sessionId: '', roomType: '', status: '', roomNo: '' })
const list = ref([])
const stats = reactive({ totalRooms: 0, totalOccupied: 0, availableBeds: 0, pendingConfirm: 0 })
const detailVisible = ref(false); const detail = reactive({ roomNo: '', roomType: '', totalBeds: 0, matchStatus: '', occupants: [], adjustLogs: [] })
const adjustVisible = ref(false); const adjustForm = reactive({ currentRoom: '', targetRoomId: '', reason: '', roomId: '' }); const availableRooms = ref([])
const releaseVisible = ref(false); const releaseForm = reactive({ roomNo: '', reason: '', roomId: '' })

function statusType(status) { const map = { '已入住': 'success', '待确认': 'warning', '待匹配': 'info', '未入住': '', '部分入住': 'primary' }; return map[status] || '' }

async function fetchData() { const res = await getRoomList(query); list.value = res.data?.records || [] }
async function fetchStats() { const res = await getRoomStats(query); Object.assign(stats, res.data || {}) }
async function showDetail(row) { const res = await getRoomDetail(row.id); Object.assign(detail, res.data || {}); detailVisible.value = true }
async function showAdjust(row) { adjustForm.roomId = row.id; adjustForm.currentRoom = row.roomNo; adjustForm.targetRoomId = ''; adjustForm.reason = ''; const res = await getAvailableRooms({ sessionId: query.sessionId, excludeId: row.id }); availableRooms.value = res.data || []; adjustVisible.value = true }
async function submitAdjust() { await adjustRoom(adjustForm); ElMessage.success('调房成功'); adjustVisible.value = false; fetchData(); fetchStats() }
function handleRelease(row) { releaseForm.roomId = row.id; releaseForm.roomNo = row.roomNo; releaseForm.reason = ''; releaseVisible.value = true }
async function submitRelease() { await releaseRoom(releaseForm); ElMessage.success('已释放'); releaseVisible.value = false; fetchData(); fetchStats() }
async function exportData() { await exportRooms(query); ElMessage.success('导出成功') }
onMounted(() => { fetchData(); fetchStats() })
</script>