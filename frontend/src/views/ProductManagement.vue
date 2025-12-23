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
        <a href="#products" class="nav-item active">
          <span class="nav-icon">📦</span>
          <span class="nav-text">Sản phẩm</span>
        </a>
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
          <h1>Quản Lý Sản Phẩm</h1>
          <p>Quản lý toàn bộ sản phẩm trong cửa hàng</p>
        </div>
        <div class="top-bar-actions">
          <button @click="showCreateModal = true" class="btn-primary">
            <span>➕</span>
            <span>Thêm Sản Phẩm</span>
          </button>
        </div>
      </header>

      <!-- Stats Cards -->
      <section class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon products">📦</div>
          <div class="stat-content">
            <div class="stat-label">Tổng sản phẩm</div>
            <div class="stat-value">{{ products.length }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon active">✅</div>
          <div class="stat-content">
            <div class="stat-label">Đang hoạt động</div>
            <div class="stat-value">{{ activeProducts }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon low-stock">⚠️</div>
          <div class="stat-content">
            <div class="stat-label">Sắp hết hàng</div>
            <div class="stat-value">{{ lowStockProducts }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon revenue">💰</div>
          <div class="stat-content">
            <div class="stat-label">Tổng giá trị</div>
            <div class="stat-value">{{ formatCurrency(totalValue) }}</div>
          </div>
        </div>
      </section>

      <!-- Products Table -->
      <section class="table-section">
        <div class="table-header">
          <h2>Danh Sách Sản Phẩm</h2>
          <div class="table-actions">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="🔍 Tìm kiếm sản phẩm..."
              class="search-input"
            />
            <select v-model="filterCategory" class="filter-select">
              <option value="">Tất cả danh mục</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>
        </div>

        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>Hình ảnh</th>
                <th>Tên sản phẩm</th>
                <th>Danh mục</th>
                <th>Giá</th>
                <th>Tồn kho</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="product in filteredProducts" :key="product.id">
                <td>
                  <img :src="product.imageUrl" :alt="product.name" class="product-image" />
                </td>
                <td>
                  <div class="product-info">
                    <div class="product-name">{{ product.name }}</div>
                    <div class="product-desc">{{ truncateText(product.description, 50) }}</div>
                  </div>
                </td>
                <td>
                  <span class="category-badge">{{ getCategoryName(product.categoryId) }}</span>
                </td>
                <td>
                  <div class="price-info">{{ formatCurrency(product.price) }}</div>
                </td>
                <td>
                  <span :class="['stock-badge', getStockClass(product.stock)]">
                    {{ product.stock }} 📦
                  </span>
                </td>
                <td>
                  <span :class="['status-badge', product.isActive ? 'active' : 'inactive']">
                    {{ product.isActive ? 'Hoạt động' : 'Ngừng' }}
                  </span>
                </td>
                <td>
                  <div class="action-buttons">
                    <button @click="editProduct(product)" class="btn-icon" title="Chỉnh sửa">
                      ✏️
                    </button>
                    <button @click="deleteProduct(product.id)" class="btn-icon danger" title="Xóa">
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
            Hiển thị {{ filteredProducts.length }} sản phẩm
          </div>
        </div>
      </section>
    </main>

    <!-- Create/Edit Modal -->
    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <h2>{{ showEditModal ? 'Chỉnh Sửa Sản Phẩm' : 'Thêm Sản Phẩm Mới' }}</h2>
        <form @submit.prevent="saveProduct">
          <div class="form-group">
            <label>Tên sản phẩm *</label>
            <input v-model="form.name" type="text" required placeholder="VD: iPhone 15 Pro Max">
          </div>
          <div class="form-group">
            <label>Mô tả *</label>
            <textarea v-model="form.description" required placeholder="Mô tả chi tiết về sản phẩm..."></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Giá (VNĐ) *</label>
              <input v-model.number="form.price" type="number" required placeholder="35000000">
            </div>
            <div class="form-group">
              <label>Tồn kho *</label>
              <input v-model.number="form.stock" type="number" required placeholder="100">
            </div>
          </div>
          <div class="form-group">
            <label>Danh mục *</label>
            <select v-model="form.categoryId" required>
              <option value="">Chọn danh mục</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>URL hình ảnh *</label>
            <input v-model="form.imageUrl" type="text" required placeholder="https://example.com/image.jpg">
          </div>
          <div class="form-group checkbox">
            <label>
              <input v-model="form.isActive" type="checkbox">
              Hoạt động
            </label>
          </div>
          <div class="form-actions">
            <button type="button" @click="closeModal" class="btn-cancel">Hủy</button>
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
import api from '../services/api'

const router = useRouter()
const authStore = useAuthStore()

const products = ref([])
const categories = ref([])
const searchQuery = ref('')
const filterCategory = ref('')
const showCreateModal = ref(false)
const showEditModal = ref(false)
const form = ref({
  id: null,
  name: '',
  description: '',
  price: 0,
  stock: 0,
  categoryId: '',
  imageUrl: '',
  isActive: true
})

const activeProducts = computed(() => products.value.filter(p => p.isActive).length)
const lowStockProducts = computed(() => products.value.filter(p => p.stock < 10).length)
const totalValue = computed(() => 
  products.value.reduce((sum, p) => sum + (p.price * p.stock), 0)
)

const filteredProducts = computed(() => {
  let filtered = products.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(p => 
      p.name.toLowerCase().includes(query) || 
      p.description.toLowerCase().includes(query)
    )
  }

  if (filterCategory.value) {
    filtered = filtered.filter(p => p.categoryId === filterCategory.value)
  }

  return filtered
})

const fetchProducts = async () => {
  try {
    const response = await api.getProducts()
    products.value = response.data
  } catch (error) {
    console.error('Lỗi khi tải sản phẩm:', error)
    alert('Không thể tải danh sách sản phẩm!')
  }
}

const fetchCategories = async () => {
  try {
    const response = await api.getCategories()
    categories.value = response.data.filter(c => c.isActive)
  } catch (error) {
    console.error('Lỗi khi tải danh mục:', error)
  }
}

const saveProduct = async () => {
  try {
    if (showEditModal.value) {
      await api.updateProduct(form.value.id, form.value)
      alert('Cập nhật sản phẩm thành công!')
    } else {
      await api.createProduct(form.value)
      alert('Thêm sản phẩm mới thành công!')
    }
    closeModal()
    fetchProducts()
  } catch (error) {
    console.error('Lỗi khi lưu sản phẩm:', error)
    alert(error.response?.data?.message || 'Không thể lưu sản phẩm!')
  }
}

const editProduct = (product) => {
  form.value = { ...product }
  showEditModal.value = true
}

const deleteProduct = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) return
  
  try {
    await api.deleteProduct(id)
    alert('Xóa sản phẩm thành công!')
    fetchProducts()
  } catch (error) {
    console.error('Lỗi khi xóa sản phẩm:', error)
    alert('Không thể xóa sản phẩm!')
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  form.value = {
    id: null,
    name: '',
    description: '',
    price: 0,
    stock: 0,
    categoryId: '',
    imageUrl: '',
    isActive: true
  }
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value)
}

