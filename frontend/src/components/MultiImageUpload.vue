<template>
  <div class="multi-image-upload">
    <label class="section-label">Hình ảnh sản phẩm *</label>
    
    <!-- Image Gallery Preview -->
    <div v-if="previewImages.length > 0" class="image-gallery">
      <div 
        v-for="(image, index) in previewImages" 
        :key="index" 
        class="gallery-item"
        :class="{ primary: index === 0 }"
      >
        <img :src="image.url" :alt="'Ảnh ' + (index + 1)" />
        <div class="image-overlay">
          <div class="overlay-badges">
            <span v-if="index === 0" class="primary-badge">Ảnh chính</span>
            <span class="index-badge">{{ index + 1 }}</span>
          </div>
          <div class="overlay-actions">
            <button 
              v-if="index > 0"
              type="button" 
              @click="moveUp(index)" 
              class="btn-move"
              title="Di chuyển lên"
            >
              ⬆️
            </button>
            <button 
              v-if="index < previewImages.length - 1"
              type="button" 
              @click="moveDown(index)" 
              class="btn-move"
              title="Di chuyển xuống"
            >
              ⬇️
            </button>
            <button 
              type="button" 
              @click="removeImage(index)" 
              class="btn-remove"
              title="Xóa ảnh"
            >
              🗑️
            </button>
          </div>
        </div>
        <div v-if="image.uploading" class="upload-progress">
          <div class="spinner"></div>
          <span>Đang upload...</span>
        </div>
        <div v-if="image.error" class="upload-error">
          ❌ {{ image.error }}
        </div>
      </div>
      
      <!-- Add More Button -->
      <div 
        v-if="previewImages.length < maxImages" 
        class="gallery-item add-more"
        @click="triggerFileInput"
      >
        <div class="add-more-content">
          <span class="add-icon">➕</span>
          <span class="add-text">Thêm ảnh</span>
        </div>
      </div>
    </div>

    <!-- Initial Upload Area -->
    <div v-else class="upload-area" @click="triggerFileInput">
      <div class="upload-content">
        <div class="upload-icon">📤</div>
        <div class="upload-text">
          <strong>Click để chọn ảnh</strong>
          <span>hoặc kéo thả file vào đây</span>
        </div>
        <div class="upload-hint">
          PNG, JPG, GIF (Max {{ maxFileSize }}MB mỗi file, tối đa {{ maxImages }} ảnh)
        </div>
      </div>
    </div>

    <!-- Hidden File Input -->
    <input 
      ref="fileInput"
      type="file" 
      multiple
      accept="image/*"
      @change="handleFileSelect"
      style="display: none;"
    />

    <!-- Upload Info -->
    <div class="upload-info">
      <div class="info-item">
        <span class="info-label">Đã chọn:</span>
        <span class="info-value">{{ previewImages.length }}/{{ maxImages }} ảnh</span>
      </div>
      <div v-if="previewImages.length > 0" class="info-hint">
        💡 Ảnh đầu tiên sẽ là ảnh chính hiển thị. Kéo để sắp xếp lại thứ tự.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import api from '../services/api'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  maxImages: {
    type: Number,
    default: 10
  },
  maxFileSize: {
    type: Number,
    default: 5 // MB
  }
})

const emit = defineEmits(['update:modelValue'])

const fileInput = ref(null)
const previewImages = ref([])

// Initialize với giá trị từ parent
watch(() => props.modelValue, (newValue) => {
  if (newValue && newValue.length > 0 && previewImages.value.length === 0) {
    previewImages.value = newValue.map((url, index) => ({
      url: url,
      uploaded: true,
      uploading: false,
      error: null
    }))
  }
}, { immediate: true })

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelect = async (event) => {
  const files = Array.from(event.target.files)
  
  if (previewImages.value.length + files.length > props.maxImages) {
    alert(`❌ Chỉ được upload tối đa ${props.maxImages} ảnh!`)
    return
  }

  for (const file of files) {
    // Validate file
    if (!file.type.startsWith('image/')) {
      alert(`❌ File ${file.name} không phải là ảnh!`)
      continue
    }

    if (file.size > props.maxFileSize * 1024 * 1024) {
      alert(`❌ File ${file.name} quá lớn (max ${props.maxFileSize}MB)!`)
      continue
    }

    // Create preview
    const reader = new FileReader()
    const imageObj = {
      url: '',
      uploaded: false,
      uploading: true,
      error: null,
      file: file
    }
    
    previewImages.value.push(imageObj)
    const currentIndex = previewImages.value.length - 1

    reader.onload = async (e) => {
      imageObj.url = e.target.result

      // Upload to server
      try {
        const response = await api.uploadImage(file)
        imageObj.url = response.data.imageUrl
        imageObj.uploaded = true
        imageObj.uploading = false
        
        updateParentValue()
      } catch (error) {
        console.error('Upload error:', error)
        imageObj.error = error.response?.data?.error || 'Upload thất bại'
        imageObj.uploading = false
      }
    }

    reader.readAsDataURL(file)
  }

  // Reset input
  event.target.value = ''
}

