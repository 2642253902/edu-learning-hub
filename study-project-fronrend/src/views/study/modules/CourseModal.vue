<template>
  <div>
    <el-dialog v-model="visible" :title="title" :width="800" @close="handleCancel" destroy-on-close>
      <CourseForm ref="realForm" :disabled="disableSubmit" @ok="submitCallback" />
      <template #footer>
        <el-button @click="handleCancel">取消</el-button>
        <el-button v-if="!disableSubmit" type="primary" @click="handleOk" :loading="confirmLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import CourseForm from './CourseForm.vue'

const visible = ref(false)
const title = ref('')
const disableSubmit = ref(false)
const confirmLoading = ref(false)
const realForm = ref()

const emit = defineEmits(['ok'])

const add = () => {
  title.value = '新增课程'
  visible.value = true
  disableSubmit.value = false
  nextTick(() => realForm.value.add())
}

const edit = (record: any) => {
  title.value = '编辑课程'
  visible.value = true
  disableSubmit.value = false
  nextTick(() => realForm.value.edit(record))
}

const detail = (record: any) => {
  title.value = '课程详情'
  visible.value = true
  disableSubmit.value = true
  nextTick(() => realForm.value.edit(record))
}

const handleOk = () => {
  realForm.value.submitForm()
}

const handleCancel = () => {
  visible.value = false
}

const submitCallback = () => {
  visible.value = false
  emit('ok')
}

defineExpose({ add, edit, detail })
</script>
