<template>
  <div>
    <el-form ref="formRef" :model="model" :rules="validatorRules" label-width="120px" class="p-4" :disabled="disabled">
      <el-row>
        <el-col :span="24">
          <el-form-item label="课程名称" prop="courseName">
            <el-input v-model="model.courseName" placeholder="请输入课程名称" />
          </el-form-item>
        </el-col>

        <el-col :span="24">
          <el-form-item label="课程分类" prop="courseTypeId">
            <el-select v-model="model.courseTypeId" placeholder="请选择课程分类" class="w-full">
              <el-option v-for="type in model.courseTypes" :key="type.id" :label="type.courseTypeName"
                :value="String(type.id)" />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :span="24">
          <el-form-item label="课程标签" prop="courseTag">
            <el-input v-model="model.courseTag" placeholder="请输入课程标签，如：必修、选修" />
          </el-form-item>
        </el-col>

        <el-col :span="24">
          <el-form-item label="教师" prop="teacherId">
            <!-- <el-input v-model="model.teacherId" placeholder="教师" /> -->
            <el-select v-model="model.teacherId" placeholder="请选择教师" class="w-full">
              <el-option v-for="teacher in model.teachers" :key="teacher.id" :label="getTeacherLabel(teacher)"
                :value="String(teacher.id)" />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="总课时" prop="courseHours">
            <el-input-number v-model="model.courseHours" :min="0" class="w-full" />
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="课程状态" prop="courseStatus">
            <el-radio-group v-model="model.courseStatus">
              <el-radio :value="1">启用</el-radio>
              <el-radio :value="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { get, post } from '@/net'
import { ElMessage, type FormInstance } from 'element-plus'

interface OptionItem {
  id: string | number
  courseTypeName?: string
  realname?: string
  username?: string
  name?: string
}

interface CourseFormModel {
  id: string
  courseName: string
  courseTypeId: string
  courseTag: string
  teacherId: string
  courseHours: number
  courseStatus: number
  courseTypes: OptionItem[]
  teachers: OptionItem[]
}

const props = defineProps({
  disabled: { type: Boolean, default: false }
})

const emit = defineEmits(['ok'])
const formRef = ref<FormInstance>()
const confirmLoading = ref(false)

const model = reactive<CourseFormModel>({
  id: '',
  courseName: '',
  courseTypeId: '',
  courseTag: '',
  teacherId: '',
  courseHours: 0,
  courseStatus: 1,
  courseTypes: [],
  teachers: []
})

const validatorRules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseTypeId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  courseStatus: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const add = () => {
  Object.assign(model, {
    id: '',
    courseName: '',
    courseTypeId: '',
    courseTag: '',
    teacherId: '',
    courseStatus: 1,
    courseHours: 0
  })
}

const edit = (record: any) => {
  Object.assign(model, {
    ...record,
    courseTypeId: record?.courseTypeId ? String(record.courseTypeId) : '',
    teacherId: record?.teacherId ? String(record.teacherId) : ''
  })
}

const unwrapListData = (payload: any): any[] => {
  if (Array.isArray(payload)) return payload
  if (Array.isArray(payload?.records)) return payload.records
  if (Array.isArray(payload?.list)) return payload.list
  return []
}

const loadCourseTypes = () => {
  get('/study/cloudComputingCourseType/list', (msg, data) => {
    model.courseTypes = unwrapListData(data)
  }, () => { }, () => {
  })
}

const loadTeachers = () => {
  get('/api/user/list/teachers', (msg, data) => {
    model.teachers = unwrapListData(data)
  }, () => { }, () => {
  })
}

const getTeacherLabel = (teacher: OptionItem) => {
  return teacher.realname || teacher.username || teacher.name || '-'
}

loadCourseTypes()
loadTeachers()



const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid: boolean) => {
    if (valid) {
      confirmLoading.value = true
      const url = model.id ? '/study/cloudComputingCourse/edit' : '/study/cloudComputingCourse/add'
      post(url, model, (msg) => {
        ElMessage.success(msg)
        emit('ok')
        confirmLoading.value = false
      }, (failMsg) => {
        ElMessage.warning(failMsg)
        confirmLoading.value = false
      })
    }
  })
}

defineExpose({ add, edit, submitForm })
</script>
