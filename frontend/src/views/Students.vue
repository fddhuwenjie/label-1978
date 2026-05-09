<template>
  <div class="page">
    <div class="page-header">
      <div class="page-title">
        <h1>学生管理</h1>
        <p>管理和查看所有学生信息</p>
      </div>
      <el-button v-if="isAdmin()" type="primary" @click="showAddDialog" class="add-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="btn-icon">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        添加学生
      </el-button>
    </div>

    <div class="content-card">
      <div class="card-toolbar">
        <div class="search-box">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="search-icon">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input 
            v-model="keyword" 
            type="text" 
            placeholder="搜索学生姓名、学号..." 
            @keyup.enter="handleSearch"
          />
          <button v-if="keyword" class="clear-btn" @click="keyword = ''; loadStudents()">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="stats">
          <span class="stat-item">共 <strong>{{ students.length }}</strong> 名学生</span>
        </div>
      </div>

      <div class="table-container" v-loading="loading">
        <table class="data-table">
          <thead>
            <tr>
              <th>学号</th>
              <th>姓名</th>
              <th>性别</th>
              <th>年龄</th>
              <th>班级</th>
              <th>专业</th>
              <th>联系电话</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="student in students" :key="student.id">
              <td>
                <span class="student-no">{{ student.studentNo }}</span>
              </td>
              <td>
                <div class="student-name">
                  <div class="avatar">{{ student.name?.charAt(0) }}</div>
                  <span>{{ student.name }}</span>
                </div>
              </td>
              <td>
                <span class="gender-tag" :class="student.gender === '男' ? 'male' : 'female'">
                  {{ student.gender }}
                </span>
              </td>
              <td>{{ student.age }}</td>
              <td>{{ student.className }}</td>
              <td>{{ student.major }}</td>
              <td>{{ student.phone }}</td>
              <td>
                <div class="actions">
                  <button class="action-btn view" @click="viewStudent(student)" title="查看详情">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                  </button>
                  <button v-if="isAdmin()" class="action-btn edit" @click="editStudent(student)" title="编辑">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                    </svg>
                  </button>
                  <button v-if="isAdmin()" class="action-btn delete" @click="deleteStudent(student)" title="删除">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-if="!loading && students.length === 0" class="empty-state">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <line x1="17" y1="11" x2="23" y2="11"/>
          </svg>
          <h3>暂无学生数据</h3>
          <p>点击上方按钮添加第一个学生</p>
        </div>
      </div>
    </div>

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" class="student-dialog">
      <el-form :model="studentForm" :rules="formRules" ref="studentFormRef" label-position="top">
        <div class="form-row">
          <el-form-item label="学号" prop="studentNo" class="form-col">
            <el-input v-model="studentForm.studentNo" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="姓名" prop="name" class="form-col">
            <el-input v-model="studentForm.name" placeholder="请输入姓名" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="性别" class="form-col">
            <el-radio-group v-model="studentForm.gender">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="年龄" class="form-col">
            <el-input-number v-model="studentForm.age" :min="1" :max="100" style="width: 100%" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="班级" class="form-col">
            <el-input v-model="studentForm.className" placeholder="请输入班级" />
          </el-form-item>
          <el-form-item label="专业" class="form-col">
            <el-input v-model="studentForm.major" placeholder="请输入专业" />
          </el-form-item>
        </div>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="studentForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="家庭地址">
          <el-input v-model="studentForm.address" type="textarea" :rows="2" placeholder="请输入家庭地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          {{ isEditing ? '保存修改' : '添加学生' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { studentApi } from '../utils/api'
import { isAdmin } from '../utils/auth'

const router = useRouter()
const students = ref([])
const loading = ref(false)
const keyword = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('添加学生')
const submitting = ref(false)
const studentFormRef = ref(null)
const isEditing = ref(false)

const studentForm = reactive({
  id: null, studentNo: '', name: '', gender: '男', age: 18,
  className: '', major: '', phone: '', address: ''
})

const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const formRules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }]
}

onMounted(() => loadStudents())

