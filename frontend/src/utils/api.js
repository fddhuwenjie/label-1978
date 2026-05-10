import axios from 'axios'
import { getSession, clearSession, refreshSession } from './auth'
import router from '../router'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const session = getSession()
  if (session && session.sessionId) {
    config.headers['X-Session-Id'] = session.sessionId
    refreshSession()
  }
  return config
})

api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response && error.response.status === 401) {
      clearSession()
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
      return Promise.reject(error.response.data || { code: 401, message: '登录已过期，请重新登录' })
    }
    return Promise.reject(error.response ? error.response.data : error)
  }
)

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  logout: () => api.post('/auth/logout')
}

export const userApi = {
  getProfile: () => api.get('/user/profile'),
  changePassword: (data) => api.post('/user/password', data),
  listUsers: () => api.get('/user/list'),
  updateRole: (data) => api.post('/user/role', data)
}

export const studentApi = {
  list: () => api.get('/students'),
  search: (keyword) => api.get('/students/search', { params: { keyword } }),
  get: (id) => api.get(`/students/${id}`),
  create: (data) => api.post('/students', data),
  update: (data) => api.put('/students', data),
  delete: (id) => api.delete(`/students/${id}`)
}

export default api
