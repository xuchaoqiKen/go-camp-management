import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { registrationApi, paymentApi } from '@/api'

export const useRegistrationStore = defineStore('registration', () => {
  // 营期数据
  const sessions = ref([])
  const currentSessionIndex = ref(0)

  // 学员列表
  const students = ref([])

  // 费用配置
  const feeConfig = ref(null)

  // 当前订单
  const currentOrders = ref([])
  const paymentRecord = ref(null)

  // 计算属性
  const currentSession = computed(() => sessions.value[currentSessionIndex.value] || null)

  const totalLogisticsFee = computed(() => {
    return currentOrders.value.reduce((sum, order) => sum + (order.logisticsFee || 0), 0)
  })

  // 方法
  async function fetchSessions() {
    const res = await registrationApi.getSessions()
    sessions.value = res.data || []
  }

  async function fetchFeeConfig(sessionId) {
    const res = await registrationApi.getFeeConfig(sessionId)
    feeConfig.value = res.data
  }

  function addStudent(student) {
    students.value.push({
      id: Date.now().toString(),
      ...student,
      selectedSessions: [],
      schedules: {}
    })
  }

  function removeStudent(studentId) {
    students.value = students.value.filter(s => s.id !== studentId)
  }

  function updateStudent(studentId, data) {
    const index = students.value.findIndex(s => s.id === studentId)
    if (index !== -1) {
      students.value[index] = { ...students.value[index], ...data }
    }
  }

  function setStudentSessions(studentId, sessionIds) {
    updateStudent(studentId, { selectedSessions: sessionIds })
  }

  function setStudentSchedule(studentId, sessionIndex, date, scheduleData) {
    const student = students.value.find(s => s.id === studentId)
    if (student) {
      if (!student.schedules[sessionIndex]) {
        student.schedules[sessionIndex] = {}
      }
      student.schedules[sessionIndex][date] = scheduleData
    }
  }

  async function calculateFee() {
    const data = {
      students: students.value.map(s => ({
        studentId: s.id,
        name: s.name,
        gender: s.gender,
        age: s.age,
        rank: s.rank,
        selectedSessions: s.selectedSessions,
        schedules: s.schedules
      }))
    }
    const res = await registrationApi.calculateFee(data)
    currentOrders.value = res.data || []
    return res.data
  }

  async function submitOrder() {
    const data = {
      students: students.value.map(s => ({
        studentId: s.id,
        name: s.name,
        gender: s.gender,
        age: s.age,
        rank: s.rank,
        city: s.city,
        companion: s.companion,
        selectedSessions: s.selectedSessions,
        schedules: s.schedules
      }))
    }
    const res = await registrationApi.submitOrder(data)
    return res.data
  }

  async function createPayment(orderIds) {
    const res = await paymentApi.createPayment({ orderIds })
    paymentRecord.value = res.data
    return res.data
  }

  function reset() {
    students.value = []
    currentOrders.value = []
    paymentRecord.value = null
    currentSessionIndex.value = 0
  }

  return {
    sessions,
    currentSessionIndex,
    students,
    feeConfig,
    currentOrders,
    paymentRecord,
    currentSession,
    totalLogisticsFee,
    fetchSessions,
    fetchFeeConfig,
    addStudent,
    removeStudent,
    updateStudent,
    setStudentSessions,
    setStudentSchedule,
    calculateFee,
    submitOrder,
    createPayment,
    reset
  }
})