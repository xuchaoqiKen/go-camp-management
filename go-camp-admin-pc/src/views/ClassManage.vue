<template>
  <div class="class-page">
    <!-- 查询区域 -->
    <div class="query-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="班级名称">
          <el-input v-model="query.name" placeholder="请输入班级名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="所属期数">
          <el-select v-model="query.sessionId" clearable placeholder="请选择期数" style="width: 200px">
            <el-option v-for="s in sessionList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="请选择状态" style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="showAdd">新增班级</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 班级列表 -->
    <el-table :data="list" class="class-table" stripe border>
      <el-table-column prop="name" label="班级名称" width="160" />
      <el-table-column prop="sessionName" label="所属期数" width="120" />
      <el-table-column prop="rankDesc" label="段位范围/说明" min-width="180" show-overflow-tooltip />
      <el-table-column prop="advisorName" label="教务（班主任）" width="120" />
      <el-table-column prop="coachName" label="授课教练" width="120" />
      <el-table-column prop="studentCount" label="学生人数" width="100" align="center">
        <template #default="{ row }">
          <span class="count-badge">{{ row.studentCount || 0 }}/{{ row.maxStudents }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="showEdit(row)">编辑</el-button>
          <el-button type="success" size="small" link @click="showStudents(row)">学生管理</el-button>
          <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="page"
        :total="total"
        :page-size="pageSize"
        @current-change="fetchData"
        @size-change="handleSizeChange"
        layout="total, prev, pager, next, sizes"
      />
    </div>

    <!-- 新增/编辑班级弹窗 -->
    <el-dialog v-model="formVisible" :title="isEdit ? '编辑班级' : '新增班级'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="班级名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="所属期数" prop="sessionId">
          <el-select v-model="form.sessionId" placeholder="请选择期数" style="width: 100%">
            <el-option v-for="s in sessionList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="段位说明">
          <el-input v-model="form.rankDesc" type="textarea" :rows="2" placeholder="请输入段位范围或说明" />
        </el-form-item>
        <el-form-item label="人数上限">
          <el-input-number v-model="form.maxStudents" :min="1" :max="200" style="width: 100%" />
        </el-form-item>
        <el-form-item label="教务（班主任）">
          <el-select v-model="form.advisorId" clearable filterable placeholder="请选择班主任" style="width: 100%">
            <el-option v-for="u in advisorList" :key="u.id" :label="u.name" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="授课教练">
          <el-select v-model="form.coachId" clearable filterable placeholder="请选择教练" style="width: 100%">
            <el-option v-for="u in coachList" :key="u.id" :label="u.name" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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

    <!-- 学生管理弹窗 -->
    <el-dialog v-model="studentVisible" title="班级学生管理" width="900px">
      <div class="student-header">
        <span class="class-info">
          <strong>{{ currentClassName }}</strong>
          <el-tag type="info">学生数：{{ classStudents.length }}</el-tag>
        </span>
        <div class="student-actions">
          <el-select v-model="addStudentId" filterable placeholder="搜索并添加学生" style="width: 350px">
            <el-option v-for="s in availableStudents" :key="s.id" :label="`${s.name} - ${s.rankName || '无段位'}`" :value="s.id" />
          </el-select>
          <el-button type="primary" @click="addStudent" :disabled="!addStudentId">添加</el-button>
          <el-button type="success" @click="exportStudents">导出名单</el-button>
        </div>
      </div>

      <el-table :data="classStudents" border stripe max-height="500">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" align="center" />
        <el-table-column prop="rankName" label="段位" width="100" />
        <el-table-column prop="accommodationType" label="住宿类型" width="120" />
        <el-table-column prop="leaveStatus" label="请假状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.leaveStatus === 'LEAVE'" type="warning" size="small">请假中</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="showTransfer(row)">调整班级</el-button>
            <el-button size="small" type="danger" @click="removeStudent(row)">移出</el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="studentVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 调整班级弹窗 -->
    <el-dialog v-model="transferVisible" title="调整学生班级" width="500px">
      <el-form label-width="100px">
        <el-form-item label="学生姓名">
          <el-input :value="transferStudent?.name" disabled />
        </el-form-item>
        <el-form-item label="当前班级">
          <el-input :value="currentClassName" disabled />
        </el-form-item>
        <el-form-item label="目标班级" prop="targetClassId">
          <el-select v-model="transferTargetId" placeholder="请选择目标班级" style="width: 100%">
            <el-option v-for="c in transferClassList" :key="c.id" :label="`${c.name} (${c.sessionName})`" :value="c.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmTransfer">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getClassList, addClass, updateClass, deleteClass,
  getClassStudents, addClassStudent, removeClassStudent, exportClassStudents,
  getUserList, getSessionList
} from '@/api'

