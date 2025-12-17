<template>
  <div class="auth-page">
    <router-link to="/" class="home-button">
      <span class="home-icon">🏠</span>
      <span class="home-text">Trang chủ</span>
    </router-link>
    
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <div class="logo-circle">
            <span class="logo-icon">🛒</span>
          </div>
          <h2>Đăng Nhập</h2>
          <p>Chào mừng bạn quay trở lại ShopHub!</p>
        </div>

        <form @submit.prevent="handleLogin" class="auth-form">
          <div class="input-group">
            <div class="input-icon">📧</div>
            <input
              v-model="email"
              type="email"
              required
              placeholder="Email của bạn"
              autocomplete="email"
            />
          </div>

          <div class="input-group">
            <div class="input-icon">🔒</div>
            <input
              v-model="password"
              type="password"
              required
              placeholder="Mật khẩu"
              autocomplete="current-password"
            />
          </div>

          <div v-if="errorMessage" class="error-message">
            ⚠️ {{ errorMessage }}
          </div>

          <button type="submit" class="btn-submit" :disabled="loading">
            <span v-if="loading">
              <span class="spinner"></span>
              Đang đăng nhập...
            </span>
            <span v-else>Đăng Nhập</span>
          </button>
        </form>

        <div class="auth-footer">
          <p>Chưa có tài khoản? <router-link to="/register" class="link-primary">Đăng ký ngay</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  loading.value = true
  errorMessage.value = ''

  const result = await authStore.login(email.value, password.value)

  if (result.success) {
    // Admin chuyển đến Dashboard, user thường về trang chủ
    if (authStore.isAdmin) {
      router.push('/admin')
    } else {
      router.push('/')
    }
  } else {
    errorMessage.value = result.message
  }

  loading.value = false
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  margin: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.home-button {
  position: absolute;
  top: 30px;
  left: 30px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  color: white;
  text-decoration: none;
  font-weight: 600;
  font-size: 15px;
  transition: all 0.3s ease;
  z-index: 10;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.home-button:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateX(-4px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.home-icon {
  font-size: 20px;
}

.home-text {
  font-weight: 700;
}

.auth-page::before {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  border-radius: 50%;
  top: -250px;
  right: -250px;
  animation: float 6s ease-in-out infinite;
}

.auth-page::after {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255,255,255,0.08) 0%, transparent 70%);
  border-radius: 50%;
  bottom: -200px;
  left: -200px;
  animation: float 8s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}

.auth-container {
  width: 100%;
  max-width: 440px;
  position: relative;
  z-index: 1;
  padding: 40px 20px;
}

.auth-card {
  background: white;
  border-radius: 24px;
  padding: 50px 45px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.auth-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-circle {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.logo-icon {
  font-size: 40px;
}

.auth-header h2 {
  font-size: 32px;
  color: #2c3e50;
  margin-bottom: 10px;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.auth-header p {
  color: #7f8c8d;
  font-size: 16px;
  font-weight: 500;
}

.auth-form {
  margin-bottom: 30px;
}

.input-group {
  position: relative;
  margin-bottom: 24px;
}

.input-icon {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 20px;
  z-index: 1;
}

.input-group input {
  width: 100%;
  padding: 18px 20px 18px 56px;
  border: 2px solid #e8ecef;
  border-radius: 14px;
  font-size: 16px;
  transition: all 0.3s ease;
  box-sizing: border-box;
  font-family: inherit;
  font-weight: 500;
  background: #f8f9fa;
}

.input-group input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.input-group input::placeholder {
  color: #adb5bd;
  font-weight: 400;
}

.error-message {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: white;
  padding: 16px 20px;
  border-radius: 12px;
  margin-bottom: 24px;
  font-size: 15px;
  font-weight: 600;
  text-align: center;
  animation: shake 0.5s ease;
  box-shadow: 0 4px 12px rgba(238, 90, 111, 0.3);
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-8px); }
  75% { transform: translateX(8px); }
}

.btn-submit {
  width: 100%;
  padding: 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.btn-submit:active:not(:disabled) {
  transform: translateY(0);
}

.btn-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.spinner {
  width: 18px;
  height: 18px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.auth-footer {
  text-align: center;
  padding-top: 24px;
  border-top: 2px solid #f1f3f5;
}

.auth-footer p {
  color: #7f8c8d;
  font-size: 15px;
  font-weight: 500;
}

.link-primary {
  color: #667eea;
  text-decoration: none;
  font-weight: 700;
  transition: all 0.3s ease;
  position: relative;
}

.link-primary::after {
  content: '';
  position: absolute;
  width: 0;
  height: 2px;
  bottom: -2px;
  left: 0;
  background: #667eea;
  transition: width 0.3s ease;
}

.link-primary:hover {
  color: #764ba2;
}

.link-primary:hover::after {
  width: 100%;
}

@media (max-width: 768px) {
  .home-button {
    top: 20px;
    left: 20px;
    padding: 10px 16px;
    font-size: 14px;
  }

  .home-icon {
    font-size: 18px;
  }

  .auth-card {
    padding: 40px 30px;
  }

  .auth-header h2 {
    font-size: 28px;
  }

  .logo-circle {
    width: 70px;
    height: 70px;
  }

  .logo-icon {
    font-size: 36px;
  }
}
</style>
