<template>
  <div class="page-container">
    <van-nav-bar title="退费申请" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-form @submit="onSubmit">
      <div class="card">
        <div class="section-title">退费信息</div>
        <van-cell-group inset>
          <van-field v-model="form.studentId" label="选择学员" placeholder="请选择" readonly is-link required :rules="[{ required: true }]" @click="showStudentPicker = true" :model-value="selectedStudentName" />
          <van-field v-model="form.sessionId" label="选择期数" placeholder="请选择" readonly is-link required :rules="[{ required: true }]" @click="showSessionPicker = true" :model-value="selectedSessionName" />
        </van-cell-group>
      </div>

      <!-- 可退项目 -->
      <div class="card mt-12">
        <div class="section-title">可退项目</div>
        <van-checkbox-group v-model="form.items">
          <van-cell-group inset>
            <van-cell title="早餐" label="¥15/份 × 3份未消费" clickable @click="toggleItem('breakfast')">
              <template #right-icon><van-checkbox name="breakfast" /></template>
            </van-cell>
            <van-cell title="午餐" label="¥25/份 × 2份未消费" clickable @click="toggleItem('lunch')">
              <template #right-icon><van-checkbox name="lunch" /></template>
            </van-cell>
            <van-cell title="晚餐" label="¥25/份 × 2份未消费" clickable @click="toggleItem('dinner')">
              <template #right-icon><van-checkbox name="dinner" /></template>
            </van-cell>
            <van-cell title="住宿" label="全托管 ¥300/晚 × 1晚未消费" clickable @click="toggleItem('accommodation')">
              <template #right-icon><van-checkbox name="accommodation" /></template>
            </van-cell>
          </van-cell-group>
        </van-checkbox-group>
      </div>

      <!-- 退款金额预览 -->
      <div class="card mt-12">
        <div class="section-title">退款预览</div>
        <div class="flex-between mt-8">
          <span>可退金额</span>
          <span class="price">¥{{ refundAmount }}</span>
        </div>
        <van-field v-model="form.reason" label="退款原因" placeholder="请输入原因（选填）" type="textarea" rows="2" />
      </div>

      <div class="card mt-12">
        <div class="flex-center">
          <van-icon name="warning-o" color="#ee0a24" size="16" />
          <span class="text-danger ml-8">退款需提前12小时申请，逾期将无法提交</span>
        </div>
      </div>

      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="submitting" :disabled="form.items.length === 0">提交退费申请</van-button>
      </div>
    </van-form>

    <van-popup v-model:show="showStudentPicker" position="bottom" round>
      <van-picker :columns="studentOptions" @confirm="onStudentConfirm" @cancel="showStudentPicker = false" />
    </van-popup>
    <van-popup v-model:show="showSessionPicker" position="bottom" round>
      <van-picker :columns="sessionOptions" @confirm="onSessionConfirm" @cancel="showSessionPicker = false" />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { refundApi } from '@/api'

const router = useRouter()
const form = reactive({ studentId: '', sessionId: '', items: [], reason: '' })
const submitting = ref(false)
const showStudentPicker = ref(false)
const showSessionPicker = ref(false)

const studentOptions = ref([{ text: '张三', value: '1' }])
const sessionOptions = ref([{ text: '第一期', value: '1' }, { text: '第二期', value: '2' }])

const priceMap = { breakfast: 45, lunch: 50, dinner: 50, accommodation: 300 }

const refundAmount = computed(() => form.items.reduce((sum, item) => sum + (priceMap[item] || 0), 0))

const selectedStudentName = computed(() => studentOptions.value.find(s => s.value === form.studentId)?.text || '')
const selectedSessionName = computed(() => sessionOptions.value.find(s => s.value === form.sessionId)?.text || '')

function toggleItem(name) {
  const idx = form.items.indexOf(name)
  if (idx > -1) form.items.splice(idx, 1)
  else form.items.push(name)
}

function onStudentConfirm({ selectedOptions }) { form.studentId = selectedOptions[0]?.value || ''; showStudentPicker.value = false }
function onSessionConfirm({ selectedOptions }) { form.sessionId = selectedOptions[0]?.value || ''; showSessionPicker.value = false }

async function onSubmit() {
  if (!form.studentId || !form.sessionId || form.items.length === 0) { showToast('请完善信息'); return }
  submitting.value = true
  try {
    await refundApi.submitRefund(form)
    showSuccessToast('提交成功，请等待审核')
    setTimeout(() => router.back(), 1500)
  } catch (e) { showToast('提交失败') }
  finally { submitting.value = false }
}
</script>

<style scoped>
.ml-8 { margin-left: 8px; }
.text-danger { color: #ee0a24; font-size: 13px; }
</style>