async function loadStudents() {
  loading.value = true
  try {
    const res = await studentApi.list()
    students.value = res.data || []
  } catch (err) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

async function handleSearch() {
  if (!keyword.value.trim()) {
    loadStudents()
    return
  }
  loading.value = true
  try {
    const res = await studentApi.search(keyword.value)
    students.value = res.data || []
  } catch (err) {
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

function viewStudent(row) {
  router.push(`/dashboard/students/${row.id}`)
}

function showAddDialog() {
  isEditing.value = false
  dialogTitle.value = '添加学生'
  Object.assign(studentForm, { id: null, studentNo: '', name: '', gender: '男', age: 18, className: '', major: '', phone: '', address: '' })
  dialogVisible.value = true
}

function editStudent(row) {
  isEditing.value = true
  dialogTitle.value = '编辑学生'
  Object.assign(studentForm, row)
  dialogVisible.value = true
}

async function submitForm() {
  const valid = await studentFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  try {
    if (isEditing.value) {
      await studentApi.update(studentForm)
      ElMessage.success('更新成功')
    } else {
      await studentApi.create(studentForm)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadStudents()
  } catch (err) {
    ElMessage.error(err.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

async function deleteStudent(row) {
  try {
    await ElMessageBox.confirm(`确定要删除学生「${row.name}」吗？此操作不可恢复。`, '删除确认', { 
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    await studentApi.delete(row.id)
    ElMessage.success('删除成功')
    loadStudents()
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('删除失败')
  }
}
</script>

<style scoped>
.page {
  width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.page-title h1 {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
}

.page-title p {
  font-size: 14px;
  color: #6b7280;
}

.add-btn {
  height: 44px !important;
  padding: 0 20px !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  border-radius: 10px !important;
  display: flex !important;
  align-items: center !important;
  gap: 8px !important;
}

.btn-icon {
  width: 18px;
  height: 18px;
}

.content-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 1px 2px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.card-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f3f4f6;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
  width: 320px;
}

.search-icon {
  position: absolute;
  left: 14px;
  width: 18px;
  height: 18px;
  color: #9ca3af;
}

.search-box input {
  width: 100%;
  height: 42px;
  padding: 0 40px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  font-size: 14px;
  background: #f9fafb;
  transition: all 0.2s;
}

.search-box input:focus {
  outline: none;
  border-color: #6366f1;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.search-box input::placeholder {
  color: #9ca3af;
}

.clear-btn {
  position: absolute;
  right: 10px;
  width: 24px;
  height: 24px;
  border: none;
  background: #e5e7eb;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
}

.clear-btn:hover {
  background: #d1d5db;
}

.clear-btn svg {
  width: 14px;
  height: 14px;
}

.stats {
  font-size: 14px;
  color: #6b7280;
}

.stats strong {
  color: #111827;
  font-weight: 600;
}

.table-container {
  min-height: 500px;
  position: relative;
}

.data-table {
  table-layout: fixed;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  padding: 14px 20px;
  text-align: left;
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  background: #f9fafb;
  border-bottom: 1px solid #f3f4f6;
}

.data-table td {
  padding: 16px 20px;
  font-size: 14px;
  color: #374151;
  border-bottom: 1px solid #f3f4f6;
}

.data-table tbody tr {
  transition: background 0.15s;
}

.data-table tbody tr:hover {
  background: #f9fafb;
}

.student-no {
  font-family: 'SF Mono', Monaco, monospace;
  font-size: 13px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 4px 8px;
  border-radius: 6px;
}

.student-name {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.student-name span {
  font-weight: 500;
  color: #111827;
}

.gender-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 20px;
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

.actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.action-btn.view {
  background: rgba(99, 102, 241, 0.1);
  color: #6366f1;
}

.action-btn.view:hover {
  background: rgba(99, 102, 241, 0.2);
}

.action-btn.edit {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.action-btn.edit:hover {
  background: rgba(245, 158, 11, 0.2);
}

.action-btn.delete {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.2);
}

.empty-state {
  padding: 80px 20px;
  text-align: center;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 4px;
}

.empty-state p {
  font-size: 14px;
  color: #9ca3af;
}

/* 弹窗样式 */
.form-row {
  display: flex;
  gap: 16px;
}

.form-col {
  flex: 1;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #374151;
}
</style>
