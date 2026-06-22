<template>
  <div class="page-container">
    <el-button @click="$router.back()" style="margin-bottom:16px">← 返回</el-button>
    <el-card>
      <template #header>订单基本信息</template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="学员订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="支付编号">{{ order.paymentNo }}</el-descriptions-item>
        <el-descriptions-item label="学员姓名">{{ order.studentName }}</el-descriptions-item>
        <el-descriptions-item label="报名期数">{{ order.sessionName }}</el-descriptions-item>
        <el-descriptions-item label="段位分类">{{ order.rankCategory }}</el-descriptions-item>
        <el-descriptions-item label="住宿类型">{{ order.accommodationType }}</el-descriptions-item>
        <el-descriptions-item label="后勤金额">¥{{ order.logisticsAmount }}</el-descriptions-item>
        <el-descriptions-item label="支付状态"><el-tag :type="order.payStatus==='PAID'?'success':'warning'">{{ order.payStatus }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="订单状态"><el-tag>{{ order.orderStatus }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="房间号">{{ order.roomNo || '待匹配' }}</el-descriptions-item>
        <el-descriptions-item label="床位号">{{ order.bedNo || '待匹配' }}</el-descriptions-item>
        <el-descriptions-item label="所属班级">{{ order.className || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top:16px">
      <template #header>学员信息</template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ order.studentName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ order.gender }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ order.age }}</el-descriptions-item>
        <el-descriptions-item label="段位">{{ order.rank }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ order.city }}</el-descriptions-item>
        <el-descriptions-item label="陪同人员">{{ order.companion }}</el-descriptions-item>
        <el-descriptions-item label="家长姓名">{{ order.parentName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order.parentPhone }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top:16px">
      <template #header>每日排期</template>
      <el-table :data="order.schedules" border stripe>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="hasClass" label="上课" width="70"><template #default="{row}"><el-tag :type="row.hasClass?'success':'info'">{{ row.hasClass?'是':'否' }}</el-tag></template></el-table-column>
        <el-table-column prop="hasBreakfast" label="早餐" width="70"><template #default="{row}"><el-tag :type="row.hasBreakfast?'success':'info'">{{ row.hasBreakfast?'是':'否' }}</el-tag></template></el-table-column>
        <el-table-column prop="breakfastSource" label="早餐来源" width="100" />
        <el-table-column prop="hasLunch" label="午餐" width="70"><template #default="{row}"><el-tag :type="row.hasLunch?'success':'info'">{{ row.hasLunch?'是':'否' }}</el-tag></template></el-table-column>
        <el-table-column prop="hasDinner" label="晚餐" width="70"><template #default="{row}"><el-tag :type="row.hasDinner?'success':'info'">{{ row.hasDinner?'是':'否' }}</el-tag></template></el-table-column>
        <el-table-column prop="accommodationType" label="住宿模式" width="100" />
      </el-table>
    </el-card>

    <el-card style="margin-top:16px">
      <template #header>费用明细</template>
      <el-table :data="order.feeDetails" border stripe>
        <el-table-column prop="item" label="费用项" width="120" />
        <el-table-column prop="unitPrice" label="单价" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="amount" label="金额" width="100" />
      </el-table>
    </el-card>

    <el-card style="margin-top:16px" v-if="order.refunds?.length">
      <template #header>退款记录</template>
      <el-table :data="order.refunds" border stripe>
        <el-table-column prop="refundNo" label="退款编号" width="180" />
        <el-table-column prop="item" label="退款项目" width="100" />
        <el-table-column prop="amount" label="退款金额" width="100" />
        <el-table-column prop="status" label="状态" width="100"><template #default="{row}"><el-tag :type="row.status==='REFUNDED'?'success':'warning'">{{ row.status }}</el-tag></template></el-table-column>
        <el-table-column prop="wxRefundNo" label="微信退款流水号" width="200" />
        <el-table-column prop="createTime" label="申请时间" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getOrderDetail } from '@/api'

const route = useRoute()
const order = ref({})

onMounted(async () => {
  const res = await getOrderDetail(route.params.id)
  order.value = res.data || {}
})
</script>