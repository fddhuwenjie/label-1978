<template>
  <div class="users-page">
    <div class="page-header">
      <div class="header-content">
        <h1>用户管理</h1>
        <p>管理系统用户和角色权限</p>
      </div>
    </div>

    <el-card class="users-card">
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'info'">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.role !== 'admin'" 
              type="primary" 
              size="small" 
              @click="promoteToAdmin(row)"
            >
              升级为管理员
            </el-button>
            <el-button 
              v-else-if="row.id !== currentUserId" 
              type="warning" 
              size="small" 
              @click="demoteToUser(row)"
            >
              降级为用户
            </el-button>
            <span v-else class="current-user-tag">当前用户</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '../utils/api'
import { getSession } from '../utils/auth'

const users = ref([])
const loading = ref(false)
const currentUserId = ref(getSession()?.userId)

onMounted(() => {
  loadUsers()
})

async function loadUsers() {
  loading.value = true
  try {
    const res = await userApi.listUsers()
    users.value = res.data || []
  } catch (err) {
    ElMessage.error(err.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}

async function promoteToAdmin(user) {
  try {
    await ElMessageBox.confirm(
      `确定要将用户 "${user.username}" 升级为管理员吗？`,
      '确认操作',
      { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
    )
    await userApi.updateRole({ userId: user.id, role: 'admin' })
    ElMessage.success('角色更新成功')
    loadUsers()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error(err.message || '操作失败')
    }
  }
}

async function demoteToUser(user) {
  try {
    await ElMessageBox.confirm(
      `确定要将用户 "${user.username}" 降级为普通用户吗？`,
      '确认操作',
      { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
    )
    await userApi.updateRole({ userId: user.id, role: 'user' })
    ElMessage.success('角色更新成功')
    loadUsers()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error(err.message || '操作失败')
    }
  }
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<style scoped>
.users-page {
  width: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.header-content h1 {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.header-content p {
  color: var(--text-secondary);
  font-size: 14px;
}

.users-card {
  border-radius: var(--radius-md);
}

.current-user-tag {
  color: var(--text-muted);
  font-size: 12px;
}
</style>
