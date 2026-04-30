<template>
    <div class="forget-page">
        <div class="header">
            <h2 class="title" v-if="active === 0">重置密码</h2>
            <h2 class="title" v-if="active === 1">设置新密码</h2>
            <p class="subtitle">通过已绑定的电子邮箱验证您的身份</p>
        </div>

        <div class="steps-container">
            <el-steps :active="active" finish-status="success" simple>
                <el-step title="身份验证" />
                <el-step title="重设密码" />
            </el-steps>
        </div>

        <div class="form-container mt-8">
            <transition name="el-fade-in-linear" mode="out-in">
                <div v-if="active === 0">
                    <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                        <el-form-item prop="email">
                            <el-input v-model="form.email" size="large" type="email" placeholder="绑定的邮箱地址">
                                <template #prefix>
                                    <el-icon>
                                        <Message />
                                    </el-icon>
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item prop="code">
                            <div class="code-row">
                                <el-input v-model="form.code" :maxlength="6" size="large" placeholder="验证码">
                                    <template #prefix>
                                        <el-icon>
                                            <EditPen />
                                        </el-icon>
                                    </template>
                                </el-input>
                                <el-button type="success" size="large" class="code-btn" @click="validateEmail"
                                    :disabled="!isEmailValid || coldTime > 0">
                                    {{ coldTime > 0 ? coldTime + 's' : '获取' }}
                                </el-button>
                            </div>
                        </el-form-item>
                    </el-form>

                    <div class="actions mt-10">
                        <el-button class="submit-btn reset-btn" size="large" type="danger" @click="startReset()">
                            验证并下一步
                        </el-button>
                    </div>
                </div>

                <div v-else-if="active === 1">
                    <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                        <el-form-item prop="password">
                            <el-input v-model="form.password" :maxlength="16" size="large" type="password"
                                placeholder="输入新密码">
                                <template #prefix>
                                    <el-icon>
                                        <Lock />
                                    </el-icon>
                                </template>
                            </el-input>
                        </el-form-item>

                        <el-form-item prop="password_repeat">
                            <el-input v-model="form.password_repeat" :maxlength="16" size="large" type="password"
                                placeholder="确认新密码">
                                <template #prefix>
                                    <el-icon>
                                        <Lock />
                                    </el-icon>
                                </template>
                            </el-input>
                        </el-form-item>
                    </el-form>

                    <div class="actions mt-10">
                        <el-button class="submit-btn reset-btn" size="large" type="danger" @click="doReset()">
                            立即修改密码
                        </el-button>
                    </div>
                </div>
            </transition>

            <div class="login-link">
                记起密码了? <el-link type="primary" underline="hover" @click="router.push('/')">立即登录</el-link>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { EditPen, Lock, Message } from "@element-plus/icons-vue";
import { postForm } from "@/net";
import { ElMessage } from "element-plus";
import router from "@/router";

// 0：先验证邮箱和验证码；1：再输入新密码完成重置。
const active = ref(0)
const formRef = ref()
// 邮箱验证码同样需要倒计时，防止重复发送干扰用户体验。
const coldTime = ref(0)
// 只有邮箱通过格式校验后，才允许发起验证码请求。
const isEmailValid = ref(false)

const form = reactive({
    email: '',
    code: '',
    password: '',
    password_repeat: ''
})

const validatePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== form.password) {
        callback(new Error("两次输入的密码不一致"))
    } else {
        callback()
    }
}

const rules = {
    // 两个步骤共用同一份表单实例，所以这里只保留当前步骤需要的校验规则。
    email: [
        { required: true, message: '请输入邮件地址', trigger: 'blur' },
        { type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change'] }
    ],
    code: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change'] }
    ],
    password_repeat: [
        { validator: validatePassword, trigger: ['blur', 'change'] },
    ],
}

function onValidate(prop, isValid) {
    if (prop === 'email')
        isEmailValid.value = isValid
}

function validateEmail() {
    // 只有在邮箱可用时才进入冷却，成功后才真正开始倒计时。
    coldTime.value = 60
    postForm(`/api/auth/validate-reset-email`, { email: form.email, type: 'reset' }, (message) => {
        ElMessage.success(message)
        const handle = setInterval(() => {
            coldTime.value--
            if (coldTime.value === 0) {
                clearInterval(handle)
            }
        }, 1000)
    }, (message) => {
        ElMessage.warning(message)
        coldTime.value = 0
    })
}

function startReset() {
    // 第一阶段只确认身份，不提前暴露新密码输入入口。
    formRef.value.validate((isValid) => {
        if (isValid) {
            postForm('/api/auth/start-reset', {
                email: form.email,
                code: form.code
            }, () => active.value++)
        }
    })
}

function doReset() {
    // 第二阶段提交新密码时，沿用同一邮箱与验证码，确保重置链路闭环。
    formRef.value.validate((isValid) => {
        if (isValid) {
            postForm('/api/auth/do-reset', {
                email: form.email,
                code: form.code,
                password: form.password
            }, (message) => {
                ElMessage.success(message)
                router.push('/')
            })
        }
    })
}
</script>

<style scoped>
.forget-page {
    padding: 10px 0;
}

.header {
    margin-bottom: 24px;
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

.steps-container {
    margin-bottom: 32px;
}

:deep(.el-steps--simple) {
    padding: 13px 8%;
    background: #fafafa;
    border-radius: 4px;
}

.code-row {
    display: flex;
    gap: 12px;
    width: 100%;
}

.code-btn {
    white-space: nowrap;
    min-width: 90px;
}

.mt-8 {
    margin-top: 32px;
}

.mt-10 {
    margin-top: 40px;
}

.submit-btn {
    width: 100%;
    border-radius: 4px;
    font-weight: 500;
    height: 48px;
    font-size: 16px;
}

.reset-btn {
    background: #ff4d4f;
    border-color: #ff4d4f;
}

.reset-btn:hover {
    background: #ff7875;
    border-color: #ff7875;
}

.login-link {
    margin-top: 24px;
    font-size: 14px;
    color: #8c8c8c;
    text-align: center;
}
</style>