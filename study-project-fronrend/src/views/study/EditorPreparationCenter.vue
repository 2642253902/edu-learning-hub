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
                  <el-input :model-value="selectedTeacherLabel" placeholder="请选择负责教师" class="w-full" disabled />
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
                <div class="review-section">
                  <div class="review-section-head">
                    <div>
                      <div class="section-kicker">课程评价管理</div>
                      <h3 class="section-title">当前课程的评价与反馈</h3>
                    </div>

                  </div>

                  <div class="review-toolbar">
                    <el-button v-if="!disabled" type="primary" @click="openReviewDialog()">新增评价</el-button>
                  </div>

                  <el-alert type="info" show-icon :closable="false" 
                    class="mb-4" />

                  <el-table :data="reviewList" v-loading="reviewLoading" border stripe>
                    <el-table-column type="index" label="#" width="60" align="center" />
                    <el-table-column label="评价人" prop="username" width="140" align="center" />
                    <el-table-column label="评分" width="100" align="center">
                      <template #default="{ row }">
                        <el-rate :model-value="Number(row.rating || 0)" disabled />
                      </template>
                    </el-table-column>
                    <el-table-column label="评价内容" prop="content" min-width="260" show-overflow-tooltip />
                    <el-table-column label="点赞" prop="likes" width="90" align="center" />
                    <el-table-column label="发布时间" width="180" align="center">
                      <template #default="{ row }">
                        {{ row.createTime || '-' }}
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="120" align="center">
                      <template #default="{ row }">
                        <el-button v-if="!disabled" link type="primary" @click="openReviewDialog(row)">编辑</el-button>
                        <el-popconfirm title="确定删除此评价吗？" @confirm="handleDeleteReview(row)">
                          <template #reference>
                            <el-button link type="danger">删除</el-button>
                          </template>
                        </el-popconfirm>
                      </template>
                    </el-table-column>
                  </el-table>
                  <el-empty v-if="!reviewList || reviewList.length === 0" description="暂无评价记录" />
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </template>
      </el-skeleton>
    </el-card>

    <CourseResourceModal ref="resourceModalRef" @ok="handleResourceModalOk" />

    <el-dialog v-model="reviewDialogVisible" :title="reviewDialogMode === 'add' ? '新增评价' : '编辑评价'" width="680px">
      <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="90px">
        <el-form-item label="所属课程">
          <el-input :model-value="getReviewCourseLabel(form.id)" disabled />
        </el-form-item>
        <el-form-item label="评价人" prop="username">
          <el-input v-model="reviewForm.username" placeholder="当前登录用户" disabled />
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="reviewForm.rating" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="reviewForm.content" type="textarea" :rows="5" placeholder="请输入评价内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewSaving" @click="submitReview">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watchEffect, defineAsyncComponent } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { deleteMapping, get, post } from '@/net'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Plus, VideoCamera, Document, Files } from '@element-plus/icons-vue'

/**
 * 前后端协同注释（课程备课编辑中心）
 * - 这是课程备课的核心编辑页，负责把课程主信息、资源列表和评价管理串联到同一条编辑链路中。
 * - 主要接口：课程详情回显、课程分类列表、教师列表、课程资源增删改、评价增删改查；页面根据 `mode` 决定新增/编辑/只读。
 * - 数据约定：父页面可通过 `sessionStorage.currentCourseEdit` 传入缓存记录，若路由 `id` 匹配则直接回填，减少重复请求并保持前后端状态一致。
 * - 交互约定：保存成功后应回到列表页或触发父组件刷新；只读模式仅展示后端回显结果，不允许修改。
 */

const CourseResourceModal = defineAsyncComponent(() => import('./modules/CourseResourceModal.vue'))

interface TeacherItem {
  id: string | number
  realname?: string
  username?: string
  email?: string
  name?: string
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
// 评价作者默认取当前登录用户，保证新增/编辑评价时用户名来源统一
const currentUsername = computed(() => {
  const user = userStore.auth.user || {}
  return user.realname || user.username || user.name || ''
})

// 状态
const title = ref('课程信息')
const confirmLoading = ref(false)
const disabled = ref(false)
const isAddMode = ref(false)
const activeTab = ref('video')

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
const reviewList = ref<any[]>([])
const reviewLoading = ref(false)
// reviews are bound to the current course (form.id)
const reviewDialogVisible = ref(false)
const reviewDialogMode = ref<'add' | 'edit'>('add')
const reviewSaving = ref(false)
const reviewFormRef = ref()
const reviewForm = reactive({
  id: '',
  courseId: '',
  username: '',
  rating: 5,
  content: ''
})
const reviewRules = {
  username: [{ required: true, message: '请输入评价人', trigger: 'blur' }],
  rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
  content: [{ required: true, message: '请输入评价内容', trigger: 'blur' }]
}

const resourceModalRef = ref()

const getReviewCourseLabel = (courseId: string) => {
  return '课程 ' + (form.courseName || form.id || '')
}

// 初始化：根据路由 mode 决定页面是新增、编辑还是只读
const init = async () => {
  const { id, mode } = route.query

  isAddMode.value = mode === 'add'
  disabled.value = mode === 'readonly'

  if (mode === 'readonly') title.value = '课程详情'
  else if (mode === 'add') title.value = '添加课程'
  else title.value = '编辑课程'

  await loadCourseTypes()
  loadTeachers()

  // 优先读取列表页缓存，减少重复请求并保留用户在列表页的上下文
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
    get('/study/cloudComputingCourseType/list?pageNo=1&pageSize=1000', (_message: string, data: any) => {
      courseTypeList.value = data?.records || []
      resolve(true)
    })
  })
}

