import axios from 'axios'
import { showToast } from 'vant'

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  res => {
    const data = res.data
    if (data.code !== 200) {
      showToast(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data
  },
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
      return Promise.reject(err)
    }
    const msg = err.response?.data?.message || '请求失败'
    showToast(msg)
    return Promise.reject(err)
  }
)

export default request