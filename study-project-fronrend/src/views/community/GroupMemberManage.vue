<template>
    <div class="member-manage">
        <el-card shadow="never" class="header-card">
            <div style="display:flex;justify-content:space-between;align-items:center">
                <div>
                    <h3>学习小组成员管理</h3>
                    <p class="desc">按小组查看成员并进行删除操作。</p>
                </div>
            </div>
        </el-card>

        <el-card class="mt-4" shadow="never">
            <div style="display:flex;gap:12px;align-items:center;margin-bottom:12px">
                <el-select v-model="selectedGroup" placeholder="请选择小组" style="width:320px" clearable>
                    <el-option v-for="g in groups" :key="g.id" :label="g.name" :value="g.id" />
                </el-select>
                <el-button type="primary" @click="loadMembers">加载成员</el-button>
                <el-button type="danger" @click="deleteSelected">删除选中</el-button>
            </div>

            <el-table :data="members" v-loading="loading" border stripe @selection-change="onSelectionChange">
                <el-table-column type="selection" width="60" />
                <el-table-column prop="username" label="用户名" width="180" />
                <el-table-column prop="userId" label="用户ID" width="160" />
                <el-table-column prop="createTime" label="加入时间" width="180" />
                <el-table-column label="操作" width="120" align="center">
                    <template #default="{ row }">
                        <el-popconfirm title="确定删除此成员？" @confirm="handleDelete(row.id)">
                            <template #reference>
                                <el-button link type="danger">删除</el-button>
                            </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get, deleteMapping } from '@/net'
import { ElMessage } from 'element-plus'

const groups = ref<any[]>([])
const selectedGroup = ref<string | null>(null)
const members = ref<any[]>([])
const loading = ref(false)
const selectedRows = ref<any[]>([])

const unwrap = (p: any) => {
    if (Array.isArray(p)) return p
    if (Array.isArray(p?.records)) return p.records
    return []
}

const loadGroups = () => {
    get('/api/community/groups/all', (_m, d) => {
        groups.value = unwrap(d)
    })
}

const loadMembers = () => {
    if (!selectedGroup.value) {
        ElMessage.warning('请选择小组')
        return
    }
    loading.value = true
    get(`/study/studyGroupMember/listByGroupId?groupId=${selectedGroup.value}`, (_m, d) => {
        members.value = d || []
        loading.value = false
    }, () => { loading.value = false })
}

const handleDelete = (id: string) => {
    deleteMapping('/study/studyGroupMember/delete', { id }, (msg) => {
        ElMessage.success(msg)
        loadMembers()
    })
}

const onSelectionChange = (rows: any[]) => { selectedRows.value = rows }

const deleteSelected = () => {
    if (!selectedRows.value.length) {
        ElMessage.warning('请先选择要删除的成员')
        return
    }
    const ids = selectedRows.value.map(r => r.id).join(',')
    deleteMapping('/study/studyGroupMember/deleteBatch', { ids }, (msg) => {
        ElMessage.success(msg)
        loadMembers()
    })
}

onMounted(() => {
    loadGroups()
})
</script>

<style scoped>
.mt-4 {
    margin-top: 16px
}

.desc {
    margin: 6px 0 0;
    color: #6b7280
}
</style>
