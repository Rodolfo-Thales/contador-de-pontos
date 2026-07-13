import { ref } from 'vue'
import { api } from '../api/client'

const isAdmin = ref(false)

export function useAuth() {
  async function checkSession() {
    try {
      const data = await api.get('/api/auth/me')
      isAdmin.value = data.authenticated
    } catch {
      isAdmin.value = false
    }
  }

  async function login(password) {
    await api.post('/api/auth/login', { password })
    isAdmin.value = true
  }

  async function logout() {
    try {
      await api.post('/api/auth/logout')
    } finally {
      isAdmin.value = false
    }
  }

  return { isAdmin, checkSession, login, logout }
}
