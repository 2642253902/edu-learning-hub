import { ref, watch } from 'vue'
import { defineStore } from 'pinia'

export const useMenuStore = defineStore('menu', () => {
    const storage = typeof window !== 'undefined' ? window.localStorage : null

    const menuListStr = storage?.getItem('menuList')
    const menuList = ref(menuListStr ? JSON.parse(menuListStr) : [])

    // 监听菜单列表同步到 localStorage
    watch(menuList, (newVal) => {
        storage?.setItem('menuList', JSON.stringify(newVal))
    }, { deep: true })

    return { menuList }
})