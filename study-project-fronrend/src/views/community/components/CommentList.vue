<template>
  <div class="comment-panel">
    <el-card class="reply-card" shadow="never">
      <el-form :model="form" class="reply-form">
        <el-form-item>
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="写回复..." />
        </el-form-item>
        <el-form-item class="reply-actions">
          <el-button type="primary" @click="submit">回复</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="comments-list">
      <el-card v-for="c in comments" :key="c.id" class="comment-card" shadow="never">
        <div class="comment-head">
          <div class="comment-author">{{ c.username || '匿名' }}</div>
          <div class="comment-time">{{ formatTime(c.createTime || c.create_time || c.createdAt) }}</div>
        </div>
        <div class="comment-content">{{ c.content }}</div>
      </el-card>

      <div v-if="comments.length === 0" class="empty-state">暂无回复，快来抢沙发~</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { get, post } from '@/net'

const props = defineProps<{ postId?: string }>()

const comments = ref<any[]>([])
const form = ref({ content: '' })

const load = () => {
  if (!props.postId) return
  get(`/api/community/posts/${props.postId}/comments`, (_message: string, d: any) => {
    comments.value = (d || []).sort((a: any, b: any) => {
      const timeA = Number(a.createTime || a.create_time || a.createdAt || 0)
      const timeB = Number(b.createTime || b.create_time || b.createdAt || 0)
      return timeA - timeB
    })
  })
}

const submit = () => {
  if (!props.postId) return
  if (!form.value.content.trim()) return
  post(`/api/community/posts/${props.postId}/comments`, form.value, () => {
    form.value.content = ''
    load()
  })
}

const formatTime = (value: any) => {
  if (!value) return ''
  const timestamp = typeof value === 'number' ? value : Number(value)
  const date = new Date(timestamp)
  if (Number.isNaN(date.getTime())) return String(value)
  const pad = (num: number) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

onMounted(load)
</script>

<style scoped>
.comment-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.reply-card,
.comment-card {
  border-radius: 14px;
  border: 1px solid #ebeef5;
  background: linear-gradient(180deg, #ffffff 0%, #fcfcfd 100%);
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.05);
}

.reply-card {
  padding: 4px;
}

.reply-form :deep(.el-textarea__inner) {
  border-radius: 12px;
  resize: none;
  min-height: 88px;
}

.reply-actions {
  margin-bottom: 0;
  display: flex;
  justify-content: flex-end;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-card {
  padding: 12px 16px;
}

.comment-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: #1f2937;
}

.comment-time {
  font-size: 12px;
  color: #94a3b8;
  white-space: nowrap;
}

.comment-content {
  color: #374151;
  line-height: 1.7;
  white-space: pre-wrap;
}

.empty-state {
  text-align: center;
  color: #94a3b8;
  padding: 20px 0;
  font-size: 14px;
}
</style>
