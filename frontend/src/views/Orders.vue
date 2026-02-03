<template>
  <div class="orders-container">
    <div class="orders-header">
      <h1>📦 Đơn Hàng Của Tôi</h1>
      <p class="orders-subtitle">Theo dõi tình trạng đơn hàng của bạn</p>
    </div>

    <div v-if="loading" class="loading">
      <div class="spinner">🔄</div>
      <p>Đang tải đơn hàng...</p>
    </div>

    <div v-else-if="orders.length === 0" class="no-orders">
      <div class="empty-icon">📭</div>
      <h2>Chưa có đơn hàng nào</h2>
      <p>Bạn chưa đặt đơn hàng nào. Hãy bắt đầu mua sắm ngay!</p>
      <button class="btn btn-primary" @click="goToShop">
        <span>Khám Phá Sản Phẩm</span>
        <span class="arrow">→</span>
      </button>
    </div>

    <div v-else class="orders-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-info">
            <h3>🛍️ Đơn Hàng #{{ order.id }}</h3>
            <p class="order-date">📅 {{ formatDate(order.orderDate) }}</p>
          </div>
          <span class="order-status" :class="statusClass(order.status)">
            {{ translateStatus(order.status) }}
          </span>
        </div>

        <div class="order-details">
          <div class="detail-row">
            <span class="detail-label">💰 Tổng tiền:</span>
            <span class="detail-value">{{ formatPrice(order.totalAmount) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">💳 Thanh toán:</span>
            <span class="detail-value">{{ translatePaymentMethod(order.paymentMethod) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">📍 Địa chỉ:</span>
            <span class="detail-value">{{ order.address }}</span>
          </div>
          <div class="detail-row" v-if="order.fullName">
            <span class="detail-label">👤 Người nhận:</span>
            <span class="detail-value">{{ order.fullName }} - {{ order.phone }}</span>
          </div>
        </div>

        <div v-if="order.orderItems && order.orderItems.length" class="order-items">
          <h4>Sản phẩm đã đặt:</h4>
          <div v-for="item in order.orderItems" :key="item.id" class="order-item">
            <div class="item-info">
              <div class="item-name">
                <span class="item-icon">📱</span>
                <span>{{ item.productName }}</span>
                <span class="item-qty">× {{ item.quantity }}</span>
              </div>
              <span class="item-price">{{ formatPrice(item.price * item.quantity) }}</span>
            </div>
            
            <!-- Nút đánh giá - chỉ hiện khi đơn hàng đã thanh toán -->
            <button 
              v-if="canReviewProduct(order, item.productId)"
              @click="openReviewModal(order.id, item.productId, item.productName)"
              class="btn-review"
            >
              ⭐ Đánh giá
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Review Modal -->
    <div v-if="showReviewModal" class="modal-overlay" @click="closeReviewModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ reviewProductName }}</h3>
          <button class="close-btn" @click="closeReviewModal">×</button>
        </div>
        <ReviewForm 
          :product-id="reviewProductId"
          :order-id="reviewOrderId"
          @submitted="handleReviewSubmit"
          @cancel="closeReviewModal"
        />
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
import ReviewForm from '../components/ReviewForm.vue'

export default {
  name: 'Orders',
  components: {
    ReviewForm
  },
  setup() {
    const router = useRouter()
    const cartStore = useCartStore()
    const authStore = useAuthStore()
    const notification = useNotification()
    
    const orders = ref([])
    const loading = ref(false)
    
    // Review modal states
    const showReviewModal = ref(false)
    const reviewOrderId = ref(null)
    const reviewProductId = ref(null)
    const reviewProductName = ref('')
    const reviewedProducts = ref(new Set()) // Track đã review

    const fetchOrders = async () => {
      loading.value = true
      try {
        const userId = authStore.user?.id || cartStore.userId
        const response = await api.getOrdersByUser(userId)
        orders.value = response.data
        
        // Load trạng thái đã review cho các sản phẩm
        await loadReviewedProducts()
      } catch (error) {
        console.error('Error fetching orders:', error)
      } finally {
        loading.value = false
      }
    }

    const loadReviewedProducts = async () => {
      try {
        const myReviews = await api.getMyReviews()
        // Tạo set các key "orderId-productId" đã review
        reviewedProducts.value = new Set(
          myReviews.map(review => `${review.order.id}-${review.product.id}`)
        )
      } catch (error) {
        console.error('Error loading reviewed products:', error)
      }
    }

    const canReviewProduct = (order, productId) => {
      // Các trạng thái cho phép review
      const reviewableStatuses = ['PAID', 'SHIPPED', 'DELIVERED']
      
      // Kiểm tra: 
      // 1. Đơn hàng ở trạng thái cho phép review
      // 2. Chưa review sản phẩm này trong đơn hàng này
      const key = `${order.id}-${productId}`
      return reviewableStatuses.includes(order.status) && !reviewedProducts.value.has(key)
    }

    const openReviewModal = (orderId, productId, productName) => {
      reviewOrderId.value = orderId
      reviewProductId.value = productId
      reviewProductName.value = productName
      showReviewModal.value = true
    }

    const closeReviewModal = () => {
      showReviewModal.value = false
      reviewOrderId.value = null
      reviewProductId.value = null
      reviewProductName.value = ''
    }

    const handleReviewSubmit = async (reviewData) => {
      try {
        await api.createReview(reviewData)
        
        notification.success('Cảm ơn bạn đã đánh giá!')
        
        // Thêm vào danh sách đã review
        const key = `${reviewData.orderId}-${reviewData.productId}`
        reviewedProducts.value.add(key)
        
        closeReviewModal()
      } catch (error) {
        console.error('Error submitting review:', error)
        
        // Xử lý lỗi 401 - token không hợp lệ
        if (error.response?.status === 401) {
          notification.error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!')
          // Đợi một chút để user đọc thông báo trước khi redirect
          setTimeout(() => {
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            router.push('/login')
          }, 2000)
        } else {
          notification.error(error.response?.data?.error || 'Có lỗi xảy ra khi gửi đánh giá')
        }
      }
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('vi-VN', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const formatPrice = (price) => {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0
      }).format(price)
    }

    const translateStatus = (status) => {
      const statusMap = {
        'PENDING': 'Chờ xử lý',
        'PAID': 'Đã thanh toán',
        'SHIPPED': 'Đang giao hàng',
        'DELIVERED': 'Đã giao hàng',
        'CANCELLED': 'Đã hủy'
      }
      return statusMap[status] || status
    }

    const translatePaymentMethod = (method) => {
      const methodMap = {
        'COD': 'Thanh toán khi nhận hàng (COD)',
        'Credit Card': 'Thẻ tín dụng/Ghi nợ',
        'E-wallet': 'Ví điện tử'
      }
      return methodMap[method] || method
    }

    const statusClass = (status) => {
      const classes = {
        'PENDING': 'status-pending',
        'PAID': 'status-paid',
        'SHIPPED': 'status-shipped',
        'DELIVERED': 'status-delivered',
        'CANCELLED': 'status-cancelled'
      }
      return classes[status] || ''
    }

    const goToShop = () => {
      router.push('/')
    }

    onMounted(() => {
      fetchOrders()
    })

    return {
      orders,
      loading,
      formatDate,
      formatPrice,
      translateStatus,
      translatePaymentMethod,
      statusClass,
      goToShop,
      // Review
      showReviewModal,
      reviewOrderId,
      reviewProductId,
      reviewProductName,
      canReviewProduct,
      openReviewModal,
      closeReviewModal,
      handleReviewSubmit
    }
  }
}
</script>

<style scoped>
.orders-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 60px 24px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
}

.orders-header {
  margin-bottom: 50px;
  text-align: center;
  animation: fadeInDown 0.6s ease-out;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.orders-header h1 {
  font-size: 52px;
  font-weight: 900;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -1px;
  text-shadow: 0 4px 20px rgba(102, 126, 234, 0.2);
  animation: gradientShift 3s ease infinite;
  background-size: 200% auto;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.orders-subtitle {
  font-size: 20px;
  color: #64748b;
  font-weight: 600;
  letter-spacing: 0.3px;
}

/* Loading State */
.loading {
  text-align: center;
  padding: 100px 40px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.15);
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.spinner {
  font-size: 72px;
  animation: spin 1.5s ease-in-out infinite;
  margin-bottom: 24px;
  filter: drop-shadow(0 4px 8px rgba(102, 126, 234, 0.3));
}

@keyframes spin {
  0% { transform: rotate(0deg) scale(1); }
  50% { transform: rotate(180deg) scale(1.1); }
  100% { transform: rotate(360deg) scale(1); }
}

.loading p {
  font-size: 19px;
  color: #64748b;
  font-weight: 600;
}

/* Empty State */
.no-orders {
  text-align: center;
  padding: 100px 50px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
  border-radius: 32px;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.15);
  max-width: 700px;
  margin: 0 auto;
  border: 1px solid rgba(102, 126, 234, 0.1);
  position: relative;
  overflow: hidden;
  animation: fadeIn 0.6s ease-out;
}

.no-orders::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.empty-icon {
  font-size: 120px;
  margin-bottom: 32px;
  animation: float 3s ease-in-out infinite;
  filter: drop-shadow(0 8px 16px rgba(102, 126, 234, 0.2));
}

@keyframes float {
  0%, 100% { 
    transform: translateY(0px) rotate(0deg); 
  }
  50% { 
    transform: translateY(-25px) rotate(5deg); 
  }
}

.no-orders h2 {
  font-size: 36px;
  color: #1e293b;
  margin-bottom: 16px;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.no-orders p {
  font-size: 19px;
  color: #64748b;
  margin-bottom: 40px;
  line-height: 1.7;
  font-weight: 500;
}

.btn {
  padding: 18px 48px;
  border: none;
  border-radius: 16px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  gap: 12px;
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.5s;
}

.btn:hover::before {
  left: 100%;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.btn:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.5);
}

.arrow {
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 20px;
}

.no-orders .btn:hover .arrow {
  transform: translateX(8px);
}

/* Orders List */
.orders-list {
  display: grid;
  gap: 32px;
  animation: fadeIn 0.6s ease-out;
}

.order-card {
  background: linear-gradient(135deg, #ffffff 0%, #fafbff 100%);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.12);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(102, 126, 234, 0.08);
}

.order-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  background-size: 200% auto;
  animation: gradientMove 3s linear infinite;
}

@keyframes gradientMove {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}

.order-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at top right, rgba(102, 126, 234, 0.03), transparent 60%);
  pointer-events: none;
}

.order-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.15);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
  padding-bottom: 24px;
  border-bottom: 2px solid rgba(102, 126, 234, 0.1);
  position: relative;
  z-index: 1;
}

