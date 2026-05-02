<template>
  <div class="manage-page">
    <el-card shadow="never" class="header-card">
      <div class="header-flex">
        <div>
          <h2 class="title">学习小组管理</h2>
          <p class="desc">管理员可对学习小组进行增删改查。</p>
        </div>
        <el-button type="primary" :icon="Plus" @click="openAdd">新建小组</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="mt-4 list-card">
      <!-- 后台管理表格直接消费后端的分页/列表结构，重点展示创建者、时间和可编辑字段 -->
      <el-table :data="groups" v-loading="loading" border stripe>
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column prop="name" label="名称" min-width="180" />
        <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="ownerName" label="创建者" width="140" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-popconfirm title="确定删除此小组吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新建小组' : '编辑小组'" width="520px">
      <!-- 弹窗表单与后端小组模型对齐，保存成功后刷新列表并关闭弹窗 -->
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="请输入小组名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入小组描述" />
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

/**
 * 前后端协同注释（学习小组管理）
 * - 接口：GET /api/community/groups/all 拉取管理视角列表；POST /api/community/groups 新建；POST /api/community/groups/{id} 编辑；DELETE /api/community/groups/{id} 删除。
 * - 管理语义：此页偏后台维护，只负责数据维护和列表刷新，不承担成员权限或帖子权限判定。
 * - 返回结构：支持数组或 records 包装两种情况，前端统一做一次解包以适配不同后端实现。
 */

const loading = ref(false)
const groups = ref<any[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const form = reactive({ id: '', name: '', description: '' })

const unwrapList = (payload: any) => {
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.records)) return payload.records
  return []
}

const loadData = () => {
  loading.value = true
  get('/api/community/groups/all', (_msg, data) => {
    groups.value = unwrapList(data)
    loading.value = false
  }, () => {
    loading.value = false
  })
}

const openAdd = () => {
  dialogMode.value = 'add'
  form.id = ''
  form.name = ''
  form.description = ''
  dialogVisible.value = true
}

const openEdit = (row: any) => {
  dialogMode.value = 'edit'
  form.id = row.id
  form.name = row.name || ''
  form.description = row.description || ''
  dialogVisible.value = true
}

const submit = () => {
  const payload = { name: form.name, description: form.description }
  if (!payload.name.trim()) {
    ElMessage.warning('请输入小组名称')
    return
  }
  const url = dialogMode.value === 'add' ? '/api/community/groups' : `/api/community/groups/${form.id}`
  post(url, dialogMode.value === 'add' ? payload : { ...payload, id: form.id }, (msg) => {
    ElMessage.success(msg)
    dialogVisible.value = false
    loadData()
  })
}

const handleDelete = (row: any) => {
  deleteMapping(`/api/community/groups/${row.id}`, { id: row.id }, (msg) => {
    ElMessage.success(msg)
    loadData()
  })
}

onMounted(loadData)
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
