<template>
  <div class="manage-page">
    <el-card shadow="never" class="header-card">
      <div class="header-flex">
        <div>
          <h2 class="title">公共讨论管理</h2>
          <p class="desc">管理员可对公共讨论和小组帖子进行增删改查。</p>
        </div>
        <el-button type="primary" :icon="Plus" @click="openAdd">新建帖子</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="mt-4 list-card">
      <el-table :data="posts" v-loading="loading" border stripe>
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column label="所属范围" width="180" align="center">
          <template #default="{ row }">
            {{ getGroupLabel(row.groupId) }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
        <el-table-column prop="username" label="作者" width="120" align="center" />
        <el-table-column prop="commentCount" label="评论数" width="100" align="center" />
        <el-table-column prop="createTime" label="发布时间" width="180" align="center" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-popconfirm title="确定删除此帖子吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新建帖子' : '编辑帖子'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="所属范围">
          <el-select v-model="form.groupId" filterable clearable placeholder="公共讨论请留空" style="width: 100%">
            <el-option label="公共讨论区" value="" />
            <el-option v-for="group in groups" :key="group.id" :label="group.name" :value="String(group.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { deleteMapping, get, post } from '@/net'

const loading = ref(false)
const posts = ref<any[]>([])
const groups = ref<any[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const form = reactive({ id: '', groupId: '', title: '', content: '' })

const unwrapList = (payload: any) => {
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.records)) return payload.records
  return []
}

const loadGroups = () => {
  get('/api/community/groups/all', (_msg, data) => {
    groups.value = unwrapList(data)
  })
}

const loadData = () => {
  loading.value = true
  get('/api/community/posts/all', (_msg, data) => {
    posts.value = unwrapList(data)
    loading.value = false
  }, () => {
    loading.value = false
  })
}

const getGroupLabel = (groupId: any) => {
  if (!groupId) return '公共讨论区'
  const match = groups.value.find(item => String(item.id) === String(groupId))
  return match ? match.name : String(groupId)
}

const openAdd = () => {
  dialogMode.value = 'add'
  form.id = ''
  form.groupId = ''
  form.title = ''
  form.content = ''
  dialogVisible.value = true
}

const openEdit = (row: any) => {
  dialogMode.value = 'edit'
  form.id = row.id
  form.groupId = row.groupId || ''
  form.title = row.title || ''
  form.content = row.content || ''
  dialogVisible.value = true
}

const submit = () => {
  if (!form.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  const url = dialogMode.value === 'add' ? '/api/community/posts' : `/api/community/posts/${form.id}`
  post(url, dialogMode.value === 'add' ? { groupId: form.groupId, title: form.title, content: form.content } : { ...form }, (msg) => {
    ElMessage.success(msg)
    dialogVisible.value = false
    loadData()
  })
}

const handleDelete = (row: any) => {
  deleteMapping(`/api/community/posts/${row.id}`, { id: row.id }, (msg) => {
    ElMessage.success(msg)
    loadData()
  })
}

onMounted(() => {
  loadGroups()
  loadData()
})
</script>

<style scoped>
.manage-page {
  padding: 0;
}

.header-card,
.list-card {
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
}

.header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.title {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: #111827;
}

.desc {
  margin: 6px 0 0;
  color: #6b7280;
}

.mt-4 {
  margin-top: 16px;
}
</style>
