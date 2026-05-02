<template>
    <el-container class="layout-container">
        <!-- 顶部导航栏 -->
        <el-header class="top-nav">
            <div class="top-nav-brand">
                <div class="logo-mark">
                    <el-icon class="logo-icon">
                        <Reading />
                    </el-icon>
                </div>
                <div class="logo-copy">
                    <span class="logo-text">校园智慧学习社区</span>
                    <span class="logo-subtitle">Learning Hub Console</span>
                </div>
            </div>

            <el-scrollbar class="top-nav-scroll">
                <el-menu :default-active="route.path" class="el-menu-horizontal" mode="horizontal"
                    background-color="#2b303b" text-color="#a3a6ad" active-text-color="#ffffff" router>
                    <el-menu-item index="/index/home">
                        <el-icon>
                            <House />
                        </el-icon>
                        <template #title>工作台首页</template>
                    </el-menu-item>
                    <template v-for="menu in visibleMenuList">
                        <el-sub-menu v-if="menu.children && menu.children.length > 0" :key="`${menu.id}-sub`"
                            :index="menu.path">
                            <template #title>
                                <el-icon>
                                    <Menu />
                                </el-icon>
                                <span>{{ menu.remark }}</span>
                            </template>

                            <template v-for="child in menu.children">
                                <el-sub-menu v-if="child.children && child.children.length > 0" :key="`${child.id}-sub`"
                                    :index="child.path">
                                    <template #title>
                                        <span>{{ child.remark }}</span>
                                    </template>

                                    <el-menu-item v-for="grandChild in child.children" :key="grandChild.id"
                                        :index="grandChild.path">
                                        {{ grandChild.remark }}
                                    </el-menu-item>
                                </el-sub-menu>

                                <el-menu-item v-else :key="`${child.id}-item`" :index="child.path">
                                    {{ child.remark }}
                                </el-menu-item>
                            </template>
                        </el-sub-menu>

                        <el-menu-item v-else :key="`${menu.id}-item`" :index="menu.path">
                            <el-icon>
                                <Menu />
                            </el-icon>
                            <template #title>{{ menu.remark }}</template>
                        </el-menu-item>
                    </template>
                </el-menu>
            </el-scrollbar>
        </el-header>

        <!-- 右侧主体内容 -->
        <el-container class="main-container">
            <!-- 顶部通知/导航栏 -->
            <el-header class="header">
                <div class="header-left">
                    <el-breadcrumb separator="/" class="breadcrumb">
                        <el-breadcrumb-item :to="{ path: '/index/home' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
                    </el-breadcrumb>
                </div>

                <div class="header-right">
                    <el-select v-model="searchModulePath" class="search-select" filterable clearable
                        :filter-method="filterModuleOptions" placeholder="搜索菜单模块并跳转" @change="handleModuleSearchChange">
                        <el-option v-for="item in filteredModuleOptions" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>

                    <el-popover placement="bottom-end" :width="380" trigger="click" @show="handleMessagePopoverShow">
                        <template #reference>
                            <el-badge :value="unreadMessageCount" :hidden="unreadMessageCount === 0"
                                class="notice-badge">
                                <el-icon class="notice-icon">
                                    <Bell />
                                </el-icon>
                            </el-badge>
                        </template>

                        <div class="message-popover">
                            <div class="message-popover-head">
                                <span>消息提醒</span>
                                <el-link type="primary" @click="goMessages">查看全部</el-link>
                            </div>

                            <el-scrollbar max-height="280px">
                                <div v-for="item in messageList" :key="item.key" class="message-popover-item"
                                    :class="{ unread: item.unread }" @click="openMessage(item)">
                                    <div class="message-popover-title">{{ item.title }}</div>
                                    <div class="message-popover-desc">{{ item.desc }}</div>
                                    <div class="message-popover-time">{{ item.timeText }}</div>
                                </div>
                                <el-empty v-if="messageList.length === 0" description="暂无消息" />
                            </el-scrollbar>
                        </div>
                    </el-popover>

                    <el-dropdown trigger="click">
                        <div class="user-info">
                            <!-- 随机学生头像，可以日后替换掉 -->
                            <el-avatar size="small"
                                src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                            <span class="username">{{ userStore.auth.user?.username || '默认用户' }}</span>
                            <el-icon>
                                <CaretBottom />
                            </el-icon>
                        </div>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item @click="goPersonalInfo">
                                    <el-icon>
                                        <User />
                                    </el-icon> 个人信息
                                </el-dropdown-item>
                                <el-dropdown-item divided @click="logout()">
                                    <el-icon>
                                        <SwitchButton />
                                    </el-icon> 退出登录
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </el-header>

            <!-- 核心主视图区域 -->
            <el-main class="main-content">
                <!-- 为了让里面内容更好看，包裹了一个白色卡片底座 -->
                <div class="content-wrapper">
                    <router-view v-slot="{ Component }">
                        <transition name="fade-transform" mode="out-in">
                            <component :is="Component" />
                        </transition>
                    </router-view>
                </div>
            </el-main>
        </el-container>
    </el-container>
    <AiChat />
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from "element-plus";
import {
    Menu,
    Reading,
    Bell,
    CaretBottom,
    User,
    SwitchButton,
    House
} from '@element-plus/icons-vue'
import { get, post } from "@/net";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import { resetRoutes } from '@/router'
import AiChat from './sys/AiChat.vue'


