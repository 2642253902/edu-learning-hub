import { ref, watch } from 'vue'
import { defineStore } from 'pinia'

type MenuItem = {
    [key: string]: any
    children?: MenuItem[]
}

export const useMenuStore = defineStore('menu', () => {
    // 兼容 SSR/预渲染：只有在浏览器环境下才访问 localStorage。
    const storage = typeof window !== 'undefined' ? window.localStorage : null

    const parseMenuList = (value: string | null): MenuItem[] => {
        if (!value) return []

        try {
            const parsed = JSON.parse(value)
            return Array.isArray(parsed) ? parsed as MenuItem[] : []
        } catch {
            // 本地菜单缓存损坏时，直接清掉，避免初始化阶段报错。
            storage?.removeItem('menuList')
            return []
        }
    }

    // 启动时优先从本地缓存恢复菜单树，避免刷新后侧边栏短暂丢失。
    const menuList = ref<MenuItem[]>(parseMenuList(storage?.getItem('menuList') ?? null))

    // 菜单有增删改时，实时同步到本地，供下次刷新直接复用。
    watch(menuList, (newVal) => {
        storage?.setItem('menuList', JSON.stringify(newVal))
    }, { deep: true })

    return { menuList }
})