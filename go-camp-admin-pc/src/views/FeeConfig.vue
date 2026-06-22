<template>
  <div class="page-container">
    <el-form :inline="true" :model="query">
      <el-form-item label="费用项"><el-input v-model="query.name" clearable /></el-form-item>
      <el-form-item label="期数"><el-select v-model="query.sessionId" clearable><el-option label="第一期" :value="1" /><el-option label="第二期" :value="2" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" @click="fetchData">查询</el-button><el-button type="success" @click="showAdd">新增</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border stripe>
      <el-table-column prop="name" label="费用项" width="120" />
      <el-table-column prop="code" label="编码" width="100" />
      <el-table-column prop="unitPrice" label="单价(元)" width="100" />
      <el-table-column prop="unit" label="计费单位" width="100" />
      <el-table-column prop="sessionName" label="适用期数" width="100" />
      <el-table-column prop="startDate" label="开始日期" width="120" />
      <el-table-column prop="endDate" label="结束日期" width="120" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}"><el-button size="small" @click="showEdit(row)">编辑</el-button><el-button size="small" @click="showHistory(row)">历史</el-button><el-button size="small" type="danger" @click="toggleStatus(row)">{{ row.status===1?'停用':'启用' }}</el-button></template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" :title="isEdit?'编辑费用':'新增费用'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.code" :disabled="isEdit" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="form.unitPrice" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="单位"><el-select v-model="form.unit"><el-option label="按份" value="PER_MEAL" /><el-option label="按晚/天" value="PER_NIGHT" /><el-option label="按房间/晚" value="PER_ROOM" /></el-select></el-form-item>
        <el-form-item label="适用期数"><el-select v-model="form.sessionId"><el-option label="第一期" :value="1" /><el-option label="第二期" :value="2" /></el-select></el-form-item>
        <el-form-item label="日期范围"><el-date-picker v-model="form.dateRange" type="daterange" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="formVisible=false">取消</el-button><el-button type="primary" @click="submitForm">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="historyVisible" title="价格变更历史" width="600px">
      <el-table :data="historyList" border stripe>
        <el-table-column prop="unitPrice" label="单价" width="100" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="reason" label="原因" min-width="150" />
        <el-table-column prop="createTime" label="操作时间" width="160" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeeConfigList, addFeeConfig, updateFeeConfig, toggleFeeConfigStatus, getFeeConfigHistory } from '@/api'

const query = reactive({ name: '', sessionId: '' })
const list = ref([])
const formVisible = ref(false); const isEdit = ref(false)
const form = reactive({ id: '', name: '', code: '', unitPrice: 0, unit: 'PER_MEAL', sessionId: 1, dateRange: [], remark: '' })
const historyVisible = ref(false); const historyList = ref([])

async function fetchData() { const res = await getFeeConfigList(query); list.value = res.data?.records || [] }
function showAdd() { isEdit.value = false; Object.assign(form, { id: '', name: '', code: '', unitPrice: 0, unit: 'PER_MEAL', sessionId: 1, dateRange: [], remark: '' }); formVisible.value = true }
function showEdit(row) { isEdit.value = true; Object.assign(form, { ...row, dateRange: [row.startDate, row.endDate] }); formVisible.value = true }
async function submitForm() { if (isEdit.value) await updateFeeConfig(form); else await addFeeConfig(form); ElMessage.success('保存成功'); formVisible.value = false; fetchData() }
async function toggleStatus(row) { await toggleFeeConfigStatus({ id: row.id, status: row.status===1?0:1 }); ElMessage.success('操作成功'); fetchData() }
async function showHistory(row) { const res = await getFeeConfigHistory(row.id); historyList.value = res.data || []; historyVisible.value = true }
onMounted(fetchData)
</script>