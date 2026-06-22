<template>
  <div id="app">
    <router-view />
    <van-tabbar v-if="showTabbar" v-model="active" route>
      <van-tabbar-item icon="wap-home" to="/workbench">工作台</van-tabbar-item>
      <van-tabbar-item icon="user" to="/mine">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const active = ref(0)

const showTabbar = computed(() => {
  const tabRoutes = ['/workbench', '/mine']
  return tabRoutes.includes(route.path)
})
</script>