<template>
    <div class="register-page">
        <div class="header">
            <h2 class="title">加入我们</h2>
            <p class="subtitle">填写以下信息，开启您的智慧学习之旅</p>
        </div>

        <div class="form-container">
            <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                <el-form-item prop="username">
                    <el-input v-model="form.username" :maxlength="8" size="large" type="text" placeholder="设置用户名">
                        <template #prefix>
                            <el-icon>
                                <User />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item prop="password">
                    <el-input v-model="form.password" :maxlength="16" size="large" type="password" placeholder="设置登录密码">
                        <template #prefix>
                            <el-icon>
                                <Lock />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item prop="password_repeat">
                    <el-input v-model="form.password_repeat" :maxlength="16" size="large" type="password"
                        placeholder="确认登录密码">
                        <template #prefix>
                            <el-icon>
                                <Lock />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item prop="email">
                    <el-input v-model="form.email" size="large" type="email" placeholder="邮箱地址">
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

            <div class="actions mt-6">
                <el-button class="submit-btn" size="large" type="primary" @click="register">
                    立即注册
                </el-button>
                <div class="login-link">
                    已经有账号了? <el-link type="primary" underline="hover" @click="router.push('/')">返回登录</el-link>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { EditPen, Lock, Message, User } from "@element-plus/icons-vue";
import router from "@/router";
import { reactive, ref, computed } from "vue";
import { ElMessage } from "element-plus";
import { post, postForm } from "@/net";

const form = reactive({
    username: '',
    password: '',
    password_repeat: '',
    email: '',
    code: ''
})

const formRef = ref()
const coldTime = ref(0)
const isEmailValid = ref(false)

const validateUsername = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请输入用户名'))
    } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
        callback(new Error('用户名只能包含字母、数字和汉字'))
    } else {
        callback()
    }
}

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
    username: [
        { validator: validateUsername, trigger: ['blur', 'change'] },
        { min: 2, max: 8, message: '用户名的长度必须在2-8个字符之间', trigger: ['blur', 'change'] },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change'] }
    ],
    password_repeat: [
        { validator: validatePassword, trigger: ['blur', 'change'] },
    ],
    email: [
        { required: true, message: '请输入邮件地址', trigger: 'blur' },
        { type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change'] }
    ],
    code: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
    ]
}

function onValidate(prop, isValid) {
    if (prop === 'email')
        isEmailValid.value = isValid
}

function register() {
    formRef.value.validate((isValid) => {
        if (isValid) {
            postForm('/api/auth/register', {
                username: form.username,
                password: form.password,
                email: form.email,
                code: form.code
            }, (message) => {
                ElMessage.success(message)
                router.push("/")
            })
        } else {
            ElMessage.warning('请完整填写注册表单内容！')
        }
    })
}

function validateEmail() {
    coldTime.value = 60
    get(`/api/auth/valid-register-email?email=${form.email}`, (message) => {
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
</script>

<style scoped>
.register-page {
    padding: 10px 0;
}

.header {
    margin-bottom: 30px;
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

.code-row {
    display: flex;
    gap: 12px;
    width: 100%;
}

.code-btn {
    white-space: nowrap;
    min-width: 100px;
}

.mt-6 {
    margin-top: 24px;
}

.submit-btn {
    width: 100%;
    border-radius: 4px;
    font-weight: 500;
    height: 48px;
    font-size: 16px;
    background: #fa8c16;
    border-color: #fa8c16;
}

.submit-btn:hover {
    background: #ff9c6e;
    border-color: #ff9c6e;
}

.login-link {
    margin-top: 24px;
    font-size: 14px;
    color: #8c8c8c;
    text-align: center;
}
</style>