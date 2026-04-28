<template>
  <el-card>
    <el-form :model="form" label-width="60px">
      <el-form-item label="评分">
        <el-rate v-model="form.rating" :allow-half="false" :max="5" />
      </el-form-item>
      <el-form-item label="评论">
        <el-input type="textarea" v-model="form.content" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { communityApi } from '@/net'

const props = defineProps<{ resourceId: string }>()
const emit = defineEmits(['saved'])

const form = ref({ rating: 5, content: '' })

const submit = () => {
  communityApi.createReview({ resourceId: props.resourceId, rating: form.value.rating, content: form.value.content }, (d) => {
    emit('saved', d)
    form.value.content = ''
  })
}
</script>
