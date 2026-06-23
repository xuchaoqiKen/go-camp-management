<template>
  <div class="page-container">
    <van-nav-bar title="我的" fixed placeholder />
    <div class="card">
      <van-cell-group inset>
        <van-cell title="姓名" :value="info.name" />
        <van-cell title="手机号" :value="info.phone" />
        <van-cell title="所属部门" :value="info.department" />
        <van-cell title="角色" :value="info.role" />
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api'

const info = ref({ name: '', phone: '', department: '', role: '' })

onMounted(async () => {
  try {
    const res = await adminApi.getMineInfo()
    if (res.data) info.value = res.data
  } catch (e) { /* default */ }
})
</script>
