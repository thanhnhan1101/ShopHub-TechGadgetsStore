import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  const setAuth = (authData) => {
    token.value = authData.token
    user.value = {
      id: authData.id,
      email: authData.email,
      fullName: authData.fullName,
      phone: authData.phone,
      role: authData.role
    }
    localStorage.setItem('token', authData.token)
    localStorage.setItem('user', JSON.stringify(user.value))
    
    // Set default Authorization header
    axios.defaults.headers.common['Authorization'] = `Bearer ${authData.token}`
  }

  const loadUserFromStorage = () => {
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')
    
    console.log('Loading user from storage:', { hasToken: !!storedToken, hasUser: !!storedUser })
    
    if (storedToken && storedUser) {
      token.value = storedToken
      user.value = JSON.parse(storedUser)
      axios.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`
      console.log('User loaded:', user.value)
    } else {
      console.warn('No user data in localStorage')
    }
  }

  const login = async (email, password) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/login`, {
        email,
        password
      })
      setAuth(response.data)
      return { success: true }
    } catch (error) {
      console.error('Login error:', error)
      
      let errorMessage = 'Login failed'
      
      if (error.response) {
        // Server responded with error
        errorMessage = error.response?.data?.message || error.response?.data?.error || `Server error: ${error.response.status}`
      } else if (error.request) {
        // Request was made but no response
        errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra backend có đang chạy không.'
      } else {
        // Something else happened
        errorMessage = error.message || 'Login failed'
      }
      
      return {
        success: false,
        message: errorMessage
      }
    }
  }

  const register = async (email, password, fullName, phone) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/register`, {
        email,
        password,
        fullName,
        phone
      })
      setAuth(response.data)
      return { success: true }
    } catch (error) {
      console.error('Register error:', error)
      
      let errorMessage = 'Registration failed'
      
      if (error.response) {
        // Server responded with error
        errorMessage = error.response?.data?.message || error.response?.data?.error || `Server error: ${error.response.status}`
      } else if (error.request) {
        // Request was made but no response
        errorMessage = 'Không thể kết nối đến server. Vui lòng kiểm tra backend có đang chạy không.'
      } else {
        // Something else happened
        errorMessage = error.message || 'Registration failed'
      }
      
      return {
        success: false,
        message: errorMessage
      }
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    delete axios.defaults.headers.common['Authorization']
  }

  const fetchCurrentUser = async () => {
    if (!token.value) return

    try {
      const response = await axios.get(`${API_BASE_URL}/auth/me`, {
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })
      user.value = response.data
      localStorage.setItem('user', JSON.stringify(user.value))
    } catch (error) {
      console.error('Failed to fetch user:', error)
      logout()
    }
  }

  // Load user on store initialization
  loadUserFromStorage()

  return {
    user,
    token,
    isAuthenticated,
    isAdmin,
    login,
    register,
    logout,
    fetchCurrentUser,
    loadUserFromStorage
  }
})
