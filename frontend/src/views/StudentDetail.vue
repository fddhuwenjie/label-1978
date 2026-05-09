<template>
  <div class="page" v-loading="loading">
    <div class="page-header">
      <button class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="19" y1="12" x2="5" y2="12"/>
          <polyline points="12 19 5 12 12 5"/>
        </svg>
        返回列表
      </button>
    </div>

    <template v-if="student">
      <div class="card">
        <div class="student-header">
          <div class="student-avatar">{{ student.name?.charAt(0) }}</div>
          <div class="student-info">
            <h1>{{ student.name }}</h1>
            <div class="student-meta">
              <span class="student-no">{{ student.studentNo }}</span>
              <span class="gender-tag" :class="student.gender === '男' ? 'male' : 'female'">
                {{ student.gender }}
              </span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h4>基本信息</h4>
          <div class="info-grid">
            <div class="info-item">
              <label>年龄</label>
              <span>{{ student.age }} 岁</span>
            </div>
            <div class="info-item">
              <label>班级</label>
              <span>{{ student.className || '-' }}</span>
            </div>
            <div class="info-item">
              <label>专业</label>
              <span>{{ student.major || '-' }}</span>
            </div>
            <div class="info-item">
              <label>联系电话</label>
              <span>{{ student.phone || '-' }}</span>
            </div>
            <div class="info-item full">
              <label>家庭地址</label>
              <span>{{ student.address || '-' }}</span>
            </div>
          </div>
        </div>

        <div class="info-section">
          <h4>系统信息</h4>
          <div class="info-grid">
            <div class="info-item">
              <label>创建时间</label>
              <span>{{ student.createdAt || '-' }}</span>
            </div>
            <div class="info-item">
              <label>更新时间</label>
              <span>{{ student.updatedAt || '-' }}</span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { studentApi } from '../utils/api'

const route = useRoute()
const student = ref(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await studentApi.get(route.params.id)
    student.value = res.data
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

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: none;
  background: #f3f4f6;
  border-radius: 8px;
  font-size: 14px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.15s;
}

.back-btn svg {
  width: 16px;
  height: 16px;
}

.back-btn:hover {
  background: #e5e7eb;
  color: #374151;
}

.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  padding: 24px;
}

.student-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f3f4f6;
  margin-bottom: 20px;
}

.student-avatar {
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

.student-info h1 {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 6px;
}

.student-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.student-no {
  font-family: 'SF Mono', Monaco, monospace;
  font-size: 13px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 2px 8px;
  border-radius: 4px;
}

.gender-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.gender-tag.male {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.gender-tag.female {
  background: rgba(236, 72, 153, 0.1);
  color: #ec4899;
}

.info-section {
  margin-bottom: 20px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.info-section h4 {
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full {
  grid-column: span 2;
}

.info-item label {
  font-size: 12px;
  color: #9ca3af;
}

.info-item span {
  font-size: 14px;
  color: #111827;
  font-weight: 500;
}
</style>
