<template>
  <div class="discussion-page">
    <div class="page-hero">
      <div>
        <h3 class="page-title">公共讨论区</h3>
        <p class="page-subtitle">在这里发布问题、交流经验、查看别人的回复。</p>
      </div>
    </div>

    <el-card class="section-card composer-card">
      <post-form groupId="" @created="loadPosts" />
    </el-card>

    <el-card class="section-card list-card" shadow="never">
      <el-table :data="posts" stripe class="discussion-table">
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column prop="username" label="作者" width="140" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openPost(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showPost" width="68%" class="post-dialog">
      <template #header>帖子详情</template>
      <div v-if="currentPost" class="post-detail">
        <div class="post-detail-head">
          <h4 class="post-title">{{ currentPost.title }}</h4>
          <div class="post-meta">
            <span>{{ currentPost.username || '匿名' }}</span>
            <span>{{ currentPost.createTime }}</span>
          </div>
        </div>
        <div class="post-content">{{ currentPost.content }}</div>
        <div class="comment-block">
          <comment-list v-if="currentPost?.id" :postId="String(currentPost.id)" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PostForm from './components/PostForm.vue'
import CommentList from './components/CommentList.vue'
import { communityApi } from '@/net'

const posts = ref<any[]>([])
const showPost = ref(false)
const currentPost = ref<any>(null)

const loadPosts = () => {
  communityApi.listPosts('', (d: any) => {
    posts.value = d || []
  })
}

const openPost = (row: any) => {
  currentPost.value = row
  showPost.value = true
}

onMounted(loadPosts)
</script>

<style scoped>
.discussion-page {
  padding: 20px;
  background:
    radial-gradient(circle at top left, rgba(59, 130, 246, 0.08), transparent 30%),
    linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  min-height: 100%;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  color: #111827;
}

.page-subtitle {
  margin: 6px 0 0;
  color: #6b7280;
  font-size: 14px;
}

.section-card {
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.05);
  margin-bottom: 16px;
}

.composer-card {
  background: linear-gradient(180deg, #ffffff 0%, #fafcff 100%);
}

.list-card {
  overflow: hidden;
}

.discussion-table {
  width: 100%;
}

.post-dialog :deep(.el-dialog__body) {
  padding-top: 6px;
}

.post-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-detail-head {
  padding-bottom: 12px;
  border-bottom: 1px solid #eef2f7;
}

.post-title {
  margin: 0;
  font-size: 20px;
  line-height: 1.4;
  color: #111827;
}

.post-meta {
  margin-top: 8px;
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
  color: #94a3b8;
  font-size: 13px;
}

.post-content {
  color: #374151;
  line-height: 1.8;
  white-space: pre-wrap;
  background: #f9fafb;
  border: 1px solid #eef2f7;
  border-radius: 14px;
  padding: 16px;
}

.comment-block {
  padding-top: 4px;
}
</style>
