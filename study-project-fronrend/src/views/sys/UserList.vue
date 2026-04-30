<template>
  <div class="user-manager">
    <el-card shadow="never" class="header-card">
      <div class="header-flex">
        <div class="header-info">
          <h2 class="title">用户管理</h2>
          <p class="desc">管理系统所有用户账户，分配角色和权限。</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" :icon="Plus" @click="openAdd">新增用户</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="mt-4" shadow="hover">
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索用户名或邮箱..." class="search-input" clearable
          @input="handleSearch" />
      </div>

      <el-table :data="users" v-loading="loading" border stripe class="mt-4" style="width: 100%">
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="roleDescription" label="角色" width="150" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">{{ row.roleDescription }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-button link type="success" size="small" @click="openChangeRole(row)">改角色</el-button>
            <el-divider direction="vertical" />
            <el-button link type="warning" size="small" @click="handleResetPassword(row)">重置密码</el-button>
            <el-divider direction="vertical" />
            <el-popconfirm title="确定要删除此用户吗？" @confirm="handleDelete(row)" width="220">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container mt-4">
        <el-pagination v-model:current-page="pageNo" v-model:page-size="pageSize" :total="total"
          layout="total, prev, pager, next, sizes" @current-change="loadUsers" @size-change="loadUsers" />
      </div>
    </el-card>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog v-model="userDialog.visible" :title="userDialog.mode === 'add' ? '新增用户' : '编辑用户'" width="500px"
      @close="resetForm">
      <el-form ref="formRef" :model="userDialog.form" label-width="100px" :rules="formRules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userDialog.form.username" placeholder="输入用户名" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userDialog.form.email" placeholder="输入邮箱" type="email" />
        </el-form-item>


        <el-form-item v-if="userDialog.mode === 'add'" label="密码" prop="password">
          <el-input v-model="userDialog.form.password" placeholder="输入密码" type="password" show-password />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="userDialog.form.role" placeholder="选择角色" style="width: 100%">
            <el-option v-for="role in roles" :key="role.id" :label="role.description" :value="Number(role.id)" />
          </el-select>
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="userDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitUser">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改角色对话框 -->
    <el-dialog v-model="roleDialog.visible" title="修改用户角色" width="400px">
      <div class="role-select-wrap">
        <p class="user-info">用户: <strong>{{ roleDialog.username }}</strong></p>
        <el-select v-model="roleDialog.newRole" placeholder="选择新角色" style="width: 100%">
          <el-option v-for="role in roles" :key="role.id" :label="role.description" :value="Number(role.id)" />
        </el-select>
      </div>
      <template #footer>
        <el-button @click="roleDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRole">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { deleteMapping, get, post } from '@/net'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'


const loading = ref(false)
const users = ref<any[]>([])
const roles = ref<any[]>([])
const total = ref(0)
const pageNo = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const formRef = ref<FormInstance>()

const userDialog = reactive({
  visible: false,
  mode: 'add' as 'add' | 'edit',
  form: {
    id: '',
    username: '',
    email: '',
    password: '',
    role: 3
  },
  showPassword: false
})

const roleDialog = reactive({
  visible: false,
  userId: '',
  username: '',
  newRole: 3
})

const formRules = {
  username: [
    { required: true, message: '用户名不能为空', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '邮箱不能为空', trigger: 'blur' },
    {
      type: 'email',
      message: '请输入正确的邮箱地址',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度为 6-16 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const getRoleTagType = (role: number) => {
  // 标签颜色仅用于视觉区分角色，不参与权限判断。
  const types: Record<number, string> = {
    1: 'danger',
    2: 'success',
    3: 'info',
    4: 'warning'
  }
  return types[role] || 'info'
}

const loadUsers = () => {
  loading.value = true
  // 列表接口同时承载分页与关键字搜索，保持参数来源统一，便于后续扩展筛选项。
  const search = new URLSearchParams()
  search.append('pageNo', String(pageNo.value))
  search.append('pageSize', String(pageSize.value))
  search.append('keyword', searchKeyword.value)
  get(
    `/api/user/manage/list?${search.toString()}`,
    (_msg, data) => {
      users.value = data?.records || []
      total.value = data?.total || 0
      loading.value = false
    },
    () => {
      loading.value = false
    }
  )
}

const loadRoles = () => {
  get('/api/role/list', (_msg, data) => {
    roles.value = data || []
  })
}

const handleSearch = () => {
  // 关键字变更后回到第一页，避免保留旧页码导致“有数据但当前页为空”。
  pageNo.value = 1
  loadUsers()
}

const openAdd = () => {
  userDialog.mode = 'add'
  userDialog.form = { id: '', username: '', email: '', password: '', role: 3 }
  userDialog.showPassword = false
  userDialog.visible = true
}

const openEdit = (row: any) => {
  userDialog.mode = 'edit'
  userDialog.form = {
    id: row.id,
    username: row.username,
    email: row.email,
    password: '',
    role: row.role
  }
  userDialog.visible = true
}

const openChangeRole = (row: any) => {
  roleDialog.userId = row.id
  roleDialog.username = row.username
  roleDialog.newRole = row.role
  roleDialog.visible = true
}

const resetForm = () => {
  formRef.value?.resetFields()
}

const handleSubmitUser = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const url = userDialog.mode === 'add' ? '/api/user/manage/add' : '/api/user/manage/edit'
  // 编辑时不默认提交空密码，只有用户主动输入新密码时才携带该字段。
  const editPayload: any = {
    id: userDialog.form.id,
    username: userDialog.form.username,
    email: userDialog.form.email,
    role: userDialog.form.role
  }
  if (userDialog.form.password?.trim()) {
    editPayload.password = userDialog.form.password
  }

  post(
    url,
    userDialog.mode === 'add' ? userDialog.form : editPayload,
    (msg) => {
      ElMessage.success(msg)
      userDialog.visible = false
      loadUsers()
    }
  )
}

const handleSubmitRole = () => {
  // 使用轻量接口只修改角色，避免走全量编辑接口带来字段冲突。
  post(`/api/user/manage/changeRole?id=${roleDialog.userId}&role=${roleDialog.newRole}`, {}, (msg) => {
    ElMessage.success(msg)
    roleDialog.visible = false
    loadUsers()
  })
}

const handleResetPassword = (row: any) => {
  ElMessageBox.confirm(
    `确定要将 ${row.username} 的密码重置为默认密码（123456）吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    post(`/api/user/manage/resetPassword?id=${row.id}`, {}, (msg) => {
      ElMessage.success(msg)
      loadUsers()
    })
  })
}

const handleDelete = (row: any) => {
  deleteMapping('/api/user/manage/delete', { id: row.id }, (msg) => {
    ElMessage.success(msg || '删除成功')
    // 删除用户后同步刷新角色和列表，保证“角色人数/用户列表”相关视图一致。
    loadRoles()
    loadUsers()
  })
}

onMounted(() => {
  loadUsers()
  loadRoles()
})
</script>

<style scoped>
.user-manager {
  padding: 0;
}

.header-card {
  border: none;
  background-color: #ffffff;
  border-bottom: 1px solid #f0f0f0;
}

.header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

.desc {
  margin: 4px 0 0;
  font-size: 14px;
  color: #8c8c8c;
}

.mt-4 {
  margin-top: 16px;
}

.search-bar {
  margin-bottom: 16px;
}

.search-input {
  max-width: 300px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}

.role-select-wrap {
  padding: 16px 0;
}

.user-info {
  margin-bottom: 12px;
  color: #606266;
}
</style>
