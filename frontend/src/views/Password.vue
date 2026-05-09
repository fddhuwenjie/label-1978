<template>
  <div class="page">
    <div class="page-header">
      <h1>修改密码</h1>
      <p>更新您的账户密码</p>
    </div>

    <div class="card">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="password-form">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" placeholder="请输入当前密码" show-password size="large" />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码（至少6位）" show-password size="large" />
        </el-form-item>
        
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" show-password size="large" />
        </el-form-item>

        <div class="form-actions">
          <el-button type="primary" @click="handleSubmit" :loading="loading" size="large" class="submit-btn">
            {{ loading ? '提交中...' : '确认修改' }}
          </el-button>
        </div>
      </el-form>
    </div>

    <div class="card tips-card">
      <h4>密码安全提示</h4>
      <ul>
        <li>密码长度至少为6个字符</li>
        <li>建议使用字母、数字和特殊字符的组合</li>
        <li>请勿使用与其他网站相同的密码</li>
        <li>定期更换密码可以提高账户安全性</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '../utils/api'

const formRef = ref(null)
const loading = ref(false)
const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await userApi.changePassword({ oldPassword: form.oldPassword, newPassword: form.newPassword })
    ElMessage.success('密码修改成功')
    form.oldPassword = ''
    form.newPassword = ''
    form.confirmPassword = ''
  } catch (err) {
    ElMessage.error(err.message || '修改失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page {
  width: 100%;
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
  margin-bottom: 16px;
}

.password-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.password-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.password-form :deep(.el-input__wrapper) {
  padding: 4px 14px;
  height: 44px;
  border-radius: 8px !important;
  background: #f9fafb;
  box-shadow: 0 0 0 1px #e5e7eb !important;
}

.password-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #d1d5db !important;
}

.password-form :deep(.el-input__wrapper.is-focus) {
  background: #fff;
  box-shadow: 0 0 0 2px #6366f1 !important;
}

.form-actions {
  margin-top: 28px;
}

.submit-btn {
  width: 100%;
  height: 44px !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  border-radius: 8px !important;
}

.tips-card {
  background: rgba(99, 102, 241, 0.03);
  border-color: rgba(99, 102, 241, 0.15);
}

.tips-card h4 {
  font-size: 14px;
  font-weight: 600;
  color: #6366f1;
  margin-bottom: 12px;
}

.tips-card ul {
  margin: 0;
  padding-left: 18px;
}

.tips-card li {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 6px;
}

.tips-card li:last-child {
  margin-bottom: 0;
}
</style>
