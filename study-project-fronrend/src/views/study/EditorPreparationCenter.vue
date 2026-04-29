<template>
  <div class="p-4 bg-gray-50 min-h-screen">
    <el-card shadow="never">
      <template #header>
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <h4 class="m-0 text-lg font-bold text-gray-800">{{ title }}</h4>
            <el-tag v-if="disabled" type="info">查看模式</el-tag>
          </div>
          <div class="flex gap-2">
            <el-button @click="handleCancel">{{ disabled ? '返回' : '取消' }}</el-button>
            <el-button v-if="!disabled" type="primary" @click="handleSubmit" :loading="confirmLoading">提交</el-button>
          </div>
        </div>
      </template>

      <el-skeleton :loading="confirmLoading" animated>
        <template #default>
          <el-form :model="form" label-width="100px" :disabled="disabled">
            <!-- 第一行 -->
            <el-row :gutter="20">
              <el-col :xl="8" :lg="8" :md="12" :sm="24">
                <el-form-item label="课程名称" prop="courseName">
                  <el-input v-model="form.courseName" placeholder="课程名称由课程类型自动生成" disabled />
                </el-form-item>
              </el-col>

              <el-col :xl="8" :lg="8" :md="12" :sm="24">
                <el-form-item label="课程类别" prop="courseTypeId" required>
                  <el-select v-model="form.courseTypeId" placeholder="请选择课程类别" @change="handleCourseTypeChange"
                    class="w-full">
                    <el-option v-for="item in courseTypeList" :key="item.id" :label="item.courseTypeName"
                      :value="item.id" />
                  </el-select>
                </el-form-item>
              </el-col>

              <el-col :xl="8" :lg="8" :md="12" :sm="24">
                <el-form-item label="课程状态" prop="courseStatus">
                  <el-switch v-model="form.courseStatus" active-text="已开课" inactive-text="未开课" :active-value="1"
                    :inactive-value="0" />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 第二行 -->
            <el-row :gutter="20">
              <el-col :xl="8" :lg="8" :md="12" :sm="24">
                <el-form-item label="课程标签" prop="courseTag">
                  <el-input v-model="form.courseTag" placeholder="请输入课程标签" />
                </el-form-item>
              </el-col>

              <el-col :xl="8" :lg="8" :md="12" :sm="24">
                <el-form-item label="负责教师" prop="teacherId">
                  <el-select v-model="form.teacherId" placeholder="请选择负责教师" class="w-full" :disabled="isTeacherSelectDisabled">
                    <el-option
                      v-for="teacher in teacherList"
                      :key="teacher.id"
                      :label="getTeacherLabel(teacher)"
                      :value="String(teacher.id)"
                    />
                  </el-select>
                </el-form-item>
              </el-col>

              <el-col :xl="8" :lg="8" :md="12" :sm="24">
                <el-form-item label="总课时" prop="courseHours">
                  <el-input-number v-model="form.courseHours" :min="0" class="w-full" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>

          <div v-show="!isAddMode" class="mt-8">
            <el-tabs v-model="activeTab" type="border-card">
              <!-- 视频 -->
                <el-tab-pane label="视频" name="video">
                <div class="flex justify-between items-center mb-4">
                  <el-button v-if="!disabled" type="primary" :icon="Plus" @click="showModal(1)">添加视频</el-button>
                  <span class="text-gray-500">已上传：{{ videoFiles.length }} 个视频</span>
                </div>
                <el-table :data="videoFiles" border stripe size="small">
                  <el-table-column label="名称" prop="resourceName">
                    <template #default="{ row }">
                      <el-icon class="mr-1 text-blue-500">
                        <VideoCamera />
                      </el-icon>
                      {{ row.resourceName }}
                    </template>
                  </el-table-column>
                  <el-table-column label="资源链接" prop="resourceUrl" show-overflow-tooltip />
                  <el-table-column label="排序" prop="resourceSort" width="80" align="center" />
                  <el-table-column label="操作" width="180" align="center">
                    <template #default="{ row }">
                      <el-button link type="primary" @click="handleEditResource(row)">编辑</el-button>
                      <el-button link type="primary" @click="handleDetailResource(row)">详情</el-button>
                      <el-popconfirm v-if="!disabled" title="确定要删除此文件吗?" @confirm="handleDeleteResource(row)">
                        <template #reference>
                          <el-button link type="danger">删除</el-button>
                        </template>
                      </el-popconfirm>
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>

              <!-- 讲义 -->
              <el-tab-pane label="讲义" name="lecture">
                <div class="flex justify-between items-center mb-4">
                  <el-button v-if="!disabled" type="primary" :icon="Plus" @click="showModal(2)">添加讲义</el-button>
                  <span class="text-gray-500">已上传：{{ lectureFiles.length }} 个讲义</span>
                </div>
                <el-table :data="lectureFiles" border stripe size="small">
                  <el-table-column label="名称" prop="resourceName">
                    <template #default="{ row }">
                      <el-icon class="mr-1 text-red-500">
                        <Document />
                      </el-icon>
                      {{ row.resourceName }}
                    </template>
                  </el-table-column>
                  <el-table-column label="资源链接" prop="resourceUrl" show-overflow-tooltip />
                  <el-table-column label="排序" prop="resourceSort" width="80" align="center" />
                  <el-table-column label="操作" width="180" align="center">
                    <template #default="{ row }">
                      <el-button link type="primary" @click="handleEditResource(row)">编辑</el-button>
                      <el-button link type="primary" @click="handleDetailResource(row)">详情</el-button>
                      <el-popconfirm v-if="!disabled" title="确定要删除此文件吗?" @confirm="handleDeleteResource(row)">
                        <template #reference>
                          <el-button link type="danger">删除</el-button>
                        </template>
                      </el-popconfirm>
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>

              <!-- 资料 -->
              <el-tab-pane label="资料" name="experiment">
                <div class="flex justify-between items-center mb-4">
                  <el-button v-if="!disabled" type="primary" :icon="Plus" @click="showModal(3)">添加资料</el-button>
                  <span class="text-gray-500">已上传：{{ experimentFiles.length }} 个资料</span>
                </div>
                <el-table :data="experimentFiles" border stripe size="small">
                  <el-table-column label="名称" prop="resourceName">
                    <template #default="{ row }">
                      <el-icon class="mr-1 text-green-500">
                        <Files />
                      </el-icon>
                      {{ row.resourceName }}
                    </template>
                  </el-table-column>
                  <el-table-column label="资源链接" prop="resourceUrl" show-overflow-tooltip />
                  <el-table-column label="排序" prop="resourceSort" width="80" align="center" />
                  <el-table-column label="操作" width="180" align="center">
                    <template #default="{ row }">
                      <el-button link type="primary" @click="handleEditResource(row)">编辑</el-button>
                      <el-button link type="primary" @click="handleDetailResource(row)">详情</el-button>
                      <el-popconfirm v-if="!disabled" title="确定要删除此文件吗?" @confirm="handleDeleteResource(row)">
                        <template #reference>
                          <el-button link type="danger">删除</el-button>
                        </template>
                      </el-popconfirm>
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>

              <!-- 评价 -->
              <el-tab-pane label="评价" name="review">
                <div class="mt-4">
                  <resource-review-form v-if="form.id" :resourceId="form.id" @saved="handleReviewSaved" />
                  <div class="mt-4">
                    <resource-review-list ref="reviewListRef" v-if="form.id" :resourceId="form.id" />
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </template>
      </el-skeleton>
    </el-card>

    <CourseResourceModal ref="resourceModalRef" @ok="handleResourceModalOk" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { deleteMapping, get, post } from '@/net'
