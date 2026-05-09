<template>
  <div class="page" v-loading="loading">
    <div class="page-header">
      <h1>个人信息</h1>
      <p>查看您的账户详情</p>
    </div>

    <template v-if="user">
      <div class="card">
        <div class="user-section">
          <div class="profile-avatar">
            {{ user.username?.charAt(0).toUpperCase() }}
          </div>
          <div class="profile-info">
            <h2>{{ user.username }}</h2>
            <span class="role-badge" :class="user.role">
              {{ user.role === 'admin' ? '管理员' : '普通用户' }}
            </span>
          </div>
        </div>

        <div class="info-section">
          <div class="info-row">
            <div class="info-label">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              用户ID
            </div>
            <div class="info-value">{{ user.id }}</div>
          </div>
          <div class="info-row">
            <div class="info-label">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                <polyline points="22,6 12,13 2,6"/>
              </svg>
              邮箱地址
            </div>
            <div class="info-value">{{ user.email || '未设置' }}</div>
          </div>
          <div class="info-row">
            <div class="info-label">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12 6 12 12 16 14"/>
              </svg>
              注册时间
            </div>
            <div class="info-value">{{ user.createdAt }}</div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '../utils/api'

const user = ref(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await userApi.getProfile()
    user.value = res.data
  } catch (err) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page {
  width: 100%;
  min-height: 400px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

.page-header p {
  font-size: 14px;
  color: #6b7280;
}

.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  padding: 24px;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f3f4f6;
  margin-bottom: 20px;
}

.profile-avatar {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 22px;
  font-weight: 600;
}

.profile-info h2 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

.role-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.role-badge.admin {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.role-badge.user {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.info-section {
  display: flex;
  flex-direction: column;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #f3f4f6;
}

.info-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #6b7280;
}

.info-label svg {
  width: 18px;
  height: 18px;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: #111827;
}
</style>
