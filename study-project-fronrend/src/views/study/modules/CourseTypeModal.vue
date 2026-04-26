<template>
  <div>
    <el-dialog v-model="visible" :title="title" width="600px" @close="handleCancel">
    <!-- 详情模式：显示核心分类信息 -->
    <div v-if="disabled" class="detail-container">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="分类名称">{{ model.courseTypeName }}</el-descriptions-item>
        <el-descriptions-item label="包含课程数量">{{ model.courseCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="系统标识 ID">{{ model.id }}</el-descriptions-item>
        <el-descriptions-item label="创建日期">{{ model.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- 编辑/新增模式：显示表单 -->
    <el-form v-else ref="formRef" :model="model" :rules="rules" label-width="100px">
      <el-form-item label="分类名称" prop="courseTypeName">
        <el-input v-model="model.courseTypeName" placeholder="请输入课程分类名称" maxlength="50" show-word-limit />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">{{ disabled ? '返回' : '取消' }}</el-button>
        <el-button v-if="!disabled" type="primary" :loading="loading" @click="handleSubmit">提交</el-button>
      </div>
    </template>
  </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { post } from '@/net'
import { ElMessage } from 'element-plus'

const visible = ref(false)
const title = ref('')
const disabled = ref(false)
const loading = ref(false)
const formRef = ref()
const model = reactive<any>({
  id: '',
  courseTypeName: '',
  courseCount: 0,
  createTime: ''
})

const emit = defineEmits(['ok'])

const rules = {
  courseTypeName: [
    { required: true, message: '分类名称不能为空', trigger: 'blur' },
    { min: 2, max: 20, message: '长度应在 2 到 20 个字符之间', trigger: 'blur' }
  ]
}

const add = () => {
  title.value = '新增课程分类'
  disabled.value = false
  Object.assign(model, { id: '', courseTypeName: '', courseCount: 0, createTime: '' })
  visible.value = true
}

const edit = (record: any) => {
  title.value = '编辑课程分类'
  disabled.value = false
  Object.assign(model, record)
  visible.value = true
}

const detail = (record: any) => {
  title.value = '分类详情'
  disabled.value = true
  Object.assign(model, record)
  visible.value = true
}

const handleCancel = () => {
  visible.value = false
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      const url = model.id ? '/study/cloudComputingCourseType/edit' : '/study/cloudComputingCourseType/add'
      post(url, model, (msg) => {
        ElMessage.success(msg)
        visible.value = false
        emit('ok')
        loading.value = false
      }, (fail) => {
        ElMessage.warning(fail)
        loading.value = false
      })
    }
  })
}

defineExpose({ add, edit, detail })
</script>

<style scoped>
.detail-container {
  padding: 20px;
}
</style>
