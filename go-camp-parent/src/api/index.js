import request from '@/utils/request'

// 认证相关
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  wxLogin: (data) => request.post('/auth/wx-login', data),
  logout: () => request.post('/auth/logout'),
  getUserInfo: () => request.get('/auth/user-info')
}

// 学员相关
export const studentApi = {
  list: (params) => request.get('/student/list', { params }),
  create: (data) => request.post('/student', data),
  update: (id, data) => request.put(`/student/${id}`, data),
  getById: (id) => request.get(`/student/${id}`),
  delete: (id) => request.delete(`/student/${id}`)
}

// 报名相关
export const registrationApi = {
  getSessions: () => request.get('/registration/sessions'),
  getFeeConfig: (sessionId) => request.get('/registration/fee-config', { params: { sessionId } }),
  calculateFee: (data) => request.post('/registration/calculate-fee', data),
  submitOrder: (data) => request.post('/registration/submit-order', data),
  getOrders: (params) => request.get('/registration/orders', { params }),
  getOrderDetail: (id) => request.get(`/registration/orders/${id}`),
  getDailySchedule: (orderId) => request.get(`/registration/orders/${orderId}/schedule`)
}

// 支付相关
export const paymentApi = {
  createPayment: (data) => request.post('/payment/create', data),
  queryPayment: (paymentId) => request.get(`/payment/${paymentId}`),
  getPaymentStatus: (orderId) => request.get(`/payment/status/${orderId}`)
}

// 请假相关
export const leaveApi = {
  apply: (data) => request.post('/leave/apply', data),
  list: (params) => request.get('/leave/list', { params }),
  getDetail: (id) => request.get(`/leave/${id}`),
  checkTimeRule: (data) => request.post('/leave/check-time-rule', data)
}

// 退款相关
export const refundApi = {
  apply: (data) => request.post('/refund/apply', data),
  list: (params) => request.get('/refund/list', { params }),
  getDetail: (id) => request.get(`/refund/${id}`),
  getRefundableItems: (orderId) => request.get(`/refund/refundable-items/${orderId}`)
}

// 我的
export const mineApi = {
  getProfile: () => request.get('/mine/profile'),
  getLeaveRecords: (params) => request.get('/mine/leave-records', { params }),
  getRegistrationResult: () => request.get('/mine/registration-result'),
  getScoreInfo: () => request.get('/mine/score-info')
}