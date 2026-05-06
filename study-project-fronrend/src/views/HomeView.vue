<template>
    <div class="home-shell">
        <el-card shadow="never" class="hero-card">
            <div class="hero-content">
                <div class="hero-copy">
                    <div class="hero-tag">首页入口</div>
                    <h2>学习社区控制台</h2>
                    <p>{{ heroTip }}</p>
                </div>

                <div class="hero-actions">
                    <el-button type="primary" @click="router.push(continuePath)">{{ continueAction }}</el-button>
                    <el-button plain @click="router.push('/community/PublicDiscussion')">社区讨论</el-button>
                </div>
            </div>
        </el-card>

        <el-row :gutter="16" class="dashboard-row">
            <el-col :span="16">
                <el-card shadow="never" class="content-card">
                    <template #header>
                        <div class="card-header">
                            <span class="title">我的学习概览</span>
                            <span class="subtitle">{{ dashboardGreeting || '实时汇总课程、学习和社区数据' }}</span>
                        </div>
                    </template>
                    <div v-if="dashboardSummaryCards.length > 0" class="summary-grid">
                        <div v-for="item in dashboardSummaryCards" :key="item.label" class="summary-item">
                            <div class="summary-value">{{ formatSummaryValue(item.value) }}<span
                                    class="summary-suffix">{{ item.suffix
                                    }}</span></div>
                            <div class="summary-label">{{ item.label }}</div>
                            <div class="summary-desc">{{ item.description }}</div>
                        </div>
                    </div>
                    <el-empty v-else description="暂无可展示的数据" :image-size="72" />
                </el-card>

                <el-card shadow="never" class="content-card" style="margin-top: 16px;">
                    <template #header>
                        <div class="card-header">
                            <span class="title">最新公告</span>
                            <el-button link @click="router.push('/sys/MessageCenter')">更多</el-button>
                        </div>
                    </template>
                    <div v-if="noticeList.length > 0" class="announcement-list">
                        <div v-for="item in noticeList" :key="item.id" class="announcement-item">
                            <div class="dot" />
                            <div class="text">
                                <div class="msg">{{ item.title }}</div>
                                <div class="time">{{ item.time }}</div>
                            </div>
                        </div>
                    </div>
                    <el-empty v-else description="暂无公告" :image-size="60" />
                </el-card>

                <el-card shadow="never" class="content-card" style="margin-top: 16px;">
                    <template #header>
                        <div class="card-header">
                            <span class="title">数据高亮</span>
                            <el-button link @click="router.push('/index/home')">刷新</el-button>
                        </div>
                    </template>
                    <div v-if="dashboardHighlights.length > 0" class="highlight-list">
                        <div v-for="item in dashboardHighlights" :key="item.label" class="highlight-item">
                            <el-tag type="info" effect="plain">{{ item.label }}</el-tag>
                            <span class="highlight-value">{{ item.value }}</span>
                        </div>
                    </div>
                    <el-empty v-else description="暂无高亮数据" :image-size="60" />
                </el-card>
            </el-col>

            <el-col :span="8">
                <!-- 快捷入口/小工具 -->
                <el-card shadow="never" class="side-card">
                    <template #header>
                        <span class="title">快捷功能</span>
                    </template>
                    <div class="quick-links">
                        <div class="link-item" @click="router.push('/study/PreparationCenter')">
                            <el-icon>
                                <Reading />
                            </el-icon>
                            <span>备课中心</span>
                        </div>
                        <div class="link-item" @click="router.push('/study/LearningCenter')">
                            <el-icon>
                                <Reading />
                            </el-icon>
                            <span>学习中心</span>
                        </div>
                        <div class="link-item" @click="router.push('/study/CourseResourceList')">
                            <el-icon>
                                <Files />
                            </el-icon>
                            <span>资源库</span>
                        </div>
                    </div>
                </el-card>

                <el-card shadow="never" class="side-card" style="margin-top: 16px;">
                    <template #header>
                        <span class="title">学习进度</span>
                    </template>
                    <div class="progress-info">
                        <div class="label">当前看板状态</div>
                        <div class="value">{{ dashboardSummaryCards.length }} 项数据</div>
                        <el-progress :percentage="progressPercent" :format="() => ''" />
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { get } from '../net'
import { Reading, Files } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const role = computed(() => String(userStore.auth.user?.role ?? ''))

const roleTip = computed(() => {
    if (role.value === '2') {
        return '当前账号是教师，首页会展示课程建设、资源分布和学生学习概览。'
    }
    if (role.value === '3') {
        return '当前账号是学生，首页会展示课程学习、公告消息和社区动态。'
    }
    return '当前账号会根据权限展示对应的学习看板和消息概览。'
})

const heroTip = computed(() => dashboardGreeting.value || roleTip.value)

const continuePath = computed(() => {
    return '/study/LearningCenter'
})

const continueAction = computed(() => {
    if (role.value === '2') {
        return '进入学习中心'
    }
    if (role.value === '3') {
        return '进入学习中心'
    }
    return '继续学习'
})

