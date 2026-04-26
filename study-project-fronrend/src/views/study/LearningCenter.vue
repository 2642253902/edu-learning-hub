<template>
  <div class="learning-center">
    <!-- 顶部搜索区域 -->
    <div class="top-section">
      <div class="title-wrapper">
        <h1 class="page-title">课程列表</h1>
      </div>
      <div class="search-wrapper">
        <el-input v-model="searchValue" placeholder="请输入课程名称" class="search-input" clearable
          @keyup.enter="handleSearch">
          <template #append>
            <el-button :icon="Search" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 无数据提示 -->
    <div v-else-if="hasNoCourseData" class="no-data-container">
      <el-empty :description="noDataDescription" />
    </div>

    <!-- 课程列表 -->
    <div v-else class="course-list">
      <div v-for="category in courseTypes" :key="`category-${category.id}`" class="course-category"
        v-show="categoryData[category.id] && categoryData[category.id].length > 0">
        <!-- 课程分类标题 -->
        <div class="category-title">
          <span class="title-text">{{ category.courseTypeName }}</span>
          <span class="course-count">（{{ categoryPagination[category.id]?.total || 0 }}个课程）</span>
        </div>

        <!-- 课程内容 -->
        <div class="course-content">
          <!-- 单个课程项 -->
          <div v-for="(lesson, lessonIndex) in categoryData[category.id]" :key="lesson.id" class="lesson-item"
            :class="{ 'disabled': !lesson.available }">
            <!-- 课程头部信息 -->
            <div class="lesson-header">
              <div class="lesson-name">
                <span class="lesson-index">{{ getGlobalIndex(lessonIndex, category.id) }}:</span>
                <el-link type="primary" underline="never" @click="handleCourseNameClick(lesson)" class="lesson-link">
                  {{ lesson.courseName }}
                </el-link>
              </div>
              <el-tag :type="lesson.courseTag === '必修' ? 'danger' : 'info'" effect="plain" size="small"
                class="lesson-tag">
                {{ lesson.courseTag }}
              </el-tag>
            </div>

            <!-- 课程资源统计 -->
            <div class="lesson-resources">
              <div v-for="resourceType in resourceTypes" :key="resourceType.key" class="resource-item">
                <el-link :disabled="!hasResources(lesson, resourceType.key)"
                  @click="handleResourceClick(lesson, resourceType.key)" class="resource-link">
                  <span class="resource-text">{{ resourceType.name }}</span>
                  <span class="resource-count">({{ getResourceCount(lesson, resourceType.key) }})</span>
                </el-link>
              </div>
            </div>

            <!-- 课程未开始遮罩 -->
            <div v-if="!lesson.available" class="lesson-mask">
              <div class="mask-text">课程暂未开始</div>
            </div>
          </div>
        </div>

        <!-- 分类内部分页组件 -->
        <div class="category-pagination"
          v-if="categoryPagination[category.id] && categoryPagination[category.id].total > categoryPagination[category.id].pageSize">
          <el-pagination v-model:current-page="categoryPagination[category.id].pageNo"
            :page-size="categoryPagination[category.id].pageSize" :total="categoryPagination[category.id].total"
            layout="total, prev, pager, next, jumper"
            @current-change="(val) => handleCategoryPageChange(val, category.id)" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/net'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// --- 资源类型配置 ---
const resourceTypes = [
  { key: 'video', name: '视频', field: 'videoCount' },
  { key: 'lecture', name: '讲义', field: 'lectureCount' },
  { key: 'data', name: '资源', field: 'resourceCount' }
]

// --- 状态数据 ---
const router = useRouter()
const searchValue = ref('')
const isSearching = ref(false)
const loading = ref(false)
const courseTypes = ref<any[]>([])
const categoryPagination = reactive<Record<string, any>>({})
const categoryData = reactive<Record<string, any[]>>({})

// --- 计算属性 ---
const noDataDescription = computed(() => searchValue.value ? '未找到相关课程' : '暂无课程数据')
const hasNoCourseData = computed(() => {
  return Object.values(categoryData).every(courses => courses.length === 0)
})

// --- 方法 ---
const handleSearch = () => {
  isSearching.value = !!searchValue.value.trim()
  loadAllCategoryData()
}