const userStore = useUserStore()
const menuStore = useMenuStore()
const router = useRouter()
const route = useRoute()

const searchModulePath = ref('')
const searchKeyword = ref('')
const messageList = ref<any[]>([])
let messageTimer: number | undefined

const unreadMessageCount = computed(() => messageList.value.filter((item: any) => item.unread).length)

const currentPageTitle = computed(() => String(route.meta.title || '学习社区控制台'))


const isMenuVisible = (menu: any) => (menu?.menuVisible ?? 1) === 1

const buildVisibleMenus = (menus: any[]): any[] => {
    return (menus || [])
        .filter((menu) => isMenuVisible(menu))
        .map((menu: any) => ({
            ...menu,
            children: buildVisibleMenus(menu.children || [])
        }))
}

const visibleMenuList = computed(() => buildVisibleMenus(menuStore.menuList as any[]))

const flattenMenus = (menus: any[], parent = ''): Array<{ label: string; value: string }> => {
    const result: Array<{ label: string; value: string }> = []
        ; (menus || []).forEach((item: any) => {
            const title = item.remark || item.name || item.path
            const label = parent ? `${parent} / ${title}` : title
            if (item.path) {
                result.push({ label, value: item.path })
            }
            if (item.children && item.children.length > 0) {
                result.push(...flattenMenus(item.children, label))
            }
        })
    return result
}

const moduleOptions = computed(() => {
    return flattenMenus(visibleMenuList.value || [])
})

const filteredModuleOptions = computed(() => {
    if (!searchKeyword.value) return moduleOptions.value
    const keyword = searchKeyword.value.toLowerCase()
    return moduleOptions.value.filter((item: { label: string; value: string }) => item.label.toLowerCase().includes(keyword) || item.value.toLowerCase().includes(keyword))
})

const filterModuleOptions = (keyword: string) => {
    searchKeyword.value = keyword
}

const handleModuleSearchChange = (path: string) => {
    if (!path) return
    router.push(path)
    searchModulePath.value = ''
}

const formatTime = (value: any) => {
    if (!value) return ''
    const date = new Date(value)
    if (Number.isNaN(date.getTime())) return String(value)
    const pad = (num: number) => String(num).padStart(2, '0')
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

const loadMessages = async () => {
    get('/api/message/user/list?limit=12', (_message: string, list: any[]) => {
        messageList.value = (list || []).map((item: any) => ({
            ...item,
            key: item.id,
            desc: item.content,
            timeText: formatTime(item.createTime),
            unread: Number(item.unread) === 1,
            target: '/sys/MessageCenter'
        }))
    })
}

const markMessagesRead = () => {
    post('/api/message/user/readAll', {}, () => {
        messageList.value = messageList.value.map((item: any) => ({ ...item, unread: false }))
    })
}

const handleMessagePopoverShow = () => {
    loadMessages()
}

const openMessage = (item: any) => {
    if (!item?.id) return
    post(`/api/message/user/read?messageId=${encodeURIComponent(item.id)}`, {}, () => {
        loadMessages()
        if (item?.target) {
            router.push(item.target)
        }
    })
}

const goMessages = () => {
    markMessagesRead()
    router.push('/sys/MessageCenter')
}

const goPersonalInfo = () => {
    router.push('/sys/PersonalInfo')
}

const logout = () => {
    get('/api/auth/logout', (message: string) => {
        ElMessage.success(message)
        userStore.auth.user = null
        resetRoutes() // 退出登录时，清空动态路由及菜单
        router.push('/')
    })
}

onMounted(() => {
    loadMessages()
    messageTimer = window.setInterval(loadMessages, 60000)
})

onUnmounted(() => {
    if (messageTimer) {
        window.clearInterval(messageTimer)
    }
})
</script>

<style scoped>
.layout-container {
    height: 100vh;
    width: 100vw;
    background-color: #f5f7fa;
    display: flex;
    flex-direction: column;
}

.top-nav {
    height: 64px;
    background: linear-gradient(135deg, #202733 0%, #2b303b 55%, #1f2937 100%);
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 0 20px;
    box-shadow: 0 2px 8px rgba(0, 21, 41, 0.08);
    z-index: 10;
    flex-shrink: 0;
}

.top-nav-brand {
    display: flex;
    align-items: center;
    gap: 12px;
    color: #fff;
    white-space: nowrap;
    padding: 8px 14px;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.08);
    backdrop-filter: blur(8px);
    box-shadow: 0 10px 24px rgba(15, 23, 42, 0.16);
}

.logo-mark {
    width: 40px;
    height: 40px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.28), rgba(96, 165, 250, 0.12));
    border: 1px solid rgba(96, 165, 250, 0.25);
}

