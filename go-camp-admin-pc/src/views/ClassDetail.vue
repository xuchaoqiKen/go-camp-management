<template>
  <div class="page-container">
    <el-button @click="$router.back()" style="margin-bottom:16px">← 返回</el-button>
    <el-card>
      <template #header>班级信息</template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="班级名称">{{ classInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="期数">{{ classInfo.sessionName }}</el-descriptions-item>
        <el-descriptions-item label="段位说明">{{ classInfo.rankDesc }}</el-descriptions-item>
        <el-descriptions-item label="人数上限">{{ classInfo.maxStudents }}</el-descriptions-item>
        <el-descriptions-item label="教务(班主任)">{{ classInfo.advisorName || '未指定' }}</el-descriptions-item>
        <el-descriptions-item label="授课教练">{{ classInfo.coachName || '未指定' }}</el-descriptions-item>
        <el-descriptions-item label="学生人数">{{ classInfo.studentCount }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="classInfo.status===1?'success':'danger'">{{ classInfo.status===1?'启用':'停用' }}</el-tag></el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top:16px">
      <template #header>学生名单 <el-button size="small" type="primary" @click="exportList" style="float:right">导出名单</el-button></template>
      <el-table :data="classInfo.students" border stripe>
        <el-table-column prop="name" label="姓名" width="80" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="rankName" label="段位" width="80" />
        <el-table-column prop="accommodationType" label="住宿类型" width="100" />
        <el-table-column prop="leaveStatus" label="请假状态" width="80" />
        <el-table-column prop="roomNo" label="房间号" width="80" />
        <el-table-column prop="bedNo" label="床位号" width="80" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getClassDetail, exportClassStudents } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const classInfo = ref({})

onMounted(async () => {
  const res = await getClassDetail(route.params.id)
  classInfo.value = res.data || {}
})

async function exportList() {
  await exportClassStudents(route.params.id)
  ElMessage.success('导出成功')
}
</script>