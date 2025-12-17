<template>
  <div class="auth-page">
    <router-link to="/" class="home-button">
      <span class="home-icon">🏠</span>
      <span class="home-text">Trang chủ</span>
    </router-link>
    
    <div class="auth-container">
      <!-- Marketing Panel -->
      <div class="marketing-panel">
        <div class="marketing-content">
          <div class="brand">
            <div class="brand-logo">🛒</div>
            <h1 class="brand-name">ShopHub</h1>
            <p class="brand-tagline">Tech Gadgets Store</p>
          </div>

          <div class="features">
            <div class="feature-item">
              <div class="feature-icon">🚀</div>
              <div class="feature-text">
                <h3>Giao hàng nhanh</h3>
                <p>Miễn phí vận chuyển cho đơn trên 500k</p>
              </div>
            </div>

            <div class="feature-item">
              <div class="feature-icon">🔒</div>
              <div class="feature-text">
                <h3>Thanh toán an toàn</h3>
                <p>Bảo mật thông tin tuyệt đối</p>
              </div>
            </div>

            <div class="feature-item">
              <div class="feature-icon">🎁</div>
              <div class="feature-text">
                <h3>Ưu đãi độc quyền</h3>
                <p>Giảm giá 20% cho thành viên mới</p>
              </div>
            </div>

            <div class="feature-item">
              <div class="feature-icon">💳</div>
              <div class="feature-text">
                <h3>Tích điểm đổi quà</h3>
                <p>Mỗi đơn hàng đều được hoàn xu</p>
              </div>
            </div>
          </div>

          <div class="stats">
            <div class="stat-item">
              <div class="stat-number">10K+</div>
              <div class="stat-label">Khách hàng</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">500+</div>
              <div class="stat-label">Sản phẩm</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">4.9★</div>
              <div class="stat-label">Đánh giá</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Register Form -->
      <div class="auth-card">
        <div class="auth-header">
          <div class="logo-circle">
            <span class="logo-icon">🛒</span>
          </div>
          <h2>Đăng Ký Tài Khoản</h2>
          <p>Tạo tài khoản mới để bắt đầu mua sắm!</p>
        </div>

        <!-- Social Login -->
        <div class="social-login">
          <p class="social-text">Đăng ký bằng tài khoản mạng xã hội</p>
          <div class="social-buttons">
            <button type="button" class="btn-social google">
              <span class="social-icon">🔵</span> Google
            </button>
            <button type="button" class="btn-social facebook">
              <span class="social-icon">📘</span> Facebook
            </button>
          </div>
          <div class="divider">
            <span>Hoặc điền thông tin sau</span>
          </div>
        </div>

        <form @submit.prevent="handleRegister" class="auth-form">
          <!-- Personal Information Section -->
          <div class="section-title">Thông tin cá nhân</div>
          
          <div class="form-row">
            <div class="input-group half">
              <div class="input-icon">👤</div>
              <input
                v-model="fullName"
                type="text"
                required
                placeholder="Họ và tên"
                autocomplete="name"
              />
            </div>

            <div class="input-group half">
              <div class="input-icon">📱</div>
              <input
                v-model="phone"
                type="tel"
                required
                placeholder="Số điện thoại"
                autocomplete="tel"
              />
            </div>
          </div>

          <div class="input-group">
            <div class="input-icon">📧</div>
            <input
              v-model="email"
              type="email"
              required
              placeholder="Email"
              autocomplete="email"
            />
          </div>

          <!-- Password Section -->
          <div class="section-title">Tạo mật khẩu</div>

          <div class="input-group">
            <div class="input-icon">🔒</div>
            <input
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              required
              placeholder="Mật khẩu (tối thiểu 6 ký tự)"
              autocomplete="new-password"
              minlength="6"
              @input="checkPasswordStrength"
            />
            <button type="button" class="toggle-password" @click="showPassword = !showPassword">
              {{ showPassword ? '👁️' : '👁️‍🗨️' }}
            </button>
          </div>

          <div v-if="password" class="password-strength">
            <div class="strength-bar">
              <div class="strength-fill" :class="passwordStrength"></div>
            </div>
            <span class="strength-text">{{ passwordStrengthText }}</span>
          </div>

          <div class="input-group">
            <div class="input-icon">🔒</div>
            <input
              v-model="confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              required
              placeholder="Nhập lại mật khẩu"
              autocomplete="new-password"
            />
            <button type="button" class="toggle-password" @click="showConfirmPassword = !showConfirmPassword">
              {{ showConfirmPassword ? '👁️' : '👁️‍🗨️' }}
            </button>
          </div>

          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <button type="submit" class="btn-submit" :disabled="loading">
            <div v-if="loading" class="spinner"></div>
            <span v-if="loading">Đang đăng ký...</span>
            <span v-else>Hoàn Tất Đăng Ký</span>
          </button>
        </form>

        <div class="auth-footer">
          <p>Đã có tài khoản? <router-link to="/login" class="link-primary">Đăng nhập ngay</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const fullName = ref('')
