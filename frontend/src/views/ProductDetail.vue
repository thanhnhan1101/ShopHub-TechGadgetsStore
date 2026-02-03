<template>
  <div class="product-detail">
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Đang tải...</p>
    </div>
    
    <div v-else-if="product" class="detail-wrapper">
      <div class="breadcrumb">
        <span @click="goBack" class="breadcrumb-link">Trang chủ</span>
        <span class="separator">/</span>
        <span class="breadcrumb-link">{{ product.category?.name }}</span>
        <span class="separator">/</span>
        <span class="current">{{ product.name }}</span>
      </div>

      <div class="detail-container">
        <div class="image-section">
          <!-- Main Image Display -->
          <div class="product-image-large">
            <img v-if="currentImage" :src="currentImage" :alt="product.name" />
            <div v-else class="placeholder-large">
              <span class="icon">📱</span>
              <p>Không có ảnh</p>
            </div>
            <div class="image-badge" v-if="product.stock < 10 && product.stock > 0">
              🔥 Sắp hết hàng
            </div>
            <div class="image-badge out" v-if="product.stock === 0">
              ❌ Hết hàng
            </div>
            
            <!-- Navigation Arrows -->
            <div v-if="productImages.length > 1" class="image-navigation">
              <button class="nav-btn prev" @click="previousImage">
                <span>‹</span>
              </button>
              <button class="nav-btn next" @click="nextImage">
                <span>›</span>
              </button>
            </div>

            <!-- Image Counter -->
            <div v-if="productImages.length > 1" class="image-counter">
              {{ currentImageIndex + 1 }} / {{ productImages.length }}
            </div>
          </div>

          <!-- Thumbnail Gallery -->
          <div v-if="productImages.length > 1" class="thumbnail-container">
            <button 
              v-if="canScrollLeft" 
              class="thumbnail-nav-btn left" 
              @click="scrollThumbnails('left')"
            >
              ‹
            </button>
            
            <div class="thumbnail-gallery" ref="thumbnailGalleryRef">
              <div 
                v-for="(image, index) in productImages" 
                :key="index"
                class="thumbnail-item"
                :class="{ active: currentImageIndex === index }"
                @click="selectImage(index)"
              >
                <img :src="image" :alt="product.name + ' - ' + (index + 1)" />
              </div>
            </div>
            
            <button 
              v-if="canScrollRight" 
              class="thumbnail-nav-btn right" 
              @click="scrollThumbnails('right')"
            >
              ›
            </button>
          </div>
        </div>

        <div class="info-section">
          <div class="product-header">
            <span class="category-badge">{{ product.category?.name }}</span>
            <h1 class="product-title">{{ product.name }}</h1>
            
            <div class="price-section">
              <span class="price-label">Giá bán:</span>
              <span class="price">{{ formatPrice(product.price) }}</span>
            </div>
            
            <div class="stock-info" :class="stockClass">
              <span class="stock-icon">{{ stockIcon }}</span>
              <span>{{ stockText }}</span>
            </div>
          </div>

          <div class="divider"></div>

          <div class="description-section">
            <h3 class="section-title">📋 Mô tả sản phẩm</h3>
            <p class="description">{{ product.description }}</p>
          </div>

          <div class="divider"></div>

          <div class="purchase-section">
            <div class="quantity-selector">
              <label class="quantity-label">Số lượng:</label>
              <div class="quantity-controls">
                <button @click="decreaseQuantity" class="btn-qty" :disabled="quantity <= 1">
                  <span>−</span>
                </button>
                <input v-model.number="quantity" type="number" min="1" :max="product.stock" readonly />
                <button @click="increaseQuantity" class="btn-qty" :disabled="quantity >= product.stock">
                  <span>+</span>
                </button>
              </div>
              <span class="stock-available">{{ product.stock }} sản phẩm có sẵn</span>
            </div>

            <div class="action-buttons">
              <button 
                class="btn btn-cart"
                @click="addToCart"
                :disabled="product.stock === 0"
              >
                <span class="btn-icon">🛒</span>
                <span>Thêm vào giỏ hàng</span>
              </button>
              <button class="btn btn-back" @click="goBack">
                <span class="btn-icon">←</span>
                <span>Quay lại</span>
              </button>
            </div>
          </div>

          <div class="features">
            <div class="feature-item">
              <span class="feature-icon">🚚</span>
              <div>
                <strong>Giao hàng nhanh</strong>
                <p>Miễn phí ship đơn từ 500k</p>
              </div>
            </div>
            <div class="feature-item">
              <span class="feature-icon">✅</span>
              <div>
                <strong>Chính hãng 100%</strong>
                <p>Cam kết hàng chính hãng</p>
              </div>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🔄</span>
              <div>
                <strong>Đổi trả dễ dàng</strong>
                <p>Trong vòng 7 ngày</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Review Section -->
      <div class="reviews-section">
        <ReviewList 
          v-if="product"
          :product-id="product.id"
          ref="reviewListRef"
        />
      </div>
    </div>

    <div v-else class="not-found">
      <div class="not-found-icon">😢</div>
      <h2>Không tìm thấy sản phẩm</h2>
      <button class="btn btn-back" @click="goBack">Quay lại trang chủ</button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { useNotification } from '../composables/useNotification'
