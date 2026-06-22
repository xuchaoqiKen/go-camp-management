<template>
  <div class="page-container">
    <el-table :data="list" border stripe>
      <el-table-column prop="name" label="部门名称" width="150" />
      <el-table-column prop="code" label="部门编码" width="120" />
      <el-table-column prop="leader" label="负责人" width="100" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="sort" label="排序" width="60" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'停用' }}</el-tag></template></el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="160" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{row}"><el-button size="small" @click="showEdit(row)">编辑</el-button></template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" title="编辑部门" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" disabled /></el-form-item>
        <el-form-item label="负责人"><el-input v-model="form.leader" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="formVisible=false">取消</el-button><el-button type="primary" @click="submitForm">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDepartmentList, updateDepartment } from '@/api'

const list = ref([])
const formVisible = ref(false)
const form = reactive({ id: '', name: '', code: '', leader: '', phone: '', sort: 0, status: 1 })

async function fetchData() { const res = await getDepartmentList(); list.value = res.data || [] }
function showEdit(row) { Object.assign(form, row); formVisible.value = true }
async function submitForm() { await updateDepartment(form); ElMessage.success('保存成功'); formVisible.value = false; fetchData() }
onMounted(fetchData)
</script>