.order-info h3 {
  font-size: 28px;
  margin-bottom: 10px;
  color: #1e293b;
  font-weight: 800;
  letter-spacing: -0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.order-date {
  color: #64748b;
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.order-status {
  padding: 12px 24px;
  border-radius: 16px;
  font-weight: 700;
  font-size: 15px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  letter-spacing: 0.5px;
  text-transform: uppercase;
  transition: all 0.3s ease;
}

.order-status:hover {
  transform: scale(1.05);
}

.status-pending {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: #78350f;
  box-shadow: 0 4px 16px rgba(251, 191, 36, 0.4);
}

.status-paid {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: #1e3a8a;
  box-shadow: 0 4px 16px rgba(96, 165, 250, 0.4);
}

.status-shipped {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  color: #064e3b;
  box-shadow: 0 4px 16px rgba(52, 211, 153, 0.4);
}

.status-delivered {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: #14532d;
  box-shadow: 0 4px 16px rgba(34, 197, 94, 0.4);
}

.status-cancelled {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  color: #7f1d1d;
  box-shadow: 0 4px 16px rgba(248, 113, 113, 0.4);
}

/* Order Details */
.order-details {
  margin-bottom: 28px;
  display: grid;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 20px;
  background: rgba(102, 126, 234, 0.03);
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 1px solid rgba(102, 126, 234, 0.08);
}

.detail-row:hover {
  background: rgba(102, 126, 234, 0.06);
  border-color: rgba(102, 126, 234, 0.15);
  transform: translateX(4px);
}

.detail-label {
  font-weight: 700;
  color: #475569;
  font-size: 16px;
  min-width: 150px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-value {
  text-align: right;
  color: #1e293b;
  font-weight: 700;
  font-size: 16px;
  word-break: break-word;
}

/* Order Items */
.order-items {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.04) 0%, rgba(118, 75, 162, 0.04) 100%);
  border-radius: 16px;
  padding: 28px;
  margin-top: 24px;
  border: 1px solid rgba(102, 126, 234, 0.1);
  position: relative;
  z-index: 1;
}

.order-items h4 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #1e293b;
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.3px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-items h4::before {
  content: '🛍️';
  font-size: 24px;
}

.order-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 18px 20px;
  background: white;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 12px;
  border: 1px solid rgba(102, 126, 234, 0.08);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.order-item:last-child {
  margin-bottom: 0;
}

.order-item:hover {
  background: #fafbff;
  transform: translateX(4px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.12);
  border-color: rgba(102, 126, 234, 0.15);
}

.item-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.item-name {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  font-weight: 600;
  color: #1e293b;
  font-size: 16px;
}

.item-icon {
  font-size: 24px;
  filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.2));
}

