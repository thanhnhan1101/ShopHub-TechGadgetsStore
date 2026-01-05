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
        <a href="#categories" class="nav-item active">
          <span class="nav-icon">🏷️</span>
          <span class="nav-text">Danh mục</span>
        </a>
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
          <h1>Quản Lý Danh Mục</h1>
          <p>Quản lý danh mục sản phẩm</p>
        </div>
        <div class="top-bar-actions">
          <button @click="showCreateModal = true" class="btn-primary">
            <span>➕</span>
            <span>Thêm Danh Mục</span>
          </button>
        </div>
      </header>

      <!-- Stats Cards -->
      <section class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon categories">🏷️</div>
          <div class="stat-content">
            <div class="stat-label">Tổng danh mục</div>
            <div class="stat-value">{{ categories.length }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon active">✅</div>
          <div class="stat-content">
            <div class="stat-label">Đang hoạt động</div>
            <div class="stat-value">{{ activeCategories }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon inactive">🚫</div>
          <div class="stat-content">
            <div class="stat-label">Ngừng hoạt động</div>
            <div class="stat-value">{{ inactiveCategories }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon products">📦</div>
          <div class="stat-content">
            <div class="stat-label">Tổng sản phẩm</div>
            <div class="stat-value">{{ totalProducts }}</div>
          </div>
        </div>
      </section>

      <!-- Category Table -->
      <section class="table-section">
        <div class="table-header">
          <h2>Danh Sách Danh Mục</h2>
          <div class="table-actions">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="🔍 Tìm kiếm danh mục..."
              class="search-input"
            />
          </div>
        </div>

        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Tên Danh Mục</th>
                <th>Mô Tả</th>
                <th>Trạng Thái</th>
                <th>Ngày Tạo</th>
                <th>Thao Tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="category in filteredCategories" :key="category.id">
                <td>{{ category.id }}</td>
                <td>{{ category.name }}</td>
                <td>{{ category.description }}</td>
                <td>
                  <span :class="['status-badge', category.isActive ? 'active' : 'inactive']">
                    {{ category.isActive ? 'Hoạt động' : 'Ngừng' }}
                  </span>
                </td>
                <td>{{ formatDate(category.createdAt) }}</td>
                <td>
                  <div class="action-buttons">
                    <button @click="toggleStatus(category)" class="btn-icon" :title="category.isActive ? 'Vô hiệu hóa' : 'Kích hoạt'">
                      {{ category.isActive ? '🔓' : '🔒' }}
                    </button>
                    <button @click="editCategory(category)" class="btn-icon" title="Chỉnh sửa">
                      ✏️
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="table-footer">
          <div class="showing-info">
            Hiển thị {{ filteredCategories.length }} danh mục
          </div>
        </div>
      </section>
    </main>

    <!-- Create/Edit Modal -->
    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <h2>{{ showEditModal ? 'Chỉnh Sửa Danh Mục' : 'Thêm Danh Mục Mới' }}</h2>
        <form @submit.prevent="saveCategory">
          <div class="form-group">
            <label>Tên Danh Mục *</label>
            <input v-model="form.name" type="text" required placeholder="VD: Smartphones">
          </div>
          <div class="form-group">
            <label>Mô Tả *</label>
            <textarea v-model="form.description" required placeholder="Mô tả về danh mục..."></textarea>
          </div>
          <div class="form-group">
            <label>URL Hình Ảnh</label>
            <input v-model="form.imageUrl" type="text" placeholder="https://example.com/image.jpg">
          </div>
          <div class="form-group checkbox">
            <label>
              <input v-model="form.isActive" type="checkbox">
              Hoạt động
            </label>
          </div>
          <div class="form-actions">
            <button type="button" @click="closeModal" class="btn-cancel">Hủy</button>
            <button type="submit" class="btn-primary">{{ showEditModal ? 'Cập Nhật' : 'Tạo Mới' }}</button>
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
const { success, error } = useNotification()

const categories = ref([])
const products = ref([])
const searchQuery = ref('')
const showCreateModal = ref(false)
const showEditModal = ref(false)
const form = ref({
  id: null,
  name: '',
  description: '',
  imageUrl: '',
  isActive: true
})

const activeCategories = computed(() => categories.value.filter(c => c.isActive).length)
const inactiveCategories = computed(() => categories.value.filter(c => !c.isActive).length)
const totalProducts = computed(() => products.value.length)

const filteredCategories = computed(() => {
  if (!searchQuery.value) return categories.value
  
  const query = searchQuery.value.toLowerCase()
  return categories.value.filter(c => 
    c.name.toLowerCase().includes(query) || 
    c.description.toLowerCase().includes(query)
  )
})

const fetchCategories = async () => {
  try {
    console.log('Fetching categories...')
    const response = await api.getCategories()
    categories.value = response.data
    console.log('Categories loaded:', categories.value.length)
  } catch (err) {
    console.error('Lỗi khi tải danh mục:', err)
    if (err.response?.status !== 401) {
      error('Không thể tải danh sách danh mục!')
    }
  }
}

const fetchProducts = async () => {
  try {
    const response = await api.getProducts()
    products.value = response.data
  } catch (error) {
    console.error('Lỗi khi tải sản phẩm:', error)
  }
}

const saveCategory = async () => {
  try {
    if (showEditModal.value) {
      await api.updateCategory(form.value.id, form.value)
      success('Cập nhật danh mục thành công!')
    } else {
      await api.createCategory(form.value)
      success('Thêm danh mục mới thành công!')
    }
    closeModal()
    fetchCategories()
  } catch (err) {
    console.error('Lỗi khi lưu danh mục:', err)
    error('Không thể lưu danh mục!')
  }
}

const editCategory = (category) => {
  form.value = { ...category }
  showEditModal.value = true
}

const deleteCategory = async (id) => {
  if (!confirm('⚠️ BẠN CHẮC CHẮN MUỐN XÓA VĨNH VIỄN?\n\nHành động này KHÔNG THỂ HOÀN TÁC!\nDanh mục sẽ bị xóa khỏi database.\n\nNên dùng nút 🔒 để vô hiệu hóa thay vì xóa.')) return
  
  try {
    await api.deleteCategory(id)
    success('Xóa danh mục vĩnh viễn thành công!')
    fetchCategories()
  } catch (err) {
    console.error('Lỗi khi xóa danh mục:', err)
    error('Không thể xóa danh mục! Có thể danh mục đang có sản phẩm.')
  }
}

const toggleStatus = async (category) => {
  const action = category.isActive ? 'vô hiệu hóa' : 'kích hoạt'
  if (!confirm(`Bạn muốn ${action} danh mục "${category.name}"?`)) return
  
  try {
    await api.toggleCategoryStatus(category.id)
    success(`${action.charAt(0).toUpperCase() + action.slice(1)} thành công!`)
    fetchCategories()
  } catch (err) {
    console.error('Lỗi khi thay đổi trạng thái:', err)
    error('Không thể thay đổi trạng thái!')
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  form.value = {
    id: null,
    name: '',
    description: '',
    imageUrl: '',
    isActive: true
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('vi-VN')
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
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

.stat-icon.categories {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.active {
  background: linear-gradient(135deg, #51cf66 0%, #37b24d 100%);
}

.stat-icon.inactive {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
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

.status-badge.active {
  background: #d3f9d8;
  color: #2b8a3e;
}

.status-badge.inactive {
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
  margin-top: 24px;
  padding-top: 24px;
  border-top: 2px solid #f1f3f5;
}

.showing-info {
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
}

/* Modal */
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
.form-group textarea {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid #e0e6ed;
  border-radius: 12px;
  font-size: 15px;
  box-sizing: border-box;
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-group input[type="text"]:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  min-height: 120px;
  resize: vertical;
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

/* Responsive */
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
</style>
