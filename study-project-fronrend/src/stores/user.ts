import { ref, computed, reactive, watch } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  // 使用 localStorage 而非 sessionStorage，保证浏览器重启后仍能恢复会话
  const storage = typeof window !== 'undefined' ? window.localStorage : null

  // 刷新时尝试从 localStorage 中恢复用户信息
  const userStr = storage?.getItem('user')
  const auth = reactive({
    user: userStr ? JSON.parse(userStr) : null,
  })

  // 监听用户信息变化并同步到 localStorage
  watch(() => auth.user, (newVal) => {
    if (newVal) {
      storage?.setItem('user', JSON.stringify(newVal))
    } else {
      storage?.removeItem('user')
    }
  }, { deep: true })

  return { auth }
})
