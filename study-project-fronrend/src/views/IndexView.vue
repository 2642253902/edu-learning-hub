<template>
    <el-container class="layout-container">
        <!-- 左侧侧边栏 -->
        <el-aside :width="isCollapse ? '64px' : '240px'" class="aside-menu">
            <div class="logo-container" :class="{ 'collapsed': isCollapse }">
                <el-icon class="logo-icon">
                    <Reading />
                </el-icon>
                <span v-show="!isCollapse" class="logo-text">校园智慧学习社区</span>
            </div>

            <el-menu :default-active="route.path" class="el-menu-vertical" :collapse="isCollapse"
                background-color="#2b303b" text-color="#a3a6ad" active-text-color="#ffffff" router>
                <template v-for="menu in menuList" :key="menu.id">
                    <!-- 如果有子级 -->
                    <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
                        <template #title>
                            <el-icon>
                                <Menu />
                            </el-icon>
                            <span>{{ menu.remark }}</span>
                        </template>

                        <template v-for="child in menu.children" :key="child.id">
                            <!-- 二级菜单如果有三级子菜单 -->
                            <el-sub-menu v-if="child.children && child.children.length > 0" :index="child.path">
                                <template #title>
                                    <span>{{ child.remark }}</span>
                                </template>

                                <!-- 三级菜单项 -->
                                <el-menu-item v-for="grandChild in child.children" :key="grandChild.id"
                                    :index="grandChild.path">
                                    {{ grandChild.remark }}
                                </el-menu-item>
                            </el-sub-menu>

                            <!-- 二级菜单项（无子级） -->
                            <el-menu-item v-else :index="child.path">
                                {{ child.remark }}
                            </el-menu-item>
                        </template>
                    </el-sub-menu>

                    <!-- 如果没有子级（一级菜单项） -->
                    <el-menu-item v-else :index="menu.path">
                        <el-icon>
                            <Menu />
                        </el-icon>
                        <template #title>{{ menu.remark }}</template>
                    </el-menu-item>
                </template>
            </el-menu>
        </el-aside>

        <!-- 右侧主体内容 -->
        <el-container class="main-container">
            <!-- 顶部通知/导航栏 -->
            <el-header class="header">
                <div class="header-left">
                    <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
                        <Fold v-if="!isCollapse" />
                        <Expand v-else />
                    </el-icon>
                    <el-breadcrumb separator="/" class="breadcrumb">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>学习社区控制台</el-breadcrumb-item>
                    </el-breadcrumb>
                </div>

                <div class="header-right">
                    <!-- 预留的全局搜索框 -->
                    <el-input v-model="searchQuery" placeholder="搜索课程、资源..." class="search-input"
                        prefix-icon="Search" />

                    <el-badge :value="5" class="notice-badge">
                        <el-icon class="notice-icon">
                            <Bell />
                        </el-icon>
                    </el-badge>

                    <el-dropdown trigger="click">
                        <div class="user-info">
                            <!-- 随机学生头像，可以日后替换掉 -->
                            <el-avatar size="small"
                                src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                            <span class="username">{{ store.auth.user?.username || '默认用户' }}</span>
                            <el-icon>
                                <CaretBottom />
                            </el-icon>
                        </div>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item>
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
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from "element-plus";
import {
    Menu,
    Monitor,
    Setting,
    Reading,
    VideoCamera,
    DataBoard,
    ChatDotRound,
    Fold,
    Expand,
    Search,
    Bell,
    CaretBottom,
    User,
    SwitchButton
} from '@element-plus/icons-vue'
import { get } from "@/net";
import { useRouter, useRoute } from "vue-router";
import { useStore } from '@/stores/user'
import { resetRoutes } from '@/router'


const store = useStore()
const router = useRouter()
const route = useRoute()

const isCollapse = ref(false)
const searchQuery = ref('')

console.log('菜单列表：', store.menuList) // 调试输出菜单列表


const menuList =store.menuList

const logout = () => {
    get('/api/auth/logout', (message) => {
        ElMessage.success(message)
        store.auth.user = null
        resetRoutes() // 退出登录时，清空动态路由及菜单
        router.push('/')
    })
}
</script>

<style scoped>
.layout-container {
    height: 100vh;
    width: 100vw;
    background-color: #f5f7fa;
}

.aside-menu {
    background-color: #2b303b;
    transition: width 0.3s ease;
    display: flex;
    flex-direction: column;
    box-shadow: 2px 0 8px rgba(0, 21, 41, 0.08);
    z-index: 10;
}

.logo-container {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    background-color: #22262e;
    overflow: hidden;
    white-space: nowrap;
}

.logo-icon {
    font-size: 24px;
    color: #409EFF;
}

.logo-text {
    margin-left: 10px;
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 1px;
}

.el-menu-vertical {
    border-right: none;
    flex: 1;
}

.el-menu-vertical:not(.el-menu--collapse) {
    width: 240px;
}

/* 激活菜单样式美化 */
:deep(.el-menu-item.is-active) {
    background-color: #409EFF !important;
    position: relative;
}

:deep(.el-menu-item.is-active::before) {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 4px;
    background-color: #ffffff;
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

.collapse-btn {
    font-size: 20px;
    cursor: pointer;
    margin-right: 20px;
    color: #606266;
    transition: color 0.3s;
}

.collapse-btn:hover {
    color: #409EFF;
}

.breadcrumb {
    font-size: 14px;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 20px;
}

.search-input {
    width: 220px;
}

:deep(.search-input .el-input__wrapper) {
    border-radius: 20px;
    background-color: #f2f3f5;
    box-shadow: none;
}

:deep(.search-input .el-input__wrapper:focus-within) {
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