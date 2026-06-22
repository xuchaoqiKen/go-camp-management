<template>
  <div class="page-container">
    <van-nav-bar title="学员报名" left-arrow @click-left="$router.back()" fixed placeholder />

    <!-- 学员列表 -->
    <div class="card" v-for="(student, index) in store.students" :key="student.id">
      <div class="flex-between mb-12">
        <span class="section-title" style="border:none;margin:0;padding:0">学员 {{ index + 1 }}</span>
        <van-button size="small" type="danger" plain @click="store.removeStudent(student.id)">移除</van-button>
      </div>

      <van-form @submit="onSubmitStudent(student)">
        <van-cell-group inset>
          <van-field v-model="student.name" label="姓名" placeholder="请输入学员姓名" required :rules="[{ required: true, message: '请输入姓名' }]" />
          <van-field v-model="student.gender" label="性别" placeholder="请选择性别" required readonly is-link @click="showGenderPicker(student)" />
          <van-field v-model="student.age" label="年龄" placeholder="请输入年龄(7-15岁)" type="digit" required :rules="[{ required: true, validator: validateAge }]" />
          <van-field v-model="student.rank" label="段位" placeholder="请选择段位" readonly is-link @click="showRankPicker(student)" />
          <van-field v-model="student.city" label="城市" placeholder="请输入所在城市" />
          <van-field v-model="student.companion" label="陪同人员" placeholder="请输入陪同人员姓名" />
        </van-cell-group>

        <!-- 报名期数选择 -->
        <div class="mt-16">
          <div class="section-title">报名期数</div>
          <van-checkbox-group v-model="student.selectedSessions" direction="horizontal">
            <van-checkbox v-for="s in store.sessions" :key="s.id" :name="s.id" shape="square">
              {{ s.name }}
            </van-checkbox>
          </van-checkbox-group>
        </div>
      </van-form>
    </div>

    <!-- 添加学员按钮 -->
    <van-button block plain type="primary" icon="plus" @click="showAddStudent = true" class="mt-16">添加学员</van-button>

    <!-- 下一步 -->
    <van-button block type="primary" @click="goToSchedule" :disabled="store.students.length === 0" class="mt-16">
      下一步：配置排期
    </van-button>

    <!-- 添加学员弹窗 -->
    <van-popup v-model:show="showAddStudent" position="bottom" round :style="{ height: '70%' }">
      <div class="popup-content">
        <h3 class="popup-title">添加学员</h3>
        <van-form @submit="onAddStudent">
          <van-cell-group inset>
            <van-field v-model="newStudent.name" label="姓名" placeholder="请输入学员姓名" required :rules="[{ required: true }]" />
            <van-field v-model="newStudent.gender" label="性别" placeholder="请选择" readonly is-link @click="showGenderPickerForNew" />
            <van-field v-model="newStudent.age" label="年龄" placeholder="7-15岁" type="digit" required :rules="[{ required: true, validator: validateAge }]" />
            <van-field v-model="newStudent.rank" label="段位" placeholder="请选择" readonly is-link @click="showRankPickerForNew" />
            <van-field v-model="newStudent.city" label="城市" placeholder="请输入城市" />
            <van-field v-model="newStudent.companion" label="陪同人员" placeholder="请输入" />
          </van-cell-group>
          <div style="margin: 16px">
            <van-button round block type="primary" native-type="submit">确认添加</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>

    <!-- 性别选择器 -->
    <van-popup v-model:show="showGender" position="bottom" round>
      <van-picker :columns="genderOptions" @confirm="onGenderConfirm" @cancel="showGender = false" />
    </van-popup>

    <!-- 段位选择器 -->
    <van-popup v-model:show="showRank" position="bottom" round>
      <van-picker :columns="rankOptions" @confirm="onRankConfirm" @cancel="showRank = false" />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { useRegistrationStore } from '@/stores/registration'

const router = useRouter()
const store = useRegistrationStore()

const showAddStudent = ref(false)
const showGender = ref(false)
const showRank = ref(false)
const currentStudent = ref(null)
const isNewStudent = ref(false)

const newStudent = reactive({ name: '', gender: '', age: '', rank: '', city: '', companion: '' })

const genderOptions = ['男', '女']
const rankOptions = ['无基础', '18级', '17级', '16级', '15级', '14级', '13级', '12级', '11级', '10级', '9级', '8级', '7级', '6级', '5级', '4级', '3级', '2级', '1级', '1段', '2段', '3段', '4段', '5段', '6段']

onMounted(async () => {
  await store.fetchSessions()
})

function validateAge(val) {
  const age = parseInt(val)
  if (isNaN(age) || age < 7 || age > 15) {
    return '年龄需在7-15岁之间'
  }
  return true
}

function showGenderPicker(student) {
  currentStudent.value = student
  isNewStudent.value = false
  showGender.value = true
}

function showGenderPickerForNew() {
  isNewStudent.value = true
  showGender.value = true
}

function onGenderConfirm({ selectedOptions }) {
  const gender = selectedOptions[0]?.text || ''
  if (isNewStudent.value) {
    newStudent.gender = gender
  } else if (currentStudent.value) {
    currentStudent.value.gender = gender
  }
  showGender.value = false
}

function showRankPicker(student) {
  currentStudent.value = student
  isNewStudent.value = false
  showRank.value = true
}

function showRankPickerForNew() {
  isNewStudent.value = true
  showRank.value = true
}

function onRankConfirm({ selectedOptions }) {
  const rank = selectedOptions[0]?.text || ''
  if (isNewStudent.value) {
    newStudent.rank = rank
  } else if (currentStudent.value) {
    currentStudent.value.rank = rank
  }
  showRank.value = false
}

function onAddStudent() {
  if (!newStudent.name || !newStudent.gender || !newStudent.age) {
    showToast('请填写必填信息')
    return
  }
  store.addStudent({ ...newStudent })
  newStudent.name = ''
  newStudent.gender = ''
  newStudent.age = ''
  newStudent.rank = ''
  newStudent.city = ''
  newStudent.companion = ''
  showAddStudent.value = false
}

function onSubmitStudent(student) {
  // 表单校验通过后的处理
}

function goToSchedule() {
  const hasInvalid = store.students.some(s => !s.name || !s.gender || !s.age || s.selectedSessions.length === 0)
  if (hasInvalid) {
    showToast('请完善所有学员信息并选择报名期数')
    return
  }
  router.push(`/registration/${store.students[0].id}/schedule`)
}
</script>

<style scoped>
.popup-content {
  padding: 20px 16px;
}
.popup-title {
  text-align: center;
  font-size: 18px;
  margin-bottom: 20px;
}
</style>