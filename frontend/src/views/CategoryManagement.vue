<template>
  <div class="category-management">
    <div class="header">
      <h1>Quản Lý Danh Mục</h1>
      <button @click="showCreateModal = true" class="btn-primary">
        + Thêm Danh Mục Mới
      </button>
    </div>

    <!-- Category Table -->
    <div class="table-container">
      <table>
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
          <tr v-for="category in categories" :key="category.id">
            <td>{{ category.id }}</td>
            <td>{{ category.name }}</td>
            <td>{{ category.description }}</td>
            <td>
              <span :class="['status', category.isActive ? 'active' : 'inactive']">
                {{ category.isActive ? 'Hoạt động' : 'Ngừng' }}
              </span>
            </td>
            <td>{{ formatDate(category.createdAt) }}</td>
            <td>
              <button @click="editCategory(category)" class="btn-edit">Sửa</button>
              <button @click="deleteCategory(category.id)" class="btn-delete">Xóa</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

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
import { ref, onMounted } from 'vue';
import api from '../services/api';

const categories = ref([]);
const showCreateModal = ref(false);
const showEditModal = ref(false);
const form = ref({
  id: null,
  name: '',
  description: '',
  imageUrl: '',
  isActive: true
});

const fetchCategories = async () => {
  try {
    const response = await api.getCategories();
    categories.value = response.data;
  } catch (error) {
    console.error('Lỗi khi tải danh mục:', error);
    alert('Không thể tải danh sách danh mục!');
  }
};

const saveCategory = async () => {
  try {
    if (showEditModal.value) {
      await api.updateCategory(form.value.id, form.value);
      alert('Cập nhật danh mục thành công!');
    } else {
      await api.createCategory(form.value);
      alert('Thêm danh mục mới thành công!');
    }
    closeModal();
    fetchCategories();
  } catch (error) {
    console.error('Lỗi khi lưu danh mục:', error);
    alert('Không thể lưu danh mục!');
  }
};

const editCategory = (category) => {
  form.value = { ...category };
  showEditModal.value = true;
};

const deleteCategory = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa danh mục này?')) return;
  
  try {
    await api.deleteCategory(id);
    alert('Xóa danh mục thành công!');
    fetchCategories();
  } catch (error) {
    console.error('Lỗi khi xóa danh mục:', error);
    alert('Không thể xóa danh mục!');
  }
};

const closeModal = () => {
  showCreateModal.value = false;
  showEditModal.value = false;
  form.value = {
    id: null,
    name: '',
    description: '',
    imageUrl: '',
    isActive: true
  };
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString('vi-VN');
};

onMounted(() => {
  fetchCategories();
});
</script>

<style scoped>
.category-management {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  animation: slideIn 0.5s ease;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 3px solid transparent;
  border-image: linear-gradient(90deg, #667eea 0%, #764ba2 100%) 1;
}

.header h1 {
  font-size: 36px;
  color: #2c3e50;
  font-weight: 800;
  letter-spacing: -1px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 14px 28px;
  border-radius: 12px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 700;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  letter-spacing: 0.3px;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(102, 126, 234, 0.4);
}

.table-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

th {
  padding: 18px 20px;
  text-align: left;
  font-weight: 700;
  color: #495057;
  border-bottom: 2px solid #dee2e6;
  font-size: 14px;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

tbody tr {
  transition: all 0.3s ease;
  border-bottom: 1px solid #f1f3f5;
}

tbody tr:hover {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  transform: scale(1.01);
}

td {
  padding: 18px 20px;
  color: #495057;
  font-size: 15px;
}

.status {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.3px;
  display: inline-block;
}

.status.active {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  color: #155724;
  box-shadow: 0 2px 8px rgba(21, 87, 36, 0.2);
}

.status.inactive {
  background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
  color: #721c24;
  box-shadow: 0 2px 8px rgba(114, 28, 36, 0.2);
}

.btn-edit, .btn-delete {
  padding: 8px 18px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
  margin-right: 10px;
  transition: all 0.3s ease;
  letter-spacing: 0.3px;
}

.btn-edit {
  background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
  color: #000;
  box-shadow: 0 3px 10px rgba(255, 193, 7, 0.3);
}

.btn-edit:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 193, 7, 0.4);
}

.btn-delete {
  background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
  color: white;
  box-shadow: 0 3px 10px rgba(220, 53, 69, 0.3);
}

.btn-delete:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(220, 53, 69, 0.4);
}

/* Modal Styles */
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
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
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
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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
  letter-spacing: 0.3px;
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

.form-group.checkbox {
  display: flex;
  align-items: center;
}

.form-group.checkbox label {
  display: flex;
  align-items: center;
  margin: 0;
  cursor: pointer;
  font-size: 15px;
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
  transform: translateY(-1px);
}
</style>