import { ElMessage } from 'element-plus'
import { Plus, VideoCamera, Document, Files } from '@element-plus/icons-vue'
import CourseResourceModal from './modules/CourseResourceModal.vue'
import ResourceReviewForm from './components/ResourceReviewForm.vue'
import ResourceReviewList from './components/ResourceReviewList.vue'

interface TeacherItem {
  id: string | number
  realname?: string
  username?: string
  email?: string
  name?: string
}

const route = useRoute()
const router = useRouter()

// 状态
const title = ref('课程信息')
const confirmLoading = ref(false)
const disabled = ref(false)
const isAddMode = ref(false)
const activeTab = ref('video')
const isTeacherSelectDisabled = ref(false)

// 数据
const form = reactive({
  id: '',
  courseName: '',
  courseTypeId: '',
  courseStatus: 0,
  courseTag: '',
  teacherId: '',
  courseHours: 0
})

const courseTypeList = ref<any[]>([])
const teacherList = ref<TeacherItem[]>([])
const videoFiles = ref<any[]>([])
const lectureFiles = ref<any[]>([])
const experimentFiles = ref<any[]>([])

const resourceModalRef = ref()
const reviewListRef = ref()

// 初始化
const init = async () => {
  const { id, mode } = route.query

  isAddMode.value = mode === 'add'
  disabled.value = mode === 'readonly'

  if (mode === 'readonly') title.value = '课程详情'
  else if (mode === 'add') title.value = '添加课程'
  else title.value = '编辑课程'

  await loadCourseTypes()
  loadTeachers()

  // 尝试从 sessionStorage 获取 PreparationCenter 传过来的数据
  const cachedData = sessionStorage.getItem('currentCourseEdit')
  if (id && cachedData) {
    const record = JSON.parse(cachedData)
    if (String(record.id) === String(id)) {
      Object.assign(form, record)
      loadResources(id as string)
      return
    }
  }

  if (id) {
    loadCourseDetail(id as string)
    loadResources(id as string)
  }
}

