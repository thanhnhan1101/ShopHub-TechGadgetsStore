<template>
  <div class="product-detail">
    <div v-if="loading" class="loading">Loading...</div>
    
    <div v-else-if="product" class="detail-container">
      <div class="product-image-large">
        <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" />
        <div v-else class="placeholder-large">📱</div>
      </div>

      <div class="product-details">
        <h1>{{ product.name }}</h1>
        <p class="category">{{ product.category?.name }}</p>
        <p class="price">${{ product.price }}</p>
        
        <div class="stock-info" :class="stockClass">
          {{ stockText }}
        </div>

        <p class="description">{{ product.description }}</p>

        <div class="quantity-selector">
          <label>Quantity:</label>
          <div class="quantity-controls">
            <button @click="decreaseQuantity" class="btn-qty">-</button>
            <input v-model.number="quantity" type="number" min="1" :max="product.stock" />
            <button @click="increaseQuantity" class="btn-qty">+</button>
          </div>
        </div>

        <div class="action-buttons">
          <button 
            class="btn btn-primary btn-large"
            @click="addToCart"
            :disabled="product.stock === 0"
          >
            Add to Cart
          </button>
          <button class="btn btn-secondary btn-large" @click="goBack">
            Back to Shop
          </button>
        </div>
      </div>
    </div>

    <div v-else class="not-found">
      Product not found
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import api from '../services/api'

export default {
  name: 'ProductDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const cartStore = useCartStore()
    
    const product = ref(null)
    const loading = ref(false)
    const quantity = ref(1)

    const stockClass = computed(() => {
      if (!product.value) return ''
      if (product.value.stock === 0) return 'out-of-stock'
      if (product.value.stock < 10) return 'low-stock'
      return 'in-stock'
    })

    const stockText = computed(() => {
      if (!product.value) return ''
      if (product.value.stock === 0) return 'Out of Stock'
      if (product.value.stock < 10) return `Only ${product.value.stock} left in stock`
      return `${product.value.stock} in stock`
    })

    const fetchProduct = async () => {
      loading.value = true
      try {
        const response = await api.getProductById(route.params.id)
        product.value = response.data
      } catch (error) {
        console.error('Error fetching product:', error)
      } finally {
        loading.value = false
      }
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
        alert(`Added ${quantity.value} item(s) to cart!`)
        quantity.value = 1
      } catch (error) {
        alert('Failed to add to cart')
      }
    }

    const goBack = () => {
      router.push('/')
    }

    onMounted(() => {
      fetchProduct()
    })

    return {
      product,
      loading,
      quantity,
      stockClass,
      stockText,
      increaseQuantity,
      decreaseQuantity,
      addToCart,
      goBack
    }
  }
}
</script>

<style scoped>
.detail-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-top: 20px;
}

.product-image-large {
  width: 100%;
  height: 500px;
  background: #f8f9fa;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.product-image-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-large {
  font-size: 120px;
}

.product-details h1 {
  font-size: 32px;
  margin-bottom: 10px;
}

.category {
  color: #667eea;
  font-weight: 500;
  margin-bottom: 15px;
}

.price {
  font-size: 36px;
  font-weight: bold;
  color: #27ae60;
  margin-bottom: 15px;
}

.stock-info {
  padding: 8px 12px;
  border-radius: 5px;
  display: inline-block;
  margin-bottom: 20px;
  font-weight: 500;
}

.description {
  line-height: 1.6;
  color: #555;
  margin-bottom: 30px;
}

.quantity-selector {
  margin-bottom: 20px;
}

.quantity-selector label {
  display: block;
  margin-bottom: 10px;
  font-weight: 500;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-qty {
  width: 40px;
  height: 40px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 5px;
  cursor: pointer;
  font-size: 20px;
  font-weight: bold;
}

.btn-qty:hover {
  background: #f8f9fa;
}

.quantity-controls input {
  width: 80px;
  text-align: center;
  font-size: 16px;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.btn-large {
  flex: 1;
  padding: 15px 30px;
  font-size: 16px;
}

.loading, .not-found {
  text-align: center;
  padding: 60px;
  font-size: 20px;
  color: #7f8c8d;
}

@media (max-width: 768px) {
  .detail-container {
    grid-template-columns: 1fr;
  }
  
  .product-image-large {
    height: 350px;
  }
}
</style>
