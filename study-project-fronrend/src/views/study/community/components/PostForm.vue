<template>
  <div>
    <el-form :model="form">
      <el-form-item label="标题">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="内容">
        <el-input type="textarea" v-model="form.content" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">发布</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { communityApi } from '@/net'

const props = defineProps<{ groupId: string }>()
const emit = defineEmits(['created'])

const form = ref({ title: '', content: '', groupId: props.groupId })

const submit = () => {
  communityApi.createPost(form.value, (d) => {
    emit('created', d)
    form.value.title = ''
    form.value.content = ''
  })
}
</script>
