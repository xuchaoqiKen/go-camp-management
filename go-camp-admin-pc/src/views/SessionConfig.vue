<template>
  <div class="page-container">
    <el-button type="success" @click="showAdd" style="margin-bottom:16px">新增期数</el-button>
    <el-table :data="list" border stripe>
      <el-table-column prop="name" label="期数名称" width="120" />
      <el-table-column prop="startDate" label="开始日期" width="120" />
      <el-table-column prop="endDate" label="结束日期" width="120" />
      <el-table-column prop="checkInDate" label="报到日" width="120" />
      <el-table-column prop="checkOutDate" label="退房日" width="120" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}"><el-button size="small" @click="showEdit(row)">编辑</el-button><el-button size="small" @click="showDailyRules(row)">每日规则</el-button><el-button size="small" type="danger" @click="toggleStatus(row)">{{ row.status===1?'停用':'启用' }}</el-button></template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" :title="isEdit?'编辑期数':'新增期数'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="报到日"><el-date-picker v-model="form.checkInDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="退房日"><el-date-picker v-model="form.checkOutDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="formVisible=false">取消</el-button><el-button type="primary" @click="submitForm">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="rulesVisible" title="每日规则配置" width="700px">
      <el-table :data="dailyRules" border stripe>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="hasClass" label="上课" width="70"><template #default="{row}"><el-checkbox v-model="row.hasClass" /></template></el-table-column>
        <el-table-column prop="hasBreakfast" label="早餐" width="70"><template #default="{row}"><el-checkbox v-model="row.hasBreakfast" /></template></el-table-column>
        <el-table-column prop="hasLunch" label="午餐" width="70"><template #default="{row}"><el-checkbox v-model="row.hasLunch" /></template></el-table-column>
        <el-table-column prop="hasDinner" label="晚餐" width="70"><template #default="{row}"><el-checkbox v-model="row.hasDinner" /></template></el-table-column>
        <el-table-column prop="canStay" label="可住宿" width="70"><template #default="{row}"><el-checkbox v-model="row.canStay" /></template></el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120"><template #default="{row}"><el-input v-model="row.remark" size="small" /></template></el-table-column>
      </el-table>
      <template #footer><el-button @click="rulesVisible=false">取消</el-button><el-button type="primary" @click="submitRules">保存规则</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSessionList, addSession, updateSession, toggleSessionStatus, getDailyRules, saveDailyRules } from '@/api'

const list = ref([])
const formVisible = ref(false); const isEdit = ref(false)
const form = reactive({ id: '', name: '', startDate: '', endDate: '', checkInDate: '', checkOutDate: '', status: 1 })
const rulesVisible = ref(false); const dailyRules = ref([]); const currentSessionId = ref('')

async function fetchData() { const res = await getSessionList(); list.value = res.data || [] }
function showAdd() { isEdit.value = false; Object.assign(form, { id: '', name: '', startDate: '', endDate: '', checkInDate: '', checkOutDate: '', status: 1 }); formVisible.value = true }
function showEdit(row) { isEdit.value = true; Object.assign(form, row); formVisible.value = true }
async function submitForm() { if (isEdit.value) await updateSession(form); else await addSession(form); ElMessage.success('保存成功'); formVisible.value = false; fetchData() }
async function toggleStatus(row) { await toggleSessionStatus({ id: row.id, status: row.status===1?0:1 }); ElMessage.success('操作成功'); fetchData() }
async function showDailyRules(row) { currentSessionId.value = row.id; const res = await getDailyRules(row.id); dailyRules.value = res.data || []; rulesVisible.value = true }
async function submitRules() { await saveDailyRules({ sessionId: currentSessionId.value, rules: dailyRules.value }); ElMessage.success('规则已保存'); rulesVisible.value = false }
onMounted(fetchData)
</script>