import api from '../services/api'
import ReviewList from '../components/ReviewList.vue'

export default {
  name: 'ProductDetail',
  components: {
    ReviewList
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const cartStore = useCartStore()
    const { showNotification } = useNotification()
    
    const product = ref(null)
    const loading = ref(false)
    const quantity = ref(1)
    const currentImage = ref('')
    const currentImageIndex = ref(0)
    const reviewListRef = ref(null)
    const thumbnailGalleryRef = ref(null)
    const canScrollLeft = ref(false)
    const canScrollRight = ref(false)
    let autoPlayInterval = null

    // Computed: Lấy danh sách ảnh của sản phẩm
    const productImages = computed(() => {
      if (!product.value) return []
      
      // Nếu có images array (nhiều ảnh)
      if (product.value.images && product.value.images.length > 0) {
        return product.value.images.map(img => img.imageUrl)
      }
      
      // Fallback: imageUrl cũ (1 ảnh)
      if (product.value.imageUrl) {
        return [product.value.imageUrl]
      }
      
      return []
    })

    const stockClass = computed(() => {
      if (!product.value) return ''
      if (product.value.stock === 0) return 'out-of-stock'
      if (product.value.stock < 10) return 'low-stock'
      return 'in-stock'
    })

    const stockText = computed(() => {
      if (!product.value) return ''
      if (product.value.stock === 0) return 'Hết hàng'
      if (product.value.stock < 10) return `Chỉ còn ${product.value.stock} sản phẩm`
      return 'Còn hàng'
    })

    const stockIcon = computed(() => {
      if (!product.value) return ''
      if (product.value.stock === 0) return '❌'
      if (product.value.stock < 10) return '⚠️'
      return '✅'
    })

    const formatPrice = (price) => {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
      }).format(price)
    }

    const fetchProduct = async () => {
      loading.value = true
      try {
        const response = await api.getProductById(route.params.id)
        product.value = response.data
        
        // Set ảnh đầu tiên làm ảnh hiện tại
        if (productImages.value.length > 0) {
          currentImageIndex.value = 0
          currentImage.value = productImages.value[0]
          
          // Bắt đầu auto-play nếu có nhiều hơn 1 ảnh
          if (productImages.value.length > 1) {
            startAutoPlay()
            // Check scroll buttons sau khi DOM update
            setTimeout(checkScrollButtons, 100)
          }
        }
      } catch (error) {
        console.error('Error fetching product:', error)
      } finally {
        loading.value = false
      }
    }

    const startAutoPlay = () => {
      stopAutoPlay() // Clear existing interval nếu có
      autoPlayInterval = setInterval(() => {
        nextImage()
      }, 4000) // 3 giây
    }

    const stopAutoPlay = () => {
      if (autoPlayInterval) {
        clearInterval(autoPlayInterval)
        autoPlayInterval = null
      }
    }

    const selectImage = (index) => {
      currentImageIndex.value = index
      currentImage.value = productImages.value[index]
      // Reset auto-play khi user click vào thumbnail
      if (productImages.value.length > 1) {
        startAutoPlay()
      }
    }

    const nextImage = () => {
      if (productImages.value.length === 0) return
      currentImageIndex.value = (currentImageIndex.value + 1) % productImages.value.length
      currentImage.value = productImages.value[currentImageIndex.value]
    }

    const previousImage = () => {
      if (productImages.value.length === 0) return
      currentImageIndex.value = currentImageIndex.value === 0 
        ? productImages.value.length - 1 
        : currentImageIndex.value - 1
      currentImage.value = productImages.value[currentImageIndex.value]
      // Reset auto-play khi user click nút điều hướng
      if (productImages.value.length > 1) {
        startAutoPlay()
      }
    }

    const checkScrollButtons = () => {
      if (!thumbnailGalleryRef.value) return
      const gallery = thumbnailGalleryRef.value
      canScrollLeft.value = gallery.scrollLeft > 0
      canScrollRight.value = gallery.scrollLeft < (gallery.scrollWidth - gallery.clientWidth - 1)
    }

    const scrollThumbnails = (direction) => {
      if (!thumbnailGalleryRef.value) return
      const gallery = thumbnailGalleryRef.value
      const scrollAmount = 200
      
      if (direction === 'left') {
        gallery.scrollBy({ left: -scrollAmount, behavior: 'smooth' })
      } else {
        gallery.scrollBy({ left: scrollAmount, behavior: 'smooth' })
      }
      
      setTimeout(checkScrollButtons, 300)
    }

    const increaseQuantity = () => {
      if (product.value && quantity.value < product.value.stock) {
        quantity.value++
      }
    }

    const decreaseQuantity = () => {
      if (quantity.value > 1) {
        quantity.value--
      }
    }

    const addToCart = async () => {
      try {
        await cartStore.addToCart(product.value, quantity.value)
        showNotification(`Đã thêm ${quantity.value} sản phẩm vào giỏ hàng!`, 'success')
        quantity.value = 1
      } catch (error) {
        showNotification('Không thể thêm vào giỏ hàng', 'error')
      }
    }

    const goBack = () => {
      router.push('/')
    }

    onMounted(() => {
      fetchProduct()
    })

    onUnmounted(() => {
      stopAutoPlay()
    })

    return {
      product,
      loading,
      quantity,
      currentImage,
      currentImageIndex,
      productImages,
      stockClass,
      stockText,
      stockIcon,
      formatPrice,
      increaseQuantity,
      decreaseQuantity,
      addToCart,
      goBack,
      selectImage,
      nextImage,
      previousImage,
      reviewListRef,
      thumbnailGalleryRef,
      canScrollLeft,
      canScrollRight,
      scrollThumbnails
    }
  }
}
</script>

