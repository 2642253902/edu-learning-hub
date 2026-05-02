<template>
  <div>
    <router-view />
  </div>
</template>

<script setup>
import { get } from "@/net";
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 根组件先从后端补齐当前登录态，再把结果交给前端路由守卫和页面布局复用。
if (userStore.auth.user == null) {
  get('/api/user/me', (message, data) => {
    // 成功后把后端返回的用户信息写回 store，供前端各层统一读取。
    userStore.auth.user = data
  }, () => {
    // 请求失败时显式置空，保证前后端都按未登录态继续处理。
    userStore.auth.user = null
  })
}


</script>