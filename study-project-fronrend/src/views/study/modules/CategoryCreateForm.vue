<template>
  <div>
    <el-form ref="formRef" :model="model" :rules="validatorRules" label-width="120px" class="p-4"
      :disabled="props.disabled">
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

/**
 * 前后端协同注释（分类创建表单）
 * - 目的：封装课程分类的新增/编辑表单，字段 `courseTypeName` 对应后端 DTO。
 * - 提交接口：POST /study/cloudComputingCourseType/add 或 /edit，成功后父组件应刷新分类列表。
 * - 校验：前端做最小长度/必填校验，后端仍需做完整校验与重复性检查。
 */

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
    // 分类名称是新增/编辑的唯一核心字段
    { required: true, message: '分类名称不能为空', trigger: 'blur' },
    { min: 2, max: 50, message: '长度应在 2 到 50 个字符之间', trigger: 'blur' }
  ]
}

// 新增时重置表单数据。
const add = () => {
  // 保证新增态不会复用上一次编辑残留的数据
  Object.assign(model, { id: '', courseTypeName: '' })
}

const edit = (record: Partial<CategoryFormModel>) => {
  // 编辑态直接把当前记录灌入表单，减少额外转换逻辑
  Object.assign(model, record)
}

const submitForm = async () => {
  if (!formRef.value) return
  // 先做前端校验，再决定走新增还是编辑接口
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  confirmLoading.value = true
  const url = model.id ? '/study/cloudComputingCourseType/edit' : '/study/cloudComputingCourseType/add'
  post(url, model, (msg) => {
    // 成功后通知父组件刷新列表
    ElMessage.success(msg)
    emit('ok')
    confirmLoading.value = false
  }, (failMsg) => {
    // 失败时也要释放 loading，避免按钮一直转圈
    ElMessage.warning(failMsg)
    confirmLoading.value = false
  })
}

defineExpose({ add, edit, submitForm })
</script>
