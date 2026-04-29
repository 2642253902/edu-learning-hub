<template>
  <div class="p-4">
    <el-card shadow="never">
      <div class="mb-4">
        <el-form :inline="true" :model="queryParam">
          <el-form-item label="学生">
            <el-select v-model="queryParam.userId" filterable clearable placeholder="请选择学生" style="width: 220px">
              <el-option v-for="item in studentOptions" :key="item.id" :label="getUserLabel(item)" :value="String(item.id)" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属课程">
            <el-select v-model="queryParam.courseId" filterable clearable placeholder="请选择课程" style="width: 220px">
              <el-option v-for="item in courseOptions" :key="item.id" :label="item.courseName || '-'" :value="String(item.id)" />
            </el-select>
          </el-form-item>
          <el-form-item label="学习资源">
            <el-select v-model="queryParam.contentId" filterable clearable placeholder="请选择资源" style="width: 240px">
              <el-option v-for="item in resourceOptions" :key="item.id" :label="getResourceLabel(item)" :value="String(item.id)" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParam.learningStatus" placeholder="请选择" clearable style="width: 120px">
              <el-option label="未开始" :value="0" />
              <el-option label="学习中" :value="1" />
              <el-option label="已完成" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="loadData(1)">查询</el-button>
            <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="dataSource" v-loading="loading" border stripe>
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column label="学生" align="center" show-overflow-tooltip>
          <template #default="{ row }">
            {{ getUserLabelById(row.userId, row.userId_dictText) }}
          </template>
        </el-table-column>
        <el-table-column label="所属课程" align="center" show-overflow-tooltip>
          <template #default="{ row }">
            {{ getCourseLabelById(row.courseId, row.courseId_dictText) }}
          </template>
        </el-table-column>
        <el-table-column label="学习资源" align="center" show-overflow-tooltip>
          <template #default="{ row }">
            {{ getResourceLabelById(row.contentId, row.contentId_dictText) }}
          </template>
        </el-table-column>
        <el-table-column label="观看进度" width="220" align="center">
          <template #default="{ row }">
            <el-progress :percentage="getProgress(row)" :status="getProgress(row) >= 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column label="最后学习时间" prop="lastLearnTime" align="center" width="180" />
        <el-table-column label="状态" prop="learningStatus" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="String(row.learningStatus) === '2' ? 'success' : 'warning'">
              {{ getStatusText(row.learningStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-popconfirm title="确定删除此记录吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex justify-end mt-4">
        <el-pagination :current-page="ipagination.current" :page-size="ipagination.pageSize"
          :total="ipagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
          @update:current-page="handleCurrentChange" @update:page-size="handleSizeChange"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, deleteMapping } from '@/net'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const dataSource = ref<any[]>([])
const studentOptions = ref<any[]>([])
const courseOptions = ref<any[]>([])
const resourceOptions = ref<any[]>([])
const queryParam = reactive({
  userId: '',
  courseId: '',
  contentId: '',
  learningStatus: ''
})

const ipagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const unwrapListData = (payload: any): any[] => {
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.records)) return payload.records
  if (Array.isArray(payload?.list)) return payload.list
  return []
}

const getUserLabel = (item: any) => item?.realname || item?.username || item?.email || item?.name || '-'
const getResourceLabel = (item: any) => item?.resourceName || item?.fileName || item?.name || '-'

const getUserLabelById = (id: any, fallback?: string) => {
  if (!id) return fallback || '-'
  const match = studentOptions.value.find(item => String(item.id) === String(id))
  return match ? getUserLabel(match) : (fallback || String(id))
}

const getCourseLabelById = (id: any, fallback?: string) => {
  if (!id) return fallback || '-'
  const match = courseOptions.value.find(item => String(item.id) === String(id))
  return match ? (match.courseName || fallback || String(id)) : (fallback || String(id))
}

const getResourceLabelById = (id: any, fallback?: string) => {
  if (!id) return fallback || '-'
  const match = resourceOptions.value.find(item => String(item.id) === String(id))
  return match ? getResourceLabel(match) : (fallback || String(id))
}

const getProgress = (row: any) => {
  const value = Number(row.watchProgress ?? row.learningProgress ?? row.progress ?? 0)
  return Number.isNaN(value) ? 0 : Math.max(0, Math.min(100, value))
}

const getStatusText = (value: any) => {
  if (String(value) === '2') return '已完成'
  if (String(value) === '1') return '学习中'
  return '未开始'
}

const loadStudents = () => {
  get('/api/user/manage/list?pageNo=1&pageSize=1000', (_msg, data) => {
    studentOptions.value = unwrapListData(data)
  })
}

const loadCourses = () => {
  get('/study/cloudComputingCourse/list?pageNo=1&pageSize=1000', (_msg, data) => {
    courseOptions.value = unwrapListData(data)
  })
}

const loadResources = () => {
  get('/study/cloudComputingCourseResource/list?pageNo=1&pageSize=1000', (_msg, data) => {
    resourceOptions.value = unwrapListData(data)
  })
}

const loadData = (arg = 1) => {
  if (arg === 1) ipagination.current = 1
  loading.value = true

  const params = new URLSearchParams()
  params.append('pageNo', String(ipagination.current))
  params.append('pageSize', String(ipagination.pageSize))

  if (queryParam.userId) params.append('userId', queryParam.userId)
  if (queryParam.courseId) params.append('courseId', queryParam.courseId)
  if (queryParam.contentId) params.append('contentId', queryParam.contentId)
  if (queryParam.learningStatus !== '' && queryParam.learningStatus !== null) {
    params.append('learningStatus', String(queryParam.learningStatus))
  }

  get(`/study/cloudComputingStudentLearningRecord/list?${params.toString()}`, (_msg, data) => {
    dataSource.value = data?.records || []
    ipagination.total = data?.total || 0
    loading.value = false
  }, () => {
    loading.value = false
  })
}

const resetQuery = () => {
  queryParam.userId = ''
  queryParam.courseId = ''
  queryParam.contentId = ''
  queryParam.learningStatus = ''
  loadData(1)
}

const handleSizeChange = (val: number) => {
  ipagination.pageSize = val
  ipagination.current = 1
  loadData()
}

const handleCurrentChange = (val: number) => {
  ipagination.current = val
  loadData(val)
}

const handleDelete = (id: string) => {
  deleteMapping('/study/cloudComputingStudentLearningRecord/delete', { id }, (msg) => {
    ElMessage.success(msg)
    loadData()
  })
}

onMounted(() => {
  loadStudents()
  loadCourses()
  loadResources()
  loadData()
})
</script>
