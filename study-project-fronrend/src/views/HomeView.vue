<template>
    <div class="home-shell">
        <el-card shadow="never" class="hero-card">
            <div class="hero-content">
                <div class="hero-copy">
                    <div class="hero-tag">首页入口</div>
                    <h2>学习社区控制台</h2>
                    <p>{{ roleTip }}</p>
                </div>

                <div class="hero-actions">
                    <el-button type="primary" @click="router.push(defaultPath)">{{ defaultAction }}</el-button>
                    <el-button plain @click="router.push('/index/student-dashboard')">学生统计</el-button>
                    <el-button plain @click="router.push('/index/teacher-dashboard')">教师统计</el-button>
                </div>
            </div>
        </el-card>

        <el-row :gutter="16" class="feature-row">
            <el-col :xs="24" :md="8">
                <el-card shadow="hover" class="feature-card feature-card-student">
                    <div class="feature-head">学生统计</div>
                    <p>查看课程学习、资源使用、小组互动和消息提醒。</p>
                    <el-button type="primary" plain @click="router.push('/index/student-dashboard')">进入学生页</el-button>
                </el-card>
            </el-col>
            <el-col :xs="24" :md="8">
                <el-card shadow="hover" class="feature-card feature-card-teacher">
                    <div class="feature-head">教师统计</div>
                    <p>查看课程建设、资源分布、学生学习和评价反馈。</p>
                    <el-button type="primary" plain @click="router.push('/index/teacher-dashboard')">进入教师页</el-button>
                </el-card>
            </el-col>
            <el-col :xs="24" :md="8">
                <el-card shadow="hover" class="feature-card feature-card-system">
                    <div class="feature-head">系统入口</div>
                    <p>消息中心、个人信息和动态菜单都可以从顶部继续进入。</p>
                    <el-button type="primary" plain @click="router.push('/sys/MessageCenter')">查看消息中心</el-button>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const role = computed(() => String(userStore.auth.user?.role ?? ''))

const roleTip = computed(() => {
    if (role.value === '2') {
        return '当前账号是教师，建议先查看教师数据统计，快速了解课程、资源和学生学习情况。'
    }
    if (role.value === '3') {
        return '当前账号是学生，建议先查看学生数据统计，集中了解课程进度和学习行为。'
    }
    return '当前账号可进入学生或教师统计页，先从控制台总入口开始浏览。'
})

const defaultPath = computed(() => {
    if (role.value === '2') {
        return '/index/teacher-dashboard'
    }
    if (role.value === '3') {
        return '/index/student-dashboard'
    }
    return '/index/student-dashboard'
})

const defaultAction = computed(() => {
    if (role.value === '2') {
        return '进入教师统计'
    }
    if (role.value === '3') {
        return '进入学生统计'
    }
    return '开始查看统计'
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

.feature-card-system {
    background: linear-gradient(180deg, #ffffff 0%, #f7fff8 100%);
}

@media (max-width: 768px) {
    .hero-content {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
const handleResize = () => {
