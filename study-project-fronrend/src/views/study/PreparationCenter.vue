<template>
  <div class="preparation-center">
    <!-- 头部横幅 -->
    <div class="page-banner">
      <div class="banner-content">
        <h2 class="banner-title">
          <el-icon class="mr-2"><Platform /></el-icon> 课程备课中心
        </h2>
        <p class="banner-subtitle">管理您的课程资源、视频讲义，构建高质量的学习内容。</p>
      </div>
      <div class="banner-actions">
           <el-button type="primary" :icon="Plus" size="large" @click="handleAdd">新建课程备课</el-button>
      </div>
    </div>

    <el-card shadow="never" class="main-content-card">
      <!-- 搜索筛选 -->
      <div class="toolbar-box">
        <div class="btn-group">
            <el-radio-group v-model="queryParam.status" @change="loadData" size="default">
              <el-radio-button :value="''">全部</el-radio-button>
              <el-radio-button :value="1">已发布</el-radio-button>
              <el-radio-button :value="0">待完善</el-radio-button>
            </el-radio-group>
        </div>
        <div class="search-group">
          <el-input v-model="queryParam.courseName" placeholder="🔍 快速定位课程名称" clearable @change="loadData" class="search-input">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-button :icon="Refresh" circle @click="loadData" class="ml-2" />
        </div>
      </div>

      <!-- 备课列表 -->
      <el-table :data="dataSource" v-loading="loading" border stripe class="modern-table">
        <el-table-column label="课程名称" min-width="220">
          <template #default="{ row }">
            <div class="course-name-cell" @click="handleDetail(row)">
              <div class="icon-wrapper">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="text-info">
                <span class="name-link">{{ row.courseName }}</span>
                <span class="tag-info">{{ row.courseTag || '通用' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="分类" width="140" align="center">
          <template #default="{ row }">
            <el-tag type="info" plain>{{ getCourseTypeText(row) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="资源概况" width="240">
          <template #default="{ row }">
            <div class="res-mini-stats">
              <el-tooltip content="课程视频">
                <span class="stat-tag blue"><el-icon><VideoPlay /></el-icon> {{ row.videoCount || 0 }}</span>
              </el-tooltip>
              <el-tooltip content="讲义文档">
                <span class="stat-tag red"><el-icon><Document /></el-icon> {{ row.lectureCount || 0 }}</span>
              </el-tooltip>
              <el-tooltip content="辅助资料">
                <span class="stat-tag gray"><el-icon><FolderOpened /></el-icon> {{ row.resourceCount || 0 }}</span>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="最后修改日期" width="180" align="center">
            <template #default="{ row }">
                <span class="time-text">{{ row.updateTime || '刚刚' }}</span>
            </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">
               <el-icon class="mr-1"><Edit /></el-icon> 编辑资源
            </el-button>
            <el-divider direction="vertical" />
            <el-dropdown trigger="click">
              <el-button link>更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleDetail(row)">详细报告</el-dropdown-item>
                  <el-dropdown-item divided @click="handleDelete(row)" class="danger-text">删除备课</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="list-footer">
        <el-pagination v-model:current-page="ipagination.current" v-model:page-size="ipagination.pageSize"
          :total="ipagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 详情侧边栏 -->
    <el-drawer v-model="drawerVisible" :title="'备课详情: ' + selectedCourse?.courseName" size="520px" destroy-on-close>
      <div v-if="selectedCourse" class="drawer-container">
         <div class="info-section">
            <div class="section-label">基础信息</div>
            <div class="info-card">
               <div class="info-row"><span class="label">课程名称:</span> <span class="val">{{ selectedCourse.courseName }}</span></div>
               <div class="info-row"><span class="label">标签:</span> <el-tag size="small">{{ selectedCourse.courseTag }}</el-tag></div>
               <div class="info-row"><span class="label">所属教师:</span> <span>{{ selectedCourse.teacherName || '管理员' }}</span></div>
               <div class="info-row"><span class="label">建议学时:</span> <span>{{ selectedCourse.courseHours }}h</span></div>
            </div>
         </div>

         <div class="info-section mt-6">
            <div class="section-label">上传资源分解</div>
            <div class="resource-grid">
               <div class="res-box vid">
                  <div class="num">{{ selectedCourse.videoCount || 0 }}</div>
                  <div class="lab">视频课件</div>
               </div>
               <div class="res-box doc">
                  <div class="num">{{ selectedCourse.lectureCount || 0 }}</div>
                  <div class="lab">文档讲义</div>
               </div>
               <div class="res-box oth">
                  <div class="num">{{ selectedCourse.resourceCount || 0 }}</div>
                  <div class="lab">教学辅助</div>
               </div>
            </div>
         </div>

         <div class="drawer-footer">
            <el-button @click="drawerVisible = false">关闭</el-button>
            <el-button type="primary" @click="handleEdit(selectedCourse)">去编辑中心</el-button>
         </div>
      </div>
    </el-drawer>

    <!-- 备课表单弹窗 -->
    <el-dialog v-model="formVisible" :title="formTitle" width="620px" destroy-on-close>
      <CourseForm ref="courseFormRef" @ok="handleFormOk" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="formVisible = false">取消</el-button>
          <el-button type="primary" @click="handleFormSubmit">保存备课</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get, deleteMapping } from '@/net'
import {
  Plus, Search, Refresh, Platform, VideoPlay, Reading, Edit,
  FolderOpened, ArrowDown, Document
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
const courseTypeList = ref<any[]>([])

const loadCourseTypes = () => {
  get('/study/cloudComputingCourseType/list?pageNo=1&pageSize=1000', (msg, data) => {
    courseTypeList.value = data?.records || []
  })
}

const getCourseTypeText = (row: any) => {
  if (row.courseTypeName) return row.courseTypeName
  const match = courseTypeList.value.find(item => String(item.id) === String(row.courseTypeId))
  return match ? match.courseTypeName : '-'
}

const loadData = (arg = 1) => {
  if (arg === 1) ipagination.current = 1
  loading.value = true
  const params = new URLSearchParams()
  params.append('pageNo', String(ipagination.current))
  params.append('pageSize', String(ipagination.pageSize))
  if (queryParam.courseName) params.append('courseName', queryParam.courseName)
  if (queryParam.status !== '' && queryParam.status !== null) params.append('courseStatus', String(queryParam.status))

  get(`/study/cloudComputingCourse/list?${params.toString()}`, (msg, data) => {
    const records = data?.records || []
    dataSource.value = records
    ipagination.total = data?.total || 0
    loading.value = false

    records.forEach((row: any) => {
      get(`/study/cloudComputingCourseResource/counts?id=${row.id}`, (m, countData) => {
        if (countData) {
          row.videoCount = countData['1'] ?? 0
          row.lectureCount = countData['2'] ?? 0
          row.resourceCount = countData['3'] ?? 0
          dataSource.value = [...dataSource.value]
        }
      })
    })
  }, () => loading.value = false)
}

const handleSizeChange = (val: number) => {
  ipagination.pageSize = val
  loadData()
}

const handleCurrentChange = (val: number) => {
  ipagination.current = val
  loadData(val)
}

const handleAdd = () => {
  formTitle.value = '新建备课'
  formVisible.value = true
  setTimeout(() => { courseFormRef.value?.add() }, 0)
}

const handleFormSubmit = () => courseFormRef.value?.submitForm()
const handleFormOk = () => {
  formVisible.value = false
  loadData()
}

const handleEdit = (row: any) => {
  router.push({
    path: '/study/EditorPreparationCenter',
    query: { id: row.id, from: '/study/PreparationCenter' }
  })
  sessionStorage.setItem('currentCourseEdit', JSON.stringify(row))
}

const handleDetail = (row: any) => {
  selectedCourse.value = row
  drawerVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除此课程备课吗？', '重要提示', {
    type: 'error',
    confirmButtonText: '立即删除',
    confirmButtonClass: 'el-button--danger'
  }).then(() => {
    deleteMapping('/study/cloudComputingCourse/delete', { id: row.id }, (msg) => {
      ElMessage.success('已移除该课程')
      loadData()
    })
  })
}

onMounted(() => {
  loadCourseTypes()
  loadData()
})
</script>

<style scoped>
.preparation-center {
  background-color: #f0f2f5;
  min-height: calc(100vh - 120px);
}

.page-banner {
  background: linear-gradient(90deg, #1890ff 0%, #40a9ff 100%);
  padding: 32px 40px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.banner-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.banner-subtitle {
  margin: 8px 0 0;
  opacity: 0.85;
  font-size: 15px;
}

.main-content-card {
  margin: -20px 24px 24px;
  border-radius: 8px;
  border: none;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.toolbar-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.search-input {
  width: 260px;
}

.course-name-cell {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.icon-wrapper {
  width: 36px;
  height: 36px;
  background-color: #e6f7ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
  font-size: 18px;
  margin-right: 12px;
}

.text-info {
  display: flex;
  flex-direction: column;
}

.name-link {
  font-weight: 600;
  color: #262626;
}

.name-link:hover {
  color: #1890ff;
}

.tag-info {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 2px;
}

.res-mini-stats {
  display: flex;
  gap: 8px;
}

.stat-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-tag.blue { background: #e6f7ff; color: #1890ff; }
.stat-tag.red { background: #fff1f0; color: #f5222d; }
.stat-tag.gray { background: #f5f5f5; color: #595959; }

.time-text {
  color: #8c8c8c;
  font-size: 14px;
}

.list-footer {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.drawer-container {
  padding: 0 10px;
}

.section-label {
  font-weight: 600;
  margin-bottom: 12px;
  color: #262626;
  border-left: 4px solid #1890ff;
  padding-left: 10px;
}

.info-card {
  background: #fafafa;
  padding: 16px;
  border-radius: 8px;
}

.info-row {
  margin-bottom: 12px;
  display: flex;
}

.info-row .label {
  color: #8c8c8c;
  width: 80px;
}

.info-row .val {
  color: #262626;
  font-weight: 500;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.res-box {
  padding: 20px 10px;
  text-align: center;
  border-radius: 8px;
}

.res-box.vid { background-color: #e6f7ff; color: #1890ff; }
.res-box.doc { background-color: #fff1f0; color: #f5222d; }
.res-box.oth { background-color: #f5f5f5; color: #595959; }

.res-box .num { font-size: 24px; font-weight: bold; }
.res-box .lab { font-size: 12px; opacity: 0.8; }

.drawer-footer {
  margin-top: 40px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.danger-text { color: #f5222d !important; }
.mr-2 { margin-right: 8px; }
.ml-2 { margin-left: 8px; }
.mt-6 { margin-top: 24px; }
</style>
