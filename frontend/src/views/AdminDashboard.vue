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
        <a href="#products" class="nav-item">
          <span class="nav-icon">📦</span>
          <span class="nav-text">Sản phẩm</span>
        </a>
        <router-link to="/admin/categories" class="nav-item">
          <span class="nav-icon">🏷️</span>
          <span class="nav-text">Danh mục</span>
        </router-link>
        <a href="#orders" class="nav-item">
          <span class="nav-icon">🛍️</span>
          <span class="nav-text">Đơn hàng</span>
        </a>
        <a href="#customers" class="nav-item">
          <span class="nav-icon">👥</span>
          <span class="nav-text">Khách hàng</span>
        </a>
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
      <section class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon revenue">💰</div>
          <div class="stat-content">
            <div class="stat-label">Doanh thu</div>
            <div class="stat-value">{{ formatCurrency(stats.revenue) }}</div>
            <div class="stat-change positive">+12.5% so với tháng trước</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon orders">🛍️</div>
          <div class="stat-content">
            <div class="stat-label">Đơn hàng</div>
            <div class="stat-value">{{ stats.orders }}</div>
            <div class="stat-change positive">+8.2% so với tháng trước</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon customers">👥</div>
          <div class="stat-content">
            <div class="stat-label">Khách hàng</div>
            <div class="stat-value">{{ stats.customers }}</div>
            <div class="stat-change positive">+15.3% so với tháng trước</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon products">📦</div>
          <div class="stat-content">
            <div class="stat-label">Sản phẩm</div>
            <div class="stat-value">{{ stats.products }}</div>
            <div class="stat-change negative">-2 sản phẩm hết hàng</div>
          </div>
        </div>
      </section>

      <!-- Charts Section -->
      <section class="charts-section">
        <div class="chart-card large">
          <div class="card-header">
            <h2>Doanh thu theo tháng</h2>
            <select class="filter-select">
              <option>6 tháng gần nhất</option>
              <option>12 tháng gần nhất</option>
              <option>Năm nay</option>
            </select>
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
              <span>Hoàn thành: 245</span>
            </div>
            <div class="legend-item">
              <span class="legend-color" style="background: #ffa500"></span>
              <span>Đang xử lý: 89</span>
            </div>
            <div class="legend-item">
              <span class="legend-color" style="background: #ff6b6b"></span>
              <span>Đã hủy: 12</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Recent Orders Table -->
      <section class="table-section">
        <div class="table-header">
          <h2>Đơn hàng gần đây</h2>
          <div class="table-actions">
            <input type="search" placeholder="Tìm kiếm đơn hàng..." class="search-input">
            <button class="btn-primary">
              <span>➕</span>
              Tạo đơn hàng
            </button>
          </div>
        </div>

        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>Mã đơn</th>
                <th>Khách hàng</th>
                <th>Sản phẩm</th>
                <th>Số tiền</th>
                <th>Trạng thái</th>
                <th>Ngày đặt</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in recentOrders" :key="order.id">
                <td><span class="order-id">{{ order.id }}</span></td>
                <td>
                  <div class="customer-info">
                    <img :src="`https://ui-avatars.com/api/?name=${order.customerName}&background=random`" alt="" class="customer-avatar">
                    <span>{{ order.customerName }}</span>
                  </div>
                </td>
                <td>{{ order.products }}</td>
                <td><strong>{{ formatCurrency(order.amount) }}</strong></td>
                <td>
                  <span class="status-badge" :class="order.status">
                    {{ getStatusText(order.status) }}
                  </span>
                </td>
                <td>{{ order.date }}</td>
                <td>
                  <div class="action-buttons">
                    <button class="btn-icon" title="Xem chi tiết">👁️</button>
                    <button class="btn-icon" title="Chỉnh sửa">✏️</button>
                    <button class="btn-icon danger" title="Xóa">🗑️</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="table-footer">
          <div class="showing-info">
            Hiển thị 1-10 của 245 đơn hàng
          </div>
          <div class="pagination">
            <button class="page-btn" disabled>‹</button>
            <button class="page-btn active">1</button>
            <button class="page-btn">2</button>
            <button class="page-btn">3</button>
            <button class="page-btn">›</button>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import Chart from 'chart.js/auto'

