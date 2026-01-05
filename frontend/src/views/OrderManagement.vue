<template>
  <div class="admin-dashboard">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo">
          <span class="logo-icon">🛒</span>
          <span class="logo-text">ShopHub Admin</span>
        </div>
      </div>

      <nav class="sidebar-nav">
        <router-link to="/admin" class="nav-item">
          <span class="nav-icon">📊</span>
          <span class="nav-text">Dashboard</span>
        </router-link>
        <router-link to="/admin/products" class="nav-item">
          <span class="nav-icon">📦</span>
          <span class="nav-text">Sản phẩm</span>
        </router-link>
        <router-link to="/admin/categories" class="nav-item">
          <span class="nav-icon">🏷️</span>
          <span class="nav-text">Danh mục</span>
        </router-link>
        <a href="#orders" class="nav-item active">
          <span class="nav-icon">🛍️</span>
          <span class="nav-text">Đơn hàng</span>
        </a>
        <router-link to="/admin/customers" class="nav-item">
          <span class="nav-icon">👥</span>
          <span class="nav-text">Khách hàng</span>
        </router-link>
        <a href="#analytics" class="nav-item">
          <span class="nav-icon">📈</span>
          <span class="nav-text">Thống kê</span>
        </a>
        <a href="#settings" class="nav-item">
          <span class="nav-icon">⚙️</span>
          <span class="nav-text">Cài đặt</span>
        </a>
      </nav>

      <div class="sidebar-footer">
        <router-link to="/" class="nav-item">
          <span class="nav-icon">🏠</span>
          <span class="nav-text">Về trang chủ</span>
        </router-link>
        <button @click="handleLogout" class="nav-item logout-btn">
          <span class="nav-icon">🚪</span>
          <span class="nav-text">Đăng xuất</span>
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Top Bar -->
      <header class="top-bar">
        <div class="page-title">
          <h1>Quản Lý Đơn Hàng</h1>
          <p>Theo dõi và xử lý đơn hàng</p>
        </div>
      </header>

      <!-- Stats Cards -->
      <section class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon orders">🛍️</div>
          <div class="stat-content">
            <div class="stat-label">Tổng đơn hàng</div>
            <div class="stat-value">{{ orders.length }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon pending">⏳</div>
          <div class="stat-content">
            <div class="stat-label">Chờ xử lý</div>
            <div class="stat-value">{{ pendingOrders }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon shipping">🚚</div>
          <div class="stat-content">
            <div class="stat-label">Đang giao</div>
            <div class="stat-value">{{ shippingOrders }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon revenue">💰</div>
          <div class="stat-content">
            <div class="stat-label">Doanh thu</div>
            <div class="stat-value">{{ formatCurrency(totalRevenue) }}</div>
          </div>
        </div>
      </section>

      <!-- Orders Table -->
      <section class="table-section">
        <div class="table-header">
          <h2>Danh Sách Đơn Hàng</h2>
          <div class="table-actions">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="🔍 Tìm kiếm đơn hàng..."
              class="search-input"
            />
            <select v-model="filterStatus" class="filter-select">
              <option value="">Tất cả trạng thái</option>
              <option value="PENDING">Chờ xử lý</option>
              <option value="PAID">Đã thanh toán</option>
              <option value="SHIPPED">Đang giao hàng</option>
              <option value="DELIVERED">Đã giao hàng</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
          </div>
        </div>

        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>Mã đơn</th>
                <th>Khách hàng</th>
                <th>Địa chỉ</th>
                <th>Tổng tiền</th>
                <th>Trạng thái</th>
                <th>Ngày đặt</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in filteredOrders" :key="order.id">
                <td>
                  <div class="order-id">#{{ order.id }}</div>
                </td>
                <td>
                  <div class="customer-info">
                    <div class="customer-name">{{ order.fullName || 'N/A' }}</div>
                    <div class="customer-phone" v-if="order.phone">{{ order.phone }}</div>
                  </div>
                </td>
                <td>
                  <div class="address-text">{{ truncateText(order.address, 30) }}</div>
                </td>
                <td>
                  <div class="price-info">{{ formatCurrency(order.totalAmount) }}</div>
                </td>
                <td>
                  <select 
                    v-model="order.status" 
                    @change="updateOrderStatus(order)"
                    :class="['status-select', getStatusClass(order.status)]"
                  >
                    <option value="PENDING">⏳ Chờ xử lý</option>
                    <option value="PAID">💳 Đã thanh toán</option>
                    <option value="SHIPPED">🚚 Đang giao hàng</option>
                    <option value="DELIVERED">✅ Đã giao hàng</option>
                    <option value="CANCELLED">❌ Đã hủy</option>
                  </select>
                </td>
                <td>{{ formatDate(order.orderDate) }}</td>
                <td>
                  <div class="action-buttons">
                    <button @click="viewOrderDetail(order)" class="btn-icon" title="Chi tiết">
                      👁️
                    </button>
                    <button @click="deleteOrder(order.id)" class="btn-icon danger" title="Xóa">
                      🗑️
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="table-footer">
          <div class="showing-info">
            Hiển thị {{ filteredOrders.length }} đơn hàng
          </div>
        </div>
      </section>
    </main>

    <!-- Order Detail Modal -->
    <div v-if="showDetailModal" class="modal-overlay" @click="showDetailModal = false">
      <div class="modal large" @click.stop>
        <h2>Chi Tiết Đơn Hàng #{{ selectedOrder.id }}</h2>
        
        <div class="order-detail-content">
          <div class="detail-section">
            <h3>Thông tin khách hàng</h3>
            <div class="detail-row">
              <span class="label">Tên:</span>
              <span class="value">{{ selectedOrder.fullName || 'N/A' }}</span>
            </div>
            <div class="detail-row">
              <span class="label">Số điện thoại:</span>
              <span class="value">{{ selectedOrder.phone || 'N/A' }}</span>
            </div>
            <div class="detail-row">
              <span class="label">Địa chỉ:</span>
              <span class="value">{{ selectedOrder.address }}</span>
            </div>
            <div class="detail-row">
              <span class="label">Ngày đặt:</span>
              <span class="value">{{ formatDate(selectedOrder.orderDate) }}</span>
            </div>
            <div class="detail-row">
              <span class="label">Trạng thái:</span>
              <span :class="['status-badge', getStatusClass(selectedOrder.status)]">
                {{ getStatusText(selectedOrder.status) }}
              </span>
            </div>
          </div>

          <div class="detail-section">
            <h3>Sản phẩm</h3>
            <div class="order-items">
              <div v-for="item in selectedOrder.items" :key="item.id" class="order-item">
                <div class="item-name">{{ item.productName || 'Sản phẩm #' + item.productId }}</div>
                <div class="item-details">
                  Số lượng: {{ item.quantity }} × {{ formatCurrency(item.price) }}
                </div>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <div class="total-row">
              <span class="label">Tổng cộng:</span>
              <span class="total-value">{{ formatCurrency(selectedOrder.totalAmount) }}</span>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" @click="showDetailModal = false" class="btn-primary">
            Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useNotification } from '../composables/useNotification'
import api from '../services/api'

const router = useRouter()
const authStore = useAuthStore()
const { success, error } = useNotification()

const orders = ref([])
const users = ref([])
const searchQuery = ref('')
const filterStatus = ref('')
const showDetailModal = ref(false)
const selectedOrder = ref(null)

const pendingOrders = computed(() => 
  orders.value.filter(o => o.status === 'PENDING').length
)
const shippingOrders = computed(() => 
  orders.value.filter(o => o.status === 'SHIPPED').length
)
const totalRevenue = computed(() => 
  orders.value
    .filter(o => o.status === 'DELIVERED')
    .reduce((sum, o) => sum + o.totalAmount, 0)
)

const filteredOrders = computed(() => {
  let filtered = orders.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(o => 
      o.id.toString().includes(query) || 
      o.address.toLowerCase().includes(query) ||
      (o.fullName && o.fullName.toLowerCase().includes(query)) ||
      (o.phone && o.phone.includes(query))
    )
  }

  if (filterStatus.value) {
    filtered = filtered.filter(o => o.status === filterStatus.value)
  }

  // Sắp xếp theo thứ tự ưu tiên giao hàng
  const statusPriority = {
    'PENDING': 1,      // Chờ xử lý - cần chuẩn bị hàng (ưu tiên cao nhất)
    'PAID': 2,         // Đã thanh toán trước - cần chuẩn bị giao
    'SHIPPED': 3,      // Đang giao hàng (bao gồm COD chưa thu tiền)
    'DELIVERED': 4,    // Đã giao hàng & thu tiền thành công
    'CANCELLED': 5     // Đã hủy - ưu tiên thấp nhất
  }

  return filtered.sort((a, b) => {
    // Sắp xếp theo thứ tự ưu tiên trước
    const priorityDiff = (statusPriority[a.status] || 999) - (statusPriority[b.status] || 999)
    if (priorityDiff !== 0) return priorityDiff
    
    // Nếu cùng trạng thái, sắp xếp theo ngày đặt hàng (mới nhất trước)
    return new Date(b.orderDate) - new Date(a.orderDate)
  })
})

const fetchOrders = async () => {
  try {
    const response = await api.getOrders()
    orders.value = response.data
  } catch (error) {
    console.error('Lỗi khi tải đơn hàng:', error)
    error('Không thể tải danh sách đơn hàng!')
  }
}

const fetchUsers = async () => {
  try {
    const response = await api.getUsers()
    users.value = response.data
  } catch (error) {
    console.error('Lỗi khi tải người dùng:', error)
  }
}

const updateOrderStatus = async (order) => {
  try {
    await api.updateOrderStatus(order.id, order.status)
    success('Cập nhật trạng thái đơn hàng thành công!')
  } catch (error) {
    console.error('Lỗi khi cập nhật trạng thái:', error)
    error('Không thể cập nhật trạng thái đơn hàng!')
    fetchOrders() // Reload to revert changes
  }
}

const viewOrderDetail = (order) => {
  selectedOrder.value = order
  showDetailModal.value = true
}

const deleteOrder = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa đơn hàng này?')) return
  
  try {
    await api.delete(`/orders/${id}`)
    success('Xóa đơn hàng thành công!')
    fetchOrders()
  } catch (error) {
    console.error('Lỗi khi xóa đơn hàng:', error)
    error('Không thể xóa đơn hàng!')
  }
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getUserName = (userId) => {
  const user = users.value.find(u => u.id === userId)
  return user ? user.fullName : `User #${userId}`
}

const getStatusClass = (status) => {
  const statusMap = {
    'PENDING': 'pending',
    'PAID': 'paid',
    'SHIPPED': 'shipping',
    'DELIVERED': 'completed',
    'CANCELLED': 'cancelled'
  }
  return statusMap[status] || ''
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '⏳ Chờ xử lý',
    'PAID': '💳 Đã thanh toán',
    'SHIPPED': '🚚 Đang giao hàng',
    'DELIVERED': '✅ Đã giao hàng',
    'CANCELLED': '❌ Đã hủy'
  }
  return statusMap[status] || status
}

