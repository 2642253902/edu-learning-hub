<template>
  <div class="post-form-wrap">
    <el-form :model="form" class="post-form">
      <el-form-item label="标题" class="field-item">
        <el-input v-model="form.title" placeholder="写一个吸引人的标题" />
      </el-form-item>
      <el-form-item label="内容" class="field-item">
        <el-input v-model="form.content" type="textarea" :rows="4" placeholder="分享你的问题、经验或者想法" />
      </el-form-item>
      <el-form-item class="submit-row">
        <el-button type="primary" @click="submit">发布</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { post } from '@/net'

const props = defineProps<{ groupId: string }>()
const emit = defineEmits(['created'])

const form = ref({ title: '', content: '', groupId: props.groupId })

const submit = () => {
  post('/api/community/posts', form.value, (_message: string, d: any) => {
    emit('created', d)
    form.value.title = ''
    form.value.content = ''
  })
}
</script>

<style scoped>
.post-form-wrap {
  padding: 4px 0;
}

.post-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
}

.post-form :deep(.el-input__inner),
.post-form :deep(.el-textarea__inner) {
  border-radius: 12px;
}

.field-item {
  margin-bottom: 18px;
}

.submit-row {
  margin-bottom: 0;
  display: flex;
  justify-content: flex-end;
}
</style>
