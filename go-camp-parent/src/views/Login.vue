<template>
  <div class="login-container">
    <div class="login-header">
      <h1>围棋训练营</h1>
      <p>家长端</p>
    </div>
    <van-form @submit="onLogin" class="login-form">
      <van-cell-group inset>
        <van-field
          v-model="form.username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
        />
        <van-field
          v-model="form.password"
          name="password"
          label="密码"
          type="password"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">
          登录
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { authApi } from '@/api'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const onLogin = async () => {
  loading.value = true
  try {
    const res = await authApi.login(form)
    localStorage.setItem('token', res.data.accessToken)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
    showToast('登录成功')
    router.replace('/workbench')
  } catch (e) {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}
.login-header {
  text-align: center;
  margin-bottom: 40px;
  color: #fff;
}
.login-header h1 {
  font-size: 28px;
  margin: 0 0 8px;
}
.login-header p {
  font-size: 16px;
  opacity: 0.8;
  margin: 0;
}
.login-form {
  width: 100%;
  max-width: 360px;
}
</style>