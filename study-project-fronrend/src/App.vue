<template>
  <div>
    <router-view />
  </div>
</template>

<script setup>
import { get } from "@/net";
import { useStore } from '@/stores/user'
import router from "./router";

const store = useStore()
if (store.auth.user == null) {
  get('/api/user/me', (message, data) => {
    store.auth.user = data
  }, () => {
    store.auth.user = null
  })
}


</script>

<style scoped></style>