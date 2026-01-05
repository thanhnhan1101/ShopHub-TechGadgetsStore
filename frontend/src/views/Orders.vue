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
            <div class="item-name">
              <span class="item-icon">📱</span>
              <span>{{ item.productName }}</span>
              <span class="item-qty">× {{ item.quantity }}</span>
            </div>
            <span class="item-price">{{ formatPrice(item.price * item.quantity) }}</span>
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
import api from '../services/api'

export default {
  name: 'Orders',
  setup() {
    const router = useRouter()
    const cartStore = useCartStore()
    const authStore = useAuthStore()
    const orders = ref([])
    const loading = ref(false)

    const fetchOrders = async () => {
      loading.value = true
      try {
        const userId = authStore.user?.id || cartStore.userId
        const response = await api.getOrdersByUser(userId)
        orders.value = response.data
      } catch (error) {
        console.error('Error fetching orders:', error)
      } finally {
        loading.value = false
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
      goToShop
    }
  }
}
</script>

<style scoped>
.orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.orders-header {
  margin-bottom: 40px;
  text-align: center;
}

.orders-header h1 {
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

.orders-subtitle {
  font-size: 18px;
  color: #7f8c8d;
  font-weight: 500;
}

/* Loading State */
.loading {
  text-align: center;
  padding: 80px 40px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.spinner {
  font-size: 60px;
  animation: spin 2s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading p {
  font-size: 18px;
  color: #7f8c8d;
  font-weight: 500;
}

/* Empty State */
.no-orders {
  text-align: center;
  padding: 80px 40px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.08);
  max-width: 600px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 100px;
  margin-bottom: 30px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.no-orders h2 {
  font-size: 32px;
  color: #2c3e50;
  margin-bottom: 15px;
  font-weight: 700;
}

.no-orders p {
  font-size: 18px;
  color: #7f8c8d;
  margin-bottom: 40px;
}

.no-orders .btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 15px 40px;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #FF6B9D 0%, #C06C84 100%);
  border: none;
  border-radius: 50px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.4);
}

.no-orders .btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.6);
  background: linear-gradient(135deg, #FF8AB4 0%, #D07B95 100%);
}

.arrow {
  transition: transform 0.3s ease;
}

.no-orders .btn:hover .arrow {
  transform: translateX(5px);
}

/* Orders List */
.orders-list {
  display: grid;
  gap: 25px;
}

.order-card {
  background: white;
  border-radius: 20px;
  padding: 35px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.order-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 5px;
  background: linear-gradient(90deg, #FF6B9D 0%, #C06C84 50%, #6C5B7B 100%);
}

.order-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 3px solid #f1f3f5;
}

.order-info h3 {
  font-size: 24px;
  margin-bottom: 8px;
  color: #2c3e50;
  font-weight: 700;
}

.order-date {
  color: #7f8c8d;
  font-size: 15px;
  font-weight: 500;
  margin: 0;
}

.order-status {
  padding: 10px 20px;
  border-radius: 25px;
  font-weight: 700;
  font-size: 14px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.status-pending {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
  color: #856404;
}

.status-paid {
  background: linear-gradient(135deg, #d1ecf1 0%, #74b9ff 100%);
  color: #0c5460;
}

.status-shipped {
  background: linear-gradient(135deg, #d4edda 0%, #55efc4 100%);
  color: #155724;
}

.status-delivered {
  background: linear-gradient(135deg, #c3e6cb 0%, #00b894 100%);
  color: #155724;
}

.status-cancelled {
  background: linear-gradient(135deg, #f8d7da 0%, #ff7675 100%);
  color: #721c24;
}

.order-details {
  margin-bottom: 20px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f1f3f5;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  font-weight: 600;
  color: #7f8c8d;
  font-size: 15px;
}

.detail-value {
  font-weight: 600;
  color: #2c3e50;
  font-size: 15px;
  text-align: right;
  max-width: 60%;
}

.order-items {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 15px;
  padding: 20px;
  margin-top: 20px;
}

.order-items h4 {
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 15px;
  font-weight: 700;
  padding-bottom: 10px;
  border-bottom: 2px solid #dee2e6;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #dee2e6;
}

.order-item:last-child {
  border-bottom: none;
}

.item-name {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.item-icon {
  font-size: 20px;
}

.item-qty {
  color: #7f8c8d;
  font-weight: 600;
  font-size: 14px;
}

.item-price {
  font-weight: 700;
  color: #27ae60;
  font-size: 16px;
}

/* Responsive */
@media (max-width: 768px) {
  .orders-container {
    padding: 20px 15px;
  }

  .orders-header h1 {
    font-size: 32px;
  }

  .order-card {
    padding: 25px;
  }

  .order-header {
    flex-direction: column;
    gap: 15px;
  }

  .order-status {
    align-self: flex-start;
  }

  .detail-value {
    max-width: 50%;
  }
}
</style>
