<template>
  <div class="p-4">
    <el-card shadow="never">
      <div class="mb-4">
        <el-form :inline="true" :model="queryParam">
          <el-form-item label="资源名称">
            <el-input v-model="queryParam.fileName" placeholder="输入资源名称" clearable />
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="queryParam.resourceType" placeholder="请选择" clearable class="w-32">
              <el-option label="视频" value="1" />
              <el-option label="讲义" value="2" />
              <el-option label="资料" value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="loadData(1)">查询</el-button>
            <el-button type="success" :icon="Plus" @click="handleAdd">新增资源</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="dataSource" v-loading="loading" border>
        <el-table-column label="资源名称" prop="resourceName" show-overflow-tooltip />
        <el-table-column label="所属课程" align="center">
          <template #default="{ row }">
            {{ getCourseText(row) }}
          </template>
        </el-table-column>
        <el-table-column label="类型" prop="resourceType" width="100">
          <template #default="{ row }">
            <el-tag v-if="String(row.resourceType) === '1'">视频</el-tag>
            <el-tag v-else-if="String(row.resourceType) === '2'" type="success">讲义</el-tag>
            <el-tag v-else type="info">资料</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定要删除此课程吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>

          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="flex justify-end mt-4">
        <el-pagination v-model:current-page="ipagination.current" v-model:page-size="ipagination.pageSize"
          :total="ipagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>
    <CourseResourceModal ref="resourceModalRef" @ok="loadData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, deleteMapping } from '@/net'
import { Search, Plus } from '@element-plus/icons-vue'
import CourseResourceModal from './modules/CourseResourceModal.vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const dataSource = ref([])
const queryParam = reactive({ fileName: '', resourceType: '' })
const courseDict = ref<any[]>([])
const resourceModalRef = ref()

const ipagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const handleAdd = () => {
  resourceModalRef.value.add()
}

const handleEdit = (row: any) => {
  resourceModalRef.value.edit(row)
}

const getCourseText = (row: any) => {
  if (row.courseId_dictText) return row.courseId_dictText
  const match = courseDict.value.find(item => String(item.id) === String(row.courseId))
  return match ? match.courseName : '未知课程'
}

const loadData = (arg = 1) => {
  if (arg === 1) ipagination.current = 1
  loading.value = true
  const params = new URLSearchParams()
  params.append('pageNo', String(ipagination.current))
  params.append('pageSize', String(ipagination.pageSize))
  if (queryParam.fileName) params.append('resourceName', queryParam.fileName) // 注意：后端查询字段通常为 resourceName
  if (queryParam.resourceType) params.append('resourceType', queryParam.resourceType)

  get(`/study/cloudComputingCourseResource/list?${params.toString()}`, (msg, data) => {
    dataSource.value = data?.records || []
    ipagination.total = data?.total || 0
    loading.value = false
  }, (err) => {
    loading.value = false
  })
}

const handleSizeChange = (val: number) => {
  ipagination.pageSize = val
  ipagination.current = 1 // 切换每页条数时，重置到第一页
  loadData(1)
}

const handleCurrentChange = (val: number) => {
  ipagination.current = val
  loadData(val)
}

const loadCourseDict = () => {
  get('/study/cloudComputingCourse/list?pageNo=1&pageSize=1000', (msg, data) => {
    courseDict.value = data?.records || []
  })
}

const handleDelete = (row: any) => {
  deleteMapping('/study/cloudComputingCourseResource/delete', { id: row.id }, (msg) => {
    ElMessage.success(msg)
    loadData()
  }, (failMsg) => {
    ElMessage.warning(failMsg)
  })
}


onMounted(() => {
  loadData()
  loadCourseDict()
})
</script>
