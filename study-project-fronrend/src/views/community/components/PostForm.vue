<template>
  <div class="post-form-wrap">
    <el-form ref="formRef" :model="form" :rules="rules" class="post-form">
      <el-form-item label="标题" prop="title" class="field-item">
        <el-input v-model="form.title" placeholder="写一个吸引人的标题" />
      </el-form-item>
      <el-form-item label="内容" prop="content" class="field-item">
        <el-input v-model="form.content" type="textarea" :rows="4" placeholder="分享你的问题、经验或者想法" />
      </el-form-item>
      <el-form-item class="submit-row">
        <el-button type="primary" @click="submit">发布</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { post } from '@/net'

/**
 * 前后端协同注释（帖子发布表单）
 * - 作用：复用在小组详情页和公共讨论页，唯一差别是 `groupId`，空值代表公共讨论，有值代表某个小组。
 * - 接口：POST /api/community/posts，后端应返回创建后的帖子对象，父组件据此刷新列表或打开详情。
 * - 事件：`created` 由父组件接收，通常用于重新拉取帖子列表，确保刚发布的内容立刻可见。
 */

const props = defineProps<{ groupId: string }>()
const emit = defineEmits(['created'])

const formRef = ref<FormInstance>()
const form = reactive({ title: '', content: '', groupId: props.groupId })

const rules: FormRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度为 2-100 个字符', trigger: ['blur', 'change'] }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 2, max: 5000, message: '内容不能为空', trigger: ['blur', 'change'] }
  ]
}

const submit = () => {
  formRef.value?.validate((valid: boolean) => {
    if (!valid) {
      ElMessage.warning('请先填写标题和内容')
      return
    }

    post('/api/community/posts', form, (_message: string, d: any) => {
      emit('created', d)
      form.title = ''
      form.content = ''
    })
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
