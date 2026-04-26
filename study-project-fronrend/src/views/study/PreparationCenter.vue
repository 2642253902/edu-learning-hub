<template>
  <div class="preparation-center">
    <el-card shadow="never" class="main-card">
      <!-- 页面头部 -->
      <template #header>
        <div class="header-content">
          <div class="header-left">
            <h2 class="page-title">
              <el-icon class="title-icon">
                <Platform />
              </el-icon>
              备课中心
            </h2>
          </div>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="handleAdd">新建备课</el-button>
            <el-button :icon="Refresh" @click="loadData">刷新</el-button>
          </div>
        </div>
      </template>

      <!-- 概览统计 -->
      <!-- <div class="stats-grid">
        <div v-for="stat in stats" :key="stat.label" class="stat-item">
          <div class="stat-icon-box" :class="stat.color">
            <el-icon>
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <div class="stat-text">
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-value">{{ stat.value }}</div>
          </div>
        </div>
      </div> -->

      <!-- 搜索筛选 -->
      <div class="toolbar">
        <el-input v-model="queryParam.courseName" placeholder="搜索课程名称" class="search-input" clearable
          @change="loadData">
          <template #prefix><el-icon>
              <Search />
            </el-icon></template>
        </el-input>
        <el-select v-model="queryParam.status" placeholder="状态筛选" clearable @change="loadData" class="status-select">
          <el-option label="已发布" :value="1" />
          <el-option label="待完善" :value="0" />
        </el-select>
      </div>

      <!-- 备课列表 -->
      <el-table :data="dataSource" v-loading="loading" border stripe class="list-table">
        <el-table-column label="课程名称" prop="courseName" min-width="200">
          <template #default="{ row }">
            <el-link type="primary" @click="handleDetail(row)" class="course-link">{{ row.courseName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="分类" prop="courseTypeName" width="150" align="center" />
        <el-table-column label="备课进度" width="220">
          <template #default="{ row }">
            <div class="progress-box">
              <span class="progress-info">完成度 {{ row.progress }}%</span>
              <el-progress :percentage="row.progress" :color="getProgressColor(row.progress)" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="资源统计" width="180">
          <template #default="{ row }">
            <div class="res-stats">
              <span title="视频"><el-icon>
                  <VideoPlay />
                </el-icon> {{ row.videoCount || 0 }}</span>
              <span title="资料"><el-icon>
                  <FolderOpened />
                </el-icon> {{ row.resourceCount || 0 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑管理</el-button>
            <el-divider direction="vertical" />
            <el-dropdown trigger="click">
              <el-button link type="info">更多<el-icon class="el-icon--right">
                  <ArrowDown />
                </el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handlePreview(row)">学生视角预览</el-dropdown-item>
                  <el-dropdown-item divided @click="handleDelete(row)" class="delete-item">删除备课</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页盖 -->
      <div class="pagination-footer">
        <el-pagination v-model:current-page="ipagination.current" v-model:page-size="ipagination.pageSize"
          :total="ipagination.total" layout="total, prev, pager, next" @current-change="loadData" />
      </div>
    </el-card>

    <!-- 备课详情抽屉 -->
    <el-drawer v-model="drawerVisible" :title="'备课详情 - ' + selectedCourse?.courseName" size="500px">
      <div v-if="selectedCourse" class="drawer-body">
        <div class="detail-header">
          <el-tag size="large">{{ selectedCourse.courseTag }}</el-tag>
          <span class="update-time">更新于: {{ selectedCourse.updateTime || '-' }}</span>
        </div>

        <el-descriptions :column="1" border class="mt-4">
          <el-descriptions-item label="课程全称">{{ selectedCourse.courseName }}</el-descriptions-item>
          <el-descriptions-item label="所属类别">{{ selectedCourse.courseTypeName }}</el-descriptions-item>
          <el-descriptions-item label="负责教师">{{ selectedCourse.teacherName || '管理员' }}</el-descriptions-item>
          <el-descriptions-item label="学时安排">{{ selectedCourse.courseHours || 0 }} 小时</el-descriptions-item>
        </el-descriptions>

        <div class="res-breakdown mt-6">
          <h4 class="section-title">已上传资源统计</h4>
          <div class="breakdown-grid">
            <div class="breakdown-item bg-blue-50">
              <span class="label">视频</span>
              <span class="val">{{ selectedCourse.videoCount || 0 }}</span>
            </div>
            <div class="breakdown-item bg-green-50">
              <span class="label">讲义</span>
              <span class="val">{{ selectedCourse.lectureCount || 0 }}</span>
            </div>
            <div class="breakdown-item bg-gray-50">
              <span class="label">资料</span>
              <span class="val">{{ selectedCourse.resourceCount || 0 }}</span>
            </div>
          </div>
        </div>

        <div class="drawer-actions">
          <el-button type="primary" size="large" @click="handleEdit(selectedCourse)">进入资源管理中心</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 备课表单弹窗 -->
    <el-dialog v-model="formVisible" :title="formTitle" width="600px" destroy-on-close>
      <CourseForm ref="courseFormRef" @ok="handleFormOk" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="formVisible = false">取消</el-button>
          <el-button type="primary" @click="handleFormSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/net'
import {
  Plus, Search, Refresh, Platform, VideoPlay,
  FolderOpened, ArrowDown
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CourseForm from './modules/CourseForm.vue'

const router = useRouter()
const loading = ref(false)
const dataSource = ref([])
const drawerVisible = ref(false)
const selectedCourse = ref<any>(null)

const formVisible = ref(false)
const formTitle = ref('新建备课')
const courseFormRef = ref()

const queryParam = reactive({ courseName: '', status: '' })
const ipagination = reactive({ current: 1, pageSize: 10, total: 0 })

const stats = [
  { label: '备课中课程', value: '12', icon: VideoPlay, color: 'icon-blue' },
  { label: '累计资源量', value: '156', icon: Platform, color: 'icon-green' },
  { label: '待处理反馈', value: '3', icon: FolderOpened, color: 'icon-orange' }
]

const loadData = () => {
  loading.value = true
  get(`/study/cloudComputingCourse/list?pageNo=${ipagination.current}&pageSize=${ipagination.pageSize}`, (msg, data) => {
    dataSource.value = (data?.records || []).map((item: any) => ({
      ...item,
    }))
    ipagination.total = data?.total || 0
    loading.value = false
  }, () => loading.value = false)
}

const getProgressColor = (p: number) => {
  if (p >= 100) return '#67C23A'
  if (p >= 80) return '#409EFF'
  return '#E6A23C'
}

const handleAdd = () => {
  formTitle.value = '新建备课'
  formVisible.value = true
  setTimeout(() => {
    courseFormRef.value?.add()
  }, 0)
}

const handleFormSubmit = () => {
  courseFormRef.value?.submitForm()
}

const handleFormOk = () => {
  formVisible.value = false
  loadData()
}

const handleEdit = (row: any) => router.push({ path: '/study/EditorPreparationCenter', query: { id: row.id } })
const handleDetail = (row: any) => {
  selectedCourse.value = row
  drawerVisible.value = true
}
const handlePreview = (row: any) => router.push({ path: '/study/CourseDetailsFrom', query: { courseId: row.id } })
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要移除此备课记录吗？').then(() => ElMessage.success('移除成功'))
}

onMounted(() => loadData())
</script>

<style scoped>
.preparation-center {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.main-card {
  border-radius: 8px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 20px;
  display: flex;
  align-items: center;
}

.title-icon {
  margin-right: 12px;
  color: #409eff;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.stat-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-right: 16px;
}

.icon-blue {
  background: #409eff;
}

.icon-green {
  background: #67c23a;
}

.icon-orange {
  background: #e6a23c;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.status-select {
  width: 150px;
}

.course-link {
  font-weight: 600;
}

.progress-info {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
  display: block;
}

.res-stats {
  display: flex;
  gap: 12px;
  color: #606266;
  font-size: 14px;
}

.pagination-footer {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.drawer-body {
  padding: 20px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.update-time {
  font-size: 12px;
  color: #909399;
}

.section-title {
  margin: 24px 0 16px;
  font-size: 15px;
  border-left: 4px solid #409eff;
  padding-left: 10px;
}

.breakdown-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.breakdown-item {
  padding: 16px;
  border-radius: 8px;
  text-align: center;
}

.breakdown-item .label {
  display: block;
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
}

.breakdown-item .val {
  font-size: 18px;
  font-weight: bold;
}

.drawer-actions {
  margin-top: 40px;
  text-align: center;
}

.delete-item {
  color: #f56c6c !important;
}

.mt-4 {
  margin-top: 16px;
}

.mt-6 {
  margin-top: 24px;
}
</style>
