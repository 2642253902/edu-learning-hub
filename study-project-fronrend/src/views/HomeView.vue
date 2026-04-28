<template>
    <div class="welcome-container">
        <!-- 欢迎头部 -->
        <el-card shadow="never" class="welcome-header">
            <div class="header-flex">
                <div class="user-info">
                    <el-avatar :size="64" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                    <div class="welcome-text">
                        <h3>{{ getTimeState() }}，{{ userStore.auth.user?.username }}！</h3>
                        <p>{{ isAdmin ? '系统管理员，今天有 5 项安全待处理。' : '学生，加油学习，今天已累计在线 2.5 小时。' }}</p>
                    </div>
                </div>
                <div class="header-stat">
                    <div class="stat-item">
                        <div class="label">{{ isAdmin ? '用户总数' : '已修课程' }}</div>
                        <div class="value">{{ isAdmin ? '1,280' : '12' }}</div>
                    </div>
                    <el-divider direction="vertical" />
                    <div class="stat-item">
                        <div class="label">{{ isAdmin ? '在线人数' : '本周排名' }}</div>
                        <div class="value">{{ isAdmin ? '156' : '15' }}</div>
                    </div>
                </div>
            </div>
        </el-card>

        <!-- 数据统计区域 -->
        <el-row :gutter="20" class="mt-4">
            <el-col :span="isAdmin ? 16 : 24">
                <el-card shadow="hover" header="学习/活跃趋势统计">
                    <div ref="chartRef" style="height: 350px; width: 100%;"></div>
                </el-card>
            </el-col>
            <el-col :span="8" v-if="isAdmin">
                <el-card shadow="hover" header="课程分类占比">
                    <div ref="pieRef" style="height: 350px; width: 100%;"></div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 快捷入口/任务 -->
        <el-row :gutter="20" class="mt-4">
            <el-col :span="12">
                <el-card shadow="hover" header="我的待办/系统消息">
                    <el-timeline>
                        <el-timeline-item timestamp="2024-03-28" type="primary">
                            {{ isAdmin ? '系统备份计划已成功完成' : '《云计算概论》课程由教师发布了新作业' }}
                        </el-timeline-item>
                        <el-timeline-item timestamp="2024-03-27" type="success">
                            {{ isAdmin ? '新增 12 名学生用户注册申请通过' : '获得“勤奋之星”勋章' }}
                        </el-timeline-item>
                        <el-timeline-item timestamp="2024-03-25" type="warning">
                            {{ isAdmin ? '服务器磁盘空间占用超过 80%' : '本月课程学时达标提醒' }}
                        </el-timeline-item>
                    </el-timeline>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card shadow="hover" header="快捷入口">
                    <div class="quick-links">
                        <el-button v-for="link in quickLinks" :key="link.name" type="primary" plain class="link-btn"
                            @click="router.push(link.path)">
                            <el-icon class="mr-1">
                                <component :is="link.icon" />
                            </el-icon>
                            {{ link.name }}
                        </el-button>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
    Collection,
    User,
    Setting,
    Monitor,
    VideoCamera,
    FolderChecked
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const chartRef = ref<HTMLElement | null>(null)
const pieRef = ref<HTMLElement | null>(null)
let mainChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

// 判断角色：1通常是管理员
const isAdmin = computed(() => userStore.auth.user?.role === 1)

const getTimeState = () => {
    const hour = new Date().getHours()
    if (hour >= 6 && hour < 12) return '早上好'
    if (hour >= 12 && hour < 18) return '下午好'
    return '晚上好'
}

const quickLinks = computed(() => {
    if (isAdmin.value) {
        return [
            { name: '用户管理', path: '/sys/user', icon: User },
            { name: '角色分配', path: '/sys/role', icon: Setting },
            { name: '课程管理', path: '/study/course-list', icon: Monitor },
            { name: '用户列表', path: '/sys/user-list', icon: FolderChecked }
        ]
    }
    return [
        { name: '我的课程', path: '/study/course-list', icon: VideoCamera },
        { name: '学习记录', path: '/study/learning-record', icon: Collection },
        { name: '课程中心', path: '/study/course-type', icon: Monitor }
    ]
})

onMounted(() => {
    // 趋势图 (折线图)
    if (chartRef.value) {
        mainChart = echarts.init(chartRef.value)
        mainChart.setOption({
            tooltip: { trigger: 'axis' },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
            yAxis: { type: 'value' },
            series: [{
                name: isAdmin.value ? '活跃人数' : '学习时长(min)',
                type: 'line',
                smooth: true,
                data: isAdmin.value ? [120, 132, 101, 134, 90, 230, 210] : [30, 45, 120, 60, 40, 150, 180],
                itemStyle: { color: '#409EFF' },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(64,158,255,0.3)' },
                        { offset: 1, color: 'rgba(64,158,255,0)' }
                    ])
                }
            }]
        })
    }

    // 管理员饼图
    if (pieRef.value && isAdmin.value) {
        pieChart = echarts.init(pieRef.value)
        pieChart.setOption({
            tooltip: { trigger: 'item' },
            legend: { bottom: '0%', left: 'center' },
            series: [{
                name: '课程占比',
                type: 'pie',
                radius: ['40%', '70%'],
                avoidLabelOverlap: false,
                itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
                label: { show: false, position: 'center' },
                data: [
                    { value: 1048, name: '编程开发' },
                    { value: 735, name: '人工智能' },
                    { value: 580, name: '网络安全' },
                    { value: 484, name: '云计算' }
                ]
            }]
        })
    }

    window.addEventListener('resize', handleResize)
})

const handleResize = () => {
    mainChart?.resize()
    pieChart?.resize()
}

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    mainChart?.dispose()
    pieChart?.dispose()
})
</script>

<style scoped>
.welcome-container {
    padding: 0;
}

.welcome-header {
    border: none;
    background: linear-gradient(to right, #ffffff, #f0f7ff);
}

.header-flex {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 20px;
}

.welcome-text h3 {
    margin: 0;
    font-size: 20px;
    color: #303133;
}

.welcome-text p {
    margin: 8px 0 0;
    color: #909399;
    font-size: 14px;
}

.header-stat {
    display: flex;
    align-items: center;
    gap: 30px;
}

.stat-item {
    text-align: center;
}

.stat-item .label {
    font-size: 13px;
    color: #909399;
    margin-bottom: 4px;
}

.stat-item .value {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
}

.mt-4 {
    margin-top: 20px;
}

.quick-links {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
}

.link-btn {
    width: calc(50% - 6px);
    margin-left: 0 !important;
    margin-bottom: 4px;
    justify-content: flex-start;
    height: 40px;
}

.mr-1 {
    margin-right: 4px;
}
</style>
