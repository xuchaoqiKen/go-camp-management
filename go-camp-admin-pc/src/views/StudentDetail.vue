<template>
  <div class="page-container">
    <el-button @click="$router.back()" style="margin-bottom:16px">← 返回</el-button>
    <el-card>
      <template #header>学员基本信息</template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="学员编号">{{ student.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ student.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ student.gender }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ student.age }}</el-descriptions-item>
        <el-descriptions-item label="段位">{{ student.rank }}</el-descriptions-item>
        <el-descriptions-item label="段位分类">{{ student.rankCategory }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ student.city }}</el-descriptions-item>
        <el-descriptions-item label="在营状态"><el-tag :type="student.campStatus==='IN_CAMP'?'success':'info'">{{ student.campStatus }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="住宿状态">{{ student.accommodationStatus || '无' }}</el-descriptions-item>
        <el-descriptions-item label="家长姓名">{{ student.parentName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ student.parentPhone }}</el-descriptions-item>
        <el-descriptions-item label="所属班级">{{ student.className || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ student.roomNo || '待匹配' }}</el-descriptions-item>
        <el-descriptions-item label="床位号">{{ student.bedNo || '待匹配' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top:16px">
      <template #header>报名记录</template>
      <el-table :data="student.orders" border stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="sessionName" label="期数" width="80" />
        <el-table-column prop="accommodationType" label="住宿类型" width="100" />
        <el-table-column prop="logisticsAmount" label="后勤金额" width="100" />
        <el-table-column prop="payStatus" label="支付状态" width="100" />
        <el-table-column prop="createTime" label="报名时间" width="160" />
      </el-table>
    </el-card>

    <el-card style="margin-top:16px">
      <template #header>请假记录</template>
      <el-table :data="student.leaves" border stripe>
        <el-table-column prop="leaveDate" label="请假日期" width="120" />
        <el-table-column prop="leaveType" label="类型" width="120" />
        <el-table-column prop="status" label="状态" width="100"><template #default="{row}"><el-tag :type="row.status==='APPROVED'?'success':row.status==='REJECTED'?'danger':'warning'">{{ row.status }}</el-tag></template></el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
      </el-table>
    </el-card>

    <el-card style="margin-top:16px">
      <template #header>退款记录</template>
      <el-table :data="student.refunds" border stripe>
        <el-table-column prop="refundNo" label="退款编号" width="180" />
        <el-table-column prop="item" label="退款项目" width="100" />
        <el-table-column prop="amount" label="金额" width="100" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="createTime" label="申请时间" width="160" />
      </el-table>
    </el-card>

    <el-card style="margin-top:16px">
      <template #header>操作日志</template>
      <el-table :data="student.logs" border stripe>
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="action" label="操作" min-width="150" />
        <el-table-column prop="remark" label="备注" min-width="120" />
        <el-table-column prop="createTime" label="时间" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getStudentDetail } from '@/api'

const route = useRoute()
const student = ref({})

onMounted(async () => {
  const res = await getStudentDetail(route.params.id)
  student.value = res.data || {}
})
</script>