const router = useRouter()
const authStore = useAuthStore()

const userName = computed(() => authStore.user?.fullName || 'Admin')

const stats = ref({
  revenue: 125680000,
  orders: 346,
  customers: 1248,
  products: 156
})

const recentOrders = ref([
  {
    id: '#ORD-2024-001',
    customerName: 'Nguyễn Văn A',
    products: 'iPhone 15 Pro Max, AirPods Pro',
    amount: 35500000,
    status: 'completed',
    date: '15/12/2024'
  },
  {
    id: '#ORD-2024-002',
    customerName: 'Trần Thị B',
    products: 'MacBook Pro M3',
    amount: 52000000,
    status: 'processing',
    date: '15/12/2024'
  },
  {
    id: '#ORD-2024-003',
    customerName: 'Lê Văn C',
    products: 'iPad Air, Apple Pencil',
    amount: 18500000,
    status: 'completed',
    date: '14/12/2024'
  },
  {
    id: '#ORD-2024-004',
    customerName: 'Phạm Thị D',
    products: 'Apple Watch Series 9',
    amount: 12000000,
    status: 'shipping',
    date: '14/12/2024'
  },
  {
    id: '#ORD-2024-005',
    customerName: 'Hoàng Văn E',
    products: 'AirPods Max',
    amount: 13500000,
    status: 'cancelled',
    date: '13/12/2024'
  },
  {
    id: '#ORD-2024-006',
    customerName: 'Vũ Thị F',
    products: 'iPhone 15, MagSafe Charger',
    amount: 22000000,
    status: 'completed',
    date: '13/12/2024'
  },
  {
    id: '#ORD-2024-007',
    customerName: 'Đỗ Văn G',
    products: 'Mac Mini M2',
    amount: 15500000,
    status: 'processing',
    date: '12/12/2024'
  },
  {
    id: '#ORD-2024-008',
    customerName: 'Bùi Thị H',
    products: 'Apple TV 4K',
    amount: 5500000,
    status: 'completed',
    date: '12/12/2024'
  },
  {
    id: '#ORD-2024-009',
    customerName: 'Ngô Văn I',
    products: 'HomePod mini (2 cái)',
    amount: 5000000,
    status: 'shipping',
    date: '11/12/2024'
  },
  {
    id: '#ORD-2024-010',
    customerName: 'Đinh Thị K',
    products: 'Magic Keyboard, Magic Mouse',
    amount: 8500000,
    status: 'completed',
    date: '11/12/2024'
  }
])

const revenueChart = ref(null)
const orderStatusChart = ref(null)

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const getStatusText = (status) => {
  const statusMap = {
    completed: 'Hoàn thành',
    processing: 'Đang xử lý',
    shipping: 'Đang giao',
    cancelled: 'Đã hủy'
  }
  return statusMap[status] || status
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  // Revenue Chart
  if (revenueChart.value) {
    new Chart(revenueChart.value, {
      type: 'line',
      data: {
        labels: ['T7', 'T8', 'T9', 'T10', 'T11', 'T12'],
        datasets: [{
          label: 'Doanh thu (triệu VNĐ)',
          data: [95, 105, 98, 115, 108, 126],
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
                return value + 'M'
              }
            }
          }
        }
      }
    })
  }

  // Order Status Chart
  if (orderStatusChart.value) {
    new Chart(orderStatusChart.value, {
      type: 'doughnut',
      data: {
        labels: ['Hoàn thành', 'Đang xử lý', 'Đã hủy'],
        datasets: [{
          data: [245, 89, 12],
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
