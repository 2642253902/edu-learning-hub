<template>
  <div>
    <el-form :model="form" class="mb-4">
      <el-form-item>
        <el-input type="textarea" v-model="form.content" placeholder="写回复..." />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">回复</el-button>
      </el-form-item>
    </el-form>

    <el-timeline>
      <el-timeline-item v-for="c in comments" :key="c.id">
        <div class="font-bold">{{ c.username || '匿名' }}</div>
        <div class="mt-1">{{ c.content }}</div>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { communityApi } from '@/net'

const props = defineProps<{ postId: string }>()

const comments = ref<any[]>([])
const form = ref({ content: '' })

const load = () => {
  communityApi.listComments(props.postId, (d) => {
    comments.value = d || []
  })
}

const submit = () => {
  communityApi.createComment(props.postId, form.value, () => {
    form.value.content = ''
    load()
  })
}

onMounted(load)
</script>
