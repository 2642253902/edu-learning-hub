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
          <el-select v-model="model.courseId" placeholder="请选择课程" :disabled="courseLocked" class="w-full" filterable>
            <el-option v-for="course in courses" :key="course.id" :label="course.courseName" :value="course.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="资源名称" prop="resourceName">
          <el-input v-model="model.resourceName" placeholder="请输入资源名称" />
        </el-form-item>

        <el-form-item label="资源文件" prop="resourceUrl">
          <el-upload class="upload-demo" :action="uploadUrl" :limit="1" :on-success="handleUploadSuccess"
            :on-error="handleUploadError" :on-remove="handleRemove" :on-preview="handlePreview" :with-credentials="true"
            :file-list="fileList" :accept="allowedFileTypes" :before-upload="beforeUpload">
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
import axios from 'axios'

const visible = ref(false)
const title = ref('')
const disabled = ref(false)
const loading = ref(false)
const courseLocked = ref(false)
const formRef = ref()
const courses = ref<any[]>([])
const fileList = ref<any[]>([])

// 拼接上传路径：沿用 axios 全局 baseURL，避免硬编码环境域名
const uploadUrl = computed(() => {
  return `${axios.defaults.baseURL}/upload/file`
})

const allowedFileTypes = computed(() => {
  // 1-视频 2-讲义 3-资料；资料放开为 * 以兼容多格式参考文件
  const type = String(model.resourceType)
  if (type === '1') return '.mp4'
  if (type === '2') return '.pdf,.doc,.docx'
  return '*'
})

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

const fetchCourses = () => {
  get('/study/cloudComputingCourse/list?pageNo=1&pageSize=1000', (msg, data: any) => {
    courses.value = data.records || []
  })
}

// 新增模式：可接受父组件透传的默认 courseId/resourceType，减少重复选择
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
  // 编辑态回填上传列表，保证用户能看到当前已绑定文件
  const fileName = record.resourceUrl ? record.resourceUrl.split('/').pop() : '已上传文件'
  fileList.value = record.resourceUrl ? [{ name: fileName, url: record.resourceUrl }] : []
  visible.value = true
}

const beforeUpload = (file: any) => {
  const fileName = file.name.toLowerCase()
  const type = String(model.resourceType)

  if (type === '1') {
    if (!fileName.endsWith('.mp4')) {
      ElMessage.warning('视频仅支持 mp4 格式')
      return false
    }
  } else if (type === '2') {
    if (!fileName.endsWith('.pdf') && !fileName.endsWith('.doc') && !fileName.endsWith('.docx')) {
      ElMessage.warning('讲义支持 pdf/doc/docx 格式')
      return false
    }
  }
  return true
}

const handleUploadSuccess = (response: any) => {
  console.log('上传原始响应:', response)

  let url = ''
  // 1. 尝试直接从 data 字段获取
  if (response.data) {
    url = response.data
  }
  // 2. 兼容历史返回：如果 data 为空，尝试从 message 中提取文件名
  // 匹配格式: "上传成功: xxx.mp4"
  else if (response.message && response.message.includes('上传成功:')) {
    url = response.message.split('上传成功:')[1].trim()
  }

  if (url) {
    model.resourceUrl = url
    // 同步更新 fileList 以便在 UI 上显示
    fileList.value = [{ name: url.split('/').pop() || '新上传文件', url: url }]
    ElMessage.success('上传成功')

    // 编辑态上传成功后立即同步数据库，避免“上传成功但未保存”造成脏数据
    if (model.id) {
      console.log('检测到编辑模式，正在自动同步数据库...')
      const syncUrl = '/study/cloudComputingCourseResource/edit'
      post(syncUrl, model, () => {
        console.log('数据库记录已自动更新')
        emit('ok') // 通知父页面刷新列表（如有必要）
      })
    }
  } else {
    ElMessage.error('上传成功但未解析到文件路径，请检查后端返回格式')
  }
}

const handleUploadError = (err: any) => {
  console.error('上传失败详情:', err)
  ElMessage.error('文件上传网络异常')
}

const handleRemove = (file: any) => {
  const fileName = model.resourceUrl
  if (!fileName) return

  // 适配 RequestParam 接口: /upload/delete?fileName=xxx
  const deleteUrl = `${axios.defaults.baseURL}/upload/delete`
  // 注意：后端使用 @RequestParam，因此 fileName 必须走 params
  axios.delete(deleteUrl, {
    params: { fileName: fileName },
    withCredentials: true
  }).then(response => {
    // 兼容不同的返回格式
    const res = response.data
    if (res.success || res.status === 200) {
      ElMessage.success('文件已从服务器删除')
      model.resourceUrl = ''
      fileList.value = []
      // 删除物理文件后，同步清空数据库中的 resourceUrl 字段
      if (model.id) {
        post('/study/cloudComputingCourseResource/edit', model, () => {
          emit('ok')
        })
      }
    } else {
      ElMessage.warning(res.message || '删除失败')
    }
  }).catch(err => {
    console.error('删除文件出错:', err)
    ElMessage.error('文件删除失败，请检查网络或权限')
  })
}

const handlePreview = (file: any) => {
  const fileName = model.resourceUrl
  if (!fileName) return
  // 适配 RequestParam 接口: /upload/download?fileName=xxx
  const downloadUrl = `${axios.defaults.baseURL}/upload/download?fileName=${encodeURIComponent(fileName)}`
  window.open(downloadUrl, '_blank')
}

const handleCancel = () => {
  visible.value = false
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      // 根据是否存在 id 自动切换新增/编辑接口
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
.w-full {
  width: 100%;
}

.upload-demo {
  margin-top: 5px;
  width: 100%;
}

/* 限制上传文件列表的宽度，防止长文件名撑开弹窗 */
:deep(.el-upload-list) {
  max-width: 100%;
}

:deep(.el-upload-list__item) {
  margin-bottom: 8px;
}

/* 限制文件名显示区域，超出部分省略号 */
:deep(.el-upload-list__item-file-name) {
  display: inline-block;
  max-width: 80%;
  /* 给删除按钮留出空间 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
}
</style>
