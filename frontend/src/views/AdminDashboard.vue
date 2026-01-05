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
        <a href="#dashboard" class="nav-item active">
          <span class="nav-icon">📊</span>
          <span class="nav-text">Dashboard</span>
        </a>
        <router-link to="/admin/products" class="nav-item">
          <span class="nav-icon">📦</span>
          <span class="nav-text">Sản phẩm</span>
        </router-link>
        <router-link to="/admin/categories" class="nav-item">
          <span class="nav-icon">🏷️</span>
          <span class="nav-text">Danh mục</span>
        </router-link>
        <router-link to="/admin/orders" class="nav-item">
          <span class="nav-icon">🛍️</span>
          <span class="nav-text">Đơn hàng</span>
        </router-link>
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
          <h1>Dashboard</h1>
          <p>Chào mừng trở lại, {{ userName }}!</p>
        </div>
        <div class="top-bar-actions">
          <button class="action-btn">
            <span>🔔</span>
            <span class="badge">5</span>
          </button>
          <div class="user-menu">
            <img src="https://ui-avatars.com/api/?name=Admin&background=667eea&color=fff" alt="Admin" class="avatar">
            <span class="user-name">{{ userName }}</span>
          </div>
        </div>
      </header>

      <!-- Stats Cards -->
      <section class="stats-grid" v-if="!loading">
        <div class="stat-card">
          <div class="stat-icon revenue">💰</div>
          <div class="stat-content">
            <div class="stat-label">Doanh thu</div>
            <div class="stat-value">{{ formatCurrency(stats.revenue) }}</div>
            <div class="stat-change positive" v-if="stats.revenue > 0">Tổng doanh thu từ đơn hoàn thành</div>
            <div class="stat-change" v-else>Chưa có doanh thu</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon orders">🛍️</div>
          <div class="stat-content">
            <div class="stat-label">Đơn hàng</div>
            <div class="stat-value">{{ stats.orders }}</div>
            <div class="stat-change positive" v-if="orderStatusData.completed > 0">
              {{ orderStatusData.completed }} đơn hoàn thành
            </div>
            <div class="stat-change" v-else>Chưa có đơn hàng</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon customers">👥</div>
          <div class="stat-content">
            <div class="stat-label">Khách hàng</div>
            <div class="stat-value">{{ stats.customers }}</div>
            <div class="stat-change positive" v-if="stats.customers > 0">Tổng số khách hàng đã đăng ký</div>
            <div class="stat-change" v-else>Chưa có khách hàng</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon products">📦</div>
          <div class="stat-content">
            <div class="stat-label">Sản phẩm</div>
            <div class="stat-value">{{ stats.products }}</div>
            <div class="stat-change positive" v-if="stats.products > 0">Tổng số sản phẩm trong cửa hàng</div>
            <div class="stat-change" v-else>Chưa có sản phẩm</div>
          </div>
        </div>
      </section>

      <!-- Loading State -->
      <section class="loading-section" v-if="loading">
        <div class="loader">
          <div class="spinner"></div>
          <p>Đang tải dữ liệu...</p>
        </div>
      </section>

      <!-- Charts Section -->
      <section class="charts-section" v-if="!loading">
        <div class="chart-card large">
          <div class="card-header">
            <h2>Doanh thu theo tháng</h2>
            <span class="filter-info">6 tháng gần nhất</span>
          </div>
          <div class="chart-container">
            <canvas ref="revenueChart"></canvas>
          </div>
        </div>

        <div class="chart-card small">
          <div class="card-header">
            <h2>Đơn hàng theo trạng thái</h2>
          </div>
          <div class="chart-container">
            <canvas ref="orderStatusChart"></canvas>
          </div>
          <div class="legend">
            <div class="legend-item">
              <span class="legend-color" style="background: #667eea"></span>
              <span>Hoàn thành: {{ orderStatusData.completed }}</span>
            </div>
            <div class="legend-item">
              <span class="legend-color" style="background: #ffa500"></span>
              <span>Đang xử lý: {{ orderStatusData.processing + orderStatusData.pending }}</span>
            </div>
            <div class="legend-item">
              <span class="legend-color" style="background: #ff6b6b"></span>
              <span>Đã hủy: {{ orderStatusData.cancelled }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Recent Orders Table -->
      <section class="table-section" v-if="!loading">
        <div class="table-header">
          <h2>Đơn hàng gần đây</h2>
          <div class="table-actions">
            <input type="search" placeholder="Tìm kiếm đơn hàng..." class="search-input">
            <router-link to="/admin/orders" class="btn-primary">
              <span>👁️</span>
              Xem tất cả
            </router-link>
          </div>
        </div>

        <div class="table-container" v-if="recentOrders.length > 0">
          <table class="data-table">
            <thead>
              <tr>
                <th>Mã đơn</th>
                <th>Khách hàng</th>
                <th>Sản phẩm</th>
                <th>Số tiền</th>
                <th>Trạng thái</th>
                <th>Ngày đặt</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in recentOrders" :key="order.id">
                <td><span class="order-id">{{ order.id }}</span></td>
                <td>
                  <div class="customer-info">
                    <img :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(order.customerName)}&background=random`" alt="" class="customer-avatar">
                    <span>{{ order.customerName }}</span>
                  </div>
                </td>
                <td>
                  <div class="product-info">
                    <span class="product-count">{{ order.productCount }} sản phẩm</span>
                  </div>
                </td>
                <td><strong>{{ formatCurrency(order.amount) }}</strong></td>
                <td>
                  <span class="status-badge" :class="getStatusClass(order.status)">
                    {{ getStatusText(order.status) }}
                  </span>
                </td>
                <td>{{ order.date }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="empty-state" v-else>
          <p>Chưa có đơn hàng nào</p>
        </div>

        <div class="table-footer" v-if="recentOrders.length > 0">
          <div class="showing-info">
            Hiển thị {{ recentOrders.length }} đơn hàng gần nhất
          </div>
          <router-link to="/admin/orders" class="view-all-link">
            Xem tất cả đơn hàng →
          </router-link>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useNotification } from '../composables/useNotification'
import api from '../services/api'
import Chart from 'chart.js/auto'

const router = useRouter()
const authStore = useAuthStore()
const { error: showError } = useNotification()

const userName = computed(() => authStore.user?.fullName || 'Admin')

const loading = ref(true)
const stats = ref({
  revenue: 0,
  orders: 0,
  customers: 0,
  products: 0
})

const recentOrders = ref([])
const allOrders = ref([])
const orderStatusData = ref({
  completed: 0,
  processing: 0,
  cancelled: 0,
  pending: 0
})
const monthlyRevenue = ref([0, 0, 0, 0, 0, 0])

const revenueChart = ref(null)
const orderStatusChart = ref(null)
let revenueChartInstance = null
let orderStatusChartInstance = null

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN')
}

const getStatusText = (status) => {
  const statusMap = {
    COMPLETED: 'Hoàn thành',
    PROCESSING: 'Đang xử lý',
    PENDING: 'Chờ xử lý',
    SHIPPING: 'Đang giao',
    CANCELLED: 'Đã hủy'
  }
  return statusMap[status] || status
}

const getStatusClass = (status) => {
  const statusClassMap = {
    COMPLETED: 'completed',
    PROCESSING: 'processing',
    PENDING: 'processing',
    SHIPPING: 'shipping',
    CANCELLED: 'cancelled'
  }
  return statusClassMap[status] || 'processing'
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

// Fetch dữ liệu thật từ API
const fetchDashboardData = async () => {
  try {
    loading.value = true
    
    // Fetch tất cả dữ liệu song song
    const [productsRes, ordersRes, usersRes] = await Promise.all([
      api.getProducts(),
      api.getOrders(),
      api.getUsers()
    ])

    // Cập nhật products count
    stats.value.products = productsRes.data.length

    // Cập nhật customers count
    stats.value.customers = usersRes.data.length

    // Xử lý orders
    allOrders.value = ordersRes.data
    stats.value.orders = allOrders.value.length

    // Tính tổng doanh thu
    stats.value.revenue = allOrders.value.reduce((sum, order) => sum + order.totalAmount, 0)

    // Đếm orders theo status
    orderStatusData.value = {
      completed: allOrders.value.filter(o => o.status === 'COMPLETED').length,
      processing: allOrders.value.filter(o => o.status === 'PROCESSING' || o.status === 'PENDING').length,
      cancelled: allOrders.value.filter(o => o.status === 'CANCELLED').length,
      pending: allOrders.value.filter(o => o.status === 'PENDING').length
    }

    // Tính doanh thu 6 tháng gần nhất
    const currentDate = new Date()
    monthlyRevenue.value = Array(6).fill(0)
    
    allOrders.value.forEach(order => {
      if (order.status === 'COMPLETED') {
        const orderDate = new Date(order.orderDate)
        const monthDiff = (currentDate.getFullYear() - orderDate.getFullYear()) * 12 
                         + currentDate.getMonth() - orderDate.getMonth()
        
        if (monthDiff >= 0 && monthDiff < 6) {
          monthlyRevenue.value[5 - monthDiff] += order.totalAmount / 1000000 // Convert to millions
        }
      }
    })

    // Lấy 10 đơn hàng gần nhất
    recentOrders.value = allOrders.value
      .sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate))
      .slice(0, 10)
      .map(order => ({
        id: `#${order.id}`,
        customerName: order.user?.fullName || 'N/A',
        products: order.orderItems?.map(item => item.product?.name).join(', ') || 'N/A',
        productCount: order.orderItems?.length || 0,
        amount: order.totalAmount,
        status: order.status,
        date: formatDate(order.orderDate)
      }))

    // Cập nhật charts sau khi có dữ liệu
    updateCharts()

  } catch (err) {
    console.error('Error fetching dashboard data:', err)
    if (err.response?.status === 403) {
      showError('Bạn không có quyền truy cập trang này. Vui lòng đăng nhập với tài khoản Admin.')
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}

const updateCharts = () => {
  // Revenue Chart
  if (revenueChart.value) {
    if (revenueChartInstance) {
      revenueChartInstance.destroy()
    }
    
    const currentMonth = new Date().getMonth()
    const monthNames = ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12']
    const labels = []
    for (let i = 5; i >= 0; i--) {
      const monthIndex = (currentMonth - i + 12) % 12
      labels.push(monthNames[monthIndex])
    }

    revenueChartInstance = new Chart(revenueChart.value, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'Doanh thu (triệu VNĐ)',
          data: monthlyRevenue.value,
          borderColor: '#667eea',
          backgroundColor: 'rgba(102, 126, 234, 0.1)',
          borderWidth: 3,
          fill: true,
          tension: 0.4,
          pointRadius: 6,
          pointHoverRadius: 8,
          pointBackgroundColor: '#667eea',
          pointBorderColor: '#fff',
          pointBorderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: function(value) {
                return value.toFixed(1) + 'M'
              }
            }
          }
        }
      }
    })
  }

  // Order Status Chart
  if (orderStatusChart.value) {
    if (orderStatusChartInstance) {
      orderStatusChartInstance.destroy()
    }

    orderStatusChartInstance = new Chart(orderStatusChart.value, {
      type: 'doughnut',
      data: {
        labels: ['Hoàn thành', 'Đang xử lý', 'Đã hủy'],
        datasets: [{
          data: [
            orderStatusData.value.completed,
            orderStatusData.value.processing + orderStatusData.value.pending,
            orderStatusData.value.cancelled
          ],
          backgroundColor: ['#667eea', '#ffa500', '#ff6b6b'],
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          }
        }
      }
    })
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

/* Sidebar */
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

/* Main Content */
.main-content {
  flex: 1;
  margin-left: 280px;
  padding: 30px;
}

/* Top Bar */
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
  gap: 20px;
}

