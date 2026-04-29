<template>
  <div class="p-4">
    <div class="flex justify-between items-center mb-4">
      <h3 class="text-lg font-bold">{{ group?.name || '小组' }}</h3>
      <div>{{ group?.description }}</div>
    </div>

    <el-card class="mb-4">
      <post-form :groupId="groupId" @created="loadPosts" />
    </el-card>

    <el-table :data="posts" stripe>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="username" label="作者" width="140" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button link @click="openPost(row.id)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showPost" width="60%" :before-close="()=>{ showPost=false }">
      <template #header>帖子</template>
      <div v-if="currentPost">
        <h4 class="font-bold">{{ currentPost.title }}</h4>
        <div class="mt-2">{{ currentPost.content }}</div>
        <div class="mt-4">
          <comment-list v-if="currentPost?.id" :postId="String(currentPost.id)" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { communityApi } from '@/net'
import PostForm from './components/PostForm.vue'
import CommentList from './components/CommentList.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const groupId = String(route.params.id || route.query.id || '')
const group = ref<any>(null)
const posts = ref<any[]>([])
const showPost = ref(false)
const currentPost = ref<any>(null)

const loadGroup = () => {
  communityApi.listGroups((d) => {
    const g = (d || []).find((x: any) => x.id === groupId)
    group.value = g
  })
}

const loadPosts = () => {
  communityApi.listPosts(groupId, (d) => {
    posts.value = d || []
  })
}

const openPost = (id:string) => {
  currentPost.value = posts.value.find(p=>p.id===id)
  showPost.value = true
}

onMounted(()=>{ loadGroup(); loadPosts() })
</script>

<style scoped>
.p-4 { padding: 16px }
</style>
