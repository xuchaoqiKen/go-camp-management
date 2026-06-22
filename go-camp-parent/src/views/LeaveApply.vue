<template>
  <div class="page-container">
    <van-nav-bar title="请假申请" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-form @submit="onSubmit">
      <div class="card">
        <div class="section-title">请假信息</div>
        <van-cell-group inset>
          <van-field
            v-model="form.studentId"
            label="选择学员"
            placeholder="请选择学员"
            readonly
            is-link
            required
            :rules="[{ required: true, message: '请选择学员' }]"
            @click="showStudentPicker = true"
            :model-value="selectedStudentName"
          />
          <van-field
            v-model="form.sessionId"
            label="选择期数"
            placeholder="请选择期数"
            readonly
            is-link
            required
            :rules="[{ required: true, message: '请选择期数' }]"
            @click="showSessionPicker = true"
            :model-value="selectedSessionName"
          />
          <van-field
            v-model="form.date"
            label="请假日期"
            placeholder="请选择日期"
            readonly
            is-link
            required
            :rules="[{ required: true, message: '请选择日期' }]"
            @click="showDatePicker = true"
          />
          <van-field
            v-model="form.type"
            label="请假类型"
            placeholder="请选择类型"
            readonly
            is-link
            required
            :rules="[{ required: true, message: '请选择类型' }]"
            @click="showTypePicker = true"
            :model-value="selectedTypeName"
          />
          <van-field v-model="form.reason" label="请假原因" placeholder="请输入原因（选填）" type="textarea" rows="3" />
        </van-cell-group>
      </div>

      <!-- 12小时红线提醒 -->
      <div class="card mt-12">
        <div class="flex-center">
          <van-icon name="warning-o" color="#ee0a24" size="16" />
          <span class="text-danger ml-8">请假需提前12小时申请，逾期将无法提交</span>
        </div>
      </div>

      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="submitting">提交申请</van-button>
      </div>
    </van-form>

    <!-- 学员选择器 -->
    <van-popup v-model:show="showStudentPicker" position="bottom" round>
      <van-picker :columns="studentOptions" @confirm="onStudentConfirm" @cancel="showStudentPicker = false" />
    </van-popup>

    <!-- 期数选择器 -->
    <van-popup v-model:show="showSessionPicker" position="bottom" round>
      <van-picker :columns="sessionOptions" @confirm="onSessionConfirm" @cancel="showSessionPicker = false" />
    </van-popup>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom" round>
      <van-date-picker
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>

    <!-- 类型选择器 -->
    <van-popup v-model:show="showTypePicker" position="bottom" round>
      <van-picker :columns="typeOptions" @confirm="onTypeConfirm" @cancel="showTypePicker = false" />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { leaveApi } from '@/api'

const router = useRouter()

const form = reactive({
  studentId: '',
  sessionId: '',
  date: '',
  type: '',
  reason: ''
})

const submitting = ref(false)
const showStudentPicker = ref(false)
const showSessionPicker = ref(false)
const showDatePicker = ref(false)
const showTypePicker = ref(false)

const studentOptions = ref([{ text: '张三', value: '1' }])
const sessionOptions = ref([{ text: '第一期', value: '1' }, { text: '第二期', value: '2' }])
const typeOptions = ref(['请假不扣课', '仅退餐饮（早餐）', '仅退餐饮（午餐）', '仅退餐饮（晚餐）'])

const minDate = new Date(2026, 6, 15)
const maxDate = new Date(2026, 7, 30)

const selectedStudentName = computed(() => {
  const s = studentOptions.value.find(s => s.value === form.studentId)
  return s?.text || ''
})

const selectedSessionName = computed(() => {
  const s = sessionOptions.value.find(s => s.value === form.sessionId)
  return s?.text || ''
})

const selectedTypeName = computed(() => form.type || '')

function onStudentConfirm({ selectedOptions }) {
  form.studentId = selectedOptions[0]?.value || ''
  showStudentPicker.value = false
}

function onSessionConfirm({ selectedOptions }) {
  form.sessionId = selectedOptions[0]?.value || ''
  showSessionPicker.value = false
}

function onDateConfirm({ selectedValues }) {
  const [y, m, d] = selectedValues
  form.date = `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`
  showDatePicker.value = false
}

function onTypeConfirm({ selectedOptions }) {
  form.type = selectedOptions[0]?.text || ''
  showTypePicker.value = false
}

async function onSubmit() {
  if (!form.studentId || !form.sessionId || !form.date || !form.type) {
    showToast('请填写必填信息')
    return
  }

  submitting.value = true
  try {
    await leaveApi.submitLeave(form)
    showSuccessToast('提交成功，请等待审核')
    setTimeout(() => router.back(), 1500)
  } catch (e) {
    showToast('提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.ml-8 {
  margin-left: 8px;
}
.text-danger {
  color: #ee0a24;
  font-size: 13px;
}
</style>