<template>
  <div class="p-4">
    <el-card shadow="never">
      <!-- 查询区域 -->
      <div class="mb-4">
        <el-form :inline="true" :model="queryParam">
          <el-form-item label="课程名称">
            <el-input v-model="queryParam.courseName" placeholder="请输入课程名称" clearable />
          </el-form-item>
          <el-form-item label="课程分类">
            <el-select v-model="queryParam.courseTypeId" placeholder="请选择课程分类" clearable style="width: 180px">
              <el-option v-for="item in courseTypeDict" :key="item.id" :label="item.courseTypeName"
                :value="String(item.id)" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
            <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作区域 -->
      <div class="mb-4">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
        <el-button :icon="Download" @click="handleExport">导出</el-button>
      </div>

      <!-- 表格区域 -->
      <el-table :data="dataSource" v-loading="loading" border stripe>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="课程名称" prop="courseName" align="center" show-overflow-tooltip />
        <el-table-column label="课程分类" align="center">
          <template #default="{ row }">
            {{ getCourseTypeText(row) }}
          </template>
        </el-table-column>
        <el-table-column label="教师" align="center">
          <template #default="{ row }">
            {{ getTeacherText(row) }}
          </template>
        </el-table-column>
        <el-table-column label="学时" prop="courseHours" align="center" width="100" />
        <el-table-column label="状态" prop="courseStatus" align="center" width="100">
          <template #default="{ row }">
            <el-tag :type="row.courseStatus === 1 ? 'success' : 'danger'">
              {{ row.courseStatus === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <!-- 表单弹窗 -->
    <CourseModal ref="modalRef" @ok="loadData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { deleteMapping, get } from '@/net'
import { ElMessage, ElMessageBox } from 'element-plus'
import CourseModal from './modules/CourseModal.vue'

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

// 兼容后端 data 结构：array / {records: []} / {list: []}
const unwrapListData = (payload: any): any[] => {
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.records)) return payload.records
  if (Array.isArray(payload?.list)) return payload.list
  return []
}

const buildQueryParams = () => {
  const params = new URLSearchParams()
  params.append('pageNo', String(ipagination.current))
  params.append('pageSize', String(ipagination.pageSize))
  if (queryParam.courseName) params.append('courseName', queryParam.courseName)
  if (queryParam.courseTypeId) params.append('courseTypeId', queryParam.courseTypeId)
  return params.toString()
}

const loadData = (arg = 1) => {
  if (arg === 1) ipagination.current = 1
  loading.value = true

  get(`/study/cloudComputingCourse/list?${buildQueryParams()}`, (msg, data) => {
    dataSource.value = unwrapListData(data)
    ipagination.total = data?.total || 0
    loading.value = false
  }, () => {
    loading.value = false
  })
}

const handleSearch = () => loadData(1)
const resetQuery = () => {
  queryParam.courseName = ''
  queryParam.courseTypeId = ''
  loadData(1)
}

const handleAdd = () => modalRef.value.add()
const handleEdit = (record: CourseRecord) => modalRef.value.edit(record)
const handleDetail = (record: CourseRecord) => modalRef.value.detail(record)

const getCourseTypeText = (row: CourseRecord) => {
  if (row.courseTypeName) return row.courseTypeName
  if (row.courseTypeId_dictText) return row.courseTypeId_dictText
  const match = courseTypeDict.value.find(item => String(item.id) === String(row.courseTypeId))
  return match?.courseTypeName || '-'
}

const getTeacherText = (row: CourseRecord) => {
  if (row.teacherName) return row.teacherName
  if (row.teacherId_dictText) return row.teacherId_dictText
  const match = teacherDict.value.find(item => String(item.id) === String(row.teacherId))
  return match?.realname || match?.username || match?.name || '-'
}

const handleDelete = (row: CourseRecord) => {
  ElMessageBox.confirm('确定删除该课程吗？', '警告', { type: 'warning' }).then(() => {
    deleteMapping('/study/cloudComputingCourse/delete', { id: row.id }, (msg) => {
      ElMessage.success(msg)
      loadData()
    }, (failMsg) => {
      ElMessage.warning(failMsg)
    })
  })
}

const handleExport = () => ElMessage.info('导出功能开发中')
const handleSizeChange = (val: number) => {
  ipagination.pageSize = val
  loadData()
}
const handleCurrentChange = (val: number) => {
  ipagination.current = val
  loadData()
}

const loadCourseTypeDict = () => {
  get('/study/cloudComputingCourseType/list', (msg, data) => {
    courseTypeDict.value = unwrapListData(data)
  })
}

const loadTeacherDict = () => {
  get('/api/user/list/teachers', (msg, data) => {
    teacherDict.value = unwrapListData(data)
  })
}

onMounted(() => {
  loadData()
  // 字典加载失败不应影响主列表 loading 状态
  loadCourseTypeDict()
  loadTeacherDict()
})


</script>
