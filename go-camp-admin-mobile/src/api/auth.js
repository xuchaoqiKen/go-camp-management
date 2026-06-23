import request from '@/utils/request'

export const authApi = {
  login: (data) => request.post('/auth/login', data),
  wxLogin: (data) => request.post('/auth/wx-login', data),
  refreshToken: (refreshToken) => request.post('/auth/refresh', null, { params: { refreshToken } }),
  logout: () => request.post('/auth/logout')
}

export default authApi