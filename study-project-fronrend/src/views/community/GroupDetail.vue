<template>
  <div class="detail-page">
    <el-card shadow="never" class="hero-card">
      <div class="hero-wrap">
        <div>
          <h3 class="hero-title">{{ group?.name || '学习小组' }}</h3>
          <p class="hero-subtitle">{{ group?.description || '在这里发帖、讨论、沉淀学习记录。' }}</p>
        </div>
        <div class="summary-chip">帖子 {{ posts.length }}</div>
      </div>
    </el-card>

    <el-card shadow="never" class="composer-card">
      <div class="section-title">发布新帖</div>
      <!-- 发帖表单直接把 groupId 透传给子组件，确保后端创建帖子时能正确挂到当前小组 -->
      <post-form :groupId="groupId" @created="loadPosts" />
    </el-card>

    <el-card shadow="never" class="posts-card">
      <div class="section-title">小组帖子</div>
      <!-- 小组内帖子列表：仅展示后端返回的基础信息，详情通过弹窗按需拉取/回填 -->
      <el-table :data="posts" stripe class="posts-table">
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column prop="username" label="作者" width="160" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button link @click="openPost(row.id)">查看</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="table-empty">还没有帖子，发一条开启讨论吧</div>
        </template>
      </el-table>
    </el-card>

    <el-dialog v-model="showPost" width="60%" :before-close="() => { showPost = false }">
      <template #header>帖子</template>
      <div v-if="currentPost">
        <h4 class="font-bold">{{ currentPost.title }}</h4>
        <div class="mt-2">{{ currentPost.content }}</div>
        <div class="mt-4">
          <!-- 评论列表以 postId 为锚点，和后端评论接口保持一对一对应 -->
          <comment-list v-if="currentPost?.id" :postId="String(currentPost.id)" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get } from '@/net'
import PostForm from './components/PostForm.vue'
import CommentList from './components/CommentList.vue'
import { useRoute } from 'vue-router'

/**
 * 前后端协同注释（小组详情）
 * - 接口：GET /api/community/groups 获取小组列表；GET /api/community/posts?groupId=... 获取当前小组帖子。
 * - 交互：发帖成功后立即刷新帖子列表，避免父页和后端状态不同步；查看帖子时通过弹窗展示正文和评论。
 * - 数据约定：groupId 来自路由参数或查询参数，确保外部链接进入时能直接定位到同一小组上下文。
 */

const route = useRoute()
const groupId = String(route.params.id || route.query.id || '')
const group = ref<any>(null)
const posts = ref<any[]>([])
const showPost = ref(false)
const currentPost = ref<any>(null)

const loadGroup = () => {
  get('/api/community/groups', (_message: string, d: any) => {
    const g = (d || []).find((x: any) => x.id === groupId)
    group.value = g
  })
}

const loadPosts = () => {
  get(`/api/community/posts?groupId=${encodeURIComponent(groupId)}`, (_message: string, d: any) => {
    posts.value = d || []
  })
}

const openPost = (id: string) => {
  currentPost.value = posts.value.find((p: any) => p.id === id)
  showPost.value = true
}

onMounted(() => { loadGroup(); loadPosts() })
</script>

<style scoped>
.detail-page {
  padding: 20px;
  background:
    radial-gradient(circle at top left, rgba(34, 197, 94, 0.1), transparent 34%),
    linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  min-height: 100%;
}

.hero-card,
.composer-card,
.posts-card {
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.05);
}

.hero-card,
.composer-card {
  margin-bottom: 16px;
}

.hero-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.hero-title {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  color: #111827;
}

.hero-subtitle {
  margin: 8px 0 0;
  font-size: 14px;
  color: #64748b;
}

.summary-chip {
  height: 34px;
  border-radius: 999px;
  padding: 0 13px;
  background: #ecfdf3;
  border: 1px solid #bbf7d0;
  color: #15803d;
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  font-weight: 700;
}

.section-title {
  margin-bottom: 12px;
  color: #1f2937;
  font-size: 15px;
  font-weight: 700;
}

.posts-card {
  overflow: hidden;
}

.posts-table {
  width: 100%;
}

.table-empty {
  color: #94a3b8;
  padding: 22px 0;
}

@media (max-width: 768px) {
  .detail-page {
    padding: 12px;
  }
}
</style>
