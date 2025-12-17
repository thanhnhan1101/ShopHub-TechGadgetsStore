<template>
  <header class="header">
    <div class="container-header">
      <router-link to="/" class="logo">
        <h1>🛒 ShopHub</h1>
      </router-link>
      
      <nav class="nav">
        <router-link to="/" class="nav-link">Trang chủ</router-link>
        <router-link v-if="isAdmin" to="/admin" class="nav-link admin-link">
          📊 Dashboard
        </router-link>
        <router-link v-if="isAdmin" to="/admin/categories" class="nav-link">Quản Lý</router-link>
        <router-link v-if="isAuthenticated" to="/orders" class="nav-link">Đơn hàng</router-link>
        <router-link to="/cart" class="nav-link cart-link">
          🛒 Giỏ hàng
          <span v-if="cartCount > 0" class="badge">{{ cartCount }}</span>
        </router-link>

        <!-- User Menu -->
        <div v-if="isAuthenticated" class="user-menu">
          <div class="user-info" @click="toggleDropdown">
            <span class="user-avatar">{{ userInitial }}</span>
            <span class="user-name">{{ userName }}</span>
            <span class="dropdown-arrow">▼</span>
          </div>
          <div v-if="showDropdown" class="dropdown">
            <div class="dropdown-item user-details">
              <strong>{{ user.fullName }}</strong>
              <span class="user-role">{{ user.role }}</span>
            </div>
            <div class="dropdown-divider"></div>
            <button @click="handleLogout" class="dropdown-item logout-btn">
              🚪 Đăng xuất
            </button>
          </div>
        </div>

        <!-- Auth Links -->
        <div v-else class="auth-links">
          <router-link to="/login" class="nav-link btn-login">
            👤 Đăng nhập
          </router-link>
        </div>
      </nav>
    </div>
  </header>
</template>

<script>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'Header',
  setup() {
    const router = useRouter()
    const cartStore = useCartStore()
    const authStore = useAuthStore()
    const showDropdown = ref(false)
    
    const cartCount = computed(() => cartStore.cartCount)
    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const isAdmin = computed(() => authStore.isAdmin)
    const user = computed(() => authStore.user)
    
    const userName = computed(() => {
      if (!authStore.user) return ''
      return authStore.user.fullName?.split(' ').slice(-1)[0] || authStore.user.email
    })

    const userInitial = computed(() => {
      if (!authStore.user?.fullName) return '👤'
      return authStore.user.fullName.charAt(0).toUpperCase()
    })

    const toggleDropdown = () => {
      showDropdown.value = !showDropdown.value
    }

    const handleLogout = () => {
      authStore.logout()
      showDropdown.value = false
      router.push('/')
    }

    // Close dropdown when clicking outside
    const handleClickOutside = (event) => {
      if (!event.target.closest('.user-menu')) {
        showDropdown.value = false
      }
    }
    
    onMounted(() => {
      cartStore.fetchCart()
      document.addEventListener('click', handleClickOutside)
    })
    
    return {
      cartCount,
      isAuthenticated,
      isAdmin,
      user,
      userName,
      userInitial,
      showDropdown,
      toggleDropdown,
      handleLogout
    }
  }
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 18px 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(10px);
}

.container-header {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  text-decoration: none;
  color: white;
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.05);
}

.logo h1 {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.5px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav {
  display: flex;
  gap: 20px;
  align-items: center;
}

.auth-links {
  display: flex;
  gap: 12px;
  align-items: center;
}

.btn-login {
  background: white !important;
  color: #667eea !important;
  padding: 10px 28px !important;
  border-radius: 25px !important;
  font-weight: 700 !important;
  box-shadow: 0 4px 15px rgba(255, 255, 255, 0.3);
}

.btn-login:hover {
  background: rgba(255, 255, 255, 0.95) !important;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 255, 255, 0.4);
}

/* User Menu Styles */
.user-menu {
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.25);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.user-name {
  font-weight: 600;
  font-size: 15px;
}

.dropdown-arrow {
  font-size: 10px;
  transition: transform 0.3s ease;
}

.user-info:hover .dropdown-arrow {
  transform: translateY(2px);
}

.dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  min-width: 220px;
  overflow: hidden;
  animation: slideDown 0.3s ease;
  z-index: 1000;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-item {
  padding: 14px 20px;
  color: #2c3e50;
  font-size: 14px;
  transition: background 0.2s ease;
  width: 100%;
  text-align: left;
  border: none;
  background: none;
  cursor: pointer;
  display: block;
}

.dropdown-item.user-details {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  cursor: default;
}

.dropdown-item.user-details strong {
  display: block;
  font-size: 15px;
  margin-bottom: 4px;
  color: #2c3e50;
}

.user-role {
  display: inline-block;
  padding: 3px 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.dropdown-divider {
  height: 1px;
  background: #e9ecef;
  margin: 0;
}

.logout-btn {
  font-weight: 600;
  color: #e74c3c;
}

.logout-btn:hover {
  background: #fee;
}
.nav-link {
  color: rgba(255, 255, 255, 0.95);
  text-decoration: none;
  font-weight: 600;
  font-size: 15px;
  transition: all 0.3s ease;
  position: relative;
  padding: 8px 16px;
  border-radius: 8px;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: white;
  transition: width 0.3s ease;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
}

.nav-link:hover::after {
  width: 80%;
}

.cart-link {
  position: relative;
  background: rgba(255, 255, 255, 0.2);
  padding: 10px 20px !important;
  border-radius: 25px;
}

.cart-link:hover {
  background: rgba(255, 255, 255, 0.3);
}

.badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(245, 87, 108, 0.4);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}
</style>
