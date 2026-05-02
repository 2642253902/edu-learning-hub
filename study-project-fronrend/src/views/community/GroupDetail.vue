<template>
  <div class="p-4">
    <div class="flex justify-between items-center mb-4">
      <h3 class="text-lg font-bold">{{ group?.name || '小组' }}</h3>
      <div>{{ group?.description }}</div>
    </div>

    <el-card class="mb-4">
      <!-- 发帖表单直接把 groupId 透传给子组件，确保后端创建帖子时能正确挂到当前小组 -->
      <post-form :groupId="groupId" @created="loadPosts" />
    </el-card>

    <!-- 小组内帖子列表：仅展示后端返回的基础信息，详情通过弹窗按需拉取/回填 -->
    <el-table :data="posts" stripe>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="username" label="作者" width="140" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button link @click="openPost(row.id)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

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
.p-4 {
  padding: 16px
}
</style>
