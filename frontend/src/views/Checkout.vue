<template>
  <div class="checkout-container">
    <div class="checkout-header">
      <h1>💳 Thanh Toán Đơn Hàng</h1>
      <p class="checkout-subtitle">Vui lòng điền thông tin giao hàng</p>
    </div>

    <div class="checkout-content">
      <div class="checkout-form">
        <div class="form-header">
          <h2>📦 Thông Tin Giao Hàng</h2>
        </div>
        <form @submit.prevent="placeOrder">
          <div class="form-group">
            <label>Họ và Tên *</label>
            <input 
              v-model="form.fullName" 
              type="text" 
              required 
              placeholder="Nhập họ và tên đầy đủ"
              pattern="[^0-9]*"
              title="Tên không được chứa số"
              @input="validateName"
            />
            <span v-if="errors.fullName" class="error-message">{{ errors.fullName }}</span>
          </div>

          <div class="form-group">
            <label>Số Điện Thoại *</label>
            <input 
              v-model="form.phone" 
              type="tel" 
              required 
              placeholder="Nhập 10 số điện thoại"
              pattern="[0-9]{10}"
              maxlength="10"
              title="Số điện thoại phải đúng 10 số"
              @input="validatePhone"
            />
            <span v-if="errors.phone" class="error-message">{{ errors.phone }}</span>
          </div>

          <div class="form-group">
            <label>Địa Chỉ Giao Hàng *</label>
            <textarea v-model="form.address" rows="3" required placeholder="Nhập địa chỉ chi tiết"></textarea>
          </div>

          <div class="form-group">
            <label>Phương Thức Thanh Toán *</label>
            <select v-model="form.paymentMethod" required>
              <option value="">Chọn phương thức thanh toán</option>
              <option value="COD">💵 Thanh toán khi nhận hàng (COD)</option>
              <option value="Credit Card">💳 Thẻ tín dụng/Ghi nợ</option>
              <option value="E-wallet">📱 Ví điện tử</option>
            </select>
          </div>

          <div class="form-group">
            <label>Ghi Chú Đơn Hàng</label>
            <textarea v-model="form.notes" rows="2" placeholder="Ghi chú cho người bán (không bắt buộc)"></textarea>
          </div>

          <button type="submit" class="btn btn-checkout" :disabled="submitting">
            <span v-if="!submitting">Đặt Hàng Ngay</span>
            <span v-else>Đang Xử Lý...</span>
            <span class="icon">🚀</span>
          </button>
        </form>
      </div>

      <div class="order-summary">
        <div class="summary-header">
          <h2>📋 Thông Tin Đơn Hàng</h2>
        </div>
        
        <div class="summary-items">
          <div v-for="item in cartStore.items" :key="item.id" class="summary-item">
            <div class="item-details">
              <span class="item-name">{{ item.product?.name }}</span>
              <span class="item-qty">× {{ item.quantity }}</span>
            </div>
            <span class="item-price">{{ formatPrice(item.product?.price * item.quantity) }}</span>
          </div>
        </div>

        <div class="summary-divider"></div>

        <div class="summary-row">
          <span>Tạm tính</span>
          <span>{{ formatPrice(cartStore.cartTotal) }}</span>
        </div>
        <div class="summary-row">
          <span>Phí vận chuyển</span>
          <span class="free">MIỄN PHÍ</span>
        </div>

        <div class="summary-divider"></div>

        <div class="summary-total">
          <span>Tổng cộng</span>
          <span class="total-amount">{{ formatPrice(cartStore.cartTotal) }}</span>
        </div>

        <div class="trust-info">
          <div class="trust-item">
            <span>✓</span>
            <span>Thanh toán an toàn</span>
          </div>
          <div class="trust-item">
            <span>✓</span>
            <span>Miễn phí vận chuyển</span>
          </div>
          <div class="trust-item">
            <span>✓</span>
            <span>Đổi trả trong 7 ngày</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { useAuthStore } from '../stores/auth'
import { useNotification } from '../composables/useNotification'
import api from '../services/api'

