<template>
  <div class="home">
    <div class="page-container">
      <!-- Sidebar Categories -->
      <aside class="sidebar-categories">
        <div 
          class="sidebar-item"
          :class="{ active: selectedCategory === null }"
          @click="filterByCategory(null)"
        >
          <span class="sidebar-icon">🌟</span>
          <span class="sidebar-text">Tất cả sản phẩm</span>
          <span class="sidebar-arrow">›</span>
        </div>
        <div 
          v-for="category in categories" 
          :key="category.id"
          class="sidebar-item"
          :class="{ active: selectedCategory === category.id }"
          @click="filterByCategory(category.id)"
        >
          <span class="sidebar-icon">{{ getCategoryIcon(category.name) }}</span>
          <span class="sidebar-text">{{ category.name }}</span>
          <span class="sidebar-arrow">›</span>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="main-content">
        <!-- Top Banners Section -->
        <section class="top-banners">
          <div class="banner-main">
            <div class="banner-main-content">
              <div class="banner-badge">🔥 HOT DEAL</div>
              <h1 class="banner-title">Săn Sale Công Nghệ</h1>
              <p class="banner-subtitle">Giảm giá đến 50% - Miễn phí vận chuyển</p>
              <button @click="scrollToProducts" class="banner-btn">
                Mua ngay
                <span class="btn-arrow">→</span>
              </button>
            </div>
            <div class="banner-decoration">
              <div class="deco-circle circle-1"></div>
              <div class="deco-circle circle-2"></div>
              <div class="floating-product">
                <span class="product-icon">📱</span>
              </div>
            </div>
          </div>

          <div class="banner-side">
            <div class="side-banner banner-promo">
              <div class="promo-badge">🎁</div>
              <h3>Ưu đãi Smember</h3>
              <p>Tích điểm đổi quà</p>
            </div>
            <div class="side-banner banner-service">
              <div class="promo-badge">🚚</div>
              <h3>Giao hàng nhanh</h3>
              <p>Freeship toàn quốc</p>
            </div>
          </div>
        </section>

        <!-- Promotion Cards -->
        <section class="promo-section">
          <div class="promo-grid">
            <div class="promo-card card-1">
              <div class="promo-content">
                <div class="promo-tag">iPhone 17 PRO MAX</div>
                <h3>Bạc đã về - Ế hễ ưu đãi</h3>
                <button class="promo-btn">XEM NGAY</button>
              </div>
            </div>
            
            <div class="promo-card card-2">
              <div class="promo-content">
                <div class="promo-tag">OPPO RENO15</div>
                <h3>Concert Mùa Sao Băng</h3>
                <button class="promo-btn">XEM NGAY</button>
              </div>
            </div>
            
            <div class="promo-card card-3">
              <div class="promo-content">
                <div class="promo-tag">SAMSUNG GALAXY</div>
                <h3>Định cao gặp mỡ</h3>
                <button class="promo-btn">XEM NGAY</button>
              </div>
            </div>
            
            <div class="promo-card card-4">
              <div class="promo-content">
                <div class="promo-tag">KHUYẾN MÃI</div>
                <h3>Mua máy chiếu, tặng loạt</h3>
                <button class="promo-btn">XEM NGAY</button>
              </div>
            </div>
          </div>
        </section>

        <!-- Deal Banners -->
        <section class="deal-section">
          <div class="deal-grid">
            <div class="deal-banner deal-galaxy">
              <div class="deal-info">
                <h3>SAMSUNG</h3>
                <h2>Galaxy A17 5G</h2>
                <div class="deal-price">
                  <span class="price-label">Ưu đãi từ</span>
                  <span class="price-value">5.89 Triệu</span>
                </div>
                <ul class="deal-features">
                  <li>Ưu đãi góp hàm 24 tháng</li>
                  <li>Trợ giá lên đời 500k+</li>
                </ul>
                <button class="deal-btn">MUA NGAY</button>
              </div>
            </div>

            <div class="deal-banner deal-watch">
              <div class="deal-info">
                <h3>APPLE</h3>
                <h2>Watch SE3 | SE2</h2>
                <div class="deal-price">
                  <span class="price-value">4.99 Triệu</span>
                </div>
                <button class="deal-btn">Mua ngay</button>
              </div>
            </div>

            <div class="deal-banner deal-laptop">
              <div class="deal-info">
                <h3>MUA LAPTOP ONLINE</h3>
                <h2>Giảm 6 đến 7 Triệu</h2>
                <div class="deal-highlight">
                  <span class="highlight-tag">+ FREE SHIP</span>
                </div>
                <p class="deal-sub">Trợ giá lên đời 4 Triệu</p>
                <button class="deal-btn">XEM NGAY</button>
              </div>
            </div>
          </div>
        </section>

        <!-- Products Section -->
        <section ref="productsSection" class="products-section">
          <div class="section-header">
            <h2 class="section-title">
              <span class="title-icon">📱</span>
              {{ productsTitle }}
            </h2>
            <div class="section-actions">
              <span class="product-count">{{ products.length }} sản phẩm</span>
            </div>
          </div>

          <div v-if="loading" class="loading-state">
            <div class="loader">
              <div class="loader-spinner"></div>
              <p>Đang tải sản phẩm...</p>
            </div>
          </div>

          <div v-else-if="products.length === 0" class="empty-state">
            <div class="empty-icon">📦</div>
            <h3>Không tìm thấy sản phẩm</h3>
            <p>Vui lòng chọn danh mục khác</p>
            <button @click="filterByCategory(null)" class="btn-back">
              ← Quay lại trang chủ
            </button>
          </div>

          <div v-else class="products-grid">
            <ProductCard 
              v-for="product in products" 
              :key="product.id"
              :product="product"
            />
          </div>
        </section>

        <!-- Features Bar -->
        <section class="features-bar">
          <div class="feature-box">
            <div class="feature-icon">🚚</div>
            <div class="feature-info">
              <h4>Giao hàng siêu tốc</h4>
              <p>Miễn phí ship toàn quốc</p>
            </div>
          </div>
          <div class="feature-box">
            <div class="feature-icon">✅</div>
            <div class="feature-info">
              <h4>Cam kết chính hãng</h4>
              <p>Bảo hành 12 tháng</p>
            </div>
          </div>
          <div class="feature-box">
            <div class="feature-icon">💰</div>
            <div class="feature-info">
              <h4>Giá tốt nhất</h4>
              <p>Hoàn tiền 200% nếu rẻ hơn</p>
            </div>
          </div>
          <div class="feature-box">
            <div class="feature-icon">🎯</div>
            <div class="feature-info">
              <h4>Hỗ trợ 24/7</h4>
              <p>Tư vấn nhiệt tình</p>
            </div>
          </div>
        </section>
      </main>
    </div>
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
    const allProducts = ref([])
    const selectedCategory = ref(null)
    const loading = ref(false)
    const productsSection = ref(null)

    const productsTitle = computed(() => {
      if (selectedCategory.value) {
        const category = categories.value.find(c => c.id === selectedCategory.value)
        return category ? category.name : 'Sản phẩm'
      }
      return 'Sản Phẩm Nổi Bật'
    })

    const getCategoryIcon = (categoryName) => {
      const iconMap = {
        'Smartphones': '📱',
        'Laptops': '💻',
        'Headphones': '🎧',
        'Smartwatches': '⌚',
        'Tablets': '📱',
        'Cameras': '📷',
        'Gaming': '🎮',
        'Accessories': '🔌',
        'Audio': '🔊',
        'Wearables': '⌚',
        'Computers': '🖥️',
        'Storage': '💾',
        'Networking': '📡'
      }
      return iconMap[categoryName] || '📦'
    }

    const getCategoryCount = (categoryId) => {
      return allProducts.value.filter(p => p.category_id === categoryId).length
    }

    const scrollToProducts = () => {
      productsSection.value?.scrollIntoView({ behavior: 'smooth' })
    }

    const fetchCategories = async () => {
      try {
        const response = await api.getActiveCategories()
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
        if (!categoryId) {
          allProducts.value = response.data
        }
      } catch (error) {
        console.error('Error fetching products:', error)
      } finally {
        loading.value = false
      }
    }

    const filterByCategory = (categoryId) => {
      selectedCategory.value = categoryId
      fetchProducts(categoryId)
      scrollToProducts()
    }

    onMounted(() => {
      fetchCategories()
      fetchProducts()
    })

    return {
      categories,
      products,
      allProducts,
      selectedCategory,
      loading,
      productsTitle,
      productsSection,
      filterByCategory,
      getCategoryIcon,
      getCategoryCount,
      scrollToProducts
    }
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.home {
  background: #f4f4f4;
  min-height: 100vh;
}

