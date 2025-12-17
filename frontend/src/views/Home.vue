<template>
  <div class="home">
    <section class="hero">
      <h1>Welcome to ShopHub</h1>
      <p>Your one-stop shop for the latest tech gadgets</p>
    </section>

    <section class="categories">
      <h2>Shop by Category</h2>
      <div class="category-list">
        <button 
          v-for="category in categories" 
          :key="category.id"
          class="category-btn"
          :class="{ active: selectedCategory === category.id }"
          @click="filterByCategory(category.id)"
        >
          {{ category.name }}
        </button>
        <button 
          class="category-btn"
          :class="{ active: selectedCategory === null }"
          @click="filterByCategory(null)"
        >
          All Products
        </button>
      </div>
    </section>

    <section class="products">
      <h2>{{ productsTitle }}</h2>
      <div v-if="loading" class="loading">Loading products...</div>
      <div v-else-if="products.length === 0" class="no-products">
        No products found
      </div>
      <div v-else class="product-grid">
        <ProductCard 
          v-for="product in products" 
          :key="product.id"
          :product="product"
        />
      </div>
    </section>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '../services/api'
import ProductCard from '../components/ProductCard.vue'

export default {
  name: 'Home',
  components: {
    ProductCard
  },
  setup() {
    const categories = ref([])
    const products = ref([])
    const selectedCategory = ref(null)
    const loading = ref(false)

    const productsTitle = computed(() => {
      if (selectedCategory.value) {
        const category = categories.value.find(c => c.id === selectedCategory.value)
        return category ? category.name : 'Products'
      }
      return 'All Products'
    })

    const fetchCategories = async () => {
      try {
        const response = await api.getCategories()
        categories.value = response.data
      } catch (error) {
        console.error('Error fetching categories:', error)
      }
    }

    const fetchProducts = async (categoryId = null) => {
      loading.value = true
      try {
        const response = await api.getProducts(categoryId)
        products.value = response.data
      } catch (error) {
        console.error('Error fetching products:', error)
      } finally {
        loading.value = false
      }
    }

    const filterByCategory = (categoryId) => {
      selectedCategory.value = categoryId
      fetchProducts(categoryId)
    }

    onMounted(() => {
      fetchCategories()
      fetchProducts()
    })

    return {
      categories,
      products,
      selectedCategory,
      loading,
      productsTitle,
      filterByCategory
    }
  }
}
</script>

<style scoped>
.home {
  min-height: calc(100vh - 200px);
}

.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 30px;
  text-align: center;
  border-radius: 20px;
  margin-bottom: 60px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.hero::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.hero h1 {
  font-size: 56px;
  margin-bottom: 16px;
  font-weight: 800;
  letter-spacing: -1px;
  position: relative;
  z-index: 1;
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.hero p {
  font-size: 22px;
  opacity: 0.95;
  position: relative;
  z-index: 1;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.categories {
  margin-bottom: 50px;
}

.categories h2 {
  font-size: 32px;
  margin-bottom: 24px;
  color: #2c3e50;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.category-list {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.category-btn {
  padding: 12px 28px;
  border: 2px solid transparent;
  background: white;
  border-radius: 30px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  color: #495057;
  letter-spacing: 0.3px;
}

.category-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.25);
  border-color: #667eea;
  color: #667eea;
}

.category-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  transform: translateY(-3px);
}

.products h2 {
  font-size: 32px;
  margin-bottom: 30px;
  color: #2c3e50;
  font-weight: 800;
  letter-spacing: -0.5px;
  text-align: center;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.loading, .no-products {
  text-align: center;
  padding: 80px 30px;
  color: #7f8c8d;
  font-size: 20px;
  font-weight: 500;
}

.loading {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
