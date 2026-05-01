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
  // 新增模式：打开可编辑弹窗并让子表单重置为新增态
  title.value = '新增课程'
  visible.value = true
  disableSubmit.value = false
  nextTick(() => realForm.value.add())
}

const edit = (record: any) => {
  // 编辑模式：把选中记录交给表单组件回填
  title.value = '编辑课程'
  visible.value = true
  disableSubmit.value = false
  nextTick(() => realForm.value.edit(record))
}

const detail = (record: any) => {
  // 详情模式：复用同一弹窗，但禁止提交，只读展示数据
  title.value = '课程详情'
  visible.value = true
  disableSubmit.value = true
  nextTick(() => realForm.value.edit(record))
}

const handleOk = () => {
  // 由子表单负责校验和提交，弹窗只负责触发
  realForm.value.submitForm()
}

const handleCancel = () => {
  // 关闭弹窗即可，具体表单状态由子组件管理
  visible.value = false
}

const submitCallback = () => {
  // 子表单提交成功后关闭弹窗并通知外层刷新
  visible.value = false
  emit('ok')
}

defineExpose({ add, edit, detail })
</script>
