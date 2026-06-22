import request from '@/utils/request'

// 认证
export const login = (data) => request.post('/auth/login', data)
export const logout = () => request.post('/auth/logout')

// 每日调度
export const getDispatchStats = (params) => request.get('/dispatch/stats', { params })
export const getDispatchList = (params) => request.get('/dispatch/list', { params })

// 段位分布
export const getRankDistribution = (params) => request.get('/dispatch/rank-distribution', { params })

// 订单管理
export const getOrderList = (params) => request.get('/order/list', { params })
export const getOrderDetail = (id) => request.get(`/order/${id}`)
export const exportOrders = (params) => request.get('/order/export', { params, responseType: 'blob' })

// 审批中心
export const getApprovalList = (params) => request.get('/approval/list', { params })
export const getLeaveDetail = (id) => request.get(`/approval/leave/${id}`)
export const approveLeave = (data) => request.post('/approval/leave', data)
export const approveRefund = (data) => request.post('/approval/refund', data)
export const approveMealCancel = (data) => request.post('/approval/meal-cancel', data)
export const executeRefund = (data) => request.post('/approval/execute-refund', data)
export const addManualLeave = (data) => request.post('/approval/manual-leave', data)

// 学员管理
export const getStudentList = (params) => request.get('/student/list', { params })
export const getStudentDetail = (id) => request.get(`/student/${id}`)
export const updateStudentRank = (data) => request.put('/student/rank', data)
export const adjustStudentClass = (data) => request.post('/student/adjust-class', data)
export const updateStudentCampStatus = (id, campStatus) => request.put(`/student/${id}/camp-status`, { campStatus })

// 用户管理
export const getUserList = (params) => request.get('/user/list', { params })
export const createUser = (data) => request.post('/user', data)
export const addUser = createUser  // 别名，兼容UserManage.vue中的引用
export const updateUser = (data) => request.put('/user', data)
export const deleteUser = (id) => request.delete(`/user/${id}`)
export const toggleUserStatus = (data) => request.put('/user/status', data)
export const resetPassword = (data) => request.put('/user/reset-password', data)

// 角色管理
export const getRoleList = (params) => request.get('/role/list', { params })
export const createRole = (data) => request.post('/role', data)
export const addRole = createRole  // 别名
export const updateRole = (data) => request.put('/role', data)
export const deleteRole = (id) => request.delete(`/role/${id}`)
export const assignPermissions = (data) => request.post('/role/assign-permissions', data)
export const assignRolePermissions = assignPermissions  // 别名

// 权限管理
export const getPermissionTree = () => request.get('/permission/tree')
export const createPermission = (data) => request.post('/permission', data)
export const addPermission = createPermission  // 别名
export const updatePermission = (data) => request.put('/permission', data)
export const deletePermission = (id) => request.delete(`/permission/${id}`)

// 部门管理
export const getDepartmentList = (params) => request.get('/department/list', { params })
export const updateDepartment = (data) => request.put('/department', data)

// 费用配置
export const getFeeConfigList = (params) => request.get('/fee-config/list', { params })
export const createFeeConfig = (data) => request.post('/fee-config', data)
export const addFeeConfig = createFeeConfig  // 别名
export const updateFeeConfig = (data) => request.put('/fee-config', data)
export const toggleFeeConfigStatus = (data) => request.put('/fee-config/status', data)
export const getFeeConfigHistory = (id) => request.get(`/fee-config/${id}/history`)

// 营期配置
export const getSessionList = () => request.get('/session/list')
export const createSession = (data) => request.post('/session', data)
export const addSession = createSession  // 别名
export const updateSession = (data) => request.put('/session', data)
export const toggleSessionStatus = (data) => request.put('/session/status', data)
export const getDailyRules = (params) => request.get('/session/daily-rules', { params })
export const updateDailyRule = (data) => request.put('/session/daily-rule', data)
export const saveDailyRules = updateDailyRule  // 别名

// 段位配置
export const getRankConfigList = () => request.get('/rank-config/list')
export const createRankConfig = (data) => request.post('/rank-config', data)
export const addRankConfig = createRankConfig  // 别名
export const updateRankConfig = (data) => request.put('/rank-config', data)
export const deleteRankConfig = (id) => request.delete(`/rank-config/${id}`)

// 班级管理
export const getClassList = (params) => request.get('/class/list', { params })
export const addClass = (data) => request.post('/class', data)
export const updateClass = (data) => request.put('/class', data)
export const deleteClass = (id) => request.delete(`/class/${id}`)
export const getClassDetail = (id) => request.get(`/class/detail/${id}`)
export const getClassStudents = (id) => request.get(`/class/${id}/students`)
export const addClassStudent = (data) => request.post('/class/add-student', data)
export const removeClassStudent = (data) => request.post('/class/remove-student', data)
export const exportClassStudents = (id) => request.get(`/class/${id}/export`, { responseType: 'blob' })

// 分房分床管理
export const getRoomStats = (params) => request.get('/room/stats', { params })
export const getRoomList = (params) => request.get('/room/list', { params })
export const getRoomDetail = (id) => request.get(`/room/${id}`)
export const createRoom = (data) => request.post('/room', data)
export const updateRoom = (data) => request.put('/room', data)
export const manualMatchRoom = (data) => request.post('/room/manual-match', data)
export const adjustRoom = manualMatchRoom  // 别名
export const releaseRoom = (data) => request.post('/room/release', data)
export const confirmRoom = (data) => request.post('/room/confirm', data)
export const getAvailableRooms = (params) => request.get('/room/available', { params })
export const exportRooms = (params) => request.get('/room/export', { params, responseType: 'blob' })
