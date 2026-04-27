<template>
  <div class="category-manager">
    <el-card shadow="never" class="header-card">
      <div class="header-flex">
        <div class="header-info">
          <h2 class="title">课程分类管理</h2>
          <p class="desc">管理学习平台内所有课程的分类标签，帮助学生快速检索资源。</p>
        </div>
        <div class="header-actions">
           <el-button type="primary" :icon="Plus" @click="handleAdd">新建分类</el-button>
        </div>
      </div>
    </el-card>

    <div class="main-content mt-4">
      <el-table :data="dataSource" v-loading="loading" border stripe class="modern-table">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column label="分类名称" prop="courseTypeName" min-width="180">
           <template #default="{ row }">
             <div class="name-cell">
               <el-tag :type="getTagType(row.id)" effect="dark" class="mr-2">{{ row.courseTypeName.charAt(0) }}</el-tag>
               <span class="name-text">{{ row.courseTypeName }}</span>
             </div>
           </template>
        </el-table-column>
        <el-table-column label="备注说明" prop="remark" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="remark-text">{{ row.remark || '暂无描述' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ row.createTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">
              <el-icon class="mr-1"><Edit /></el-icon>编辑
            </el-button>
            <el-divider direction="vertical" />
            <el-popconfirm title="确定要永久删除此分类吗？" @confirm="handleDelete(row)" width="200">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 (如果有需要) -->
    <div class="pagination-container mt-4" v-if="total > 0">
      <el-pagination v-model:current-page="pageNo" v-model:page-size="pageSize" :total="total"
        layout="total, prev, pager, next" @current-change="loadData" />
    </div>

    <CourseTypeModal ref="modalRef" @ok="loadData" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get, deleteMapping } from '@/net'
import { Plus, Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import CourseTypeModal from './modules/CourseTypeModal.vue'

const loading = ref(false)
const dataSource = ref([])
const modalRef = ref()
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)

const loadData = () => {
  loading.value = true
  // 移除 tree 相关逻辑，直接请求列表
  get(`/study/cloudComputingCourseType/list?pageNo=${pageNo.value}&pageSize=${pageSize.value}`, (msg, data) => {
    dataSource.value = data?.records || []
    total.value = data?.total || 0
    loading.value = false
  }, () => loading.value = false)
}

const getTagType = (id: any) => {
  const types = ['', 'success', 'info', 'warning', 'danger']
  const index = String(id).charCodeAt(0) % types.length
  return types[index]
}

const handleAdd = () => modalRef.value?.add()
const handleEdit = (row: any) => modalRef.value?.edit(row)

const handleDelete = (row: any) => {
  deleteMapping('/study/cloudComputingCourseType/delete', { id: row.id }, (msg) => {
    ElMessage.success(msg || '删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<style scoped>
.category-manager {
  padding: 0;
}

.header-card {
  border: none;
  background-color: #ffffff;
  border-bottom: 1px solid #f0f0f0;
}

.header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

.desc {
  margin: 4px 0 0;
  font-size: 14px;
  color: #8c8c8c;
}

.name-cell {
  display: flex;
  align-items: center;
}

.name-text {
  font-weight: 600;
  color: #262626;
}

.remark-text {
  color: #595959;
}

.time-text {
  color: #8c8c8c;
  font-size: 13px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}

.modern-table :deep(.el-table__header) th {
  background-color: #fafafa;
  color: #262626;
  font-weight: 600;
}

.mr-1 { margin-right: 4px; }
.mr-2 { margin-right: 8px; }
.mt-4 { margin-top: 16px; }
</style>