const email = ref('')
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const errorMessage = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const passwordStrength = ref('')

const passwordStrengthText = computed(() => {
  if (passwordStrength.value === 'weak') return 'Yếu'
  if (passwordStrength.value === 'medium') return 'Trung bình'
  if (passwordStrength.value === 'strong') return 'Mạnh'
  return ''
})

const checkPasswordStrength = () => {
  const pass = password.value
  if (pass.length < 6) {
    passwordStrength.value = 'weak'
  } else if (pass.length < 10) {
    passwordStrength.value = 'medium'
  } else if (pass.length >= 10 && /[A-Z]/.test(pass) && /[0-9]/.test(pass)) {
    passwordStrength.value = 'strong'
  } else {
    passwordStrength.value = 'medium'
  }
}

const handleRegister = async () => {
  errorMessage.value = ''

  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Mật khẩu xác nhận không khớp!'
    return
  }

  if (password.value.length < 6) {
    errorMessage.value = 'Mật khẩu phải có ít nhất 6 ký tự!'
    return
  }

  loading.value = true

  const result = await authStore.register(
    email.value,
    password.value,
    fullName.value,
    phone.value
  )

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
  max-width: 1200px;
  position: relative;
  z-index: 1;
  display: flex;
  gap: 0;
  align-items: stretch;
  padding: 40px 20px;
}

