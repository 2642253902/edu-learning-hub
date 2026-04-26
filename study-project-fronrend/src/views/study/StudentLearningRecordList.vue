<template>
  <div class="p-4">
    <el-card shadow="never">
      <el-table :data="dataSource" v-loading="loading" border>
        <el-table-column label="学生" prop="studentId_dictText" />
        <el-table-column label="课程" prop="courseId_dictText" />
        <el-table-column label="资源" prop="resourceId_dictText" />
        <el-table-column label="观看进度" prop="watchProgress">
          <template #default="{ row }">
            <el-progress :percentage="row.watchProgress || 0" />
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="learningStatus">
          <template #default="{ row }">
            <el-tag :type="row.learningStatus === 1 ? 'success' : 'warning'">
              {{ row.learningStatus === 1 ? '已完成' : '进行中' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get } from '@/net'

const loading = ref(false)
const dataSource = ref([])

const loadData = () => {
  loading.value = true
  get('/study/cloudComputingStudentLearningRecord/list', (msg, data) => {
    dataSource.value = data?.records || []
    loading.value = false
  }, (err) => {
    loading.value = false
  })
}

onMounted(() => loadData())
</script>
