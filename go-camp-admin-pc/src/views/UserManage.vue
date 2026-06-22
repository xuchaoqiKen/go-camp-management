<template>
  <div class="page-container">
    <el-form :inline="true" :model="query">
      <el-form-item label="用户名"><el-input v-model="query.username" clearable /></el-form-item>
      <el-form-item label="姓名"><el-input v-model="query.realName" clearable /></el-form-item>
      <el-form-item label="角色"><el-select v-model="query.roleId" clearable><el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" /></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="query.status" clearable><el-option label="启用" value="1" /><el-option label="停用" value="0" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" @click="fetchData">查询</el-button><el-button type="success" @click="showAdd">新增</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border stripe>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="roleName" label="角色" width="100" />
      <el-table-column prop="departmentName" label="部门" width="100" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'停用' }}</el-tag></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button size="small" @click="showEdit(row)">编辑</el-button>
          <el-button size="small" :type="row.status===1?'warning':'success'" @click="toggleStatus(row)">{{ row.status===1?'停用':'启用' }}</el-button>
          <el-button size="small" @click="resetPwd(row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :total="total" :page-size="10" @current-change="fetchData" layout="prev,pager,next,total" />

    <el-dialog v-model="formVisible" :title="isEdit?'编辑用户':'新增用户'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
        <el-form-item v-if="!isEdit" label="密码"><el-input v-model="form.password" type="password" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="角色"><el-select v-model="form.roleId"><el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" /></el-select></el-form-item>
        <el-form-item label="部门"><el-select v-model="form.departmentId"><el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" /></el-select></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="formVisible=false">取消</el-button><el-button type="primary" @click="submitForm">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, toggleUserStatus, resetPassword, getRoleList, getDepartmentList } from '@/api'

const query = reactive({ username: '', realName: '', roleId: '', status: '' })
const list = ref([]); const page = ref(1); const total = ref(0)
const formVisible = ref(false); const isEdit = ref(false)
const form = reactive({ id: '', username: '', password: '', realName: '', phone: '', roleId: '', departmentId: '', status: 1 })
const roles = ref([]); const departments = ref([])

async function fetchData() {
  const res = await getUserList({ ...query, page: page.value, size: 10 })
  list.value = res.data?.records || []; total.value = res.data?.total || 0
}
async function loadOptions() {
  const [r,d] = await Promise.all([getRoleList(), getDepartmentList()])
  roles.value = r.data || []; departments.value = d.data || []
}
function showAdd() { isEdit.value = false; Object.assign(form, { id: '', username: '', password: '', realName: '', phone: '', roleId: '', departmentId: '', status: 1 }); formVisible.value = true }
function showEdit(row) { isEdit.value = true; Object.assign(form, { id: row.id, username: row.username, realName: row.realName, phone: row.phone, roleId: row.roleId, departmentId: row.departmentId, status: row.status }); formVisible.value = true }
async function submitForm() {
  if (isEdit.value) await updateUser(form); else await addUser(form)
  ElMessage.success('保存成功'); formVisible.value = false; fetchData()
}
async function toggleStatus(row) {
  await ElMessageBox.confirm(`确认${row.status===1?'停用':'启用'}该用户？`)
  await toggleUserStatus({ id: row.id, status: row.status===1?0:1 })
  ElMessage.success('操作成功'); fetchData()
}
async function resetPwd(row) {
  await ElMessageBox.confirm('确认重置密码？')
  await resetPassword({ id: row.id })
  ElMessage.success('密码已重置')
}
onMounted(() => { fetchData(); loadOptions() })
</script>