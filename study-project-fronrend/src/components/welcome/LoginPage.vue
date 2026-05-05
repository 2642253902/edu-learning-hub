<template>
    <div class="login-page">
        <div class="header">
            <h2 class="title">身份验证</h2>
            <p class="subtitle">请输入您的账号密码以便进入学习空间</p>
        </div>

        <div class="form-container">
            <el-input v-model="form.username" size="large" type="text" placeholder="用户名/邮箱">
                <template #prefix>
                    <el-icon>
                        <User />
                    </el-icon>
                </template>
            </el-input>

            <el-input v-model="form.password" size="large" type="password" placeholder="密码" show-password class="mt-4">
                <template #prefix>
                    <el-icon>
                        <Lock />
                    </el-icon>
                </template>
            </el-input>

            <div class="flex-ops mt-4">
                <el-checkbox v-model="form.remember" label="记住我" />
                <el-link type="primary" underline="hover" @click="router.push('/forget')">忘记密码？</el-link>
            </div>

            <div class="actions mt-10">
                <el-button class="submit-btn" size="large" type="primary" @click="login()">
                    立即登录
                </el-button>

                <div class="register-link">
                    还没有账号? <el-link type="primary" underline="hover" @click="router.push('/register')">点击注册</el-link>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { get, postForm } from '@/net';
import { User, Lock } from '@element-plus/icons-vue'
import { reactive } from "vue";
import router from "@/router";
import { ElMessage } from "element-plus";
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const form = reactive({
    username: '',
    password: '',
    // 登录态可选记住本次会话，由后端决定持久化策略。
    remember: false
})

const login = () => {
    if (form.username === '' || form.password === '') {
        ElMessage.error("请输入用户名和密码")
        return
    } else {
        // 先拿到登录结果，再拉取当前用户信息，保证 store 中状态完整。
        postForm('/api/auth/login', {
            username: form.username,
            password: form.password,
            remember: form.remember
        }, (message, data) => {
            ElMessage.success(message)
            const loginUser = data ?? null
            if (loginUser !== null) {
                userStore.auth.user = loginUser
            }
            get('/api/user/me', (message, data) => {
                userStore.auth.user = data
                router.push({ name: 'index' })
            }, (message) => {
                // 如果 /me 临时失败，优先保留登录接口返回的用户信息，避免把会话状态直接打空。
                ElMessage.warning(message)
                if (loginUser !== null) {
                    ElMessage.info('已使用登录响应中的用户信息进入系统')
                    userStore.auth.user = loginUser
                    router.push({ name: 'index' })
                }
            })
        }, (message) => {
            ElMessage.error(message)
        })
    }
}
</script>

<style scoped>
.login-page {
    padding: 20px 0;
}

.header {
    margin-bottom: 40px;
}

.title {
    font-size: 28px;
    font-weight: 600;
    color: #262626;
    margin-bottom: 8px;
}

.subtitle {
    font-size: 14px;
    color: #8c8c8c;
}

.mt-4 {
    margin-top: 16px;
}

.mt-10 {
    margin-top: 40px;
}

.flex-ops {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.submit-btn {
    width: 100%;
    border-radius: 4px;
    font-weight: 500;
    height: 48px;
    font-size: 16px;
    box-shadow: 0 2px 0 rgba(0, 0, 0, 0.045);
}

.register-link {
    margin-top: 24px;
    font-size: 14px;
    color: #8c8c8c;
    text-align: center;
}
</style>