const loadCourseTypes = () => {
  return new Promise((resolve) => {
    get('/study/cloudComputingCourseType/list?pageNo=1&pageSize=1000', (msg, data) => {
      courseTypeList.value = data?.records || []
      resolve(true)
    })
  })
}

const loadTeachers = () => {
  get('/api/user/list/teachers', (msg, data) => {
    teacherList.value = data?.data || data?.records || data || []
  })
}

const loadCourseDetail = (id: string) => {
  confirmLoading.value = true
  get(`/study/cloudComputingCourse/list?id=${id}`, (msg, data) => {
    // 兼容 records 或 data 本身
    const record = data?.records?.[0] || data?.[0]
    if (record) {
      Object.assign(form, record)
      if (!isAddMode.value && !disabled.value) {
        title.value = '编辑课程'
      }
    }
    confirmLoading.value = false
  }, (err) => {
    confirmLoading.value = false
  })
}

const loadResources = (id: string) => {
  get(`/study/cloudComputingCourseResource/list?courseId=${id}&pageSize=100`, (msg, data) => {
    const resources = data?.records || []
    videoFiles.value = resources.filter((r: any) => String(r.resourceType) === '1')
    lectureFiles.value = resources.filter((r: any) => String(r.resourceType) === '2')
    experimentFiles.value = resources.filter((r: any) => String(r.resourceType) === '3')
  })
}

const handleCourseTypeChange = (val: string) => {
  const selected = courseTypeList.value.find(item => String(item.id) === String(val))
  if (selected) {
    form.courseName = selected.courseTypeName
  }
}

const getTeacherLabel = (teacher: TeacherItem) => {
  return teacher.realname || teacher.username || teacher.email || teacher.name || '-'
}

const handleSubmit = () => {
  if (!form.courseTypeId) {
    ElMessage.warning('请选择课程类别')
    return
  }

  confirmLoading.value = true
  const url = isAddMode.value ? '/study/cloudComputingCourse/add' : '/study/cloudComputingCourse/edit'
  post(url, form, (msg) => {
    ElMessage.success('操作成功')
    handleCancel()
  }, (err) => {
    confirmLoading.value = false
  })
}

const handleCancel = () => {
  const from = route.query.from || '/study/PreparationCenter'
  router.push(String(from))
}

const showModal = (type: number) => {
  resourceModalRef.value.add({
    courseId: form.id,
    resourceType: type
  })
}

const handleEditResource = (record: any) => {
  resourceModalRef.value.edit(record)
}

const handleDetailResource = (record: any) => {
  resourceModalRef.value.detail(record)
}

const handleResourceModalOk = () => {
  if (form.id) loadResources(form.id)
}

const handleReviewSaved = (d: any) => {
  // 如果组件返回了新建的数据，直接插入到列表里
  if (reviewListRef.value && typeof reviewListRef.value.addReview === 'function') {
    reviewListRef.value.addReview(d)
  }
  // 同步资源计数或其它数据
  if (form.id) loadResources(form.id)
}

const handleDeleteResource = (record: any) => {
  // deleteMapping(`/study/cloudComputingCourseResource/delete?id=${record.id}`, (msg) => {
  //   ElMessage.success('删除成功')
  //   if (form.id) loadResources(form.id)
  // })

  deleteMapping('/study/cloudComputingCourseResource/delete', { id: record.id }, (msg) => {
    ElMessage.success(msg)
    if (form.id) loadResources(form.id)
  }, (failMsg) => {
    ElMessage.warning(failMsg)
  })
}

onMounted(() => {
  init()
})
</script>

<style scoped>
:deep(.el-tabs__content) {
  padding: 20px;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.add-btn {
  border-radius: 4px;
}

.file-stats {
  display: flex;
  align-items: center;
  gap: 16px;
}

.file-count {
  color: #595959;
  font-size: 14px;
}

.file-table {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
}

.file-table /deep/ .ant-table-thead>tr>th {
  background-color: #fafafa;
  font-weight: 500;
}

.file-icon {
  margin-right: 8px;
  font-size: 16px;
}

.video-icon {
  color: #1890ff;
}

.pdf-icon {
  color: #f5222d;
}

.experiment-icon {
  color: #52c41a;
}

.resource-icon {
  color: #faad14;
}

.table-btn {
  padding: 0 4px;
  height: auto;
  line-height: 1;
}

.delete-btn {
  color: #ff4d4f;
}

.tab-extra-info {
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
  font-weight: 500;
}

/* 响应式处理 */
@media (max-width: 768px) {
  .tab-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .file-stats {
    width: 100%;
    justify-content: space-between;
  }

  .file-table /deep/ .ant-table {
    font-size: 12px;
  }
}
</style>