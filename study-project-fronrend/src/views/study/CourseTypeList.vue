<template>
  <div>
    <el-card shadow="never">
      <!-- 查询区域 -->
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="课程分类名称">
          <el-input v-model="queryParam.courseTypeName" placeholder="请输入课程分类名称" @keyup.enter="searchQuery" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchQuery" icon="Search">查询</el-button>
          <el-button @click="searchReset" icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮区域 -->
      <div style="margin-bottom: 16px;">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增</el-button>
      </div>

      <!-- table区域-begin -->
      <el-table :data="dataSource" v-loading="loading" border style="width: 100%" @selection-change="onSelectChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column prop="courseTypeName" label="课程分类名称" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-popconfirm title="确定删除吗?" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination v-model:current-page="ipagination.current" v-model:page-size="ipagination.pageSize"
          :page-sizes="[10, 20, 30]" layout="total, sizes, prev, pager, next, jumper" :total="ipagination.total"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 分类表单弹窗 -->
    <el-dialog v-model="formVisible" :title="formTitle" width="600px" destroy-on-close>
      <CategoryCreateForm ref="formRef" :disabled="formDisabled" @ok="handleFormOk" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="formVisible = false">取消</el-button>
          <el-button v-if="!formDisabled" type="primary" @click="formRef?.submitForm()">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { deleteMapping, get } from '@/net'
import { ElMessage } from 'element-plus'
import CategoryCreateForm from './modules/CategoryCreateForm.vue'

interface CourseTypeRecord {
  id: string
  courseTypeName: string
}

interface CourseTypeFormExpose {
  add: () => void
  edit: (record: CourseTypeRecord) => void
  submitForm: () => void
}

interface PaginationState {
  current: number
  pageSize: number
  total: number
}

const queryParam = reactive({ courseTypeName: '' })
const dataSource = ref<CourseTypeRecord[]>([])
const loading = ref(false)
const selectedRowKeys = ref<string[]>([])
const formRef = ref<CourseTypeFormExpose>()
const formVisible = ref(false)
const formTitle = ref('')
const formDisabled = ref(false)

const ipagination = reactive<PaginationState>({
  current: 1,
  pageSize: 10,
  total: 0
})

// 统一管理弹窗状态，避免新增/编辑/详情重复代码。
const openForm = (title: string, disabled: boolean, record?: CourseTypeRecord) => {
  formTitle.value = title
  formDisabled.value = disabled
  formVisible.value = true
  setTimeout(() => {
    if (record) {
      formRef.value?.edit(record)
      return
    }
    formRef.value?.add()
  }, 0)
}

const loadData = () => {
  loading.value = true
  const params = new URLSearchParams()
  params.append('pageNo', String(ipagination.current))
  params.append('pageSize', String(ipagination.pageSize))
  if (queryParam.courseTypeName) {
    params.append('courseTypeName', `*${queryParam.courseTypeName}*`)
  }

  get('/study/cloudComputingCourseType/list?' + params.toString(), (msg, data) => {
    const records = data?.records || []
    dataSource.value = records
    ipagination.total = data?.total || 0
    loading.value = false
  }, () => { loading.value = false }, () => { loading.value = false })
}

const searchQuery = () => {
  ipagination.current = 1
  loadData()
}

const searchReset = () => {
  queryParam.courseTypeName = ''
  searchQuery()
}

const handleSizeChange = (val: number) => {
  ipagination.pageSize = val
  loadData()
}

const handleCurrentChange = (val: number) => {
  ipagination.current = val
  loadData()
}

const onSelectChange = (selection: CourseTypeRecord[]) => {
  selectedRowKeys.value = selection.map(item => item.id)
}

const handleAdd = () => {
  openForm('新增课程分类', false)
}

const handleEdit = (row: CourseTypeRecord) => {
  openForm('编辑课程分类', false, row)
}

const handleDetail = (row: CourseTypeRecord) => {
  openForm('课程分类详情', true, row)
}

const handleFormOk = () => {
  formVisible.value = false
  loadData()
}

// 后端按 RequestParam 接收 id，因此 deleteMapping 内部会同时带 params 和 data。
const handleDelete = (id: string) => {
  deleteMapping('/study/cloudComputingCourseType/delete', { id }, (msg) => {
    ElMessage.success(msg || '删除成功')
    loadData()
  }, (failMsg) => {
    ElMessage.warning(failMsg || '删除失败')
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped></style>