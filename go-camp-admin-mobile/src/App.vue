<template>
  <div id="app">
    <router-view />
    <van-tabbar v-if="showTabbar" route>
      <van-tabbar-item to="/dispatch" icon="chart-trending-o">每日调度</van-tabbar-item>
      <van-tabbar-item to="/leave-manage" icon="notes-o">请假管理</van-tabbar-item>
      <van-tabbar-item to="/mine" icon="manager-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const showTabbar = computed(() => {
  return ['/dispatch', '/leave-manage', '/mine'].includes(route.path)
})
</script>