/* Marketing Panel */
.marketing-panel {
  flex: 1;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 24px 0 0 24px;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border-right: 1px solid rgba(255, 255, 255, 0.2);
  animation: slideLeft 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideLeft {
  from {
    opacity: 0;
    transform: translateX(-40px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.marketing-content {
  color: white;
}

.brand {
  text-align: center;
  margin-bottom: 50px;
}

.brand-logo {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.brand-name {
  font-size: 42px;
  font-weight: 900;
  margin-bottom: 10px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.brand-tagline {
  font-size: 16px;
  opacity: 0.95;
  font-weight: 500;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.features {
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  gap: 16px;
  margin-bottom: 28px;
  animation: fadeInUp 0.6s ease both;
}

.feature-item:nth-child(1) { animation-delay: 0.1s; }
.feature-item:nth-child(2) { animation-delay: 0.2s; }
.feature-item:nth-child(3) { animation-delay: 0.3s; }
.feature-item:nth-child(4) { animation-delay: 0.4s; }

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feature-icon {
  font-size: 32px;
  flex-shrink: 0;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
}

.feature-text h3 {
  font-size: 18px;
  margin-bottom: 6px;
  font-weight: 700;
}

.feature-text p {
  font-size: 14px;
  opacity: 0.9;
  line-height: 1.5;
}

.stats {
  display: flex;
  gap: 30px;
  padding-top: 30px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 900;
  margin-bottom: 8px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
  font-weight: 600;
}

.auth-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 0 24px 24px 0;
  padding: 35px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideRight 0.6s cubic-bezier(0.16, 1, 0.3, 1);
  overflow-y: auto;
  max-height: 120vh;
  display: flex;
  flex-direction: column;
}

/* Custom scrollbar for auth-card */
.auth-card::-webkit-scrollbar {
  width: 8px;
}

.auth-card::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

.auth-card::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.3);
  border-radius: 4px;
}

.auth-card::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.5);
}

@keyframes slideRight {
  from {
    opacity: 0;
    transform: translateX(40px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.auth-header {
  text-align: center;
  margin-bottom: 25px;
}

.logo-circle {
  width: 70px;
  height: 70px;
  margin: 0 auto 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.logo-icon {
  font-size: 36px;
}

.auth-header h2 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 8px;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.auth-header p {
  color: #7f8c8d;
  font-size: 15px;
  font-weight: 500;
}

/* Social Login Styles */
.social-login {
  margin-bottom: 25px;
}

.social-text {
  text-align: center;
  color: #7f8c8d;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 14px;
}

.social-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.btn-social {
  flex: 1;
  padding: 14px;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.9);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-social:hover {
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
  transform: translateY(-2px);
}

.social-icon {
  font-size: 20px;
}

.divider {
  position: relative;
  text-align: center;
  margin: 20px 0;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 42%;
  height: 1px;
  background: #e8ecef;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  background: rgba(255, 255, 255, 0.95);
  padding: 0 12px;
  color: #adb5bd;
  font-size: 13px;
  font-weight: 600;
  position: relative;
}

/* Form Styles */
.auth-form {
  margin-bottom: 25px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 14px;
  margin-top: 20px;
  padding-bottom: 6px;
  border-bottom: 2px solid rgba(102, 126, 234, 0.15);
}

.section-title:first-of-type {
  margin-top: 0;
}

.form-row {
  display: flex;
  gap: 14px;
  margin-bottom: 16px;
}

.input-group {
  position: relative;
  margin-bottom: 16px;
}

.input-group.half {
  flex: 1;
  margin-bottom: 0;
}

.input-icon {
  position: absolute;
  left: 18px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  z-index: 1;
}

.input-group input {
  width: 100%;
  padding: 14px 18px 14px 52px;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  box-sizing: border-box;
  font-family: inherit;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.9);
}

.input-group input:focus {
  outline: none;
  border-color: #667eea;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.input-group input::placeholder {
  color: #adb5bd;
  font-weight: 400;
}

.toggle-password {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 4px;
  opacity: 0.6;
  transition: opacity 0.3s ease;
}

.toggle-password:hover {
  opacity: 1;
}

/* Password Strength */
.password-strength {
  margin-top: -10px;
  margin-bottom: 16px;
  padding: 0 4px;
}

.strength-bar {
  height: 4px;
  background: #e8ecef;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 6px;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.strength-fill.weak {
  width: 33%;
  background: linear-gradient(90deg, #ff6b6b, #ee5a6f);
}

.strength-fill.medium {
  width: 66%;
  background: linear-gradient(90deg, #ffa500, #ff8c00);
}

.strength-fill.strong {
  width: 100%;
  background: linear-gradient(90deg, #51cf66, #37b24d);
}

.strength-text {
  font-size: 12px;
  font-weight: 600;
  color: #7f8c8d;
}

.error-message {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: white;
  padding: 14px 18px;
  border-radius: 12px;
  margin-bottom: 20px;
  font-size: 14px;
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
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 14px;
  font-size: 16px;
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
  padding-top: 20px;
  border-top: 2px solid rgba(102, 126, 234, 0.15);
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

@media (max-width: 1024px) {
  .auth-container {
    flex-direction: column;
    max-width: 600px;
  }

  .marketing-panel {
    border-radius: 24px 24px 0 0;
    padding: 40px 30px;
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  }

  .brand {
    margin-bottom: 30px;
  }

  .brand-name {
    font-size: 36px;
  }

  .features {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    margin-bottom: 30px;
  }

  .feature-item {
    margin-bottom: 0;
  }

  .auth-card {
    border-radius: 0 0 24px 24px;
    max-height: none;
  }
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

  .marketing-panel {
    padding: 30px 20px;
  }

  .brand-logo {
    width: 60px;
    height: 60px;
    font-size: 30px;
  }

  .brand-name {
    font-size: 32px;
  }

  .features {
    grid-template-columns: 1fr;
  }

  .feature-icon {
    width: 40px;
    height: 40px;
    font-size: 24px;
  }

  .feature-text h3 {
    font-size: 16px;
  }

  .stats {
    gap: 20px;
  }

  .stat-number {
    font-size: 24px;
  }

  .auth-card {
    padding: 40px 30px;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .input-group.half {
    margin-bottom: 20px;
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

  .social-buttons {
    flex-direction: column;
  }
}
</style>