export default {
  name: 'Checkout',
  setup() {
    const router = useRouter()
    const cartStore = useCartStore()
    const authStore = useAuthStore()
    const { success, error, warning } = useNotification()
    const submitting = ref(false)
    const showPaymentModal = ref(false)

    const form = ref({
      fullName: '',
      phone: '',
      address: '',
      paymentMethod: '',
      notes: ''
    })

    const paymentForm = ref({
      cardNumber: '',
      cardName: '',
      expiry: '',
      cvv: ''
    })

    const errors = ref({
      fullName: '',
      phone: ''
    })

    const validateName = (e) => {
      const value = e.target.value
      if (/\d/.test(value)) {
        errors.value.fullName = 'Tên không được chứa số'
        form.value.fullName = value.replace(/\d/g, '')
      } else {
        errors.value.fullName = ''
      }
    }

    const validatePhone = (e) => {
      const value = e.target.value
      // Chỉ cho phép số
      form.value.phone = value.replace(/\D/g, '')
      
      if (form.value.phone.length > 0 && form.value.phone.length < 10) {
        errors.value.phone = `Cần ${10 - form.value.phone.length} số nữa`
      } else if (form.value.phone.length === 10) {
        errors.value.phone = ''
      } else {
        errors.value.phone = ''
      }
    }

    const placeOrder = async () => {
      if (cartStore.items.length === 0) {
        warning('Giỏ hàng của bạn đang trống')
        return
      }

      if (!authStore.user || !authStore.user.id) {
        warning('Vui lòng đăng nhập để đặt hàng')
        router.push('/login')
        return
      }

      // Nếu chọn thanh toán online, hiện modal thanh toán
      if (form.value.paymentMethod === 'Credit Card' || form.value.paymentMethod === 'E-wallet') {
        showPaymentModal.value = true
        return
      }

      // COD - tạo đơn ngay với status PENDING
      await createOrder('PENDING')
    }

    const processPayment = async () => {
      // Giả lập xử lý thanh toán
      submitting.value = true
      
      // Kiểm tra thông tin thanh toán cơ bản
      if (form.value.paymentMethod === 'Credit Card') {
        if (!paymentForm.value.cardNumber || !paymentForm.value.cardName || 
            !paymentForm.value.expiry || !paymentForm.value.cvv) {
          warning('Vui lòng điền đầy đủ thông tin thẻ')
          submitting.value = false
          return
        }
      }

      // Giả lập delay thanh toán
      await new Promise(resolve => setTimeout(resolve, 1500))
      
      showPaymentModal.value = false
      
      // Tạo đơn với status PAID (đã thanh toán)
      await createOrder('PAID')
    }

    const createOrder = async (status) => {
      submitting.value = true
      try {
        const order = {
          user: { id: authStore.user.id },
          fullName: form.value.fullName,
          phone: form.value.phone,
          address: form.value.address,
          paymentMethod: form.value.paymentMethod,
          notes: form.value.notes,
          totalAmount: cartStore.cartTotal,
          status: status, // PENDING cho COD, PAID cho thanh toán trước
          orderItems: cartStore.items.map(item => ({
            product: { id: item.product.id },
            productName: item.product.name,
            price: item.product.price,
            quantity: item.quantity
          }))
        }

        await api.createOrder(order)
        await cartStore.clearCart()
        
        const message = status === 'PAID' 
          ? 'Thanh toán thành công! Đơn hàng của bạn đang được chuẩn bị.' 
          : 'Đặt hàng thành công! Bạn sẽ thanh toán khi nhận hàng.'
        success(message)
        router.push('/orders')
      } catch (err) {
        console.error('Error placing order:', err)
        error('Đặt hàng thất bại. Vui lòng thử lại.')
      } finally {
        submitting.value = false
      }
    }

    const closePaymentModal = () => {
      showPaymentModal.value = false
      paymentForm.value = {
        cardNumber: '',
        cardName: '',
        expiry: '',
        cvv: ''
      }
    }

    const formatPrice = (price) => {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0
      }).format(price)
    }

    onMounted(() => {
      if (cartStore.items.length === 0) {
        cartStore.fetchCart()
      }
      
      // Tự động điền thông tin từ user đã đăng nhập
      if (authStore.user) {
        form.value.fullName = authStore.user.fullName || authStore.user.username || ''
        form.value.phone = authStore.user.phone || ''
        form.value.address = authStore.user.address || ''
      }
    })

    return {
      form,
      cartStore,
      submitting,
      errors,
      showPaymentModal,
      paymentForm,
      placeOrder,
      processPayment,
      closePaymentModal,
      formatPrice,
      validateName,
      validatePhone
    }
  }
}
</script>

