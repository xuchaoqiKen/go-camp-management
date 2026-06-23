<template>
  <div class="page-container">
    <h2 class="page-title">请假管理</h2>
    <div class="filter-bar">
      <van-search v-model="searchKey" placeholder="搜索学员姓名" shape="round" @search="fetchList" />
      <van-dropdown-menu>
        <van-dropdown-item v-model="filterSession" :options="sessionOptions" @change="fetchList" />
        <van-dropdown-item v-model="filterStatus" :options="statusOptions" @change="fetchList" />
      </van-dropdown-menu>
    </div>
    <van-button type="primary" block style="margin-bottom:12px" @click="showAdd = true">手动新增请假</van-button>
    <div v-if="list.length === 0" class="empty-state"><van-icon name="notes-o" /><p>暂无请假记录</p></div>
    <div v-for="item in list" :key="item.id" class="card" @click="$router.push(`/leave-detail/${item.id}`)">
      <div class="flex-between">
        <span style="font-weight:600">{{ item.studentName }}</span>
        <van-tag :type="statusTagType(item.status)">{{ item.statusText }}</van-tag>
      </div>
      <div class="text-muted" style="font-size:13px;margin-top:4px">
        期数：{{ item.sessionText }} | 日期：{{ item.leaveDate }}
      </div>
      <div class="text-muted" style="font-size:13px">
        类型：{{ item.typeText }} | 来源：{{ item.sourceText }}
      </div>
      <div class="text-muted" style="font-size:12px;margin-top:2px">提交时间：{{ item.createTime }}</div>
    </div>
    <!-- 手动新增弹窗 -->
    <van-popup v-model:show="showAdd" position="bottom" round :style="{ height: '70%' }">
      <div style="padding:16px">
        <h3>手动新增请假</h3>
        <van-cell-group inset style="margin-top:12px">
          <van-field v-model="addForm.studentName" label="学员姓名" placeholder="请输入" />
          <van-field v-model="addForm.leaveDate" label="请假日期" readonly is-link @click="showAddDate = true" />
          <van-field v-model="addForm.reason" label="原因" placeholder="请输入原因" type="textarea" rows="2" />
        </van-cell-group>
        <van-radio-group v-model="addForm.type" direction="horizontal" style="margin:12px 16px">
          <van-radio name="NO_CLASS">请假不扣课</van-radio>
          <van-radio name="MEAL_ONLY">仅退餐饮</van-radio>
        </van-radio-group>
        <div style="padding:16px">
          <van-button type="primary" block @click="submitAdd">提交</van-button>
        </div>
      </div>
    </van-popup>
    <van-popup v-model:show="showAddDate" position="bottom">
      <van-date-picker title="选择日期" @confirm="onAddDateConfirm" @cancel="showAddDate = false" />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { showToast, showSuccessToast } from 'vant'
import adminApi from '@/api'

const searchKey = ref('')
const filterSession = ref('')
const filterStatus = ref('')
const sessionOptions = [{ text: '全部期数', value: '' }, { text: '第一期', value: '1' }, { text: '第二期', value: '2' }]
const statusOptions = [
  { text: '全部状态', value: '' }, { text: '待审核', value: 'PENDING' },
  { text: '已通过', value: 'APPROVED' }, { text: '已驳回', value: 'REJECTED' }
]
const list = ref([])
const showAdd = ref(false)
const showAddDate = ref(false)
const addForm = reactive({ studentName: '', leaveDate: '', reason: '', type: 'NO_CLASS' })

const statusTagType = (s) => {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', REFUNDED: 'primary' }
  return map[s] || 'default'
}

const fetchList = async () => {
  try {
    const res = await adminApi.getLeaveList({
      keyword: searchKey.value, session: filterSession.value, status: filterStatus.value
    })
    list.value = res.data?.records || res.data || []
  } catch (e) { /* handled */ }
}

const submitAdd = async () => {
  if (!addForm.studentName || !addForm.leaveDate) { showToast('请完善信息'); return }
  try {
    await adminApi.addLeave(addForm)
    showSuccessToast('新增成功')
    showAdd.value = false
    fetchList()
  } catch (e) { /* handled */ }
}

const onAddDateConfirm = ({ selectedValues }) => {
  addForm.leaveDate = `${selectedValues[0]}-${String(selectedValues[1]).padStart(2,'0')}-${String(selectedValues[2]).padStart(2,'0')}`
  showAddDate.value = false
}

onMounted(fetchList)
</script>