const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  fetchOrders()
  fetchUsers()
})
</script>

<style scoped>
/* Shared Admin Layout Styles */
.admin-dashboard {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.sidebar {
  width: 280px;
  background: white;
  border-right: 1px solid #e8ecef;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  z-index: 100;
}

.sidebar-header {
  padding: 30px 24px;
  border-bottom: 1px solid #e8ecef;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 32px;
}

.logo-text {
  font-size: 20px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 12px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  margin-bottom: 6px;
  border-radius: 12px;
  color: #64748b;
  text-decoration: none;
  font-weight: 600;
  font-size: 15px;
  transition: all 0.3s ease;
  cursor: pointer;
  border: none;
  background: none;
  width: 100%;
}

.nav-item:hover {
  background: #f8f9fa;
  color: #667eea;
  transform: translateX(4px);
}

.nav-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.nav-icon {
  font-size: 20px;
  width: 24px;
  text-align: center;
}

.sidebar-footer {
  padding: 20px 12px;
  border-top: 1px solid #e8ecef;
}

.logout-btn {
  color: #ff6b6b;
}

.logout-btn:hover {
  background: #fff5f5;
  color: #ff6b6b;
}

.main-content {
  flex: 1;
  margin-left: 280px;
  padding: 30px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title h1 {
  font-size: 32px;
  font-weight: 800;
  color: #2c3e50;
  margin-bottom: 4px;
}

.page-title p {
  color: #7f8c8d;
  font-size: 16px;
}

.top-bar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-primary {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  display: flex;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-icon.revenue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.orders {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.customers {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.products {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-label {
  color: #7f8c8d;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: #2c3e50;
}

.table-section {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.table-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
}

.table-actions {
  display: flex;
  gap: 12px;
}

.search-input {
  padding: 12px 20px;
  border: 2px solid #e8ecef;
  border-radius: 10px;
  font-size: 14px;
  width: 280px;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
}

.filter-select {
  padding: 12px 20px;
  border: 2px solid #e8ecef;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  background: white;
  cursor: pointer;
}

.filter-select:focus {
  outline: none;
  border-color: #667eea;
}

.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table thead {
  background: #f8f9fa;
}

.data-table th {
  padding: 16px;
  text-align: left;
  font-weight: 700;
  font-size: 13px;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.data-table td {
  padding: 16px;
  border-bottom: 1px solid #f1f3f5;
  font-size: 14px;
  color: #2c3e50;
}

.data-table tbody tr {
  transition: all 0.3s ease;
}

.data-table tbody tr:hover {
  background: #f8f9fa;
}

.status-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-weight: 700;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-badge.completed {
  background: #d3f9d8;
  color: #2b8a3e;
}

.status-badge.processing {
  background: #fff3bf;
  color: #e67700;
}

.status-badge.shipping {
  background: #d0ebff;
  color: #1971c2;
}

.status-badge.cancelled {
  background: #ffe3e3;
  color: #c92a2a;
}

.status-badge.pending {
  background: #fff3cd;
  color: #856404;
}

.status-badge.paid {
  background: #d1ecf1;
  color: #0c5460;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border: none;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-icon:hover {
  background: #e9ecef;
  transform: scale(1.1);
}

.btn-icon.danger:hover {
  background: #ffe3e3;
}

.table-footer {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 2px solid #f1f3f5;
}

.showing-info {
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
}

.order-id {
  font-weight: 700;
  color: #667eea;
}

.price-info {
  font-weight: 700;
  color: #667eea;
  font-size: 15px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal {
  background: white;
  padding: 40px;
  border-radius: 20px;
  width: 550px;
  max-width: 90%;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal.large {
  width: 700px;
}

.modal h2 {
  margin-bottom: 30px;
  color: #2c3e50;
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@media (max-width: 1024px) {
  .sidebar {
    width: 80px;
  }

  .main-content {
    margin-left: 80px;
  }

  .nav-text,
  .logo-text {
    display: none;
  }

  .logo-icon {
    margin: 0 auto;
  }

  .nav-item {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 100%;
    height: auto;
    position: static;
  }

  .main-content {
    margin-left: 0;
    padding: 20px;
  }

  .sidebar-nav {
    display: flex;
    overflow-x: auto;
    padding: 12px;
  }

  .nav-item {
    flex-direction: column;
    min-width: 80px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .top-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}

/* Order Management Specific Styles */
.stat-icon.pending {
  background: linear-gradient(135deg, #ffd43b 0%, #fab005 100%);
}

.stat-icon.shipping {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.address-text {
  color: #64748b;
  font-size: 14px;
}

.customer-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.customer-name {
  font-weight: 700;
  color: #2c3e50;
  font-size: 15px;
}

.customer-phone {
  color: #667eea;
  font-size: 13px;
  font-weight: 600;
}

.status-select {
  padding: 8px 12px;
  border: 2px solid transparent;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
}

.status-select.pending {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
  color: #856404;
}

.status-select.paid {
  background: linear-gradient(135deg, #d1ecf1 0%, #a5d8ff 100%);
  color: #0c5460;
}

.status-select.processing {
  background: linear-gradient(135deg, #d0ebff 0%, #a5d8ff 100%);
  color: #1971c2;
}

.status-select.shipping {
  background: linear-gradient(135deg, #d0ebff 0%, #a5d8ff 100%);
  color: #1971c2;
}

.status-select.completed {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  color: #155724;
}

.status-select.cancelled {
  background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
  color: #721c24;
}

.status-select:focus {
  outline: none;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.2);
}

.modal.large {
  width: 700px;
  max-width: 90%;
}

.order-detail-content {
  margin: 20px 0;
}

.detail-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.detail-section h3 {
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e9ecef;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e9ecef;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row .label {
  font-weight: 600;
  color: #64748b;
}

.detail-row .value {
  color: #2c3e50;
  font-weight: 500;
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  background: white;
  padding: 12px;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.item-name {
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.item-details {
  color: #64748b;
  font-size: 14px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.total-row .label {
  font-size: 18px;
  font-weight: 700;
}

.total-value {
  font-size: 24px;
  font-weight: 800;
}
</style>
