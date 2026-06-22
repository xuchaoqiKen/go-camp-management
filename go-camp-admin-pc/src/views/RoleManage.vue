<template>
  <div class="page-container">
    <el-card shadow="hover" class="search-card">
      <!-- 可折叠查询区域 -->
      <div class="filter-bar">
        <div class="filter-summary" @click="filterExpanded = !filterExpanded">
          <span class="filter-label">筛选：角色名称 / 角色标识 / 状态 / 创建时间</span>
          <el-button type="primary" class="query-btn" @click.stop="fetchData">查询</el-button>
          <el-button class="reset-btn" @click.stop="resetQuery">重置</el-button>
          <el-button type="success" class="add-btn" @click.stop="showAdd">新增角色</el-button>
          <el-icon class="expand-icon" :class="{ expanded: filterExpanded }"><ArrowDown /></el-icon>
        </div>
        <div class="filter-content" :class="{ expanded: filterExpanded }">
          <el-form :inline="true" :model="query">
            <el-form-item label="角色名称">
              <el-input v-model="query.name" placeholder="请输入角色名称" clearable />
            </el-form-item>
            <el-form-item label="角色标识">
              <el-input v-model="query.code" placeholder="请输入角色标识" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="query.status" clearable placeholder="请选择状态">
                <el-option label="启用" :value="1" />
                <el-option label="停用" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="创建时间">
              <el-date-picker v-model="query.createTimeRange" type="daterange" range-separator="至"
                start-placeholder="开始日期" end-placeholder="结束日期" style="width: 320px" />
            </el-form-item>
          </el-form>
        </div>
      </div>

      <el-table :data="list" border stripe class="role-table">
        <el-table-column prop="roleName" label="角色名称" width="160" />
        <el-table-column prop="roleCode" label="角色标识" width="160" />
        <el-table-column prop="description" label="权限说明" min-width="280" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}">
            <span :class="row.status === 1 ? 'status-enable' : 'status-disable'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link size="small" @click="showPermission(row)">查看权限</el-button>
            <el-button type="primary" link size="small" @click="showEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="hover" class="rule-card">
      <div class="rule-title">角色规则：教务（班主任）与教练角色拥有相同基础权限。</div>
      <div class="rule-content">额外权限：可查看自己负责班级的学员名单；不可编辑班级、不可添加/移出学生。</div>
      <div class="rule-content">审批权限仍由管理员或具备审批权限的教务管理员配置，不随班主任角色自动赋予。</div>
    </el-card>

    <el-dialog v-model="formVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="角色标识">
          <el-input v-model="form.roleCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="权限说明">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="permVisible" title="权限分配" width="600px">
      <el-tree ref="permTree" :data="permTree" show-checkbox node-key="id"
        :props="{ label: 'name', children: 'children' }"
        :default-checked-keys="checkedPerms" />
      <template #footer>
        <el-button @click="permVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPerms">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { getRoleList, addRole, updateRole, deleteRole, getPermissionTree, assignRolePermissions } from '@/api'

const filterExpanded = ref(false)
const query = reactive({ name: '', code: '', status: '', createTimeRange: [] })
const list = ref([])
const formVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: '', roleName: '', roleCode: '', description: '', status: 1 })
const permVisible = ref(false)
const permTree = ref([])
const checkedPerms = ref([])
const currentRoleId = ref('')

async function fetchData() {
  const res = await getRoleList(query)
  list.value = res.data?.records || res.data || []
}

function resetQuery() {
  Object.assign(query, { name: '', code: '', status: '', createTimeRange: [] })
  fetchData()
}

function showAdd() {
  isEdit.value = false
  Object.assign(form, { id: '', roleName: '', roleCode: '', description: '', status: 1 })
  formVisible.value = true
}

function showEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  formVisible.value = true
}

async function submitForm() {
  if (isEdit.value) {
    await updateRole(form)
  } else {
    await addRole(form)
  }
  ElMessage.success('保存成功')
  formVisible.value = false
  fetchData()
}

async function showPermission(row) {
  currentRoleId.value = row.id
  const res = await getPermissionTree()
  permTree.value = res.data?.tree || []
  checkedPerms.value = res.data?.checked || []
  permVisible.value = true
}

async function submitPerms() {
  const keys = permTree.value ? [] : []
  await assignRolePermissions({ roleId: currentRoleId.value, permissionIds: keys })
  ElMessage.success('权限已保存')
  permVisible.value = false
}

onMounted(fetchData)
</script>

<style scoped>
.page-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.search-card {
  background-color: #ffffff;
  border-radius: 12px;
  margin-bottom: 16px;
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

.add-btn {
  border-radius: 8px;
  padding: 8px 24px;
  margin-left: 12px;
  background: #059669;
  border-color: #059669;
}

.add-btn:hover {
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
.filter-content :deep(.el-select) {
  width: 200px;
}

.role-table :deep(.el-table__header-wrapper) {
  background-color: #dcfce7;
}

.role-table :deep(.el-table__header th) {
  background-color: #dcfce7 !important;
  color: #166534;
  font-weight: 600;
}

.status-enable {
  color: #16a34a;
  font-weight: 500;
}

.status-disable {
  color: #dc2626;
  font-weight: 500;
}

.rule-card {
  background-color: #ecfdf5;
  border: 1px solid #a7f3d0;
  border-radius: 12px;
}

.rule-title {
  color: #166534;
  font-weight: 600;
  margin-bottom: 8px;
}

.rule-content {
  color: #166534;
  line-height: 1.6;
}
</style>