<template>
  <div class="product-card" @click="goToDetail">
    <div class="product-image">
      <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" />
      <div v-else class="placeholder-image">📱</div>
    </div>
    
    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-description">{{ truncatedDescription }}</p>
      <div class="product-footer">
        <span class="product-price">${{ product.price }}</span>
        <span class="product-stock" :class="stockClass">
          {{ stockText }}
        </span>
      </div>
      <button 
        class="btn btn-primary add-to-cart-btn"
        @click.stop="addToCart"
        :disabled="product.stock === 0"
      >
        Add to Cart
      </button>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'

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
      if (props.product.stock === 0) return 'Out of Stock'
      if (props.product.stock < 10) return `Only ${props.product.stock} left`
      return 'In Stock'
    })
    
    const goToDetail = () => {
      router.push(`/product/${props.product.id}`)
    }
    
    const addToCart = async () => {
      try {
        await cartStore.addToCart(props.product, 1)
        alert('Product added to cart!')
      } catch (error) {
        alert('Failed to add to cart')
      }
    }
    
    return {
      truncatedDescription,
      stockClass,
      stockText,
      goToDetail,
      addToCart
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
  height: 240px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

.product-image::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.05) 100%);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.08);
}

.placeholder-image {
  font-size: 72px;
  opacity: 0.6;
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
