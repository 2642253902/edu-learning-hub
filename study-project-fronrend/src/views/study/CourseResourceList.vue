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
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="dataSource" v-loading="loading" border>
        <el-table-column label="资源名称" prop="fileName" show-overflow-tooltip />
        <el-table-column label="所属课程" prop="courseId_dictText" />
        <el-table-column label="类型" prop="resourceType" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.resourceType === '1'">视频</el-tag>
            <el-tag v-else-if="row.resourceType === '2'" type="success">讲义</el-tag>
            <el-tag v-else type="info">资料</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get } from '@/net'
import { Search } from '@element-plus/icons-vue'
import { de } from 'element-plus/es/locale/index.mjs'

const loading = ref(false)
const dataSource = ref([])
const queryParam = reactive({ fileName: '', resourceType: '' })

const loadData = (arg = 1) => {
  loading.value = true
  const params = new URLSearchParams(queryParam as any).toString()
  get(`/study/cloudComputingCourseResource/list?${params}`, (msg, data) => {
    dataSource.value = data?.records || []
    loading.value = false
  }, (err) => {
    loading.value = false
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

onMounted(() => loadData())
</script>
