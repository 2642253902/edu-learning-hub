<template>
  <div class="p-4">
    <el-card shadow="never">
      <!-- 查询区域 -->
      <div class="mb-4">
        <el-form :inline="true" :model="queryParam">
          <el-form-item label="学生名称">
            <el-input v-model="queryParam.studentName" placeholder="输入学生名称" clearable @keyup.enter="loadData(1)" />
          </el-form-item>
          <el-form-item label="课程名称">
            <el-input v-model="queryParam.courseName" placeholder="输入课程名称" clearable @keyup.enter="loadData(1)" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParam.learningStatus" placeholder="请选择" clearable style="width: 120px">
              <el-option label="进行中" :value="0" />
              <el-option label="已完成" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="loadData(1)">查询</el-button>
            <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格区域 -->
      <el-table :data="dataSource" v-loading="loading" border stripe>
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column label="学生" prop="studentId_dictText" align="center" />
        <el-table-column label="所属课程" prop="courseId_dictText" align="center" show-overflow-tooltip />
        <el-table-column label="学习资源" prop="resourceId_dictText" align="center" show-overflow-tooltip />
        <el-table-column label="观看进度" width="220" align="center">
          <template #default="{ row }">
            <el-progress :percentage="row.watchProgress || 0" :status="row.watchProgress >= 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column label="最后学习时间" prop="updateTime" align="center" width="180" />
        <el-table-column label="状态" prop="learningStatus" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.learningStatus === 1 ? 'success' : 'warning'">
              {{ row.learningStatus === 1 ? '已完成' : '进行中' }}
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

      <!-- 分页区域 -->
      <div class="flex justify-end mt-4">
        <el-pagination v-model:current-page="ipagination.current" v-model:page-size="ipagination.pageSize"
          :total="ipagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
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
const dataSource = ref([])
const queryParam = reactive({
  studentName: '',
  courseName: '',
  learningStatus: ''
})

const ipagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const loadData = (arg = 1) => {
  if (arg === 1) ipagination.current = 1
  loading.value = true

  const params = new URLSearchParams()
  params.append('pageNo', String(ipagination.current))
  params.append('pageSize', String(ipagination.pageSize))

  if (queryParam.studentName) params.append('studentId_dictText', `*${queryParam.studentName}*`)
  if (queryParam.courseName) params.append('courseId_dictText', `*${queryParam.courseName}*`)
  if (queryParam.learningStatus !== '' && queryParam.learningStatus !== null) {
    params.append('learningStatus', String(queryParam.learningStatus))
  }

  get(`/study/cloudComputingStudentLearningRecord/list?${params.toString()}`, (msg, data) => {
    dataSource.value = data?.records || []
    ipagination.total = data?.total || 0
    loading.value = false
  }, () => {
    loading.value = false
  })
}

const resetQuery = () => {
  queryParam.studentName = ''
  queryParam.courseName = ''
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

onMounted(() => loadData())
</script>
