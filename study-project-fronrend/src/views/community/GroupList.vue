<template>
  <div class="group-page">
    <el-card shadow="never" class="hero-card">
      <div class="hero-wrap">
        <div>
          <h3 class="hero-title">学习小组</h3>
          <p class="hero-subtitle">按兴趣加入讨论，在小组里沉淀问题和经验。</p>
        </div>
        <div class="hero-actions">
          <div class="count-badge">共 {{ groups.length }} 个小组</div>
          <!-- 创建入口直接打开弹窗，和后端新增接口形成一条最短链路 -->
          <el-button type="primary" @click="openCreate">创建小组</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="list-card">
      <div class="list-head">可加入的小组</div>
      <!-- 小组列表主要用于“加入”和“进入详情”两个动作，数据源应尽量轻量 -->
      <el-table :data="groups" stripe class="group-table">
        <el-table-column prop="name" label="名称" min-width="220" />
        <el-table-column prop="description" label="描述" min-width="280" show-overflow-tooltip />
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button link @click="joinGroup(row.id)">加入</el-button>
            <el-button link @click="goDetail(row.id)">进入</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="table-empty">当前还没有小组，创建第一个小组吧</div>
        </template>
      </el-table>
    </el-card>

    <el-dialog title="创建小组" v-model="showCreate" @close="resetForm">
      <!-- 表单字段和后端小组创建 DTO 保持同名，减少前后端映射成本 -->
      <el-form ref="formRef" :model="form" :rules="rules">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="create">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { get, post } from '@/net'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

/**
 * 前后端协同注释（学习小组列表）
 * - 接口：GET /api/community/groups 读取小组列表；POST /api/community/groups 创建小组；POST /api/community/groups/{id}/join 加入小组。
 * - 语义：创建/加入成功后当前页只做最小刷新或提示，不在前端重复实现成员权限判断，交由后端返回结果决定。
 * - 跳转：进入详情页时把 groupId 放进路由，便于详情页按相同上下文加载帖子和评论。
 */

const router = useRouter()
const groups = ref<any[]>([])
const showCreate = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({ name: '', description: '' })

const createGroupValidator = (_rule: any, value: string, callback: (error?: Error) => void) => {
  if (!value || !value.trim()) {
    callback(new Error('请输入内容'))
    return
  }
  callback()
}

const rules: FormRules = {
  name: [
    { validator: createGroupValidator, trigger: ['blur', 'change'] },
    { min: 2, max: 30, message: '名称长度为 2-30 个字符', trigger: ['blur', 'change'] }
  ],
  description: [
    { validator: createGroupValidator, trigger: ['blur', 'change'] },
    { min: 2, max: 200, message: '描述长度为 2-200 个字符', trigger: ['blur', 'change'] }
  ]
}

const load = () => {
  get('/api/community/groups', (_message: string, d: any) => {
    groups.value = d || []
  })
}

const openCreate = () => { showCreate.value = true }

const resetForm = () => {
  formRef.value?.clearValidate()
  form.name = ''
  form.description = ''
}

const create = () => {
  formRef.value?.validate((valid: boolean) => {
    if (!valid) {
      ElMessage.warning('请填写小组名称和描述')
      return
    }

    post('/api/community/groups', {
      name: form.name.trim(),
      description: form.description.trim()
    }, () => {
      showCreate.value = false
      resetForm()
      load()
    })
  })
}

const joinGroup = (groupId: string) => {
  post(`/api/community/groups/${groupId}/join`, {}, () => {
    ElMessage.success('已加入小组')
  })
}

const goDetail = (id: string) => {
  router.push({ path: '/community/GroupDetail', query: { id } })
}

onMounted(load)
</script>

<style scoped>
.group-page {
  padding: 20px;
  background:
    radial-gradient(circle at top left, rgba(14, 165, 233, 0.1), transparent 34%),
    linear-gradient(180deg, #f8fbff 0%, #ffffff 100%);
  min-height: 100%;
}

.hero-card,
.list-card {
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
}

.hero-card {
  margin-bottom: 16px;
}

.hero-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.hero-title {
  margin: 0;
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
}

.hero-subtitle {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 14px;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.count-badge {
  height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid #dbeafe;
  color: #1d4ed8;
  background: #eff6ff;
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  font-weight: 600;
}

.list-card {
  overflow: hidden;
}

.list-head {
  font-size: 15px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 12px;
}

.group-table {
  width: 100%;
}

.table-empty {
  color: #94a3b8;
  padding: 22px 0;
}

@media (max-width: 768px) {
  .group-page {
    padding: 12px;
  }

  .hero-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