<style scoped>
.checkout-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
}

.checkout-header {
  margin-bottom: 40px;
  text-align: center;
}

.checkout-header h1 {
  font-size: 42px;
  font-weight: 800;
  color: #2c3e50;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C06C84 50%, #6C5B7B 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.checkout-subtitle {
  font-size: 18px;
  color: #7f8c8d;
  font-weight: 500;
}

.checkout-content {
  display: grid;
  grid-template-columns: 1fr 450px;
  gap: 40px;
  align-items: start;
}

/* Form Styles */
.checkout-form {
  background: white;
  border-radius: 20px;
  padding: 35px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.form-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 3px solid #f1f3f5;
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  font-weight: 600;
  color: #2c3e50;
  font-size: 15px;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid #e8ecef;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  font-family: inherit;
  background: #f8f9fa;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #FF6B9D;
  background: white;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-group select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23333' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 18px center;
  padding-right: 45px;
}

.error-message {
  display: block;
  color: #e74c3c;
  font-size: 13px;
  font-weight: 500;
  margin-top: 8px;
  animation: shake 0.3s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.form-group input:invalid:not(:placeholder-shown),
.form-group textarea:invalid:not(:placeholder-shown) {
  border-color: #e74c3c;
}

.form-group input:valid:not(:placeholder-shown),
.form-group textarea:valid:not(:placeholder-shown) {
  border-color: #27ae60;
}

.btn-checkout {
  width: 100%;
  padding: 18px 30px;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #FF6B9D 0%, #C06C84 100%);
  color: white;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
  margin-top: 30px;
}

.btn-checkout:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(255, 107, 157, 0.6);
  background: linear-gradient(135deg, #FF8AB4 0%, #D07B95 100%);
}

.btn-checkout:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-checkout .icon {
  font-size: 22px;
  transition: transform 0.3s ease;
}

.btn-checkout:hover:not(:disabled) .icon {
  transform: scale(1.2);
}

/* Order Summary */
.order-summary {
  background: white;
  border-radius: 20px;
  padding: 35px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  position: sticky;
  top: 100px;
}

.summary-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 3px solid #f1f3f5;
}

.summary-items {
  margin-bottom: 20px;
}

.summary-item {
  padding: 15px 0;
  border-bottom: 1px solid #f1f3f5;
}

.item-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.item-name {
  font-weight: 600;
  color: #2c3e50;
  font-size: 15px;
  flex: 1;
}

.item-qty {
  color: #7f8c8d;
  font-size: 14px;
  margin-left: 10px;
}

.item-price {
  display: block;
  font-weight: 700;
  color: #27ae60;
  font-size: 16px;
  text-align: right;
}

.summary-divider {
  height: 2px;
  background: linear-gradient(90deg, transparent, #dee2e6, transparent);
  margin: 20px 0;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 15px;
  color: #7f8c8d;
}

.summary-row span:first-child {
  font-weight: 500;
}

.summary-row span:last-child {
  font-weight: 600;
  color: #2c3e50;
}

.free {
  color: #27ae60 !important;
  font-weight: 700 !important;
  font-size: 14px;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
  border-top: 3px solid #f1f3f5;
  margin-top: 10px;
}

.total-amount {
  font-size: 28px !important;
  font-weight: 800 !important;
  background: linear-gradient(135deg, #FF6B9D 0%, #C06C84 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.trust-info {
  margin-top: 25px;
  padding-top: 25px;
  border-top: 2px solid #f1f3f5;
}

.trust-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  color: #7f8c8d;
  font-size: 14px;
  font-weight: 500;
}

.trust-item span:first-child {
  color: #27ae60;
  font-weight: 700;
  font-size: 18px;
}

/* Responsive */
@media (max-width: 1024px) {
  .checkout-content {
    grid-template-columns: 1fr;
  }

  .order-summary {
    position: static;
  }
}

@media (max-width: 768px) {
  .checkout-container {
    padding: 20px 15px;
  }

  .checkout-header h1 {
    font-size: 32px;
  }

  .checkout-form,
  .order-summary {
    padding: 25px;
  }
}
</style>