<style scoped>
.product-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 30px 20px;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading p {
  font-size: 18px;
  color: #667eea;
  font-weight: 500;
}

.detail-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

.breadcrumb {
  background: white;
  padding: 15px 25px;
  border-radius: 12px;
  margin-bottom: 25px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  font-size: 14px;
}

.breadcrumb-link {
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s;
}

.breadcrumb-link:hover {
  color: #5568d3;
  text-decoration: underline;
}

.separator {
  margin: 0 10px;
  color: #bbb;
}

.current {
  color: #555;
  font-weight: 500;
}

.detail-container {
  display: grid;
  grid-template-columns: 45% 55%;
  gap: 30px;
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
}

.image-section {
  position: sticky;
  top: 20px;
  height: fit-content;
}

.product-image-large {
  position: relative;
  width: 100%;
  height: 550px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0,0,0,0.08);
  transition: transform 0.3s;
}

.product-image-large:hover {
  transform: scale(1.02);
}

.product-image-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-large {
  text-align: center;
  color: #adb5bd;
}

.placeholder-large .icon {
  font-size: 100px;
  display: block;
  margin-bottom: 15px;
}

.placeholder-large p {
  font-size: 16px;
  font-weight: 500;
}

.image-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: white;
  padding: 10px 20px;
  border-radius: 25px;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 4px 15px rgba(255,107,107,0.4);
  animation: pulse 2s infinite;
}

