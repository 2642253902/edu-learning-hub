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
      <div class="right-panel">
        <div class="panel-header">
          <span class="panel-title">{{ panelTitle }}</span>
          <el-icon>
            <MoreFilled />
          </el-icon>
        </div>
        <div class="panel-content">
          <div v-for="(item, index) in currentList" :key="index" class="resource-item-row"
            :class="{ active: currentItemIndex === index }" @click="selectItem(index)">
            <el-checkbox :model-value="item.isCompleted" disabled class="res-checkbox" />
            <span class="item-text" :title="item.fileName">{{ item.fileName }}</span>
          </div>
          <el-empty v-if="currentList.length === 0" description="暂无资源" image-size="60" />
        </div>
      </div>

      <!-- 内容展示区 -->
      <div class="content-area">
        <!-- 视频内容 -->
        <div v-if="currentModule === 'video'" class="display-box video-box">
          <div class="video-player-container">
            <video v-if="currentList[currentItemIndex]?.url" ref="videoRef" controls class="video-element"
              :src="currentList[currentItemIndex]?.url" @ended="handleVideoEnded"></video>
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
        <div v-if="currentModule === 'lecture' || currentModule === 'data'" class="display-box preview-box">
          <div class="preview-container">
            <!-- 此处需要 vue-office 组件 -->
            <div v-if="currentList[currentItemIndex]?.url" class="office-preview-placeholder">
              <el-icon size="64" color="#409eff">
                <Document />
              </el-icon>
              <p class="mt-4">文件预览模式 ({{ currentList[currentItemIndex]?.fileName }})</p>
              <el-link :href="currentList[currentItemIndex].url" target="_blank" type="primary" class="mt-2">
                点击在新窗口预览或下载
              </el-link>
            </div>
            <el-empty v-else description="暂无预览文件" />
          </div>

          <div class="nav-controls sticky-bottom">
            <el-button :icon="ArrowLeft" plain @click="navigatePrev" :disabled="currentItemIndex <= 0" />
            <span class="counter">{{ currentItemIndex + 1 }} / {{ currentList.length }}</span>
            <el-button :icon="ArrowRight" plain @click="navigateNext"
              :disabled="currentItemIndex >= currentList.length - 1" />

            <div class="zoom-tools ml-auto">
              <el-button-group>
                <el-button :icon="Minus" @click="zoomOut" />
                <el-button disabled>{{ zoomLevel }}%</el-button>
                <el-button :icon="Plus" @click="zoomIn" />
              </el-button-group>
              <el-button class="ml-2" @click="resetZoom">重置</el-button>
            </div>
          </div>
        </div>

        <!-- 详情模块 -->
        <div v-if="currentModule === 'Details'" class="display-box info-box">
          <el-descriptions title="课程详细资料" :column="1" border>
            <el-descriptions-item label="课程名称">{{ courseDetails.courseName }}</el-descriptions-item>
            <el-descriptions-item label="课程分类">{{ courseDetails.courseTypeName }}</el-descriptions-item>
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { get } from '@/net'
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
  Minus
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// --- 数据状态 ---
const isLoading = ref(true)
const error = ref('')
const courseId = ref(route.query.courseId as string)
const currentModule = ref(route.query.resourceType as string || 'video')
const currentItemIndex = ref(0)
const zoomLevel = ref(100)
const courseDetails = ref<any>({})
const allResources = ref<any[]>([])

const menuItems = [
  { key: 'video', label: '视频课程', icon: VideoPlay },
  { key: 'lecture', label: '课件讲义', icon: Files },
  { key: 'data', label: '参考资料', icon: FolderOpened },
  { key: 'Details', label: '课程详情', icon: InfoFilled }
]

// --- 计算属性 ---
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
}

const selectItem = (index: number) => {
  currentItemIndex.value = index
}

const navigatePrev = () => { if (currentItemIndex.value > 0) currentItemIndex.value-- }
const navigateNext = () => { if (currentItemIndex.value < currentList.value.length - 1) currentItemIndex.value++ }

const zoomIn = () => { if (zoomLevel.value < 200) zoomLevel.value += 10 }
const zoomOut = () => { if (zoomLevel.value > 50) zoomLevel.value -= 10 }
const resetZoom = () => zoomLevel.value = 100

const handleVideoEnded = () => {
  ElMessage.success('学习完成！')
}

const initData = async () => {
  if (!courseId.value) {
    error.value = '课程ID丢失'
    isLoading.value = false
    return
  }

  isLoading.value = true
  get(`/study/cloudComputingCourse/queryById?id=${courseId.value}`, (msg, data) => {
    courseDetails.value = data
  })

  get(`/study/cloudComputingCourseResource/list?courseId=${courseId.value}&pageSize=500`, (msg, data) => {
    allResources.value = data?.records || []
    isLoading.value = false
  }, (err) => {
    error.value = '加载资源失败'
    isLoading.value = false
  })
}

const retryLoad = () => {
  error.value = ''
  initData()
}

onMounted(() => {
  initData()
})
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

.office-preview-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
}

.info-box {
  padding: 40px;
}
</style>
