<template>
  <div class="review-form-shell">
    <div class="review-form-title">
      <div>
        <div class="review-form-kicker">发表评价</div>
        <div class="review-form-heading">说说你对这门课程的感受</div>
      </div>
      <span class="review-form-tip">支持 1 - 5 星评分</span>
    </div>

    <el-form :model="form" label-position="top" class="review-form">
      <el-form-item label="课程评分">
        <el-rate v-model="form.rating" :allow-half="false" :max="5" />
      </el-form-item>
      <el-form-item label="评价内容">
        <el-input v-model="form.content" type="textarea" :rows="4" maxlength="200" show-word-limit
          placeholder="分享你的学习体验、建议或想补充的内容" />
      </el-form-item>
      <div class="review-form-actions">
        <el-button @click="resetForm">清空</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">提交评价</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
/**
 * CourseReviewForm.vue
 * 前后端协同注释：
 * - 目的：为某门课程提交用户评价（评分 + 文本），并在提交成功后通过 `saved` 事件回传后端返回的评价对象便于列表立刻回显。
 * - 后端接口：POST /api/study/reviews
 *   - 请求体示例：{ courseId: string, rating: number, content: string }
 *   - 期望返回：创建的评价对象（至少包含 `id` 字段）。后端返回完整对象时前端直接使用；若后端仅返回成功消息，前端会构造临时回显对象（id 前缀为 local-）以提升 UX。
 * - 前端约定：回显对象字段为 { id, courseId, rating, content, userId?, username?, createTime?, likes? }
 * - 事件：`saved` — payload 为上面约定的评价对象，父组件接收后应将其插入到当前课程的评价列表并视需要触发服务端分页刷新。
 * - 校验：前端做最小校验（courseId 必须、content 非空）；后端仍需做完整的业务校验与鉴权（用户必须登录）。
 */

import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { post } from '@/net'
import { useUserStore } from '@/stores/user'

const props = defineProps<{ courseId: string }>()
const emit = defineEmits<{
  (e: 'saved', review: Review): void
}>()

interface Review {
  id: string | number
  courseId: string
  rating: number
  content: string
  userId?: string | number
  username?: string
  createTime?: string
  likes?: number
}

const form = ref<{ rating: number; content: string }>({ rating: 5, content: '' })
const submitting = ref(false)

const resetForm = () => {
  form.value.rating = 5
  form.value.content = ''
}

const submit = () => {
  if (!props.courseId) {
    ElMessage.warning('课程信息未加载完成')
    return
  }

  if (!form.value.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }

  const userStore = useUserStore()
  const payload = { courseId: props.courseId, rating: form.value.rating, content: form.value.content.trim() }
  submitting.value = true
  post(
    '/api/study/reviews',
    payload,
    (_message: string, d: any) => {
      const review: Review = (d && d.id)
        ? d
        : {
          id: `local-${Date.now()}`,
          courseId: payload.courseId,
          rating: payload.rating,
          content: payload.content,
          userId: userStore.auth.user?.id,
          username: userStore.auth.user?.username,
          createTime: new Date().toISOString(),
          likes: 0
        }
      emit('saved', review)
      submitting.value = false
      resetForm()
      ElMessage.success('评价已提交')
    },
    (message) => {
      submitting.value = false
      ElMessage.error(message || '提交失败')
    }
  )
}
</script>

<style scoped>
.review-form-shell {
  padding: 18px;
  border-radius: 16px;
  background: linear-gradient(180deg, #f9fbff 0%, #ffffff 100%);
  border: 1px solid #e8eef7;
}

.review-form-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.review-form-kicker {
  font-size: 12px;
  color: #8c8c8c;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  margin-bottom: 4px;
}

.review-form-heading {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.review-form-tip {
  font-size: 12px;
  color: #6b7280;
  white-space: nowrap;
}

.review-form :deep(.el-form-item__label) {
  color: #374151;
  font-weight: 600;
}

.review-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 640px) {
  .review-form-title {
    align-items: flex-start;
    flex-direction: column;
  }

  .review-form-actions {
    flex-direction: column;
  }

  .review-form-actions .el-button {
    width: 100%;
  }
}
</style>