.logo-icon {
    font-size: 22px;
    color: #8ec5ff;
}

.logo-copy {
    display: flex;
    flex-direction: column;
    line-height: 1.1;
}

.logo-text {
    font-size: 17px;
    font-weight: 600;
    letter-spacing: 1px;
    color: #f8fafc;
}

.logo-subtitle {
    margin-top: 4px;
    font-size: 11px;
    letter-spacing: 0.12em;
    color: rgba(226, 232, 240, 0.72);
    text-transform: uppercase;
}

.top-nav-scroll {
    flex: 1;
}

.el-menu-horizontal {
    border-bottom: none;
    display: flex;
    align-items: center;
    background-color: transparent;
}

:deep(.el-menu--horizontal > .el-menu-item),
:deep(.el-menu--horizontal > .el-sub-menu .el-sub-menu__title) {
    height: 64px;
    line-height: 64px;
    border-bottom: none;
}

:deep(.el-menu--horizontal > .el-menu-item.is-active) {
    background-color: rgba(64, 158, 255, 0.15) !important;
}

:deep(.el-menu--horizontal > .el-sub-menu.is-active .el-sub-menu__title) {
    background-color: rgba(64, 158, 255, 0.15) !important;
}

.main-container {
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.header {
    height: 60px;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    z-index: 5;
}

.header-left {
    display: flex;
    align-items: center;
}

.breadcrumb {
    font-size: 14px;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 20px;
}

.search-select {
    width: 220px;
}

:deep(.search-select .el-input__wrapper) {
    border-radius: 20px;
    background-color: #f2f3f5;
    box-shadow: none;
}

:deep(.search-select .el-input__wrapper:focus-within) {
    box-shadow: 0 0 0 1px #409EFF inset;
    background-color: #fff;
}

.notice-badge {
    display: flex;
    align-items: center;
}

.notice-icon {
    font-size: 20px;
    color: #606266;
    cursor: pointer;
    transition: color 0.3s;
}

.notice-icon:hover {
    color: #409EFF;
}

.message-popover {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.message-popover-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
}

.message-popover-item {
    padding: 10px 12px;
    border-radius: 10px;
    margin-bottom: 8px;
    cursor: pointer;
    border: 1px solid #ebeef5;
    background: #fff;
}

.message-popover-item.unread {
    background: #f0f7ff;
    border-color: #cfe2ff;
}

.message-popover-title {
    font-weight: 600;
    color: #1f2937;
}

.message-popover-desc {
    margin-top: 4px;
    font-size: 13px;
    color: #6b7280;
    line-height: 1.5;
}

.message-popover-time {
    margin-top: 6px;
    font-size: 12px;
    color: #9ca3af;
}

.user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    gap: 8px;
    padding: 4px 8px;
    border-radius: 4px;
    transition: background-color 0.3s;
}

.user-info:hover {
    background-color: #f2f3f5;
}

.username {
    font-size: 14px;
    color: #333;
    font-weight: 500;
}

.main-content {
    padding: 24px;
    background-color: #f5f7fa;
    flex: 1;
    overflow-y: auto;
}

.content-wrapper {
    background-color: #fff;
    border-radius: 8px;
    padding: 24px;
    min-height: calc(100vh - 148px);
    /* 100vh - header - padding*2 */
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* Vue 路由页面切换过渡效果 */
.fade-transform-enter-active,
.fade-transform-leave-active {
    transition: all 0.3s cubic-bezier(0.55, 0, 0.1, 1);
}

.fade-transform-enter-from {
    opacity: 0;
    transform: translateX(-20px);
}

.fade-transform-leave-to {
    opacity: 0;
    transform: translateX(20px);
}
</style>