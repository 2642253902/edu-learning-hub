<template>
  <div>
    <router-view />
  </div>
</template>

<script setup>
import { get } from "@/net";
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 根组件启动时主动向后端复验当前会话，保证本地缓存不会绕过后端会话校验。
get('/api/user/me', (message, data) => {
  // 成功后把后端返回的用户信息写回 store，供前端各层统一读取。
  userStore.auth.user = data
}, () => {
  // 请求失败或未登录时显式置空，保证前后端都按未登录态继续处理。
  userStore.auth.user = null
})


</script>