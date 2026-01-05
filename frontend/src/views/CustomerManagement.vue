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
        <router-link to="/admin/orders" class="nav-item">
          <span class="nav-icon">🛍️</span>
          <span class="nav-text">Đơn hàng</span>
        </router-link>
        <a href="#customers" class="nav-item active">
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
          <h1>Quản Lý Khách Hàng</h1>
          <p>Quản lý thông tin khách hàng</p>
        </div>
        <div class="top-bar-actions">
          <button @click="showCreateModal = true" class="btn-primary">
            <span>➕</span>
            <span>Thêm Khách Hàng</span>
          </button>
        </div>
      </header>

      <!-- Stats Cards -->
      <section class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon customers">👥</div>
          <div class="stat-content">
            <div class="stat-label">Tổng khách hàng</div>
            <div class="stat-value">{{ customers.length }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon active-customers">✅</div>
          <div class="stat-content">
            <div class="stat-label">Khách hàng hoạt động</div>
            <div class="stat-value">{{ activeCustomers }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon admins">👑</div>
          <div class="stat-content">
            <div class="stat-label">Quản trị viên</div>
            <div class="stat-value">{{ adminCount }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon new-customers">🆕</div>
          <div class="stat-content">
            <div class="stat-label">Khách hàng mới (tháng)</div>
            <div class="stat-value">{{ newCustomersThisMonth }}</div>
          </div>
        </div>
      </section>

      <!-- Customers Table -->
      <section class="table-section">
        <div class="table-header">
          <h2>Danh Sách Khách Hàng</h2>
          <div class="table-actions">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="🔍 Tìm kiếm khách hàng..."
              class="search-input"
            />
            <select v-model="filterRole" class="filter-select">
              <option value="">Tất cả vai trò</option>
              <option value="CUSTOMER">Khách hàng</option>
              <option value="ADMIN">Quản trị viên</option>
            </select>
          </div>
        </div>

        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Số điện thoại</th>
                <th>Vai trò</th>
                <th>Ngày đăng ký</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="customer in filteredCustomers" :key="customer.id">
                <td>
                  <div class="customer-id">#{{ customer.id }}</div>
                </td>
                <td>
                  <div class="customer-info">
                    <div class="avatar">{{ getInitials(customer.fullName) }}</div>
                    <div class="customer-name">{{ customer.fullName }}</div>
                  </div>
                </td>
                <td>
                  <div class="email-text">{{ customer.email }}</div>
                </td>
                <td>
                  <div class="phone-text">{{ customer.phone || 'N/A' }}</div>
                </td>
                <td>
                  <span :class="['role-badge', customer.role.toLowerCase()]">
                    {{ getRoleText(customer.role) }}
                  </span>
                </td>
                <td>{{ formatDate(customer.createdAt) }}</td>
                <td>
                  <div class="action-buttons">
                    <button @click="viewCustomerDetail(customer)" class="btn-icon" title="Chi tiết">
                      👁️
                    </button>
                    <button @click="editCustomer(customer)" class="btn-icon" title="Chỉnh sửa">
                      ✏️
                    </button>
                    <button 
                      v-if="customer.role !== 'ADMIN'" 
                      @click="deleteCustomer(customer.id)" 
                      class="btn-icon danger" 
                      title="Xóa"
                    >
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
            Hiển thị {{ filteredCustomers.length }} khách hàng
          </div>
        </div>
      </section>
    </main>

    <!-- Customer Detail Modal -->
    <div v-if="showDetailModal" class="modal-overlay" @click="showDetailModal = false">
      <div class="modal" @click.stop>
        <h2>Chi Tiết Khách Hàng</h2>
        
        <div class="customer-detail-content">
          <div class="detail-header">
            <div class="large-avatar">{{ getInitials(selectedCustomer.fullName) }}</div>
            <div class="detail-info">
              <h3>{{ selectedCustomer.fullName }}</h3>
              <span :class="['role-badge', selectedCustomer.role.toLowerCase()]">
                {{ getRoleText(selectedCustomer.role) }}
              </span>
            </div>
          </div>

          <div class="detail-section">
            <h4>Thông tin liên hệ</h4>
            <div class="detail-row">
              <span class="label">📧 Email:</span>
              <span class="value">{{ selectedCustomer.email }}</span>
            </div>
            <div class="detail-row">
              <span class="label">📱 Số điện thoại:</span>
              <span class="value">{{ selectedCustomer.phone || 'Chưa cập nhật' }}</span>
            </div>
            <div class="detail-row">
              <span class="label">📅 Ngày đăng ký:</span>
              <span class="value">{{ formatDate(selectedCustomer.createdAt) }}</span>
            </div>
          </div>

          <div class="detail-section">
            <h4>Thống kê đơn hàng</h4>
            <div class="stats-mini-grid">
              <div class="mini-stat">
                <div class="mini-stat-value">{{ getCustomerOrderCount(selectedCustomer.id) }}</div>
                <div class="mini-stat-label">Tổng đơn hàng</div>
              </div>
              <div class="mini-stat">
                <div class="mini-stat-value">{{ formatCurrency(getCustomerTotalSpent(selectedCustomer.id)) }}</div>
                <div class="mini-stat-label">Tổng chi tiêu</div>
              </div>
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

    <!-- Create/Edit Modal -->
    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click="closeFormModal">
      <div class="modal" @click.stop>
        <h2>{{ showEditModal ? 'Chỉnh Sửa Khách Hàng' : 'Thêm Khách Hàng Mới' }}</h2>
        <form @submit.prevent="saveCustomer">
          <div class="form-group">
            <label>Họ và tên *</label>
            <input v-model="form.fullName" type="text" required placeholder="Nguyễn Văn A">
          </div>
          
          <div class="form-group">
            <label>Email *</label>
            <input 
              v-model="form.email" 
              type="email" 
              required 
              placeholder="example@email.com"
              :disabled="showEditModal"
            >
            <small v-if="showEditModal" style="color: #64748b;">Email không thể thay đổi</small>
          </div>
          
          <div class="form-group">
            <label>Số điện thoại</label>
            <input v-model="form.phone" type="tel" placeholder="0901234567">
          </div>
          
          <div class="form-group">
            <label>Mật khẩu {{ showEditModal ? '(để trống nếu không đổi)' : '*' }}</label>
            <input 
              v-model="form.password" 
              type="password" 
              :required="!showEditModal"
              placeholder="••••••••"
            >
          </div>
          
          <div class="form-group">
            <label>Vai trò *</label>
            <select v-model="form.role" required>
              <option value="CUSTOMER">👤 Khách hàng</option>
              <option value="ADMIN">👑 Quản trị viên</option>
            </select>
          </div>
          
          <div class="form-group checkbox">
            <label>
              <input v-model="form.isActive" type="checkbox">
              Tài khoản hoạt động
            </label>
          </div>
          
          <div class="form-actions">
            <button type="button" @click="closeFormModal" class="btn-cancel">Hủy</button>
            <button type="submit" class="btn-primary">
              {{ showEditModal ? 'Cập Nhật' : 'Tạo Mới' }}
            </button>
          </div>
        </form>
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
const { error: showError } = useNotification()

const customers = ref([])
const orders = ref([])
const searchQuery = ref('')
const filterRole = ref('')
const showDetailModal = ref(false)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const selectedCustomer = ref(null)
const form = ref({
  id: null,
  fullName: '',
  email: '',
  phone: '',
  password: '',
  role: 'CUSTOMER',
  isActive: true
})

const activeCustomers = computed(() => customers.value.filter(c => c.role === 'CUSTOMER').length)
const adminCount = computed(() => customers.value.filter(c => c.role === 'ADMIN').length)
const newCustomersThisMonth = computed(() => {
  const now = new Date()
  const firstDayOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
  return customers.value.filter(c => new Date(c.createdAt) >= firstDayOfMonth).length
})

const filteredCustomers = computed(() => {
  let filtered = customers.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(c => 
      c.fullName.toLowerCase().includes(query) || 
      c.email.toLowerCase().includes(query) ||
      (c.phone && c.phone.includes(query))
    )
  }

  if (filterRole.value) {
    filtered = filtered.filter(c => c.role === filterRole.value)
  }

  return filtered.sort((a, b) => b.id - a.id)
})

const fetchCustomers = async () => {
  try {
    const response = await api.getUsers()
    customers.value = response.data
  } catch (err) {
    console.error('Lỗi khi tải khách hàng:', err)
    
    if (err.response?.status === 403) {
      showError('Bạn không có quyền truy cập!\n\nChỉ tài khoản ADMIN mới có thể quản lý khách hàng.\n\nVui lòng đăng nhập với tài khoản admin.')
      router.push('/admin')
    } else if (err.response?.status === 401) {
      showError('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.')
      authStore.logout()
      router.push('/login')
    } else {
      alert('❌ Không thể tải danh sách khách hàng!\n\nLỗi: ' + (error.response?.data?.message || error.message))
    }
  }
}

const fetchOrders = async () => {
  try {
    const response = await api.getOrders()
    orders.value = response.data
  } catch (error) {
    console.error('Lỗi khi tải đơn hàng:', error)
  }
}

const viewCustomerDetail = (customer) => {
  selectedCustomer.value = customer
  showDetailModal.value = true
}

/**
 * CREATE - Tạo hoặc UPDATE - Cập nhật khách hàng
 */
const saveCustomer = async () => {
  try {
    const userData = {
      fullName: form.value.fullName,
      email: form.value.email,
      phone: form.value.phone,
      role: form.value.role,
      isActive: form.value.isActive
    }
    
    // Chỉ gửi password nếu có giá trị
    if (form.value.password) {
      userData.passwordHash = form.value.password
    }
    
    if (showEditModal.value) {
      // UPDATE
      await api.put(`/users/${form.value.id}`, userData)
      alert('✅ Cập nhật khách hàng thành công!')
    } else {
      // CREATE - phải có password
      if (!form.value.password) {
        alert('❌ Vui lòng nhập mật khẩu!')
        return
      }
      userData.passwordHash = form.value.password
      await api.post('/users', userData)
      alert('✅ Thêm khách hàng mới thành công!')
    }
    
    closeFormModal()
    fetchCustomers()
  } catch (error) {
    console.error('Lỗi khi lưu khách hàng:', error)
    alert(error.response?.data || 'Không thể lưu khách hàng!')
  }
}

/**
 * Mở form EDIT với dữ liệu của customer
 */
const editCustomer = (customer) => {
  form.value = {
    id: customer.id,
    fullName: customer.fullName,
    email: customer.email,
    phone: customer.phone || '',
    password: '', // Không hiển thị password cũ
    role: customer.role,
    isActive: customer.isActive
  }
  showEditModal.value = true
}

/**
 * Đóng modal Create/Edit và reset form
 */
const closeFormModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  form.value = {
    id: null,
    fullName: '',
    email: '',
    phone: '',
    password: '',
    role: 'CUSTOMER',
    isActive: true
  }
}

const toggleRole = async (customer) => {
  const newRole = customer.role === 'CUSTOMER' ? 'ADMIN' : 'CUSTOMER'
  if (!confirm(`Bạn có chắc muốn chuyển vai trò của ${customer.fullName} thành ${newRole}?`)) return
  
  try {
    await api.updateUser(customer.id, { ...customer, role: newRole })
    alert('Cập nhật vai trò thành công!')
    fetchCustomers()
  } catch (error) {
    console.error('Lỗi khi cập nhật vai trò:', error)
    alert('Không thể cập nhật vai trò!')
  }
}

/**
 * DELETE - Xóa (soft delete) khách hàng
 */
const deleteCustomer = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa khách hàng này? (Tài khoản sẽ bị vô hiệu hóa)')) return
  
  try {
    await api.deleteUser(id)
    alert('✅ Đã vô hiệu hóa tài khoản khách hàng!')
    fetchCustomers()
  } catch (error) {
    console.error('Lỗi khi xóa khách hàng:', error)
    alert(error.response?.data || 'Không thể xóa khách hàng!')
  }
}