const query = reactive({ name: '', sessionId: '', status: '' })
const list = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const formVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: '', name: '', sessionId: '', rankDesc: '', maxStudents: 30,
  advisorId: '', coachId: '', remark: '', status: 1
})

const rules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  sessionId: [{ required: true, message: '请选择所属期数', trigger: 'change' }]
}

const sessionList = ref([])
const advisorList = ref([])
const coachList = ref([])

const studentVisible = ref(false)
const classStudents = ref([])
const availableStudents = ref([])
const addStudentId = ref('')
const currentClassId = ref('')
const currentClassName = ref('')

const transferVisible = ref(false)
const transferStudent = ref(null)
const transferTargetId = ref('')
const transferClassList = ref([])

async function fetchData() {
  const res = await getClassList({ ...query, page: page.value, size: pageSize.value })
  list.value = res.data?.records || []
  total.value = res.data?.total || 0
}

async function loadBaseData() {
  const [sessionRes, userRes] = await Promise.all([
    getSessionList(),
    getUserList({ status: 1 })
  ])
  sessionList.value = sessionRes.data || []
  const users = userRes.data?.records || []
  advisorList.value = users.filter(u => u.roleCode === 'ADVISOR' || u.roleName?.includes('教务'))
  coachList.value = users.filter(u => u.roleCode === 'COACH' || u.roleName?.includes('教练'))
}

function resetQuery() {
  Object.assign(query, { name: '', sessionId: '', status: '' })
  page.value = 1
  fetchData()
}

function showAdd() {
  isEdit.value = false
  Object.assign(form, {
    id: '', name: '', sessionId: '', rankDesc: '', maxStudents: 30,
    advisorId: '', coachId: '', remark: '', status: 1
  })
  formVisible.value = true
}

function showEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  formVisible.value = true
}

async function submitForm() {
  try {
    if (isEdit.value) {
      await updateClass(form)
    } else {
      await addClass(form)
    }
    ElMessage.success('保存成功')
    formVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('保存失败：' + (e.message || '班级名称在同期数内重复'))
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除班级"${row.name}"吗？`, '提示', { type: 'warning' })
    await deleteClass(row.id)
    ElMessage.success('已删除')
    fetchData()
  } catch { }
}

async function showStudents(row) {
  currentClassId.value = row.id
  currentClassName.value = row.name
  const res = await getClassStudents(row.id)
  classStudents.value = res.data?.students || []
  availableStudents.value = res.data?.available || []
  studentVisible.value = true
}

async function addStudent() {
  try {
    await addClassStudent({ classId: currentClassId.value, studentId: addStudentId.value })
    ElMessage.success('添加成功')
    addStudentId.value = ''
    showStudents({ id: currentClassId.value, name: currentClassName.value })
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

async function removeStudent(row) {
  try {
    await ElMessageBox.confirm(`确认将学生"${row.name}"移出班级吗？`, '提示', { type: 'warning' })
    await removeClassStudent({ classId: currentClassId.value, studentId: row.id })
    ElMessage.success('已移出')
    showStudents({ id: currentClassId.value, name: currentClassName.value })
  } catch { }
}

async function showTransfer(row) {
  transferStudent.value = row
  transferTargetId.value = ''
  transferClassList.value = list.value.filter(c => c.id != currentClassId.value && c.status === 1)
  transferVisible.value = true
}

async function confirmTransfer() {
  if (!transferTargetId.value) {
    ElMessage.warning('请选择目标班级')
    return
  }
  try {
    await removeClassStudent({ classId: currentClassId.value, studentId: transferStudent.value.id })
    await addClassStudent({ classId: transferTargetId.value, studentId: transferStudent.value.id })
    ElMessage.success('调整成功')
    transferVisible.value = false
    showStudents({ id: currentClassId.value, name: currentClassName.value })
  } catch (e) {
    ElMessage.error('调整失败')
  }
}

async function exportStudents() {
  try {
    await exportClassStudents(currentClassId.value)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

function handleSizeChange(val) {
  pageSize.value = val
  page.value = 1
  fetchData()
}

onMounted(() => {
  fetchData()
  loadBaseData()
})
</script>

<style scoped>
.class-page {
  padding: 0;
}

.query-bar {
  margin-bottom: 20px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.class-table {
  border-radius: 8px;
  overflow: hidden;
}

.count-badge {
  display: inline-block;
  padding: 2px 8px;
  background: #ecf5ff;
  color: #409eff;
  border-radius: 10px;
  font-size: 12px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;
}

.student-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.class-info {
  font-size: 16px;
}

.class-info .el-tag {
  margin-left: 12px;
}

.student-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}
</style>
