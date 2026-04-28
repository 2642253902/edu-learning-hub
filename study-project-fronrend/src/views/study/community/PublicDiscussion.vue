<template>
  <div class="p-4">
    <h3 class="text-lg font-bold mb-4">公共讨论区</h3>

    <el-card class="mb-4">
      <post-form groupId="" @created="loadPosts" />
    </el-card>

    <el-table :data="posts" stripe>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="username" label="作者" width="140" />
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="text" @click="openPost(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showPost" width="60%">
      <template #title>帖子详情</template>
      <div v-if="currentPost">
        <h4 class="font-bold">{{ currentPost.title }}</h4>
        <div class="mt-2">{{ currentPost.content }}</div>
        <div class="mt-4">
          <comment-list :postId="currentPost.id" />
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
  communityApi.listPosts('', (d) => {
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
.p-4 { padding: 16px; }
</style>
