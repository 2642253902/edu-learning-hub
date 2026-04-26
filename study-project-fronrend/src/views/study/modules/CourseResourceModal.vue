<template>
  <div>
    <el-dialog v-model="visible" :title="title" width="600px" @close="handleCancel">
      <el-form ref="formRef" :model="model" :rules="rules" label-width="120px" :disabled="disabled">
        <el-form-item label="内容类型" prop="resourceType">
          <el-select v-model="model.resourceType" placeholder="请选择资源类型" :disabled="courseLocked" class="w-full">
            <el-option label="视频" :value="1" />
            <el-option label="讲义" :value="2" />
            <el-option label="参考资料" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属课程" prop="courseId">
          <el-select 
            v-model="model.courseId" 
            placeholder="请选择课程" 
            :disabled="courseLocked"
            class="w-full"
            filterable
          >
            <el-option 
              v-for="course in courses" 
              :key="course.id" 
              :label="course.courseName" 
              :value="course.id" 
            />
          </el-select>
        </el-form-item>

        <el-form-item label="资源名称" prop="resourceName">
          <el-input v-model="model.resourceName" placeholder="请输入资源名称" />
        </el-form-item>

        <el-form-item label="资源文件" prop="resourceUrl">
          <el-upload
            class="upload-demo"
            action="/api/study/cloudComputingCourseResource/upload"
            :limit="1"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :file-list="fileList"
            :accept="allowedFileTypes"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                当前支持: {{ allowedFileTypes }}
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="排序" prop="resourceSort">
          <el-input-number v-model="model.resourceSort" :min="1" class="w-full" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button v-if="!disabled" type="primary" :loading="loading" @click="handleSubmit">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { post, get } from '@/net'
import { ElMessage } from 'element-plus'

const visible = ref(false)
const title = ref('')
const disabled = ref(false)
const loading = ref(false)
const courseLocked = ref(false)
const formRef = ref()
const courses = ref<any[]>([])
const fileList = ref<any[]>([])

const model = reactive<any>({
  id: '',
  resourceType: '',
  courseId: '',
  resourceName: '',
  resourceUrl: '',
  resourceSort: 1
})

const rules = {
  resourceType: [{ required: true, message: '请选择资源类型', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  resourceName: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
  resourceUrl: [{ required: true, message: '请上传资源文件', trigger: 'change' }]
}

const emit = defineEmits(['ok'])

const allowedFileTypes = computed(() => {
  switch (model.resourceType) {
    case 1: return '.mp4,.avi,.mov'
    case 2: return '.pdf,.pptx'
    case 3: return '.pdf,.doc,.docx,.zip,.rar'
    default: return '.pdf,.doc,.docx,.mp4,.zip'
  }
})

const fetchCourses = () => {
  get('/study/cloudComputingCourse/list?pageNo=1&pageSize=1000', (msg, data: any) => {
    courses.value = data.records || []
  })
}

const add = (defaults?: any) => {
  title.value = '添加资源'
  disabled.value = false
  courseLocked.value = !!(defaults && defaults.courseId)
  Object.assign(model, {
    id: '',
    resourceType: '',
    courseId: '',
    resourceName: '',
    resourceUrl: '',
    resourceSort: 1
  }, defaults)
  fileList.value = []
  visible.value = true
}

const edit = (record: any) => {
  title.value = '编辑资源'
  disabled.value = false
  courseLocked.value = false
  Object.assign(model, record)
  fileList.value = record.resourceUrl ? [{ name: '已上传文件', url: record.resourceUrl }] : []
  visible.value = true
}

const handleUploadSuccess = (response: any) => {
  model.resourceUrl = response.url || response.result
  ElMessage.success('上传成功')
}

const handleRemove = () => {
  model.resourceUrl = ''
}

const handleCancel = () => {
  visible.value = false
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      const url = model.id ? '/study/cloudComputingCourseResource/edit' : '/study/cloudComputingCourseResource/add'
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

onMounted(() => {
  fetchCourses()
})

defineExpose({ add, edit })
</script>

<style scoped>
.w-full { width: 100%; }
.upload-demo { margin-top: 5px; }
</style>