const loadTeachers = () => {
  get('/api/user/manage/list', (_message: string, data: any) => {
    teacherList.value = data?.data || data?.records || data || []
  })
}

const loadCourseDetail = (id: string) => {
  confirmLoading.value = true
  get(`/study/cloudComputingCourse/list?id=${id}`, (_message: string, data: any) => {
    // 兼容后端不同返回结构：可能是 { records: [] }，也可能直接返回数组
    const record = data?.records?.[0] || data?.[0]
    if (record) {
      Object.assign(form, record)
      if (!isAddMode.value && !disabled.value) {
        title.value = '编辑课程'
      }
    }
    confirmLoading.value = false
  }, (_err: any) => {
    confirmLoading.value = false
  })
}

const loadResources = (id: string) => {
  get(`/study/cloudComputingCourseResource/list?courseId=${id}&pageSize=100`, (_message: string, data: any) => {
    const resources = data?.records || []
    // resourceType: 1-视频 2-讲义 3-资料
    videoFiles.value = resources.filter((r: any) => String(r.resourceType) === '1')
    lectureFiles.value = resources.filter((r: any) => String(r.resourceType) === '2')
    experimentFiles.value = resources.filter((r: any) => String(r.resourceType) === '3')
    // no-op: reviews are bound to the current course id
  })
}

const handleCourseTypeChange = (val: string) => {
  const selected = courseTypeList.value.find((item: any) => String(item.id) === String(val))
  if (selected) {
    form.courseName = selected.courseTypeName
  }
}

const getTeacherLabel = (teacher: TeacherItem) => {
  return teacher.realname || teacher.username || teacher.email || teacher.name || '-'
}

const selectedTeacherLabel = computed(() => {
  const teacher = teacherList.value.find((item: TeacherItem) => String(item.id) === String(form.teacherId))
  return teacher ? getTeacherLabel(teacher) : form.teacherId || '-'
})

const handleSubmit = () => {
  if (!form.courseTypeId) {
    ElMessage.warning('请选择课程类别')
    return
  }

  confirmLoading.value = true
  // 通过 isAddMode 复用同一套表单提交逻辑，降低维护成本
  const url = isAddMode.value ? '/study/cloudComputingCourse/add' : '/study/cloudComputingCourse/edit'
  post(url, form, (_message: string) => {
    ElMessage.success('操作成功')
    handleCancel()
  }, (_err: any) => {
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

const loadReviews = () => {
  if (!form.id) return
  reviewLoading.value = true
  // 该页面按“课程维度”管理评价，因此 query 参数使用 courseId
  get(`/api/study/reviews?courseId=${encodeURIComponent(form.id)}`, (_message: string, data: any[]) => {
    reviewList.value = data || []
    reviewLoading.value = false
  }, () => {
    reviewLoading.value = false
  })
}

const openReviewDialog = (record?: any) => {
  reviewDialogMode.value = record ? 'edit' : 'add'
  reviewForm.id = record?.id || ''
  reviewForm.courseId = form.id
  // 新增时优先使用当前登录用户，编辑时回填已有作者信息
  reviewForm.username = currentUsername.value || record?.username || ''
  reviewForm.rating = Number(record?.rating || 5)
  reviewForm.content = record?.content || ''
  reviewDialogVisible.value = true
}

const submitReview = async () => {
  if (!reviewFormRef.value) return
  await reviewFormRef.value.validate((valid: boolean) => {
    if (!valid) return
    reviewSaving.value = true
    const payload = {
      courseId: form.id,
      // 学习模块课程评价接口写入课程 id
      courseId: form.id,
      username: currentUsername.value || reviewForm.username,
      rating: reviewForm.rating,
      content: reviewForm.content
    }

    const stopLoading = () => {
      reviewSaving.value = false
    }

    if (reviewDialogMode.value === 'add') {
      post('/api/study/reviews', payload, (_message: string) => {
        ElMessage.success(_message)
        reviewDialogVisible.value = false
        loadReviews()
        stopLoading()
      }, () => stopLoading())
      return
    }

    post(`/api/study/reviews/${reviewForm.id}`, { ...payload, id: reviewForm.id }, (_message: string) => {
      ElMessage.success(_message)
      reviewDialogVisible.value = false
      loadReviews()
      stopLoading()
    }, () => stopLoading())
  })
}

const handleDeleteReview = (record: any) => {
  deleteMapping(`/api/study/reviews/${record.id}`, { id: record.id }, () => {
    ElMessage.success('删除成功')
    loadReviews()
  })
}

const handleDeleteResource = (record: any) => {
  // deleteMapping(`/study/cloudComputingCourseResource/delete?id=${record.id}`, (msg) => {
  //   ElMessage.success('删除成功')
  //   if (form.id) loadResources(form.id)
  // })

  deleteMapping('/study/cloudComputingCourseResource/delete', { id: record.id }, (_message: string) => {
    ElMessage.success(_message)
    if (form.id) loadResources(form.id)
  }, (_failMsg: string) => {
    ElMessage.warning(_failMsg)
  })
}

onMounted(() => {
  init()
})

watchEffect(() => {
  // 当编辑对象切换后自动刷新评价列表，保持 tab 内容和课程一致
  if (form.id) loadReviews()
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

.review-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin: 16px 0;
  flex-wrap: wrap;
}

.review-resource-select {
  min-width: 280px;
  flex: 1;
  max-width: 520px;
}

.review-actions {
  display: flex;
  justify-content: flex-end;
  margin: 16px 0;
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