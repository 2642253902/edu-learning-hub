<template>
  <div class="course-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <el-button circle :icon="ArrowLeft" class="back-btn" @click="handleBack" />
      <h1 class="course-title-text">{{ courseDetails.courseName || '课程详情' }}</h1>
    </div>

    <!-- 加载与错误状态 -->
    <div v-if="isLoading" class="loading-container" v-loading="true">
      <p>正在努力加载资源...</p>
    </div>
    <div v-else-if="error" class="error-container">
      <el-result icon="error" title="加载失败" :sub-title="error">
        <template #extra>
          <el-button type="primary" @click="retryLoad">点击重试</el-button>
        </template>
      </el-result>
    </div>

    <!-- 主内容区 -->
    <div v-else class="main-content">
      <!-- 左侧导航 -->
      <div class="sidebar">
        <div class="menu-list">
          <div v-for="item in menuItems" :key="item.key" class="menu-item"
            :class="{ active: currentModule === item.key }" @click="switchModule(item.key)">
            <el-icon class="menu-icon">
              <component :is="item.icon" />
            </el-icon>
            <span class="menu-label">{{ item.label }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧列表 -->
      <div class="right-panel" v-if="currentModule !== 'Details'&&currentModule !== 'Reviews'">
        <div class="panel-header">
          <span class="panel-title">{{ panelTitle }}</span>
          <el-icon>
            <MoreFilled />
          </el-icon>
        </div>
        <div class="panel-content">
          <div v-for="(item, index) in currentList" :key="index" class="resource-item-row"
            :class="{ active: currentItemIndex === index }" @click="selectItem(index)">
            <el-checkbox :model-value="!!item.isCompleted" disabled class="res-checkbox" />
            <span class="item-text" :title="item.fileName">{{ item.fileName }}</span>
          </div>
          <el-empty v-if="currentList.length === 0" description="暂无资源" :image-size="60" />
        </div>
      </div>

      <!-- 内容展示区 -->
      <div class="content-area">
        <!-- 视频内容 -->
        <div v-if="currentModule === 'video'" class="display-box video-box">
          <div class="video-player-container">
            <video v-if="currentList[currentItemIndex]?.url" ref="videoRef" controls class="video-element"
              :src="currentList[currentItemIndex]?.url" @ended="handleVideoEnded" @play="handleVideoPlay"
              @pause="handleVideoPause"></video>
            <el-empty v-else description="无视频播放源" />
          </div>
          <div class="nav-controls">
            <el-button :icon="ArrowLeft" plain @click="navigatePrev" :disabled="currentItemIndex <= 0">上一个</el-button>
            <span class="counter">{{ currentItemIndex + 1 }} / {{ currentList.length }}</span>
            <el-button plain @click="navigateNext" :disabled="currentItemIndex >= currentList.length - 1">
              下一个<el-icon class="el-icon--right">
                <ArrowRight />
              </el-icon>
            </el-button>
          </div>
        </div>

        <!-- 讲义/资料预览 -->
        <div v-if="currentModule === 'lecture' || currentModule === 'data'" class="display-box preview-box"
          ref="previewBoxRef">
          <div class="preview-container" v-loading="previewLoading" @scroll="handleDocScroll">
            <!-- 重点：讲义内容需要一个内部容器来撑开高度以便外层 preview-container 产生滚动条 -->
            <div v-if="renderedUrl && currentModule === 'lecture'" class="office-preview-wrapper"
              :style="{ transform: `scale(${zoomLevel / 100})`, transformOrigin: 'top center' }">
              <vue-office-docx v-if="getFileType(currentList[currentItemIndex]?.url) === 'docx'" :src="renderedUrl"
                style="min-height: 100%;" @rendered="onOfficeRendered" />
              <vue-office-excel
                v-else-if="getFileType(currentList[currentItemIndex]?.url) === 'xlsx' || getFileType(currentList[currentItemIndex]?.url) === 'xls'"
                :src="renderedUrl" style="min-height: 100%;" @rendered="onOfficeRendered" />
              <vue-office-pdf v-else-if="getFileType(currentList[currentItemIndex]?.url) === 'pdf'" :src="renderedUrl"
                style="min-height: 100%;" @rendered="onOfficeRendered" />
              <div v-else class="unknown-file-type">
                <el-result icon="warning" title="不支持的预览格式" sub-title="该文件格式暂不支持在线预览">
                  <template #extra>
                    <el-button type="primary" @click="handleDownload(currentList[currentItemIndex])">下载文件</el-button>
                  </template>
                </el-result>
              </div>
            </div>
           <div v-else-if="currentModule === 'data' && currentList[currentItemIndex]" class="data-download-center">
              <el-result icon="info" title="资料下载" sub-title="点击下方按钮下载参考资料，下载完成后将自动标记为已学">
                <template #extra>
                  <el-button type="success" size="large" :icon="Document"
                    @click="handleDownload(currentList[currentItemIndex])">
                    立即下载并标记完成
                  </el-button>
                </template>
              </el-result>
            </div>
            <el-empty v-else-if="!previewLoading" description="暂无预览文件" />
          </div>

          <div class="nav-controls sticky-bottom">
            <el-button :icon="ArrowLeft" plain @click="navigatePrev" :disabled="currentItemIndex <= 0" />
            <span class="counter">{{ currentItemIndex + 1 }} / {{ currentList.length }}</span>
            <el-button :icon="ArrowRight" plain @click="navigateNext"
              :disabled="currentItemIndex >= currentList.length - 1" />

            <div class="zoom-tools ml-auto" v-if="currentModule === 'lecture'">
              <el-button-group>
                <el-button :icon="Minus" @click="zoomOut" />
                <el-button disabled>{{ zoomLevel }}%</el-button>
                <el-button :icon="Plus" @click="zoomIn" />
              </el-button-group>
              <el-button class="ml-2" @click="resetZoom">重置</el-button>
              <el-button class="ml-2" :icon="FullScreen" @click="toggleFullScreen">全屏</el-button>
            </div>
          </div>
        </div>

        <!-- 详情模块 -->
        <div v-if="currentModule === 'Details'" class="detail-stack">
          <div class="display-box info-box detail-card">
            <div class="detail-card-head">
              <div>
                <div class="section-kicker">课程概览</div>
                <h3 class="section-title">课程详细资料</h3>
              </div>
            </div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="课程名称">{{ courseDetails.courseName }}</el-descriptions-item>
              <el-descriptions-item label="课程分类">{{ courseTypeName }}</el-descriptions-item>
              <el-descriptions-item label="标签">
                <el-tag size="small">{{ courseDetails.courseTag }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="总课时">{{ courseDetails.courseHours }} 小时</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-badge :type="courseDetails.courseStatus === 1 ? 'success' : 'danger'" is-dot />
                {{ courseDetails.courseStatus === 1 ? '启用中' : '已停用' }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

         
        </div>
          <div v-if="currentModule === 'Reviews'" class="detail-stack">
           <el-card shadow="never" class="review-card">
            <template #header>
              <div class="review-card-head">
                <div>
                  <div class="section-kicker">课程互动</div>
                  <h3 class="section-title">课程评价</h3>
                </div>
                <span class="review-count">{{ courseId ? '支持实时发表与查看' : '暂无课程 ID' }}</span>
              </div>
            </template>
            <resource-review-form v-if="courseId" :resourceId="courseId" @saved="handleReviewSaved" />
            <div class="review-list-wrap">
              <resource-review-list ref="detailReviewListRef" v-if="courseId" :resourceId="courseId" />
            </div>
          </el-card>
        </div>


      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getApiBaseURL, get, getBlob, post } from '@/net'
import {
  ArrowLeft,
  ArrowRight,
  VideoPlay,
  Files,
  FolderOpened,
  InfoFilled,
  MoreFilled,
  Document,
  Plus,
  Minus,
  FullScreen
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 文档预览组件
import VueOfficeDocx from '@vue-office/docx'
import '@vue-office/docx/lib/index.css'
import VueOfficeExcel from '@vue-office/excel'
import '@vue-office/excel/lib/index.css'
import VueOfficePdf from '@vue-office/pdf'

import ResourceReviewForm from './components/ResourceReviewForm.vue'
import ResourceReviewList from './components/ResourceReviewList.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// --- 数据状态 ---
const isLoading = ref(true)
const error = ref('')
const courseId = ref(route.query.courseId as string)
const currentModule = ref(route.query.resourceType as string || 'video')
const currentItemIndex = ref(0)
const zoomLevel = ref(100)
const courseDetails = ref<any>({})
const courseTypeDict = ref<any[]>([])
const allResources = ref<any[]>([])
const detailReviewListRef = ref()

// 预览相关状态
const renderedUrl = ref('')
const previewLoading = ref(false)
const previewBoxRef = ref<HTMLElement | null>(null)

const menuItems = [
  { key: 'video', label: '视频课程', icon: VideoPlay },
  { key: 'lecture', label: '课件讲义', icon: Files },
  { key: 'data', label: '参考资料', icon: FolderOpened },
  { key: 'Details', label: '课程详情', icon: InfoFilled },
   { key: 'Reviews', label: '课程评价', icon: InfoFilled }
]

// --- 计算属性 ---
const courseTypeName = computed(() => {
  if (courseDetails.value.courseTypeName) return courseDetails.value.courseTypeName
  const match = courseTypeDict.value.find(item => String(item.id) === String(courseDetails.value.courseTypeId))
  return match ? match.courseTypeName : '-'
})

const currentList = computed(() => {
  const typeMap: Record<string, string> = { 'video': '1', 'lecture': '2', 'data': '3' }
  const targetType = typeMap[currentModule.value]
  if (!targetType) return []
  return allResources.value.filter(item => String(item.resourceType) === targetType)
})

const panelTitle = computed(() => {
  return menuItems.find(m => m.key === currentModule.value)?.label || '资源列表'
})

// --- 方法 ---
const handleBack = () => router.back()

const switchModule = (key: string) => {
  currentModule.value = key
  currentItemIndex.value = 0
  stopProgressSaveTimer() // 每次切换模块停止定时器
  // 切换模块后加载当前选中的资源的记录
  if (currentList.value.length > 0) {
    queryLearningRecord(currentList.value[0].id)
  }
}

const selectItem = (index: number) => {
  currentItemIndex.value = index
  stopProgressSaveTimer() // 每次切换项目停止定时器
  // 选中资源后加载该资源的学习记录
  const item = currentList.value[index]
  if (item) {
    queryLearningRecord(item.id)
  }
}

const navigatePrev = () => { if (currentItemIndex.value > 0) selectItem(currentItemIndex.value - 1) }
const navigateNext = () => { if (currentItemIndex.value < currentList.value.length - 1) selectItem(currentItemIndex.value + 1) }

const zoomIn = () => { if (zoomLevel.value < 200) zoomLevel.value += 10 }
const zoomOut = () => { if (zoomLevel.value > 50) zoomLevel.value -= 10 }
const resetZoom = () => zoomLevel.value = 100

const toggleFullScreen = () => {
  if (!previewBoxRef.value) return
  if (!document.fullscreenElement) {
    previewBoxRef.value.requestFullscreen().catch(err => {
      ElMessage.error(`无法进入全屏模式: ${err.message}`)
    })
  } else {
    document.exitFullscreen()
  }
}

const handleVideoEnded = () => {
  markAsCompleted()
}

// 学习记录相关状态 (参考 CourseDetailsFrom)
const learningRecord = ref<any>({
  id: null,
  courseId: courseId.value,
  contentId: null,
  learningStatus: '0',
  learningTime: 0,
  lastLearnTime: null,
  createTime: null
})

// 查询学习记录
const queryLearningRecord = async (contentId: string) => {
  if (!courseId.value || !contentId) return

  get(`/study/cloudComputingStudentLearningRecord/list?courseId=${courseId.value}&contentId=${contentId}&pageNo=1&pageSize=1`, (msg, data) => {
    if (data && data.records && data.records.length > 0) {
      learningRecord.value = data.records[0]
      // 同步本地列表中的勾选状态
      const item = currentList.value.find(i => i.id === contentId)
      if (item && learningRecord.value.learningStatus === '1') {
        item.isCompleted = 1
      }
    } else {
      // 如果没有记录，则新建一条
      createLearningRecord(contentId)
    }
  }, (msg) => {
    console.warn('查询学习记录未找到，将创建新记录:', msg)
    createLearningRecord(contentId)
  })
}

// 创建学习记录
const createLearningRecord = (contentId: string) => {
  const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
  const recordData = {
    courseId: courseId.value,
    contentId: contentId,
    // userId 不放在 JSON body 中，而是按后端要求放 URL 参数
    learningStatus: '0',
    learningTime: 0,
    lastLearnTime: now,
    createTime: now
  }

  // 后端 add 接口定义: @PostMapping(value = "/add") public RestBean<String> add(@RequestBody CloudComputingStudentLearningRecord cloudComputingStudentLearningRecord, @RequestParam(name = "userId", required = true) String userId)
  post(`/study/cloudComputingStudentLearningRecord/add?userId=${userStore.auth.user?.id}`, recordData, (msg, data) => {
    // 后端返回的是 RestBean<String> "添加成功！"，没有返回对象
    // 我们需要通过查询或重新加载来获取生成的记录 ID，或者先手动处理局部状态
    queryLearningRecord(contentId)
  }, (err) => {
    console.error('创建学习记录失败:', err)
  })
}

// 标记为完成 (参考 CourseDetailsFrom 的 updateLearningRecord 逻辑)
const markAsCompleted = () => {
  const currentRes = currentList.value[currentItemIndex.value]
  if (!currentRes || currentRes.isCompleted) return

  const now = new Date().toISOString().replace('T', ' ').substring(0, 19)
  const updateData = {
    ...learningRecord.value,
    learningStatus: '1',
    lastLearnTime: now
  }

  // 后端 edit 接口定义: @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST}) public RestBean<String> edit(@RequestBody CloudComputingStudentLearningRecord cloudComputingStudentLearningRecord)
  // 注意：如果是 POST 方式，调用 post 工具函数即可
  post('/study/cloudComputingStudentLearningRecord/edit', updateData, (msg, data) => {
    learningRecord.value.learningStatus = '1'
    // 更新当前资源项的勾选状态
    // 注意：如果是通过计算属性得到的 currentList，直接修改 item 属性可能不响应，我们需要修改源数据 allResources
    const targetItem = allResources.value.find(r => r.id === currentRes.id)
    if (targetItem) {
      targetItem.isCompleted = 1
    }
    ElMessage.success('学习完成！')
  }, (err) => {
    console.error('更新学习状态失败:', err)
    ElMessage.error('无法同步学习状态')
  })
}

// 视频播放记录逻辑
const progressSaveTimer = ref<any>(null)
const lastSaveTime = ref(0)

const handleVideoPlay = () => {
  startProgressSaveTimer()
}

const handleVideoPause = () => {
  stopProgressSaveTimer()
}

const startProgressSaveTimer = () => {
  stopProgressSaveTimer()
  progressSaveTimer.value = setInterval(() => {
    saveLearningProgress()
  }, 10000) // 每10秒同步一次进度
}

const stopProgressSaveTimer = () => {
  if (progressSaveTimer.value) {
    clearInterval(progressSaveTimer.value)
    progressSaveTimer.value = null
  }
}

const saveLearningProgress = () => {
  const video = document.querySelector('video')
  if (video) {
    // 这里可以调用接口保存视频进度，如果需要
    // const currentTime = Math.floor(video.currentTime)
  }
}

// 讲义/资料完成逻辑
const onOfficeRendered = () => {
  console.log('Office 组件渲染完成')
  // 检查是否内容太短没有滚动条
  const container = document.querySelector('.preview-container')
  if (container) {
    const { scrollHeight, clientHeight } = container
    console.log('检查内容高度:', { scrollHeight, clientHeight })
    // 如果总高度小于容器高度，或者超出很少（比如 20px 以内），视为直接完成
    if (scrollHeight <= clientHeight + 20) {
      console.log('检测到文档较短，无须滚动，自动标记完成')
      if (learningRecord.value.learningStatus !== '1') {
        markAsCompleted()
      }
    } else {
      ElMessage.info('文档已加载，向下滚动可完成学习')
    }
  }
}

const handleDocScroll = (e: any) => {
  console.log('滚动事件触发:', {
    scrollTop: e.target.scrollTop,
    scrollHeight: e.target.scrollHeight,
    clientHeight: e.target.clientHeight
  })

  if (currentModule.value !== 'lecture') return

  const target = e.target
  const { scrollTop, scrollHeight, clientHeight } = target

  // 判断逻辑：
  // 1. 如果没有滚动条 (scrollHeight <= clientHeight)，则在渲染完成时处理
  // 2. 如果有滚动条，拖到 85% 算完成
  const scrollRatio = (scrollTop + clientHeight) / scrollHeight
  const isReachedThreshold = scrollRatio >= 0.85

  if (isReachedThreshold) {
    if (learningRecord.value.learningStatus !== '1') {
      console.log('判定为滚动超过85%，触发标记完成')
      markAsCompleted()
    }
  }
}

const handleDownload = async (item: any) => {
  if (!item?.url) return

  // 资料只要点击，无论下载结果如何都标记为完成
  if (currentModule.value === 'data') {
    markAsCompleted()
  }

  try {
    const blob = await getBlob(item.url)
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = item.fileName
    link.click()
    URL.revokeObjectURL(link.href)
  } catch (err) {
    console.error('下载过程中发生错误:', err)
    // 注意：即使报错我们也保留之前的 markAsCompleted 状态，除非你想报错时撤回
    ElMessage.error('下载失败，请稍后重试')
  }
}

// 监听当前选中的 URL 变化，手动获取 Blob 以携带认证信息
watch(() => currentList.value[currentItemIndex.value]?.url, async (newUrl) => {
  // 如果当前不是预览模块，则不处理
  if (currentModule.value !== 'lecture' && currentModule.value !== 'data') {
    renderedUrl.value = ''
    return
  }

  if (!newUrl) {
    renderedUrl.value = ''
    return
  }

  previewLoading.value = true
  try {
    const blob = await getBlob(newUrl)
    // 释放旧的 URL 内存
    if (renderedUrl.value) {
      URL.revokeObjectURL(renderedUrl.value)
    }
    renderedUrl.value = URL.createObjectURL(blob)
  } catch (err: any) {
    console.error('获取预览文件失败:', err)
    ElMessage.error('无法加载预览文件，请检查登录状态或权限')
    renderedUrl.value = ''
  } finally {
    previewLoading.value = false
  }
}, { immediate: true })

// 监听模块切换，清空旧的预览地址
watch(currentModule, (newVal) => {
  if (newVal === 'video' || newVal === 'Details') {
    if (renderedUrl.value) {
      URL.revokeObjectURL(renderedUrl.value)
      renderedUrl.value = ''
    }
  }
})

// 获取文件后缀类型
const getFileType = (url: string) => {
  if (!url) return ''
  const part = url.split('.').pop()
  return part ? part.toLowerCase() : ''
}

// 拼接完整的后端路径
const getFullUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `${getApiBaseURL()}/upload/download?fileName=${encodeURIComponent(url)}`
}

const initData = async () => {
  if (!courseId.value) {
    error.value = '课程ID丢失'
    isLoading.value = false
    return
  }

  isLoading.value = true

  // 加载字典数据
  get('/study/cloudComputingCourseType/list?pageSize=1000', (msg, data) => {
    courseTypeDict.value = data?.records || data || []
  })

  get(`/study/cloudComputingCourse/list?id=${courseId.value}`, (msg, data) => {
    // 确保从返回的记录中通过 ID 查找到正确的课程详情
    const records = data?.records || data || []
    const record = Array.isArray(records)
      ? records.find((r: any) => String(r.id) === String(courseId.value)) || records[0]
      : records

    if (record) {
      courseDetails.value = record
    }
  })

  get(`/study/cloudComputingCourseResource/list?courseId=${courseId.value}&pageSize=500`, async (msg, data) => {
    const records = data?.records || []
    allResources.value = records.map((item: any) => ({
      ...item,
      fileName: item.resourceName, // 适配组件内的 fileName 引用
      url: getFullUrl(item.resourceUrl),
      isCompleted: 0 // 默认未完成，稍后通过学习记录同步
    }))

    // 加载资源后，立即为当前选中的资源初始化学习记录
    if (currentList.value.length > 0) {
      await queryLearningRecord(currentList.value[currentItemIndex.value].id)
    }

    // 批量同步所有资源的状态（可选，为了让侧边栏勾选一致）
    syncAllResourcesStatus()

    isLoading.value = false
  }, (err) => {
    error.value = '加载资源失败'
    isLoading.value = false
  })
}

const refreshCourseReviews = () => {
  if (courseId.value) {
    void initData()
  }
}

// 批量同步所有资源的状态
const syncAllResourcesStatus = () => {
  get(`/study/cloudComputingStudentLearningRecord/list?courseId=${courseId.value}&pageSize=500`, (msg, data) => {
    if (data && data.records) {
      const records = data.records
      allResources.value.forEach(resItem => {
        const record = records.find((r: any) => r.contentId === resItem.id)
        if (record && record.learningStatus === '1') {
          resItem.isCompleted = 1
        }
      })
    }
  }, (err) => {
    console.error('同步所有资源状态失败:', err)
  })
}

const retryLoad = () => {
  error.value = ''
  initData()
}

onMounted(() => {
  initData()
})

const handleReviewSaved = (d: any) => {
  if (detailReviewListRef.value && typeof detailReviewListRef.value.addReview === 'function') {
    detailReviewListRef.value.addReview(d)
  }
}
</script>

<style scoped>
.course-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f7fa;
}

.header {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.course-title-text {
  margin: 0 0 0 16px;
  font-size: 18px;
  font-weight: 600;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
  padding: 16px;
  gap: 16px;
}

.sidebar {
  width: 180px;
  background: #fff;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.menu-list {
  padding: 8px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
}

.menu-item:hover {
  background: #f0f7ff;
  color: #409eff;
}

.menu-item.active {
  background: #409eff;
  color: #fff;
}

.menu-icon {
  margin-right: 12px;
  font-size: 18px;
}

.right-panel {
  width: 260px;
  background: #fff;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.resource-item-row {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 4px;
  transition: background 0.2s;
}

.resource-item-row:hover {
  background: #f5f7fa;
}

.resource-item-row.active {
  background: #ecf5ff;
}

.resource-item-row.active .item-text {
  color: #409eff;
  font-weight: 600;
}

.item-text {
  font-size: 13px;
  margin-left: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.content-area {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.display-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  min-height: 0;
  /* 关键：允许内容区域缩小以触发内部滚动 */
}

.video-player-container {
  flex: 1;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-element {
  width: 100%;
  height: 100%;
}

.nav-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 20px;
  gap: 20px;
}

.counter {
  font-size: 14px;
  font-weight: 600;
  color: #909399;
}

.preview-box {
  display: flex !important;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  background: #fff;
}

.preview-box:fullscreen {
  padding: 40px;
  background: #f0f2f5;
  overflow-y: auto !important;
  display: block !important;
  width: 100vw !important;
  height: 100vh !important;
}

.preview-box:fullscreen .preview-container {
  max-width: 1000px;
  margin: 0 auto;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  height: auto !important;
  min-height: 100%;
  display: block !important;
  background: #fff;
}

.preview-box:fullscreen .office-preview-wrapper {
  transform: none !important;
  width: 100% !important;
  max-width: none !important;
}

.preview-box:fullscreen .nav-controls {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.9);
  padding: 10px 20px;
  border-radius: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 10000;
  width: auto;
}

.preview-container {
  flex: 1;
  overflow-y: auto !important;
  overflow-x: hidden;
  background-color: #fff;
  padding: 20px;
  display: block;
  position: relative;
}

.office-preview-wrapper {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  transition: transform 0.2s ease;
}

.unknown-file-type {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.info-box {
  padding: 40px;
}

.detail-stack {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: auto;
}

.detail-card {
  background: linear-gradient(180deg, #ffffff 0%, #fbfcfe 100%);
}

.detail-card-head,
.review-card-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
}

.section-kicker {
  color: #8c8c8c;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 4px;
}

.section-title {
  margin: 0;
  color: #1f2937;
  font-size: 18px;
}

.review-card {
  border-radius: 14px;
}

.review-count {
  color: #8c8c8c;
  font-size: 13px;
}

.review-list-wrap {
  margin-top: 18px;
}
</style>
