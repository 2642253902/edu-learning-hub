<template>
  <div class="message-page">
    <el-card shadow="never" class="hero-card">
      <div class="hero-wrap">
        <div>
          <h2 class="title">消息中心</h2>
          <p class="subtitle">管理员可发布提醒，用户可查看并标记已读。</p>
        </div>
        <div class="hero-actions">
          <el-button v-if="isAdmin" type="primary" @click="openAdd">发布消息</el-button>
          <el-button @click="markAllRead">全部标为已读</el-button>
          <el-button type="primary" :icon="Refresh" @click="loadMessages">刷新</el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="mt-4">
      <el-col :xs="24" :md="16">
        <el-card shadow="never" class="list-card" v-loading="loading">
          <template #header>
            <div class="card-head">
              <span>我的消息</span>
              <el-tag type="info">{{ unreadCount }} 条未读</el-tag>
            </div>
          </template>

          <div v-if="messages.length > 0" class="message-list">
            <div
              v-for="item in messages"
              :key="item.id"
              class="message-item"
              :class="{ unread: Number(item.unread) === 1 }"
              @click="openItem(item)"
            >
              <div class="message-main">
                <div class="message-title">{{ item.title }}</div>
                <div class="message-desc">{{ item.content }}</div>
              </div>
              <div class="message-meta">
                <span class="message-time">{{ formatTime(item.createTime) }}</span>
                <el-tag size="small" :type="levelTag(item.level)">{{ levelText(item.level) }}</el-tag>
              </div>
            </div>
          </div>

          <el-empty v-else description="暂无消息" />
        </el-card>

        <el-card v-if="isAdmin" shadow="never" class="list-card mt-4" v-loading="manageLoading">
          <template #header>
            <div class="card-head">
              <span>消息管理</span>
            </div>
          </template>

          <el-form :inline="true" :model="manageQuery" class="mb-3">
            <el-form-item label="标题">
              <el-input v-model="manageQuery.title" clearable placeholder="输入标题关键词" />
            </el-form-item>
            <el-form-item label="级别">
              <el-select v-model="manageQuery.level" clearable placeholder="全部" style="width: 120px">
                <el-option label="普通" :value="1" />
                <el-option label="重要" :value="2" />
                <el-option label="紧急" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadManageList(1)">查询</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="manageList" border>
            <el-table-column type="index" label="#" width="60" align="center" />
            <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="targetRole" label="接收角色" width="150" align="center">
              <template #default="{ row }">
                {{ roleTextByTarget(row.targetRole) }}
              </template>
            </el-table-column>
            <el-table-column prop="level" label="级别" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="levelTag(row.level)">{{ levelText(row.level) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="180" align="center">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
                <el-popconfirm title="确定删除此消息吗？" @confirm="removeNotice(row)">
                  <template #reference>
                    <el-button link type="danger">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>

          <div class="mt-3 flex-end">
            <el-pagination
              :current-page="managePagination.current"
              :page-size="managePagination.pageSize"
              :total="managePagination.total"
              layout="total, prev, pager, next"
              @current-change="handleManagePageChange"
            />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="tips-card">
          <template #header>
            <span>阅读规则</span>
          </template>
          <div class="tip-item">点击消息可标记单条已读。</div>
          <div class="tip-item">管理员可在本页直接发布或维护消息。</div>
          <div class="tip-item">顶部铃铛会同步展示未读消息数量。</div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '发布消息' : '编辑消息'" width="620px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入消息内容" />
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="form.level" style="width: 160px">
            <el-option label="普通" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="紧急" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="接收角色">
          <el-checkbox-group v-model="targetRoleList">
            <el-checkbox label="1">管理员</el-checkbox>
            <el-checkbox label="2">教师</el-checkbox>
            <el-checkbox label="3">学生</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { messageApi } from '@/net'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const messages = ref<any[]>([])
const manageLoading = ref(false)
const manageList = ref<any[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const form = ref<any>({ id: '', title: '', content: '', level: 1, targetRole: '1,2,3', enabled: 1 })
const targetRoleList = ref<string[]>(['1', '2', '3'])
const manageQuery = ref<any>({ title: '', level: '' })
const managePagination = ref({ current: 1, pageSize: 10, total: 0 })

const isAdmin = computed(() => String(userStore.auth.user?.role || '') === '1')
const unreadCount = computed(() => messages.value.filter(item => Number(item.unread) === 1).length)

const formatTime = (value: any) => {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  const pad = (num: number) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

const loadMessages = () => {
  loading.value = true
  messageApi.userList(50, (list: any[]) => {
    messages.value = list || []
    loading.value = false
  }, () => {
    loading.value = false
  })
}

const markAllRead = () => {
  messageApi.readAll((msg: any) => {
    ElMessage.success(typeof msg === 'string' ? msg : '已全部标记已读')
    loadMessages()
  })
}

const openItem = (item: any) => {
  messageApi.read(item.id, () => {
    loadMessages()
  })
}

const levelText = (level: any) => {
  if (Number(level) === 3) return '紧急'
  if (Number(level) === 2) return '重要'
  return '普通'
}

const levelTag = (level: any) => {
  if (Number(level) === 3) return 'danger'
  if (Number(level) === 2) return 'warning'
  return 'info'
}

const roleTextByTarget = (targetRole: string) => {
  if (!targetRole) return '全部角色'
  const set = targetRole.split(',').map(item => item.trim())
  const text: string[] = []
  if (set.includes('1')) text.push('管理员')
  if (set.includes('2')) text.push('教师')
  if (set.includes('3')) text.push('学生')
  return text.length > 0 ? text.join(' / ') : '全部角色'
}

const loadManageList = (arg = 0) => {
  if (!isAdmin.value) return
  if (arg === 1) managePagination.value.current = 1
  manageLoading.value = true
  messageApi.manageList({
    pageNo: managePagination.value.current,
    pageSize: managePagination.value.pageSize,
    title: manageQuery.value.title,
    level: manageQuery.value.level
  }, (data: any) => {
    manageList.value = data?.records || []
    managePagination.value.total = data?.total || 0
    manageLoading.value = false
  }, () => {
    manageLoading.value = false
  })
}

const handleManagePageChange = (page: number) => {
  managePagination.value.current = page
  loadManageList()
}

const openAdd = () => {
  dialogMode.value = 'add'
  form.value = { id: '', title: '', content: '', level: 1, targetRole: '1,2,3', enabled: 1 }
  targetRoleList.value = ['1', '2', '3']
  dialogVisible.value = true
}

const openEdit = (row: any) => {
  dialogMode.value = 'edit'
  form.value = { ...row }
  targetRoleList.value = row.targetRole ? String(row.targetRole).split(',').map((i: string) => i.trim()) : ['1', '2', '3']
  dialogVisible.value = true
}

const submitForm = () => {
  if (!form.value.title?.trim()) {
    ElMessage.warning('请输入消息标题')
    return
  }
  if (!form.value.content?.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }
  form.value.targetRole = targetRoleList.value.join(',')
  if (dialogMode.value === 'add') {
    messageApi.add(form.value, () => {
      ElMessage.success('发布成功')
      dialogVisible.value = false
      loadManageList()
      loadMessages()
    })
  } else {
    messageApi.edit(form.value, () => {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      loadManageList()
      loadMessages()
    })
  }
}

const removeNotice = (row: any) => {
  messageApi.delete(row.id, () => {
    ElMessage.success('删除成功')
    loadManageList()
    loadMessages()
  })
}

onMounted(() => {
  loadMessages()
  loadManageList()
})
</script>

<style scoped>
.message-page {
  padding: 0;
}

.hero-card,
.list-card,
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

.hero-actions {
  display: flex;
  gap: 12px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding: 14px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  cursor: pointer;
  transition: transform 0.2s ease, border-color 0.2s ease, background 0.2s ease;
  background: #fff;
}

.message-item:hover {
  transform: translateY(-1px);
  border-color: #93c5fd;
  background: #f8fbff;
}

.message-item.unread {
  border-color: #60a5fa;
  background: #eff6ff;
}

.message-main {
  flex: 1;
}

.message-title {
  font-weight: 700;
  color: #111827;
  margin-bottom: 6px;
}

.message-desc {
  color: #6b7280;
  line-height: 1.6;
}

.message-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  white-space: nowrap;
}

.message-time {
  color: #9ca3af;
  font-size: 12px;
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

.mt-3 {
  margin-top: 12px;
}

.mb-3 {
  margin-bottom: 12px;
}

.flex-end {
  display: flex;
  justify-content: flex-end;
}
</style>
