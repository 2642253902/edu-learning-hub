<template>
  <div class="manage-page">
    <el-card shadow="never" class="header-card">
      <div class="header-flex">
        <div>
          <h2 class="title">课程评价管理</h2>
          <p class="desc">管理员可查看、编辑、删除课程评价。</p>
        </div>
        <el-button type="primary" :icon="Plus" @click="openAdd">新增评价</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="mt-4 list-card">
      <el-table :data="reviews" v-loading="loading" border stripe>
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column label="课程" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">{{ getCourseLabel(row.courseId) }}</template>
        </el-table-column>
        <el-table-column prop="username" label="评价人" width="120" align="center" />
        <el-table-column label="评分" width="100" align="center">
          <template #default="{ row }">
            <el-rate :model-value="Number(row.rating || 0)" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="likes" label="点赞" width="90" align="center" />
        <el-table-column prop="createTime" label="发布时间" width="180" align="center" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-popconfirm title="确定删除此评价吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新增评价' : '编辑评价'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="课程">
          <el-select v-model="form.courseId" filterable clearable placeholder="请选择课程" style="width: 100%">
            <el-option v-for="item in courseOptions" :key="item.id" :label="item.courseName || '-'"
              :value="String(item.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="form.rating" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入评价内容" />
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
 * 前后端协同注释（评价管理）
 * - 列表接口：GET /api/study/reviews/all 或管理员筛选接口，返回评价对象数组或分页结构；
 * - 新增/编辑：POST /api/study/reviews（创建）或 /api/study/reviews/{id}（编辑）；
 * - 删除：DELETE /api/study/reviews/{id}，管理端操作应记录日志并返回明确的成功消息。
 * - 前端约定：评价对象包含 { id, courseId, username, userId, rating, content, likes, createTime }，组件按此契约渲染。
 */

const loading = ref(false)
const reviews = ref<any[]>([])
const courseOptions = ref<any[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'add' | 'edit'>('add')
const form = reactive({ id: '', courseId: '', rating: 5, content: '' })

const unwrapList = (payload: any) => {
  // 兼容不同接口返回格式，统一为数组
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.records)) return payload.records
  return []
}

const loadCourses = () => {
  get('/study/cloudComputingCourse/list?pageNo=1&pageSize=1000', (_msg, data) => {
    // 加载课程下拉选项用于评价关联选择
    courseOptions.value = unwrapList(data)
  })
}

const loadData = () => {
  loading.value = true
  get('/api/study/reviews/all', (_msg, data) => {
    // 管理端直接拉取全部评价用于表格展示
    reviews.value = unwrapList(data)
    loading.value = false
  }, () => {
    loading.value = false
  })
}

const getCourseLabel = (courseId: any) => {
  const match = courseOptions.value.find(item => String(item.id) === String(courseId))
  // 根据 id 查找展示名称，兜底显示 id
  return match ? match.courseName || '-' : String(courseId || '-')
}

const openAdd = () => {
  dialogMode.value = 'add'
  form.id = ''
  form.courseId = ''
  form.rating = 5
  form.content = ''
  dialogVisible.value = true
}

const openEdit = (row: any) => {
  dialogMode.value = 'edit'
  form.id = row.id
  form.courseId = row.courseId || ''
  form.rating = Number(row.rating || 5)
  form.content = row.content || ''
  dialogVisible.value = true
}

const submit = () => {
  const payload = {
    courseId: form.courseId,
    rating: form.rating,
    content: form.content
  }
  const url = dialogMode.value === 'add' ? '/api/study/reviews' : `/api/study/reviews/${form.id}`
  post(url, dialogMode.value === 'add' ? payload : { ...payload, id: form.id }, (msg) => {
    // 新增/编辑后刷新列表并关闭对话框
    ElMessage.success(msg)
    dialogVisible.value = false
    loadData()
  })
}

const handleDelete = (row: any) => {
  deleteMapping(`/api/study/reviews/${row.id}`, { id: row.id }, (msg) => {
    // 删除后重新加载列表
    ElMessage.success(msg)
    loadData()
  })
}

onMounted(() => {
  // 页面初始化：准备课程字典并加载评价数据
  loadCourses()
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
