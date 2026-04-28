<template>
  <div>
    <el-card v-for="r in reviews" :key="r.id" class="mb-2">
      <div class="flex justify-between items-start">
        <div>
          <div class="font-bold">{{ r.username || '匿名' }} <span class="text-sm text-gray-500">{{ formatDate(r.createTime) }}</span></div>
          <div class="text-sm">评分：{{ r.rating }} 星</div>
          <div class="mt-2">{{ r.content }}</div>
        </div>
        <div class="text-right">
          <el-button type="text" @click="like(r)">点赞 {{ r.likes || 0 }}</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { communityApi } from '@/net'

const props = defineProps<{ resourceId: string }>()
const reviews = ref<any[]>([])

const load = () => {
  communityApi.listReviews(props.resourceId, (d) => {
    reviews.value = d || []
  })
}

const like = (r:any) => {
  communityApi.likeReview(r.id, () => {
    r.likes = (r.likes || 0) + 1
  })
}

const formatDate = (s:any) => {
  try { return new Date(s).toLocaleString() } catch(e) { return '' }
}

onMounted(load)
</script>