const getInitials = (name) => {
  if (!name) return '??'
  const parts = name.split(' ')
  if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase()
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
}

const getRoleText = (role) => {
  return role === 'ADMIN' ? '👑 Admin' : '👤 Khách hàng'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const getCustomerOrderCount = (userId) => {
  return orders.value.filter(o => o.userId === userId).length
}

const getCustomerTotalSpent = (userId) => {
  return orders.value
    .filter(o => o.userId === userId && o.status === 'COMPLETED')
    .reduce((sum, o) => sum + o.totalAmount, 0)
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  // Kiểm tra quyền admin
  const currentUser = authStore.user
  console.log('Current user:', currentUser)
  
  if (!authStore.isAuthenticated) {
    alert('❌ Vui lòng đăng nhập!')
    router.push('/login')
    return
  }
  
  if (!authStore.isAdmin) {
    alert('❌ Chỉ tài khoản ADMIN mới có quyền truy cập trang này!\n\nRole hiện tại: ' + (currentUser?.role || 'N/A'))
    router.push('/admin')
    return
  }
  
  fetchCustomers()
  fetchOrders()
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

/* Customer Management Specific Styles */
.customer-id {
  font-weight: 700;
  color: #667eea;
}

.customer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
}

.customer-name {
  font-weight: 600;
  color: #2c3e50;
}

.email-text {
  color: #64748b;
  font-size: 14px;
}

.phone-text {
  color: #64748b;
  font-size: 14px;
}

.role-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  display: inline-block;
}

