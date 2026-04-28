<template>
  <div class="p-4">
    <div class="flex justify-between items-center mb-4">
      <h3 class="text-lg font-bold">学习小组</h3>
      <el-button type="primary" @click="openCreate">创建小组</el-button>
    </div>

    <el-table :data="groups" stripe>
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="text" @click="joinGroup(row.id)">加入</el-button>
          <el-button type="text" @click="goDetail(row.id)">进入</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="创建小组" v-model="showCreate">
      <el-form :model="form">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate=false">取消</el-button>
        <el-button type="primary" @click="create">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { communityApi } from '@/net'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const groups = ref<any[]>([])
const showCreate = ref(false)
const form = ref({ name: '', description: '' })

const load = () => {
  communityApi.listGroups((d) => {
    groups.value = d || []
  })
}

const openCreate = () => { showCreate.value = true }
const create = () => {
  communityApi.createGroup(form.value, () => {
    showCreate.value = false
    form.value = { name: '', description: '' }
    load()
  })
}

const joinGroup = (groupId: string) => {
  communityApi.joinGroup(groupId, () => {
    ElMessage.success('已加入小组')
  })
}

const goDetail = (id:string) => {
  router.push({ path: `/index/community/group/${id}` })
}

onMounted(load)
</script>

<style scoped>
.p-4 { padding: 16px }
</style>