.page-container {
  max-width: 1280px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 16px;
  padding: 16px;
}

/* Sidebar Categories - Giống CellphoneS */
.sidebar-categories {
  background: white;
  border-radius: 8px;
  padding: 8px 0;
  height: fit-content;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 80px;
}

.sidebar-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.sidebar-item:hover {
  background: #f8f9fa;
  border-left-color: #d70018;
}

.sidebar-item.active {
  background: #fff0f0;
  border-left-color: #d70018;
}

.sidebar-item.active .sidebar-text {
  color: #d70018;
  font-weight: 600;
}

.sidebar-icon {
  font-size: 20px;
  width: 24px;
  text-align: center;
}

.sidebar-text {
  flex: 1;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.sidebar-arrow {
  color: #999;
  font-size: 18px;
  transition: transform 0.2s ease;
}

.sidebar-item:hover .sidebar-arrow {
  transform: translateX(4px);
  color: #d70018;
}

/* Main Content */
.main-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* Top Banners - Giống CellphoneS */
.top-banners {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

.banner-main {
  background: linear-gradient(135deg, #d70018 0%, #ff4757 100%);
  border-radius: 12px;
  padding: 40px;
  position: relative;
  overflow: hidden;
  min-height: 280px;
  display: flex;
  align-items: center;
}

.banner-main::before {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  top: -100px;
  right: -100px;
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.7; }
}

.banner-main-content {
  position: relative;
  z-index: 2;
  color: white;
}

.banner-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.25);
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 16px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.banner-title {
  font-size: 42px;
  font-weight: 900;
  margin-bottom: 12px;
  line-height: 1.2;
}

.banner-subtitle {
  font-size: 16px;
  margin-bottom: 24px;
  opacity: 0.95;
}

.banner-btn {
  background: white;
  color: #d70018;
  padding: 12px 28px;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.banner-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.btn-arrow {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.banner-btn:hover .btn-arrow {
  transform: translateX(4px);
}

.banner-decoration {
  position: absolute;
  right: 40px;
  top: 50%;
  transform: translateY(-50%);
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
}

.circle-1 {
  width: 200px;
  height: 200px;
  animation: float 4s ease-in-out infinite;
}

.circle-2 {
  width: 120px;
  height: 120px;
  top: 80px;
  left: 60px;
  animation: float 5s ease-in-out infinite;
}

.floating-product {
  position: relative;
  z-index: 3;
  animation: float 3s ease-in-out infinite;
}

.product-icon {
  font-size: 120px;
  filter: drop-shadow(0 10px 30px rgba(0, 0, 0, 0.3));
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.banner-side {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.side-banner {
  background: white;
  border-radius: 12px;
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.side-banner:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.promo-badge {
  font-size: 32px;
  margin-bottom: 12px;
}

.side-banner h3 {
  font-size: 16px;
  color: #333;
  font-weight: 700;
  margin-bottom: 4px;
}

.side-banner p {
  font-size: 13px;
  color: #666;
}

/* Promotion Cards */
.promo-section {
  margin: 8px 0;
}

.promo-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.promo-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  height: 200px;
  position: relative;
}

.promo-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.9;
  transition: opacity 0.3s ease;
}

.promo-card.card-1::before { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.promo-card.card-2::before { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.promo-card.card-3::before { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.promo-card.card-4::before { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.promo-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.promo-card:hover::before {
  opacity: 1;
}

.promo-content {
  position: relative;
  z-index: 2;
  padding: 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: white;
}

.promo-tag {
  display: inline-block;
  background: rgba(255, 255, 255, 0.25);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  width: fit-content;
  margin-bottom: 8px;
}

.promo-content h3 {
  font-size: 15px;
  font-weight: 700;
  line-height: 1.3;
  margin-bottom: 16px;
}

.promo-btn {
  background: white;
  color: #333;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
  width: fit-content;
  transition: all 0.3s ease;
}

.promo-btn:hover {
  transform: scale(1.05);
}

/* Deal Banners */
.deal-section {
  margin: 8px 0;
}

.deal-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.deal-banner {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.deal-banner::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(215, 0, 24, 0.1) 0%, rgba(255, 71, 87, 0.05) 100%);
  transform: translate(50%, -50%);
}

.deal-banner:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.deal-info {
  position: relative;
  z-index: 2;
}

.deal-info h3 {
  font-size: 13px;
  color: #d70018;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.deal-info h2 {
  font-size: 22px;
  color: #333;
  font-weight: 900;
  margin-bottom: 16px;
  line-height: 1.3;
}

.deal-price {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 16px;
}

.price-label {
  font-size: 12px;
  color: #666;
}

.price-value {
  font-size: 28px;
  color: #d70018;
  font-weight: 900;
}

.deal-features {
  list-style: none;
  margin-bottom: 20px;
}

.deal-features li {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
  padding-left: 16px;
  position: relative;
}

.deal-features li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #d70018;
  font-weight: 700;
}

.deal-btn {
  background: #d70018;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  text-transform: uppercase;
}

.deal-btn:hover {
  background: #b8001a;
  transform: scale(1.05);
}

.deal-highlight {
  margin-bottom: 12px;
}

.highlight-tag {
  display: inline-block;
  background: linear-gradient(135deg, #ffd43b 0%, #fab005 100%);
  color: #333;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 800;
}

.deal-sub {
  font-size: 13px;
  color: #666;
  margin-bottom: 16px;
}

/* Products Section */
.products-section {
  margin-top: 24px;
}

.section-header {
  background: white;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 24px;
  color: #333;
  font-weight: 900;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 28px;
}

.section-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.product-count {
  font-size: 14px;
  color: #666;
  font-weight: 600;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

/* Loading & Empty States */
.loading-state,
.empty-state {
  background: white;
  border-radius: 12px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.loader {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.loader-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f4f6;
  border-top-color: #d70018;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loader p {
  font-size: 16px;
  color: #666;
  font-weight: 600;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h3 {
  font-size: 24px;
  color: #333;
  font-weight: 700;
  margin-bottom: 12px;
}

.empty-state p {
  font-size: 15px;
  color: #666;
  margin-bottom: 24px;
}

.btn-back {
  background: #d70018;
  color: white;
  border: none;
  padding: 12px 28px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: #b8001a;
  transform: translateY(-2px);
}

/* Features Bar */
.features-bar {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-top: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.feature-box {
  display: flex;
  align-items: center;
  gap: 16px;
}

.feature-icon {
  font-size: 36px;
  flex-shrink: 0;
}

.feature-info h4 {
  font-size: 14px;
  color: #333;
  font-weight: 700;
  margin-bottom: 4px;
}

.feature-info p {
  font-size: 12px;
  color: #666;
}

/* Responsive */
@media (max-width: 1024px) {
  .page-container {
    grid-template-columns: 1fr;
  }

  .sidebar-categories {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 8px;
    padding: 16px;
    position: static;
  }

  .sidebar-item {
    flex-direction: column;
    text-align: center;
    padding: 16px 8px;
    border-left: none;
    border-top: 3px solid transparent;
  }

  .sidebar-item:hover,
  .sidebar-item.active {
    border-left: none;
    border-top-color: #d70018;
  }

  .sidebar-arrow {
    display: none;
  }

  .top-banners {
    grid-template-columns: 1fr;
  }

  .promo-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .deal-grid {
    grid-template-columns: 1fr;
  }

  .features-bar {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-container {
    padding: 12px;
  }

  .banner-main {
    padding: 32px 24px;
    min-height: 220px;
  }

  .banner-title {
    font-size: 28px;
  }

  .banner-decoration {
    display: none;
  }

  .promo-grid {
    grid-template-columns: 1fr;
  }

  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .features-bar {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