.role-badge.customer {
  background: linear-gradient(135deg, #d0ebff 0%, #a5d8ff 100%);
  color: #1971c2;
}

.role-badge.admin {
  background: linear-gradient(135deg, #ffd43b 0%, #fab005 100%);
  color: #856404;
}

.stat-icon.active-customers {
  background: linear-gradient(135deg, #51cf66 0%, #37b24d 100%);
}

.stat-icon.admins {
  background: linear-gradient(135deg, #ffd43b 0%, #fab005 100%);
}

.stat-icon.new-customers {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.customer-detail-content {
  margin: 20px 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
  margin-bottom: 20px;
}

.large-avatar {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 32px;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.detail-info h3 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
}

.detail-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
}

.detail-section h4 {
  font-size: 16px;
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

.stats-mini-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.mini-stat {
  background: white;
  padding: 16px;
  border-radius: 10px;
  text-align: center;
  border: 2px solid #e9ecef;
}

.mini-stat-value {
  font-size: 24px;
  font-weight: 800;
  color: #667eea;
  margin-bottom: 4px;
}

.mini-stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 600;
}

/* Form Styles */
.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  font-weight: 700;
  color: #495057;
  font-size: 14px;
}

.form-group input[type="text"],
.form-group input[type="email"],
.form-group input[type="tel"],
.form-group input[type="password"],
.form-group select {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid #e0e6ed;
  border-radius: 12px;
  font-size: 15px;
  box-sizing: border-box;
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.form-group input:disabled {
  background: #f8f9fa;
  cursor: not-allowed;
}

.form-group.checkbox label {
  display: flex;
  align-items: center;
  margin: 0;
  cursor: pointer;
}

.form-group.checkbox input {
  margin-right: 10px;
  cursor: pointer;
  width: 20px;
  height: 20px;
}

.form-group select {
  background: white;
  cursor: pointer;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px solid #f1f3f5;
}

.btn-cancel {
  padding: 12px 24px;
  border: 2px solid #dee2e6;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 700;
  transition: all 0.3s ease;
  color: #495057;
}

.btn-cancel:hover {
  background: #f8f9fa;
  border-color: #adb5bd;
}

.form-group small {
  display: block;
  margin-top: 6px;
  font-size: 12px;
}
</style>