.item-qty {
  color: #64748b;
  font-weight: 700;
  font-size: 15px;
  background: rgba(102, 126, 234, 0.1);
  padding: 4px 12px;
  border-radius: 8px;
}

.item-price {
  font-weight: 800;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 18px;
  letter-spacing: -0.3px;
}

/* Review Button */
.btn-review {
  padding: 10px 20px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  align-self: flex-start;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
  letter-spacing: 0.3px;
}

.btn-review:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.5);
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(30, 41, 59, 0.8);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
  animation: fadeIn 0.3s ease;
}

.modal-content {
  background: white;
  border-radius: 24px;
  max-width: 650px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideUp 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(102, 126, 234, 0.1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(50px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28px 32px;
  border-bottom: 2px solid rgba(102, 126, 234, 0.1);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
}

.modal-header h3 {
  margin: 0;
  color: #1e293b;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.3px;
}

.close-btn {
  background: rgba(102, 126, 234, 0.08);
  border: none;
  font-size: 32px;
  color: #64748b;
  cursor: pointer;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 300;
  line-height: 1;
}

.close-btn:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: rotate(90deg) scale(1.1);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

/* Responsive */
@media (max-width: 768px) {
  .orders-container {
    padding: 32px 16px;
  }

  .orders-header h1 {
    font-size: 38px;
  }

  .orders-subtitle {
    font-size: 17px;
  }

  .order-card {
    padding: 28px;
  }

  .order-header {
    flex-direction: column;
    gap: 16px;
  }

  .order-info h3 {
    font-size: 24px;
  }

  .order-status {
    align-self: flex-start;
    font-size: 13px;
    padding: 10px 20px;
  }

  .detail-row {
    flex-direction: column;
    gap: 8px;
    padding: 14px 16px;
  }

  .detail-value {
    text-align: left;
    max-width: 100%;
  }

  .detail-label {
    min-width: auto;
  }

  .item-name {
    font-size: 15px;
  }

  .item-price {
    font-size: 16px;
  }

  .modal-content {
    border-radius: 20px;
  }

  .modal-header {
    padding: 20px 24px;
  }

  .modal-header h3 {
    font-size: 20px;
  }

  .no-orders {
    padding: 60px 32px;
  }

  .empty-icon {
    font-size: 100px;
  }

  .no-orders h2 {
    font-size: 28px;
  }
}

/* Smooth scrollbar */
.modal-content::-webkit-scrollbar {
  width: 8px;
}

.modal-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

.modal-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
}

.modal-content::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}
</style>
