<template>
  <div>
    <router-view />
  </div>
</template>

<script setup>
import { get } from "@/net";
import { useUserStore } from '@/stores/user'
import router from "./router";

const userStore = useUserStore()
if (userStore.auth.user == null) {
  get('/api/user/me', (message, data) => {
    userStore.auth.user = data
  }, () => {
    userStore.auth.user = null
  })
}


</script>

<style scoped></style>