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
 * 前后端协同注释（ResourceReviewList）
 * - 列表接口：GET /api/community/reviews?resourceId=... 返回该资源下的评价数组（非分页）；
 * - 点赞接口：POST /api/community/reviews/{id}/like，前端在成功后应本地同步 `likes` 与 `liked` 字段以优化 UX；
 * - 插入新评价：父组件在收到 `saved` 事件时调用组件的 `addReview` 方法以实现即时回显，同时后台应保证最终一致性。
 */

const props = defineProps<{ resourceId: string }>()
const reviews = ref<any[]>([])
const userStore = useUserStore()

const load = () => {
  get(`/api/community/reviews?resourceId=${encodeURIComponent(props.resourceId)}`, (_message: string, d: any) => {
    // 后端返回后统一补 liked 字段，方便前端直接控制点赞状态。
    reviews.value = (d || []).map((it: any) => ({ ...it, liked: !!it.liked }))
  })
}

const addReview = (r: any) => {
  if (!r) return
  // 新评价插到最前面，保证前端提交后立刻可见且和后端时间线一致。
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
  // 点赞接口只负责后端累加，成功后前端本地同步 likes 与 liked。
  post(`/api/community/reviews/${r.id}/like`, {}, () => {
    r.likes = (r.likes || 0) + 1
    r.liked = true
  })
}

const getInitial = (name: any) => {
  // 头像首字母兜底，避免匿名或空用户名在前端显示异常。
  const text = String(name || 'A').trim()
  return text ? text.slice(0, 1).toUpperCase() : 'A'
}

const formatDate = (s: any) => {
  // 统一将后端时间格式化为本地可读字符串。
  try { return new Date(s).toLocaleString() } catch (e) { return '' }
}

// 组件挂载后先拉取当前资源评价列表，保持和后端最新数据同步。
onMounted(load)

// 暴露刷新与新增接口，供父组件在提交评价后联动更新列表。
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
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-item {
  border-radius: 14px;
  border: 1px solid #edf2f7;
  background: linear-gradient(180deg, #ffffff 0%, #fcfdff 100%);
}

.review-item-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.review-user {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.review-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.review-name-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.review-name {
  font-weight: 600;
  color: #1f2937;
}

.review-date {
  color: #9ca3af;
  font-size: 12px;
}

.review-content {
  margin-top: 14px;
  line-height: 1.7;
  color: #374151;
  background: #f8fafc;
  border-radius: 12px;
  padding: 14px 16px;
  white-space: pre-wrap;
}

.like-btn {
  color: #409eff;
}

@media (max-width: 640px) {

  .review-list-head,
  .review-item-top {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
