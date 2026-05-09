import { createRouter, createWebHistory } from 'vue-router'
import { getSession } from '../utils/auth'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { public: true } },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue'), meta: { public: true } },
  { path: '/dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'),
    children: [
      { path: '', redirect: '/dashboard/students' },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue') },
      { path: 'students', name: 'Students', component: () => import('../views/Students.vue') },
      { path: 'students/:id', name: 'StudentDetail', component: () => import('../views/StudentDetail.vue') },
      { path: 'password', name: 'Password', component: () => import('../views/Password.vue') },
      { path: 'users', name: 'Users', component: () => import('../views/Users.vue'), meta: { adminOnly: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const session = getSession()
  if (!to.meta.public && !session) {
    next('/login')
  } else if (to.meta.public && session && (to.path === '/login' || to.path === '/register')) {
    next('/dashboard')
  } else if (to.meta.adminOnly && session?.role !== 'admin') {
    next('/dashboard/students')
  } else {
    next()
  }
})

export default router
