<template>
  <div>
    <el-form ref="formRef" :model="model" :rules="validatorRules" label-width="120px" class="p-4" :disabled="props.disabled">
      <el-row>
        <el-col :span="24">
          <el-form-item label="分类名称" prop="courseTypeName">
            <el-input v-model="model.courseTypeName" placeholder="请输入课程分类名称" maxlength="50" show-word-limit />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { post } from '@/net'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

interface CategoryFormModel {
  id: string
  courseTypeName: string
}

const props = withDefaults(defineProps<{ disabled?: boolean }>(), {
  disabled: false
})

const emit = defineEmits(['ok'])
const formRef = ref<FormInstance>()
const confirmLoading = ref(false)

const model = reactive<CategoryFormModel>({
  id: '',
  courseTypeName: ''
})

const validatorRules: FormRules = {
  courseTypeName: [
    { required: true, message: '分类名称不能为空', trigger: 'blur' },
    { min: 2, max: 50, message: '长度应在 2 到 50 个字符之间', trigger: 'blur' }
  ]
}

// 新增时重置表单数据。
const add = () => {
  Object.assign(model, { id: '', courseTypeName: '' })
}

const edit = (record: Partial<CategoryFormModel>) => {
  Object.assign(model, record)
}

const submitForm = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  confirmLoading.value = true
  const url = model.id ? '/study/cloudComputingCourseType/edit' : '/study/cloudComputingCourseType/add'
  post(url, model, (msg) => {
    ElMessage.success(msg)
    emit('ok')
    confirmLoading.value = false
  }, (failMsg) => {
    ElMessage.warning(failMsg)
    confirmLoading.value = false
  })
}

defineExpose({ add, edit, submitForm })
</script>
