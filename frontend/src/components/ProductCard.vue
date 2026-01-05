<template>
  <div class="product-card" @click="goToDetail">
    <div class="product-image">
      <img 
        v-if="product.imageUrl && !imageError" 
        :src="product.imageUrl" 
        :alt="product.name"
        loading="lazy"
        @error="handleImageError"
      />
      <div v-else class="placeholder-image">📱</div>
    </div>
    
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-description">{{ truncatedDescription }}</p>
      <div class="product-footer">
        <span class="product-price">{{ formatPrice(product.price) }}</span>
        <span class="product-stock" :class="stockClass">
          {{ stockText }}
        </span>
      </div>
      <button 
        class="btn btn-primary add-to-cart-btn"
        @click.stop="addToCart"
        :disabled="product.stock === 0"
      >
        Thêm vào giỏ
      </button>
    </div>
  </div>
</template>

<script>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { useAuthStore } from '../stores/auth'
import { useNotification } from '../composables/useNotification'

export default {
  name: 'ProductCard',
  props: {
    product: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const router = useRouter()
    const cartStore = useCartStore()
    const authStore = useAuthStore()
    const imageError = ref(false)
    const { success, error, warning } = useNotification()
    
    const handleImageError = () => {
      imageError.value = true
    }
    
    const truncatedDescription = computed(() => {
      if (!props.product.description) return ''
      return props.product.description.length > 80
        ? props.product.description.substring(0, 80) + '...'
        : props.product.description
    })
    
    const stockClass = computed(() => {
      if (props.product.stock === 0) return 'out-of-stock'
      if (props.product.stock < 10) return 'low-stock'
      return 'in-stock'
    })
    
    const stockText = computed(() => {
      if (props.product.stock === 0) return 'Hết hàng'
      if (props.product.stock < 10) return `Chỉ còn ${props.product.stock}`
      return 'Còn hàng'
    })
    
    const formatPrice = (price) => {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
      }).format(price)
    }
    
    const goToDetail = () => {
      router.push(`/product/${props.product.id}`)
    }
    
    const addToCart = async () => {
      // Check authentication using authStore
      const token = localStorage.getItem('token')
      const user = localStorage.getItem('user')
      
      console.log('=== DEBUG ADD TO CART ===')
      console.log('Token in localStorage:', token ? 'EXISTS' : 'MISSING')
      console.log('User in localStorage:', user ? JSON.parse(user) : 'MISSING')
      console.log('authStore.isAuthenticated:', authStore.isAuthenticated)
      console.log('authStore.user:', authStore.user)
      console.log('========================')
      
      if (!authStore.isAuthenticated || !authStore.user?.id) {
        if (confirm('Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng. Đăng nhập ngay?')) {
          router.push('/login')
        }
        return
      }
      
      try {
        console.log('ProductCard - Adding to cart:', props.product)
        await cartStore.addToCart(props.product, 1)
        success('Đã thêm sản phẩm vào giỏ hàng!')
      } catch (err) {
        console.error('ProductCard - Error adding to cart:', err)
        console.error('Error response:', err.response)
        if (err.response?.status === 403) {
          warning('Backend từ chối request. Có thể token đã hết hạn hoặc không hợp lệ.')
          if (confirm('Đăng nhập lại?')) {
            authStore.logout()
            router.push('/login')
          }
        } else {
          error('Không thể thêm vào giỏ hàng')
        }
      }
    }
    
    return {
      imageError,
      handleImageError,
      truncatedDescription,
      stockClass,
      stockText,
      goToDetail,
      addToCart,
      formatPrice
    }
  }
}
</script>

<style scoped>
.product-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.25);
  border-color: rgba(102, 126, 234, 0.3);
}

.product-image {
  width: 100%;
  height: 280px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  padding: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.product-image::after {
  display: none;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center;
  transition: transform 0.4s ease;
  filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.08));
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.placeholder-image {
  font-size: 80px;
  opacity: 0.3;
  z-index: 1;
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 19px;
  font-weight: 700;
  margin-bottom: 10px;
  color: #2c3e50;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-description {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 16px;
  min-height: 42px;
  line-height: 1.5;
  white-space: pre-line;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.product-price {
  font-size: 26px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.product-stock {
  font-size: 12px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 20px;
  letter-spacing: 0.3px;
}

.in-stock {
  background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
  color: #155724;
  box-shadow: 0 2px 8px rgba(21, 87, 36, 0.15);
}

.low-stock {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
  color: #856404;
  box-shadow: 0 2px 8px rgba(133, 100, 4, 0.15);
}

.out-of-stock {
  background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
  color: #721c24;
  box-shadow: 0 2px 8px rgba(114, 28, 36, 0.15);
}

.add-to-cart-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 14px 24px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  letter-spacing: 0.5px;
}

.add-to-cart-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.add-to-cart-btn:active {
  transform: translateY(0);
}

.add-to-cart-btn:disabled {
  background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%);
  cursor: not-allowed;
  box-shadow: none;
}
</style>
