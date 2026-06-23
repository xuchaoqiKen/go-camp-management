<template>
  <div class="page-container">
    <van-nav-bar title="班级详情" left-arrow @click-left="$router.back()" />
    <div class="card" v-if="detail">
      <div class="flex-between"><span style="font-weight:600;font-size:16px">{{ detail.className }}</span><van-tag type="primary">{{ detail.sessionText }}</van-tag></div>
      <van-cell-group inset style="margin-top:12px">
        <van-cell title="段位说明" :value="detail.rankDesc || '-'" />
        <van-cell title="班主任" :value="detail.headTeacher || '-'" />
        <van-cell title="授课教练" :value="detail.coach || '-'" />
        <van-cell title="学生人数" :value="detail.studentCount" />
        <van-cell title="人数上限" :value="detail.maxStudents" />
        <van-cell title="状态" :value="detail.status === 'ACTIVE' ? '启用' : '停用'" />
      </van-cell-group>
    </div>
    <div class="card" v-if="students.length > 0">
      <h4 style="margin-bottom:12px">学生名单</h4>
      <div v-for="s in students" :key="s.id" class="student-item">
        <div class="flex-between">
          <span style="font-weight:500">{{ s.studentName }}</span>
          <van-tag :type="s.leaveStatus === 'ON_LEAVE' ? 'warning' : 'success'" size="small">{{ s.leaveStatus === 'ON_LEAVE' ? '请假中' : '在营' }}</van-tag>
        </div>
        <div class="text-muted" style="font-size:12px;margin-top:2px">
          年龄：{{ s.age }} | 段位：{{ s.rank }} | 住宿：{{ s.accommodationText || '-' }}
        </div>
        <div class="text-muted" style="font-size:12px">房间：{{ s.roomNo || '待匹配' }} | 床位：{{ s.bedNo || '-' }}</div>
      </div>
    </div>
    <div v-if="students.length === 0 && detail" class="empty-state"><van-icon name="user-o" /><p>暂无学生</p></div>
    <div v-if="!detail" class="empty-state"><van-loading /><p>加载中...</p></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import adminApi from '@/api'

const route = useRoute()
const detail = ref(null)
const students = ref([])

const fetchData = async () => {
  try {
    const res = await adminApi.getClassDetail(route.params.id)
    detail.value = res.data?.classInfo
    students.value = res.data?.students || []
  } catch (e) { /* handled */ }
}

onMounted(fetchData)
</script>

<style scoped>
.student-item { padding:10px 0; border-bottom:1px solid #ebedf0 }
.student-item:last-child { border-bottom:none }
</style>