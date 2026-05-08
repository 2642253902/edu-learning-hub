<template>
  <div class="course-list-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParam" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="queryParam.courseName" placeholder="🔍 搜索课程名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="课程分类">
          <el-select v-model="queryParam.courseTypeId" placeholder="请选择分类" clearable style="width: 200px">
            <el-option v-for="item in courseTypeDict" :key="item.id" :label="item.courseTypeName"
              :value="String(item.id)" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="list-card mt-4">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">课程管理列表</span>
          </div>
          <div class="header-right">
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增课程</el-button>
            <!-- <el-button :icon="Download" plain @click="handleExport">导出数据</el-button> -->
          </div>
        </div>
      </template>

      <el-table :data="dataSource" v-loading="loading" border stripe class="modern-table">
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <el-table-column label="课程名称" min-width="200">
          <template #default="{ row }">
            <div class="course-info-cell">
              <!-- <el-avatar :size="32" :src="'https://api.dicebear.com/7.x/identicon/svg?seed=' + row.id" class="mr-2" /> -->
              <span class="course-name">{{ row.courseName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="150" align="center">
          <template #default="{ row }">
            <el-tag effect="light" round>{{ getCourseTypeText(row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="负责教师" width="150" align="center">
          <template #default="{ row }">
            <span class="teacher-name">{{ getTeacherText(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="学时" prop="courseHours" align="center" width="100">
          <template #default="{ row }">
            <span class="hours-val">{{ row.courseHours || 0 }}</span>h
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-badge is-dot :type="row.courseStatus === 1 ? 'success' : 'danger'" class="status-dot" />
            <span class="ml-2">{{ row.courseStatus === 1 ? '进行中' : '已停用' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-divider direction="vertical" />
            <el-popconfirm title="确定要删除此课程吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="ipagination.current" v-model:page-size="ipagination.pageSize"
          :total="ipagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <CourseModal ref="modalRef" @ok="loadData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { deleteMapping, get } from '@/net'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Refresh, Download } from '@element-plus/icons-vue'
import CourseModal from './modules/CourseModal.vue'

/**
 * 前后端协同注释（课程列表）
 * - 列表接口：GET /study/cloudComputingCourse/list?pageNo=&pageSize=&...；返回约定 `{ records: [], total: number }`。
 * - 导出/批量操作：后端应提供导出接口，前端仅触发并提示下载状态；
 * - 删除接口：DELETE /study/cloudComputingCourse/delete，前端传 `{ id }` 并在成功后刷新列表；
 * - 字典数据（分类/教师）可通过单独接口预加载并用于筛选/回显，避免在列表请求中重复携带大对象。
 */

interface CourseRecord {
  id: string
  courseName?: string
  courseTypeId?: string | number
  courseTypeName?: string
  courseTypeId_dictText?: string
  teacherId?: string | number
  teacherName?: string
  teacherId_dictText?: string
  courseHours?: number
  courseStatus?: number
}

interface CourseTypeItem {
  id: string | number
  courseTypeName?: string
}

interface TeacherItem {
  id: string | number
  realname?: string
  username?: string
  name?: string
}

interface QueryParam {
  courseName: string
  courseTypeId: string
}

interface PaginationState {
  current: number
  pageSize: number
  total: number
}

const loading = ref(false)
const dataSource = ref<CourseRecord[]>([])
const queryParam = reactive<QueryParam>({
  courseName: '',
  courseTypeId: ''
})
const courseTypeDict = ref<CourseTypeItem[]>([])
const teacherDict = ref<TeacherItem[]>([])
const modalRef = ref<any>()

const ipagination = reactive<PaginationState>({
  current: 1,
  pageSize: 10,
  total: 0
})

const unwrapListData = (payload: any): any[] => {
  // 兼容后端不同返回格式，优先返回数组或 records/list 字段
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.records)) return payload.records
  if (Array.isArray(payload?.list)) return payload.list
  return []
}

const buildQueryParams = () => {
  const params = new URLSearchParams()
  params.append('pageNo', String(ipagination.current))
  params.append('pageSize', String(ipagination.pageSize))
  // 只在有值时追加查询参数，避免发送空字段
  if (queryParam.courseName) params.append('courseName', queryParam.courseName)
  if (queryParam.courseTypeId) params.append('courseTypeId', queryParam.courseTypeId)
  return params.toString()
}

const loadData = (arg = 1) => {
  if (arg === 1) ipagination.current = 1
  loading.value = true
  get('/study/cloudComputingCourse/list?' + buildQueryParams(), (msg, data) => {
    // 将服务端返回规范化为数组放入表格数据
    dataSource.value = unwrapListData(data)
    // 总数用于分页组件展示
    ipagination.total = data?.total || 0
    loading.value = false
  }, () => loading.value = false)
}

const loadDictData = () => {
  get('/study/cloudComputingCourseType/list?pageSize=1000', (msg, data) => {
    // 课程分类字典，供筛选下拉使用
    courseTypeDict.value = unwrapListData(data)
  })
}

const getCourseTypeText = (row: CourseRecord) => {
  // if (row.courseTypeName) return row.courseTypeName
  // 从字典中匹配分类名称，兜底为 '-'
  const match = courseTypeDict.value.find(item => String(item.id) === String(row.courseTypeId))
  return match ? match.courseTypeName : '-'
}

const getTeacherText = (row: CourseRecord) => {
  // if (row.teacherName) return row.teacherName
  // 尝试从教师字典中取显式名称，最后兜底显示后端返回的映射字段
  const match = teacherDict.value.find(item => String(item.id) === String(row.teacherId))
  if (match) return match.realname || match.username || match.name || '-'
  return row.teacherId_dictText || '-'
}

const handleSearch = () => loadData(1)
const resetQuery = () => {
  queryParam.courseName = ''
  queryParam.courseTypeId = ''
  loadData(1)
}

const handleAdd = () => modalRef.value?.add()
const handleEdit = (row: CourseRecord) => modalRef.value?.edit(row)
const handleDetail = (row: CourseRecord) => modalRef.value?.detail(row)

const handleDelete = (row: CourseRecord) => {
  deleteMapping('/study/cloudComputingCourse/delete', { id: row.id }, (msg) => {
    // 删除成功后刷新当前列表
    ElMessage.success(msg || '删除成功')
    loadData()
  })
}

const handleExport = () => ElMessage.info('导出功能开发中')
const handleSizeChange = (val: number) => {
  ipagination.pageSize = val
  loadData()
}
const handleCurrentChange = (val: number) => {
  ipagination.current = val
  loadData(val)
}

const loadTeachers = () => {
  get('/api/user/list/teachers', (msg, data) => {
    // 教师列表供负责教师筛选/展示使用
    teacherDict.value = unwrapListData(data)
  }, () => { }, () => {
  })
}

onMounted(() => {
  // 页面初始化：加载列表、分类字典及教师数据
  loadData()
  loadDictData()
  loadTeachers()
})
</script>

<style scoped>
.course-list-container {
  padding: 0;
}

.search-card {
  margin-bottom: 16px;
  border-radius: 4px;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0px;
}

.list-card {
  border-radius: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.course-info-cell {
  display: flex;
  align-items: center;
}

.course-name {
  font-weight: 500;
  color: #1890ff;
}

.hours-val {
  font-weight: bold;
  color: #606266;
}

.status-dot {
  margin-top: -2px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

.modern-table :deep(.el-table__header-wrapper) th {
  background-color: #fafafa !important;
  color: #262626;
  font-weight: 600;
}

.mt-4 {
  margin-top: 16px;
}

.ml-2 {
  margin-left: 8px;
}

.mr-2 {
  margin-right: 8px;
}
</style>