const getGlobalIndex = (lessonIndex: number, categoryId: string) => {
  const pagination = categoryPagination[categoryId]
  return (pagination.pageNo - 1) * pagination.pageSize + lessonIndex + 1
}

const hasResources = (lesson: any, resourceType: string) => {
  const typeConfig = resourceTypes.find(type => type.key === resourceType)
  return typeConfig ? (lesson[typeConfig.field] || 0) > 0 : false
}

const getResourceCount = (lesson: any, resourceType: string) => {
  const typeConfig = resourceTypes.find(type => type.key === resourceType)
  return typeConfig ? (lesson[typeConfig.field] || 0) : 0
}

const handleCourseNameClick = (lesson: any) => {
  router.push({ path: '/study/CourseDetail', query: { courseId: lesson.id, resourceType: 'video' } })
}

const handleResourceClick = (lesson: any, resourceType: string) => {
  if (!hasResources(lesson, resourceType)) {
    ElMessage.info('暂无相关资源')
    return
  }
  router.push({ path: '/study/CourseDetail', query: { courseId: lesson.id, resourceType } })
}

const loadCourseTypes = async () => {
  return new Promise<void>((resolve) => {
    get('/study/cloudComputingCourseType/list?pageNo=1&pageSize=1000', (msg, data) => {
      const records = data?.records || data
      courseTypes.value = records.map((item: any) => ({
        id: item.id,
        courseTypeName: item.courseTypeName
      }))

      courseTypes.value.forEach(type => {
        if (!categoryPagination[type.id]) {
          categoryPagination[type.id] = { pageNo: 1, pageSize: 12, total: 0 }
        }
        if (!categoryData[type.id]) {
          categoryData[type.id] = []
        }
      })
      resolve()
    }, () => resolve())
  })
}

const loadCourseListByCategory = async (categoryId: string) => {
  const pagination = categoryPagination[categoryId]
  let url = `/study/cloudComputingCourse/list?pageNo=${pagination.pageNo}&pageSize=${pagination.pageSize}&courseTypeId=${categoryId}`
  if (isSearching.value && searchValue.value.trim()) {
    url += `&courseName=*${searchValue.value.trim()}*`
  }

  return new Promise<void>((resolve) => {
    get(url, (msg, data) => {
      categoryData[categoryId] = (data?.records || []).map((course: any) => ({
        id: course.id,
        courseName: course.courseName || '未命名课程',
        courseTag: course.courseTag || '选修',
        videoCount: course.videoCount || 0,
        lectureCount: course.lectureCount || 0,
        resourceCount: course.resourceCount || 0,
        available: course.courseStatus === 1
      }))
      categoryPagination[categoryId].total = msg.total || 0
      resolve()
    }, () => resolve())
  })
}

const loadAllCategoryData = async () => {
  loading.value = true
  if (courseTypes.value.length === 0) await loadCourseTypes()
  await Promise.all(courseTypes.value.map(type => loadCourseListByCategory(type.id)))
  loading.value = false
}

const handleCategoryPageChange = (page: number, categoryId: string) => {
  categoryPagination[categoryId].pageNo = page
  loadCourseListByCategory(categoryId)
}

onMounted(() => {
  loadAllCategoryData()
})

// 防抖搜索
let searchTimer: any = null
watch(searchValue, (newVal) => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    handleSearch()
  }, 500)
})
</script>

<style scoped>
.learning-center {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.top-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: #fff;
  padding: 16px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f1f1f;
}

.search-input {
  width: 320px;
}

.course-category {
  margin-bottom: 24px;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.category-title {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
}

.title-text {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  border-left: 4px solid #1890ff;
  padding-left: 12px;
}

.course-count {
  font-size: 14px;
  color: #8c8c8c;
  margin-left: 8px;
}

.course-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.lesson-item {
  position: relative;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s;
}

.lesson-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  background: #fff;
}

.lesson-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.lesson-name {
  font-size: 16px;
  font-weight: 500;
  flex: 1;
  margin-right: 8px;
}

.lesson-index {
  color: #8c8c8c;
  margin-right: 4px;
}

.lesson-link {
  font-weight: 500;
}

.lesson-resources {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.resource-link {
  font-size: 13px;
}

.resource-count {
  color: #bfbfbf;
}

.lesson-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  z-index: 10;
}

.mask-text {
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.disabled {
  opacity: 0.8;
}

.category-pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style>
