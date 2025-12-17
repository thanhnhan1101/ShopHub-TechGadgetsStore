<template>
  <div class="orders">
    <h1>My Orders</h1>

    <div v-if="loading" class="loading">Loading orders...</div>

    <div v-else-if="orders.length === 0" class="no-orders">
      <p>You haven't placed any orders yet</p>
      <button class="btn btn-primary" @click="goToShop">Start Shopping</button>
    </div>

    <div v-else class="orders-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div>
            <h3>Order #{{ order.id }}</h3>
            <p class="order-date">{{ formatDate(order.orderDate) }}</p>
          </div>
          <span class="order-status" :class="statusClass(order.status)">
            {{ order.status }}
          </span>
        </div>

        <div class="order-details">
          <p><strong>Total:</strong> ${{ order.totalAmount }}</p>
          <p><strong>Payment:</strong> {{ order.paymentMethod }}</p>
          <p><strong>Address:</strong> {{ order.address }}</p>
        </div>

        <div v-if="order.orderItems && order.orderItems.length" class="order-items">
          <div v-for="item in order.orderItems" :key="item.id" class="order-item">
            <span>{{ item.productName }} × {{ item.quantity }}</span>
            <span>${{ (item.price * item.quantity).toFixed(2) }}</span>
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
import api from '../services/api'

export default {
  name: 'Orders',
  setup() {
    const router = useRouter()
    const cartStore = useCartStore()
    const orders = ref([])
    const loading = ref(false)

    const fetchOrders = async () => {
      loading.value = true
      try {
        const response = await api.getOrdersByUser(cartStore.userId)
        orders.value = response.data
      } catch (error) {
        console.error('Error fetching orders:', error)
      } finally {
        loading.value = false
      }
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
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
      statusClass,
      goToShop
    }
  }
}
</script>

<style scoped>
.orders h1 {
  margin-bottom: 30px;
}

.loading, .no-orders {
  text-align: center;
  padding: 60px;
}

.no-orders p {
  font-size: 20px;
  color: #7f8c8d;
  margin-bottom: 20px;
}

.orders-list {
  display: grid;
  gap: 20px;
}

.order-card {
  background: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #eee;
}

.order-header h3 {
  font-size: 20px;
  margin-bottom: 5px;
}

.order-date {
  color: #7f8c8d;
  font-size: 14px;
}

.order-status {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-paid {
  background: #d1ecf1;
  color: #0c5460;
}

.status-shipped {
  background: #d4edda;
  color: #155724;
}

.status-delivered {
  background: #c3e6cb;
  color: #155724;
}

.status-cancelled {
  background: #f8d7da;
  color: #721c24;
}

.order-details {
  margin-bottom: 15px;
}

.order-details p {
  margin-bottom: 8px;
  color: #555;
}

.order-items {
  background: #f8f9fa;
  border-radius: 5px;
  padding: 15px;
  margin-top: 15px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #ddd;
}

.order-item:last-child {
  border-bottom: none;
}
</style>
