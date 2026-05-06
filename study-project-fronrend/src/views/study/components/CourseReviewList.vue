<template>
  <div class="review-list-shell">
    <div class="review-list-head">
      <div>
        <div class="review-list-kicker">评价记录</div>
        <div class="review-list-heading">看看大家怎么说</div>
      </div>
      <span class="review-list-tip">共 {{ reviews.length }} 条</span>
    </div>

    <el-empty v-if="reviews.length === 0" description="暂无评价，快来发表第一条评价吧" />

    <div v-else class="review-list">
      <el-card v-for="r in reviews" :key="r.id" shadow="never" class="review-item">
        <div class="review-item-top">
          <div class="review-user">
            <el-avatar :size="38">{{ getInitial(r.username) }}</el-avatar>
            <div class="review-meta">
              <div class="review-name-row">
                <span class="review-name">{{ r.username || '匿名用户' }}</span>
                <span class="review-date">{{ formatDate(r.createTime) }}</span>
              </div>
              <el-rate :model-value="Number(r.rating || 0)" disabled size="small" />
            </div>
          </div>

          <el-button text type="primary" class="like-btn" @click="like(r)"
            :disabled="String(r.userId) === String(userStore.auth.user?.id) || r.liked">
            <template v-if="r.liked">已点赞 {{ r.likes || 0 }}</template>
            <template v-else-if="String(r.userId) === String(userStore.auth.user?.id)">不能点赞自己</template>
            <template v-else>点赞 {{ r.likes || 0 }}</template>
          </el-button>
        </div>

        <div class="review-content">{{ r.content || '用户未填写评价内容' }}</div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get, post } from '@/net'
import { useUserStore } from '@/stores/user'

/**
 * 前后端协同注释（CourseReviewList）
 * - 列表接口：GET /api/study/reviews?courseId=... 返回该课程下的评价数组（非分页）；
 * - 点赞接口：POST /api/study/reviews/{id}/like，前端在成功后应本地同步 `likes` 与 `liked` 字段以优化 UX；
 * - 插入新评价：父组件在收到 `saved` 事件时调用组件的 `addReview` 方法以实现即时回显，同时后台应保证最终一致性。
 */

const props = defineProps<{ courseId: string }>()
const reviews = ref<any[]>([])
const userStore = useUserStore()

const load = () => {
  get(`/api/study/reviews?courseId=${encodeURIComponent(props.courseId)}`, (_message: string, d: any) => {
    reviews.value = (d || []).map((it: any) => ({ ...it, liked: !!it.liked }))
  })
}

const addReview = (r: any) => {
  if (!r) return
  const item = {
    id: r.id || `local-${Date.now()}`,
    username: r.username || userStore.auth.user?.username || '我',
    createTime: r.createTime || new Date().toISOString(),
    rating: r.rating || 5,
    content: r.content || '',
    likes: r.likes || 0,
    userId: r.userId || userStore.auth.user?.id,
    liked: false
  }
  reviews.value.unshift(item)
}

const like = (r: any) => {
  if (String(r.userId) === String(userStore.auth.user?.id)) return
  if (r.liked) return
  post(`/api/study/reviews/${r.id}/like`, {}, () => {
    r.likes = (r.likes || 0) + 1
    r.liked = true
  })
}

const getInitial = (name: any) => {
  const text = String(name || 'A').trim()
  return text ? text.slice(0, 1).toUpperCase() : 'A'
}

const formatDate = (s: any) => {
  try { return new Date(s).toLocaleString() } catch (e) { return '' }
}

onMounted(load)

defineExpose({ load, addReview })
</script>

<style scoped>
.review-list-shell {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.review-list-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 12px;
}

.review-list-kicker {
  font-size: 12px;
  color: #8c8c8c;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 4px;
}

.review-list-heading {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.review-list-tip {
  color: #8c8c8c;
  font-size: 12px;
}

.review-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.review-item {
  border-radius: 14px;
  border: 1px solid #e8eef7;
}

.review-item-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.review-user {
  display: flex;
  gap: 12px;
  align-items: center;
}

.review-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.review-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.review-name {
  font-weight: 700;
  color: #111827;
}

.review-date {
  font-size: 12px;
  color: #6b7280;
}

.review-content {
  margin-top: 12px;
  color: #374151;
  line-height: 1.7;
  white-space: pre-wrap;
}

@media (max-width: 640px) {
  .review-list-head,
  .review-item-top {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