.action-btn {
  position: relative;
  width: 48px;
  height: 48px;
  border: none;
  background: white;
  border-radius: 12px;
  font-size: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ff6b6b;
  color: white;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 10px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px 8px 8px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
}

.user-name {
  font-weight: 600;
  color: #2c3e50;
}

/* Stats Grid */
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
  margin-bottom: 8px;
}

.stat-change {
  font-size: 13px;
  font-weight: 600;
}

.stat-change.positive {
  color: #51cf66;
}

.stat-change.negative {
  color: #ff6b6b;
}

/* Charts */
.charts-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 30px;
}

.chart-card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.card-header h2 {
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
}

.filter-select {
  padding: 8px 16px;
  border: 2px solid #e8ecef;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-select:focus {
  outline: none;
  border-color: #667eea;
}

.filter-info {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 600;
}

.chart-container {
  height: 300px;
}

.legend {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 2px solid #f1f3f5;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

/* Table */
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

.order-id {
  font-weight: 700;
  color: #667eea;
}

.customer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.customer-avatar {
  width: 36px;
  height: 36px;
  border-radius: 8px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 2px solid #f1f3f5;
}

.showing-info {
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
}

.pagination {
  display: flex;
  gap: 8px;
}

.page-btn {
  width: 40px;
  height: 40px;
  border: 2px solid #e8ecef;
  background: white;
  border-radius: 8px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
}

.page-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* Loading State */
.loading-section {
  padding: 100px 0;
  text-align: center;
}

.loader {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid #f3f4f6;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loader p {
  font-size: 16px;
  color: #7f8c8d;
  font-weight: 600;
}

/* Empty State */
.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #7f8c8d;
  font-size: 16px;
}

/* Product Info */
.product-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-count {
  font-weight: 600;
  color: #667eea;
  font-size: 13px;
}

/* View All Link */
.view-all-link {
  color: #667eea;
  font-weight: 700;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
}

.view-all-link:hover {
  color: #764ba2;
  transform: translateX(4px);
}

/* Responsive */
@media (max-width: 1400px) {
  .charts-section {
    grid-template-columns: 1fr;
  }
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

  .user-name {
    display: none;
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

  .table-actions {
    flex-direction: column;
    width: 100%;
  }

  .search-input {
    width: 100%;
  }
}
</style>