const removeImage = (index) => {
  previewImages.value.splice(index, 1)
  updateParentValue()
}

const moveUp = (index) => {
  if (index > 0) {
    const temp = previewImages.value[index]
    previewImages.value[index] = previewImages.value[index - 1]
    previewImages.value[index - 1] = temp
    updateParentValue()
  }
}

const moveDown = (index) => {
  if (index < previewImages.value.length - 1) {
    const temp = previewImages.value[index]
    previewImages.value[index] = previewImages.value[index + 1]
    previewImages.value[index + 1] = temp
    updateParentValue()
  }
}

const updateParentValue = () => {
  const urls = previewImages.value
    .filter(img => img.uploaded && !img.error)
    .map(img => img.url)
  emit('update:modelValue', urls)
}
</script>

<style scoped>
.multi-image-upload {
  margin-bottom: 1.5rem;
}

.section-label {
  display: block;
  font-weight: 600;
  font-size: 0.95rem;
  color: #2d3748;
  margin-bottom: 0.75rem;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1rem;
  margin-bottom: 1rem;
}

.gallery-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 12px;
  overflow: hidden;
  border: 2px solid #e2e8f0;
  background: #f7fafc;
  cursor: pointer;
  transition: all 0.3s ease;
}

.gallery-item:hover {
  border-color: #4299e1;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.2);
}

.gallery-item.primary {
  border-color: #48bb78;
  border-width: 3px;
}

.gallery-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.6) 0%, transparent 40%, transparent 60%, rgba(0,0,0,0.6) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 0.5rem;
}

.gallery-item:hover .image-overlay {
  opacity: 1;
}

.overlay-badges {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.primary-badge,
.index-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  font-size: 0.7rem;
  font-weight: 600;
  color: white;
}

.primary-badge {
  background: #48bb78;
}

.index-badge {
  background: #4299e1;
}

.overlay-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
}

.btn-move,
.btn-remove {
  background: white;
  border: none;
  border-radius: 6px;
  width: 32px;
  height: 32px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-move:hover {
  background: #4299e1;
  transform: scale(1.1);
}

.btn-remove:hover {
  background: #f56565;
  transform: scale(1.1);
}

.add-more {
  border: 2px dashed #cbd5e0;
  background: #f7fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.add-more:hover {
  border-color: #4299e1;
  background: #ebf8ff;
}

.add-more-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.add-icon {
  font-size: 2rem;
}

.add-text {
  font-size: 0.85rem;
  color: #4a5568;
  font-weight: 500;
}

.upload-area {
  border: 2px dashed #cbd5e0;
  border-radius: 12px;
  padding: 3rem 2rem;
  text-align: center;
  background: #f7fafc;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-area:hover {
  border-color: #4299e1;
  background: #ebf8ff;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.upload-icon {
  font-size: 3rem;
}

.upload-text {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.upload-text strong {
  font-size: 1.1rem;
  color: #2d3748;
}

.upload-text span {
  font-size: 0.9rem;
  color: #718096;
}

.upload-hint {
  font-size: 0.85rem;
  color: #a0aec0;
}

.upload-progress {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #e2e8f0;
  border-top-color: #4299e1;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.upload-error {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: #f56565;
  color: white;
  padding: 0.5rem;
  font-size: 0.75rem;
  text-align: center;
}

.upload-info {
  background: #ebf8ff;
  border: 1px solid #bee3f8;
  border-radius: 8px;
  padding: 0.75rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-weight: 500;
  color: #2c5282;
}

.info-value {
  font-weight: 600;
  color: #2b6cb0;
}

.info-hint {
  font-size: 0.85rem;
  color: #2c5282;
  line-height: 1.4;
}
</style>
