import request from '@/utils/request'

export const authApi = {
  login: (data) => request.post('/auth/login', data),
  logout: () => request.post('/auth/logout'),
  getUserInfo: () => request.get('/auth/user-info')
}

export const coachApi = {
  getSchedule: (params) => request.get('/coach/schedule', { params }),
  submitSchedule: (data) => request.post('/coach/schedule', data),
  getMealCancelList: (params) => request.get('/coach/meal-cancel', { params }),
  submitMealCancel: (data) => request.post('/coach/meal-cancel', data),
  getStudentLeaveList: (params) => request.get('/coach/student-leave', { params }),
  getDailyList: (params) => request.get('/coach/daily-list', { params }),
  getRankDistribution: (params) => request.get('/coach/rank-distribution', { params }),
  getMineInfo: () => request.get('/coach/mine'),
  getMealCancelRecords: (params) => request.get('/coach/meal-cancel-records', { params })
}

export default coachApi