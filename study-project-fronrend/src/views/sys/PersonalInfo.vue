<template>
  <div class="profile-page">
    <el-card shadow="never" class="hero-card">
      <div class="hero-wrap">
        <div class="avatar-block">
          <el-avatar :size="76" :src="avatarUrl" />
          <div>
            <h2 class="title">个人信息</h2>
            <p class="subtitle">查看当前登录账号的基础资料与角色信息。</p>
          </div>
        </div>
        <div class="hero-actions">
          <el-button @click="openEdit">编辑资料</el-button>
          <el-button @click="router.back()">返回</el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="mt-4">
      <el-col :xs="24" :md="14">
        <el-card shadow="never" class="info-card" v-loading="loading">
          <template #header>
            <div class="card-head">
              <span>账号资料</span>
              <el-tag :type="roleTagType">{{ roleText }}</el-tag>
            </div>
          </template>

          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户ID">{{ profile.id || '-' }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ profile.username || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ profile.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ roleText }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="10">
        <el-card shadow="never" class="tips-card">
          <template #header>
            <span>说明</span>
          </template>
          <div class="tip-item">当前页面基于 /api/user/me 获取登录态信息。</div>
          <div class="tip-item">如果需要编辑资料，可以继续接入后端个人信息更新接口。</div>
          <div class="tip-item">消息中心与课程管理入口已在顶部导航和首页快捷入口补充。</div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="editDialog.visible" title="编辑个人信息" width="520px" @close="resetForm">
      <el-form ref="formRef" :model="editDialog.form" label-width="90px" :rules="formRules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editDialog.form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editDialog.form.email" placeholder="请输入邮箱" type="email" />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="editDialog.form.password" placeholder="留空表示不修改密码" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '../../net'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const profile = ref<any>({})
const formRef = ref<FormInstance>()
const avatarUrl = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const editDialog = reactive({
  visible: false,
  form: {
    id: '',
    username: '',
    email: '',
    password: '',
    role: ''
  }
})

const formRules = {
  username: [
    { required: true, message: '用户名不能为空', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '邮箱不能为空', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { min: 6, max: 16, message: '密码长度为 6-16 个字符', trigger: 'blur' }
  ]
}

// 与后端角色码保持一致，集中在计算属性中做展示映射，模板层只消费结果。
const roleText = computed(() => {
  const role = String(profile.value?.role || '')
  if (role === '1') return '管理员'
  if (role === '2') return '教师'
  if (role === '3') return '学生'
  return '未知角色'
})

const roleTagType = computed(() => {
  const role = String(profile.value?.role || '')
  if (role === '1') return 'danger'
  if (role === '2') return 'success'
  if (role === '3') return 'info'
  return 'warning'
})

const openEdit = () => {
  editDialog.form = {
    id: String(profile.value?.id || ''),
    username: profile.value?.username || '',
    email: profile.value?.email || '',
    password: '',
    role: String(profile.value?.role || '')
  }
  editDialog.visible = true
}

const resetForm = () => {
  formRef.value?.clearValidate()
  editDialog.form.password = ''
}

const loadProfile = () => {
  loading.value = true
  // 统一从登录态接口获取“当前用户”信息，避免依赖本地缓存造成数据漂移。
  get('/api/user/me', (_msg, data) => {
    profile.value = data || {}
    loading.value = false
  }, () => {
    loading.value = false
  })
}

const submitProfile = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const payload: Record<string, any> = {
    id: editDialog.form.id,
    username: editDialog.form.username,
    email: editDialog.form.email
  }

  if (editDialog.form.password.trim()) {
    payload.password = editDialog.form.password
  }

  post('/api/user/manage/edit', payload, (msg) => {
    ElMessage.success(msg || '保存成功')
    editDialog.visible = false
    profile.value = {
      ...profile.value,
      username: editDialog.form.username,
      email: editDialog.form.email
    }
    if (editDialog.form.password.trim()) {
      editDialog.form.password = ''
    }
  })
}

onMounted(loadProfile)
</script>

<style scoped>
.profile-page {
  padding: 0;
}

.hero-card,
.info-card,
.tips-card {
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
}

.hero-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.avatar-block {
  display: flex;
  align-items: center;
  gap: 16px;
}

.title {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  color: #111827;
}

.subtitle {
  margin: 6px 0 0;
  color: #6b7280;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tip-item {
  padding: 10px 0;
  color: #4b5563;
  line-height: 1.7;
  border-bottom: 1px dashed #e5e7eb;
}

.tip-item:last-child {
  border-bottom: 0;
}

.mt-4 {
  margin-top: 16px;
}
</style>
