<template>
  <div>
    <router-view />
  </div>
</template>

<script setup>
import { get } from "@/net";
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 根组件只负责恢复会话态：如果本地还没有用户信息，就向后端补一次当前登录态。
if (userStore.auth.user == null) {
  get('/api/user/me', (message, data) => {
    // 成功后把用户写回 store，供路由守卫和页面布局复用。
    userStore.auth.user = data
  }, () => {
    // 请求失败时显式置空，保证后续逻辑按未登录态处理。
    userStore.auth.user = null
  })
}


</script>