<template>
  <div class="page-container">
    <el-form :inline="true" :model="query">
      <el-form-item label="权限名称"><el-input v-model="query.name" clearable /></el-form-item>
      <el-form-item label="类型"><el-select v-model="query.type" clearable><el-option label="菜单" value="MENU" /><el-option label="按钮" value="BUTTON" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" @click="fetchData">查询</el-button><el-button type="success" @click="showAdd(null)">新增根权限</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border stripe row-key="id" default-expand-all :tree-props="{children:'children'}">
      <el-table-column prop="name" label="权限名称" width="180" />
      <el-table-column prop="code" label="权限标识" width="180" />
      <el-table-column prop="type" label="类型" width="80" />
      <el-table-column prop="path" label="路由/组件" width="180" />
      <el-table-column prop="sort" label="排序" width="60" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="showAdd(row)">新增子级</el-button>
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" :title="isEdit?'编辑权限':'新增权限'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="标识"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.type"><el-option label="菜单" value="MENU" /><el-option label="按钮" value="BUTTON" /></el-select></el-form-item>
        <el-form-item label="路由/组件"><el-input v-model="form.path" /></el-form-item>
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
import { getPermissionTree, addPermission, updatePermission, deletePermission } from '@/api'

const query = reactive({ name: '', type: '' })
const list = ref([])
const formVisible = ref(false); const isEdit = ref(false)
const form = reactive({ id: '', parentId: '', name: '', code: '', type: 'MENU', path: '', sort: 0, status: 1 })

async function fetchData() { const res = await getPermissionTree(query); list.value = res.data || [] }
function showAdd(parent) { isEdit.value = false; Object.assign(form, { id: '', parentId: parent?.id || 0, name: '', code: '', type: 'MENU', path: '', sort: 0, status: 1 }); formVisible.value = true }
function showEdit(row) { isEdit.value = true; Object.assign(form, row); formVisible.value = true }
async function submitForm() { if (isEdit.value) await updatePermission(form); else await addPermission(form); ElMessage.success('保存成功'); formVisible.value = false; fetchData() }
async function handleDelete(row) { await ElMessageBox.confirm('确认删除？子权限将一并删除'); await deletePermission(row.id); ElMessage.success('已删除'); fetchData() }
onMounted(fetchData)
</script>