.image-badge.out {
  background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
  animation: none;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.thumbnail-container {
  position: relative;
  margin-top: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.thumbnail-gallery {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  scroll-behavior: smooth;
  scrollbar-width: none;
  -ms-overflow-style: none;
  flex: 1;
}

.thumbnail-gallery::-webkit-scrollbar {
  display: none;
}

.thumbnail-item {
  flex: 0 0 80px;
  width: 80px;
  height: 80px;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.thumbnail-item:hover {
  border-color: #667eea;
  transform: scale(1.05);
}

.thumbnail-item.active {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-nav-btn {
  flex: 0 0 40px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: white;
  border: 2px solid #667eea;
  color: #667eea;
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 2;
}

.thumbnail-nav-btn:hover {
  background: #667eea;
  color: white;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.thumbnail-nav-btn:active {
  transform: scale(0.95);
}

.image-navigation {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  transform: translateY(-50%);
  display: flex;
  justify-content: space-between;
  padding: 0 15px;
  pointer-events: none;
}

.nav-btn {
  pointer-events: all;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.95);
  border: 2px solid #667eea;
  color: #667eea;
  font-size: 32px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.nav-btn:hover {
  background: #667eea;
  color: white;
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.nav-btn:active {
  transform: scale(0.95);
}

.nav-btn span {
  line-height: 1;
  display: block;
}

.image-counter {
  position: absolute;
  bottom: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.product-header {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.category-badge {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 8px 18px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  width: fit-content;
  box-shadow: 0 4px 15px rgba(102,126,234,0.3);
}

.product-title {
  font-size: 32px;
  font-weight: 700;
  color: #2d3748;
  line-height: 1.3;
  margin: 0;
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 15px;
  padding: 20px 0;
}

.price-label {
  font-size: 16px;
  color: #718096;
  font-weight: 500;
}

.price {
  font-size: 40px;
  font-weight: 800;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stock-info {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 10px;
  font-weight: 600;
  font-size: 15px;
  width: fit-content;
}

.stock-info.in-stock {
  background: #d4edda;
  color: #155724;
  border-left: 4px solid #28a745;
}

.stock-info.low-stock {
  background: #fff3cd;
  color: #856404;
  border-left: 4px solid #ffc107;
}

.stock-info.out-of-stock {
  background: #f8d7da;
  color: #721c24;
  border-left: 4px solid #dc3545;
}

.stock-icon {
  font-size: 18px;
}

.divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
  margin: 10px 0;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 12px;
}

.description-section {
  background: #f7fafc;
  padding: 20px;
  border-radius: 12px;
  border-left: 4px solid #667eea;
}

.description {
  line-height: 1.8;
  color: #4a5568;
  margin: 0;
  white-space: pre-line;
  font-size: 15px;
}

.purchase-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 25px;
  border-radius: 15px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.quantity-selector {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quantity-label {
  font-weight: 600;
  color: #2d3748;
  font-size: 15px;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.btn-qty {
  width: 45px;
  height: 45px;
  border: 2px solid #667eea;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-qty:hover:not(:disabled) {
  background: #667eea;
  color: white;
  transform: scale(1.1);
}

.btn-qty:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.quantity-controls input {
  width: 80px;
  height: 45px;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  background: white;
}

.stock-available {
  font-size: 13px;
  color: #718096;
  font-style: italic;
}

.action-buttons {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 15px;
}

.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 16px 30px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.btn-cart {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-cart:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(102,126,234,0.4);
}

.btn-cart:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
  transform: none;
}

.btn-back {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
}

.btn-back:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
}

.btn-icon {
  font-size: 20px;
}

.features {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-top: 10px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  transition: transform 0.3s;
}

.feature-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.feature-icon {
  font-size: 28px;
}

.feature-item strong {
  display: block;
  font-size: 13px;
  color: #2d3748;
  margin-bottom: 3px;
}

.feature-item p {
  font-size: 11px;
  color: #718096;
  margin: 0;
}

.not-found {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  max-width: 500px;
  margin: 0 auto;
}

.not-found-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.not-found h2 {
  color: #2d3748;
  margin-bottom: 25px;
}

/* Reviews Section */
.reviews-section {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

@media (max-width: 992px) {
  .detail-container {
    grid-template-columns: 1fr;
    gap: 25px;
  }
  
  .image-section {
    position: relative;
  }
  
  .product-image-large {
    height: 400px;
  }
  
  .features {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .product-title {
    font-size: 24px;
  }
  
  .price {
    font-size: 32px;
  }
  
  .action-buttons {
    grid-template-columns: 1fr;
  }
  
  .features {
    gap: 10px;
  }
  
  .product-image-large {
    height: 350px;
  }
}
</style>
