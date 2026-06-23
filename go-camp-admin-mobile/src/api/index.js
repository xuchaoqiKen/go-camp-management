import request from '@/utils/request'

export const adminApi = {
  // 每日调度
  getDispatchStats: (params) => request.get('/admin/dispatch/stats', { params }),
  getDispatchTable: (params) => request.get('/admin/dispatch/table', { params }),
  // 段位分布
  getRankDistribution: (params) => request.get('/admin/rank-distribution', { params }),
  // 请假管理
  getLeaveList: (params) => request.get('/admin/leave/list', { params }),
  getLeaveDetail: (id) => request.get(`/admin/leave/${id}`),
  approveLeave: (id, data) => request.post(`/admin/leave/${id}/approve`, data),
  addLeave: (data) => request.post('/admin/leave/add', data),
  // 退款管理
  getRefundList: (params) => request.get('/admin/refund/list', { params }),
  getRefundDetail: (id) => request.get(`/admin/refund/${id}`),
  approveRefund: (id, data) => request.post(`/admin/refund/${id}/approve`, data),
  executeRefund: (id) => request.post(`/admin/refund/${id}/execute`),
  // 班级管理（查看）
  getClassList: (params) => request.get('/admin/class/list', { params }),
  getClassDetail: (id) => request.get(`/admin/class/${id}`),
  // 我的
  getMineInfo: () => request.get('/admin/mine')
}

export default adminApi