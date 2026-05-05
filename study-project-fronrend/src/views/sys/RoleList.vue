<template>
  <div class="role-manager">
    <el-card shadow="never" class="header-card">
      <div class="header-flex">
        <div class="header-info">
          <h2 class="title">角色管理与授权</h2>
          <p class="desc">管理系统角色，并为每个角色分配可访问菜单。</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" :icon="Plus" @click="openAdd">新增角色</el-button>
        </div>
      </div>
    </el-card>

    <el-table :data="roles" border stripe v-loading="loading" class="mt-4">
      <el-table-column type="index" label="序号" width="70" align="center" />
      <el-table-column prop="id" label="角色ID" width="120" />
      <el-table-column prop="name" label="角色名称" width="180" />
      <el-table-column prop="description" label="角色说明" />
      <el-table-column label="操作" width="300" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-divider direction="vertical" />
          <el-button link type="success" @click="openGrant(row)">授权菜单</el-button>
          <el-divider direction="vertical" />
          <el-popconfirm title="确认删除该角色？" @confirm="removeRole(row)">
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="roleDialog.visible" :title="roleDialog.mode === 'add' ? '新增角色' : '编辑角色'" width="460px">
      <el-form :model="roleDialog.form" label-width="90px">
        <!-- <el-form-item label="角色ID" v-if="roleDialog.mode === 'add'">
          <el-input v-model="roleDialog.form.id" placeholder="例如：4" />
        </el-form-item> -->
        <el-form-item label="角色名称">
          <el-input v-model="roleDialog.form.name" placeholder="例如：class-manager" />
        </el-form-item>
        <el-form-item label="角色说明">
          <el-input v-model="roleDialog.form.description" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitRole">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="grantDialog.visible" title="角色菜单授权" width="620px">
      <div class="grant-title">当前角色：{{ grantDialog.roleName }}（ID: {{ grantDialog.roleId }}）</div>
      <el-scrollbar max-height="430px" class="grant-tree-wrap">
        <el-tree ref="treeRef" :data="routeTree" node-key="id" show-checkbox
          :props="{ label: 'remark', children: 'children' }" default-expand-all check-on-click-node />
      </el-scrollbar>
      <template #footer>
        <el-button @click="grantDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitGrant">保存授权</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, nextTick } from 'vue'
import { deleteMapping, get, post } from '@/net'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const roles = ref<any[]>([])
const routeTree = ref<any[]>([])
const treeRef = ref<any>()

const roleDialog = reactive({
  visible: false,
  mode: 'add' as 'add' | 'edit',
  form: {
    id: '',
    name: '',
    description: ''
  }
})

const grantDialog = reactive({
  visible: false,
  roleId: '',
  roleName: ''
})

const loadRoles = () => {
  loading.value = true
  get('/api/role/list', (_, data) => {
    roles.value = data || []
    loading.value = false
  }, () => loading.value = false)
}

const loadRouteTree = () => {
  // 授权树使用全量路由结构；真正勾选状态在 openGrant 时按角色单独回填。
  get('/api/routes/allTree', (_, data) => {
    routeTree.value = data || []
  })
}

const openAdd = () => {
  roleDialog.mode = 'add'
  roleDialog.form = { id: '', name: '', description: '' }
  roleDialog.visible = true
}

const openEdit = (row: any) => {
  roleDialog.mode = 'edit'
  roleDialog.form = {
    id: String(row.id || ''),
    name: row.name || '',
    description: row.description || ''
  }
  roleDialog.visible = true
}

const submitRole = () => {
  if (!roleDialog.form.name) {
    ElMessage.warning('请填写角色名称')
    return
  }

  const url = roleDialog.mode === 'add' ? '/api/role/add' : '/api/role/edit'
  post(url, roleDialog.form, (msg) => {
    ElMessage.success(msg || '保存成功')
    roleDialog.visible = false
    loadRoles()
  })
}

const removeRole = (row: any) => {
  deleteMapping('/api/role/delete', { id: row.id }, (msg) => {
    ElMessage.success(msg || '删除成功')
    loadRoles()
  })
}

const openGrant = (row: any) => {
  grantDialog.roleId = String(row.id)
  grantDialog.roleName = row.name || '-'
  grantDialog.visible = true

  // 每次打开都重新拉取已授权菜单，确保弹窗展示的是最新授权结果。
  get(`/api/role/routes?roleId=${row.id}`, (_, data) => {
    const returned = Array.isArray(data) ? data : []

    // 计算出应当完全选中的节点与应当半选的节点，避免直接把父节点当作完全选中，从而误将其子节点全部勾选。
    const computeChecked = (treeData: any[], selected: any[]) => {
      const selSet = new Set((selected || []).map((s: any) => String(s)))
      const checkedKeys: string[] = []
      const halfCheckedKeys: string[] = []

      const dfs = (node: any) => {
        const id = String(node.id)
        if (!node.children || !node.children.length) {
          const isSel = selSet.has(id)
          if (isSel) checkedKeys.push(id)
          return { total: 1, sel: isSel ? 1 : 0 }
        }

        let total = 0
        let sel = 0
        for (const c of node.children) {
          const res = dfs(c)
          total += res.total
          sel += res.sel
        }

        if (sel === 0) {
          // none selected -> nothing
        } else if (sel === total) {
          // 子节点全部选中，标记父节点为完全选中
          checkedKeys.push(id)
        } else {
          // 部分选中 -> 半选
          halfCheckedKeys.push(id)
        }

        return { total, sel }
      }

      for (const n of treeData) dfs(n)

      return { checkedKeys, halfCheckedKeys }
    }

    const { checkedKeys, halfCheckedKeys } = computeChecked(routeTree.value || [], returned)

    // 等待树渲染完成后再回填勾选状态
    nextTick(() => {
      const tree = treeRef.value
      if (!tree) return
      // Element Plus 的 setCheckedKeys 支持传入对象以同时设置全选和半选
      try {
        ;(tree as any).setCheckedKeys({ checked: checkedKeys, halfChecked: halfCheckedKeys })
      } catch (e) {
        // 兼容老版本：回退到只设置完全选中节点
        (tree as any).setCheckedKeys(checkedKeys)
        // 半选节点在老版本上可能无法回填，这里无更多操作
      }
    })
  })
}

const submitGrant = () => {
  const checkedKeys = treeRef.value?.getCheckedKeys(false) || []
  const halfCheckedKeys = treeRef.value?.getHalfCheckedKeys() || []
  // 后端期望接收“最终有效节点集合”，因此要合并全选与半选节点并去重。
  const routeIds = [...new Set([...checkedKeys, ...halfCheckedKeys])]

  post('/api/role/grant', {
    roleId: grantDialog.roleId,
    routeIds
  }, (msg) => {
    ElMessage.success(msg || '授权成功')
    grantDialog.visible = false
  })
}

onMounted(() => {
  loadRoles()
  loadRouteTree()
})
</script>

<style scoped>
.role-manager {
  padding: 0;
}

.header-card {
  border: none;
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
}

.desc {
  margin: 4px 0 0;
  font-size: 14px;
  color: #8c8c8c;
}

.mt-4 {
  margin-top: 16px;
}

.grant-title {
  margin-bottom: 12px;
  color: #606266;
}

.grant-tree-wrap {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 10px;
}
</style>