const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : 'N/A'
}

const getStockClass = (stock) => {
  if (stock === 0) return 'out-of-stock'
  if (stock < 10) return 'low-stock'
  return 'in-stock'
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
  fetchProducts()
  fetchCategories()
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
.form-group input[type="number"],
.form-group select,
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

.form-group input:focus,
.form-group select:focus,
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

  .table-actions {
    flex-direction: column;
    width: 100%;
  }

  .search-input {
    width: 100%;
  }
}

/* Product Management Specific Styles */
.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-name {
  font-weight: 700;
  color: #2c3e50;
  font-size: 15px;
}

.product-desc {
  color: #7f8c8d;
  font-size: 13px;
}

.category-badge {
  padding: 6px 12px;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  color: #1565c0;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  display: inline-block;
}

.price-info {
  font-weight: 700;
  color: #667eea;
  font-size: 15px;
}

.stock-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  display: inline-block;
}

.stock-badge.in-stock {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  color: #155724;
}

.stock-badge.low-stock {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
  color: #856404;
}

.stock-badge.out-of-stock {
  background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
  color: #721c24;
}

.stat-icon.active {
  background: linear-gradient(135deg, #51cf66 0%, #37b24d 100%);
}

.stat-icon.low-stock {
  background: linear-gradient(135deg, #ffd43b 0%, #fab005 100%);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group select {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid #e0e6ed;
  border-radius: 12px;
  font-size: 15px;
  box-sizing: border-box;
  transition: all 0.3s ease;
  font-family: inherit;
  background: white;
  cursor: pointer;
}

.form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
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
</style>
