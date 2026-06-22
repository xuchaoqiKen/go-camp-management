<template>
  <div class="page-container">
    <el-button type="success" @click="showAdd" style="margin-bottom:16px">新增段位</el-button>
    <el-table :data="list" border stripe>
      <el-table-column prop="name" label="段位名称" width="120" />
      <el-table-column prop="code" label="编码" width="100" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column prop="rangeDesc" label="段位范围" min-width="150" />
      <el-table-column prop="sort" label="排序" width="60" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{row}"><el-button size="small" @click="showEdit(row)">编辑</el-button><el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button></template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" :title="isEdit?'编辑段位':'新增段位'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="分类"><el-select v-model="form.categoryId"><el-option label="无基础组" :value="1" /><el-option label="级位初级组" :value="2" /><el-option label="级位进阶组" :value="3" /><el-option label="段位组" :value="4" /><el-option label="待确认" :value="5" /></el-select></el-form-item>
        <el-form-item label="范围"><el-input v-model="form.rangeDesc" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="formVisible=false">取消</el-button><el-button type="primary" @click="submitForm">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRankConfigList, addRankConfig, updateRankConfig, deleteRankConfig } from '@/api'

const list = ref([])
const formVisible = ref(false); const isEdit = ref(false)
const form = reactive({ id: '', name: '', code: '', categoryId: 1, rangeDesc: '', sort: 0, status: 1 })

async function fetchData() { const res = await getRankConfigList(); list.value = res.data || [] }
function showAdd() { isEdit.value = false; Object.assign(form, { id: '', name: '', code: '', categoryId: 1, rangeDesc: '', sort: 0, status: 1 }); formVisible.value = true }
function showEdit(row) { isEdit.value = true; Object.assign(form, row); formVisible.value = true }
async function submitForm() { if (isEdit.value) await updateRankConfig(form); else await addRankConfig(form); ElMessage.success('保存成功'); formVisible.value = false; fetchData() }
async function handleDelete(row) { await ElMessageBox.confirm('确认删除？'); await deleteRankConfig(row.id); ElMessage.success('已删除'); fetchData() }
onMounted(fetchData)
</script>