import { reactive, watch } from 'vue'
import { defineStore } from 'pinia'

type AuthUser = {
  [key: string]: any
  id?: string | number
  username?: string
  role?: string | number
}

export const useUserStore = defineStore('user', () => {
  // 兼容 SSR/预渲染：仅在浏览器环境读取本地存储，方便前端恢复后端会话态。
  const storage = typeof window !== 'undefined' ? window.localStorage : null

  const parseUser = (value: string | null): AuthUser | null => {
    if (!value) return null

    try {
      const parsed = JSON.parse(value)
      return parsed && typeof parsed === 'object' ? parsed as AuthUser : null
    } catch {
      // 用户缓存损坏时清理掉，避免页面初始化后影响前后端会话判断。
      storage?.removeItem('user')
      return null
    }
  }

  // 页面刷新时尝试恢复用户信息，避免每次都重新登录并重新请求后端。
  const auth = reactive<{ user: AuthUser | null }>({
    user: parseUser(storage?.getItem('user') ?? null),
  })

  // 用户信息发生变化时立刻落盘；登出（null）时清理缓存，防止旧会话残留到前端。
  watch(() => auth.user, (newVal) => {
    if (newVal) {
      storage?.setItem('user', JSON.stringify(newVal))
    } else {
      storage?.removeItem('user')
    }
  }, { deep: true })

  return { auth }
})