const dashboardGreeting = ref('')
const dashboardSummaryCards = ref<Array<{ label: string; value: number | string; suffix?: string; description?: string }>>([])
const dashboardHighlights = ref<Array<{ label: string; value: string }>>([])
const noticeList = ref<Array<{ id: string; title: string; time: string }>>([])

const dashboardEndpoint = computed(() => {
    return role.value === '2' ? '/api/dashboard/teacher' : '/api/dashboard/student'
})

const formatSummaryValue = (value: number | string) => {
    if (typeof value === 'number') {
        return Number.isInteger(value) ? value.toString() : value.toFixed(1)
    }
    return value
}

const loadDashboardData = () => {
    get(dashboardEndpoint.value, (_message, data) => {
        dashboardGreeting.value = String(data?.greeting || '')
        dashboardSummaryCards.value = Array.isArray(data?.summaryCards) ? data.summaryCards.slice(0, 4) : []
        dashboardHighlights.value = Array.isArray(data?.highlights) ? data.highlights : []
    })

    get('/api/message/user/list?limit=3', (_message, data) => {
        noticeList.value = Array.isArray(data) ? data.map((item: any) => ({
            id: String(item.id ?? item.title ?? Math.random()),
            title: item.title || '未命名公告',
            time: item.createTime || '暂无时间'
        })) : []
    })
}

const progressPercent = computed(() => {
    return Math.min(100, Math.max(20, dashboardSummaryCards.value.length * 22))
})

onMounted(loadDashboardData)

watch(dashboardEndpoint, () => {
    loadDashboardData()
})
</script>

<style scoped>
.home-shell {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.hero-card {
    border: 1px solid #e5eefc;
    background: linear-gradient(135deg, #ffffff 0%, #f8fbff 45%, #eef6ff 100%);
}

.hero-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
}

.hero-tag {
    display: inline-flex;
    align-items: center;
    padding: 4px 10px;
    border-radius: 999px;
    background: rgba(37, 99, 235, 0.1);
    color: #2563eb;
    font-size: 12px;
    font-weight: 600;
}

.hero-copy h2 {
    margin: 10px 0 8px;
    font-size: 30px;
    color: #0f172a;
}

.hero-copy p {
    margin: 0;
    color: #64748b;
    line-height: 1.7;
    max-width: 720px;
}

.hero-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
}

.feature-row {
    width: 100%;
}

.feature-card {
    min-height: 220px;
    border-radius: 18px;
    display: flex;
    flex-direction: column;
    gap: 12px;
    justify-content: space-between;
}

.feature-head {
    font-size: 18px;
    font-weight: 700;
    color: #0f172a;
}

.feature-card p {
    margin: 0;
    color: #64748b;
    line-height: 1.7;
}

.feature-card-student {
    background: linear-gradient(180deg, #ffffff 0%, #f7fbff 100%);
}

.feature-card-teacher {
    background: linear-gradient(180deg, #ffffff 0%, #fffaf4 100%);
}

.dashboard-row {
    margin-top: 8px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
}

.subtitle {
    color: #94a3b8;
    font-size: 12px;
}

.title {
    font-weight: bold;
    color: #303133;
}

.summary-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
}

.summary-item {
    padding: 14px;
    border-radius: 14px;
    background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
    border: 1px solid #e6eef8;
}

.summary-value {
    font-size: 22px;
    font-weight: 700;
    color: #0f172a;
}

.summary-suffix {
    margin-left: 4px;
    font-size: 12px;
    color: #64748b;
}

.summary-label {
    margin-top: 6px;
    font-size: 14px;
    font-weight: 600;
    color: #334155;
}

.summary-desc {
    margin-top: 4px;
    font-size: 12px;
    line-height: 1.6;
    color: #64748b;
}

.announcement-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.announcement-item {
    display: flex;
    align-items: center;
    gap: 10px;
}

.announcement-item .dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background-color: #409eff;
}

.announcement-item .text {
    flex: 1;
    display: flex;
    justify-content: space-between;
    font-size: 14px;
}

.announcement-item .time {
    color: #909399;
}

.highlight-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.highlight-item {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
}

.highlight-value {
    color: #334155;
    font-weight: 600;
}

.quick-links {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
    text-align: center;
}

.link-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 8px;
    border-radius: 8px;
    transition: background 0.3s;
}

.link-item:hover {
    background: #f5f7fa;
    color: #409eff;
}

.link-item .el-icon {
    font-size: 24px;
}

.link-item span {
    font-size: 12px;
}

.progress-info {
    padding: 8px 0;
}

.progress-info .label {
    font-size: 13px;
    color: #606266;
    margin-bottom: 8px;
}

.progress-info .value {
    font-size: 20px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 12px;
}

@media (max-width: 768px) {
    .dashboard-row .el-col {
        width: 100% !important;
    }

    .summary-grid {
        grid-template-columns: 1fr;
    }
}
</style>
