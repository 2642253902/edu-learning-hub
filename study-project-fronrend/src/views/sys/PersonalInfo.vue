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
        <el-button @click="router.back()">返回</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/net'

const router = useRouter()
const loading = ref(false)
const profile = ref<any>({})
const avatarUrl = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

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
