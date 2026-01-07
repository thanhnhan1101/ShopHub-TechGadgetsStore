<template>
  <div class="cart-container">
    <div class="cart-header">
      <h1>🛒 Giỏ Hàng Của Bạn</h1>
      <p class="cart-subtitle">{{ cartStore.cartCount }} sản phẩm đang chờ thanh toán</p>
    </div>

    <div v-if="cartStore.items.length === 0" class="empty-cart">
      <div class="empty-icon">🛍️</div>
      <h2>Giỏ hàng trống</h2>
      <p>Bạn chưa có sản phẩm nào trong giỏ hàng</p>
      <button class="btn btn-primary" @click="goToShop">
        <span>Khám Phá Ngay</span>
        <span class="arrow">→</span>
      </button>
    </div>

    <div v-else class="cart-content">
      <div class="cart-items">
        <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
          <div class="item-image">
            <img v-if="getProductImage(item.product)" :src="getProductImage(item.product)" :alt="item.product?.name" />
            <div v-else class="placeholder">📱</div>
          </div>

          <div class="item-info">
            <h3 class="item-name">{{ item.product?.name }}</h3>
            <p class="item-price">
              <span class="price-label">Giá:</span>
              <span class="price-value">{{ formatPrice(item.product?.price) }}</span>
            </p>
          </div>

          <div class="item-actions">
            <div class="quantity-control">
              <button @click="decreaseQty(item)" class="btn-qty" :disabled="item.quantity <= 1">
                <span>−</span>
              </button>
              <span class="quantity">{{ item.quantity }}</span>
              <button @click="increaseQty(item)" class="btn-qty">
                <span>+</span>
              </button>
            </div>

            <div class="item-subtotal">
              <span class="subtotal-label">Tạm tính</span>
              <span class="subtotal-value">{{ formatPrice((item.product?.price * item.quantity)) }}</span>
            </div>
          </div>

          <button @click="removeItem(item.id)" class="btn-remove" title="Xóa sản phẩm">
            <span>🗑️</span>
          </button>
        </div>
      </div>

      <div class="cart-summary">
        <div class="summary-header">
          <h2>Thông Tin Đơn Hàng</h2>
        </div>
        
        <div class="summary-content">
          <div class="summary-row">
            <span>Tạm tính ({{ cartStore.cartCount }} sản phẩm)</span>
            <span>{{ formatPrice(cartStore.cartTotal) }}</span>
          </div>
          <div class="summary-row">
            <span>Phí vận chuyển</span>
            <span class="free">MIỄN PHÍ</span>
          </div>
          <div class="summary-divider"></div>
          <div class="summary-row total">
            <span>Tổng cộng</span>
            <span class="total-amount">{{ formatPrice(cartStore.cartTotal) }}</span>
          </div>
        </div>

        <div class="summary-actions">
          <button class="btn btn-checkout" @click="goToCheckout">
            <span>Thanh Toán Ngay</span>
            <span class="icon">💳</span>
          </button>
          <button class="btn btn-continue" @click="goToShop">
            <span>← Tiếp Tục Mua Sắm</span>
          </button>
        </div>

        <div class="trust-badges">
          <div class="badge">
            <span>🔒</span>
            <span>Thanh toán an toàn</span>
          </div>
          <div class="badge">
            <span>🚚</span>
            <span>Giao hàng miễn phí</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'

export default {
  name: 'Cart',
  setup() {
    const router = useRouter()
    const cartStore = useCartStore()

    const increaseQty = async (item) => {
      await cartStore.updateQuantity(item.id, item.quantity + 1)
    }

    const decreaseQty = async (item) => {
      if (item.quantity > 1) {
        await cartStore.updateQuantity(item.id, item.quantity - 1)
      }
    }

    const removeItem = async (itemId) => {
      if (confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?')) {
        await cartStore.removeItem(itemId)
      }
    }

    const goToCheckout = () => {
      router.push('/checkout')
    }

    const goToShop = () => {
      router.push('/')
    }

    const formatPrice = (price) => {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0
      }).format(price)
    }

    const getProductImage = (product) => {
      if (!product) return null
      // Ưu tiên lấy từ images array
      if (product.images && product.images.length > 0) {
        return product.images[0].imageUrl
      }
      // Fallback sang imageUrl cũ
      return product.imageUrl || null
    }

    onMounted(() => {
      cartStore.fetchCart()
    })

    return {
      cartStore,
      increaseQty,
      decreaseQty,
      removeItem,
      goToCheckout,
      goToShop,
      formatPrice,
      getProductImage
    }
  } // Đóng setup()
} // Đóng export default
</script>

<style scoped>
.cart-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
}

.cart-header {
  margin-bottom: 40px;
  text-align: center;
}

.cart-header h1 {
  font-size: 42px;
  font-weight: 800;
  color: #2c3e50;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C06C84 50%, #6C5B7B 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.cart-subtitle {
  font-size: 18px;
  color: #7f8c8d;
  font-weight: 500;
}

/* Empty Cart */
.empty-cart {
  text-align: center;
  padding: 80px 40px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.08);
  max-width: 600px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 100px;
  margin-bottom: 30px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.empty-cart h2 {
  font-size: 32px;
  color: #2c3e50;
  margin-bottom: 15px;
}

.empty-cart p {
  font-size: 18px;
  color: #7f8c8d;
  margin-bottom: 40px;
}

.empty-cart .btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 15px 40px;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #FF6B9D 0%, #C06C84 100%);
  border: none;
  border-radius: 50px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.4);
}

.empty-cart .btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.6);
  background: linear-gradient(135deg, #FF8AB4 0%, #D07B95 100%);
}

.empty-cart .arrow {
  transition: transform 0.3s ease;
}

.empty-cart .btn:hover .arrow {
  transform: translateX(5px);
}

/* Cart Content */
.cart-content {
  display: grid;
  grid-template-columns: 1fr 450px;
  gap: 40px;
  align-items: start;
}

/* Cart Items */
.cart-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cart-item {
  background: white;
  border-radius: 20px;
  padding: 30px;
  display: grid;
  grid-template-columns: 120px 1fr auto 50px;
  gap: 25px;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.cart-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FF6B9D 0%, #C06C84 50%, #6C5B7B 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.cart-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.12);
}

.cart-item:hover::before {
  transform: scaleX(1);
}

.item-image {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #FFE5EC 0%, #FFC2D4 100%);
  border-radius: 15px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.2);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.cart-item:hover .item-image img {
  transform: scale(1.1);
}

.placeholder {
  font-size: 50px;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.item-name {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  line-height: 1.4;
}

.item-price {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.price-label {
  font-size: 14px;
  color: #95a5a6;
  font-weight: 500;
}

.price-value {
  font-size: 18px;
  color: #27ae60;
  font-weight: 700;
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: flex-end;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 15px;
  background: #f8f9fa;
  border-radius: 50px;
  padding: 8px 15px;
  box-shadow: inset 0 2px 5px rgba(0,0,0,0.05);
}

.btn-qty {
  width: 36px;
  height: 36px;
  border: none;
  background: linear-gradient(135deg, #FF6B9D 0%, #C06C84 100%);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 10px rgba(255, 107, 157, 0.3);
}

.btn-qty:hover:not(:disabled) {
  transform: scale(1.1);
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.5);
  background: linear-gradient(135deg, #FF8AB4 0%, #D07B95 100%);
}

.btn-qty:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  box-shadow: none;
}

.quantity {
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
  min-width: 30px;
  text-align: center;
}

.item-subtotal {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
}

.subtotal-label {
  font-size: 13px;
  color: #95a5a6;
  font-weight: 500;
}

.subtotal-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
}

.btn-remove {
  width: 45px;
  height: 45px;
  border: none;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  font-size: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.btn-remove:hover {
  transform: scale(1.1) rotate(90deg);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.5);
}

/* Cart Summary */
.cart-summary {
  background: white;
  border-radius: 20px;
  padding: 35px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  position: sticky;
  top: 100px;
}

.summary-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 25px 0;
  padding-bottom: 20px;
  border-bottom: 3px solid #f1f3f5;
}

.summary-content {
  margin-bottom: 30px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  font-size: 16px;
  color: #7f8c8d;
}

.summary-row span:first-child {
  font-weight: 500;
}

.summary-row span:last-child {
  font-weight: 600;
  color: #2c3e50;
}

.free {
  color: #27ae60 !important;
  font-weight: 700 !important;
  font-size: 14px;
}

.summary-divider {
  height: 2px;
  background: linear-gradient(90deg, transparent, #dee2e6, transparent);
  margin: 25px 0;
}

.summary-row.total {
  font-size: 20px;
  padding: 20px 0;
  margin: 0;
  border-top: 3px solid #f1f3f5;
}

.summary-row.total span:first-child {
  font-weight: 700;
  color: #2c3e50;
}

.total-amount {
  font-size: 32px !important;
  font-weight: 800 !important;
  background: linear-gradient(135deg, #FF6B9D 0%, #C06C84 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.summary-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 25px;
}

.btn-checkout {
  width: 100%;
  padding: 18px 30px;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #FF6B9D 0%, #C06C84 100%);
  color: white;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

.btn-checkout:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(255, 107, 157, 0.6);
  background: linear-gradient(135deg, #FF8AB4 0%, #D07B95 100%);
}

.btn-checkout .icon {
  font-size: 22px;
  transition: transform 0.3s ease;
}

.btn-checkout:hover .icon {
  transform: scale(1.2);
}

.btn-continue {
  width: 100%;
  padding: 15px 30px;
  font-size: 16px;
  font-weight: 600;
  background: transparent;
  color: #FF6B9D;
  border: 2px solid #FF6B9D;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-continue:hover {
  background: linear-gradient(135deg, #FF6B9D 0%, #C06C84 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
  border-color: transparent;
}

.trust-badges {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  padding-top: 25px;
  border-top: 2px solid #f1f3f5;
}

.badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px;
  background: linear-gradient(135deg, #FFE5EC 0%, #FFC2D4 30%);
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #8B5A6F;
}

.badge span:first-child {
  font-size: 24px;
}

/* Responsive */
@media (max-width: 1024px) {
  .cart-content {
    grid-template-columns: 1fr;
  }

  .cart-summary {
    position: static;
  }
}

@media (max-width: 768px) {
  .cart-container {
    padding: 20px 15px;
  }

  .cart-header h1 {
    font-size: 32px;
  }

  .cart-item {
    grid-template-columns: 90px 1fr;
    gap: 15px;
    padding: 20px;
  }

  .item-image {
    width: 90px;
    height: 90px;
  }

  .item-info {
    grid-column: 2;
  }

  .item-actions {
    grid-column: 1 / -1;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }

  .btn-remove {
    grid-column: 1 / -1;
    width: 100%;
    border-radius: 12px;
    height: 45px;
  }

  .cart-summary {
    padding: 25px;
  }

  .trust-badges {
    grid-template-columns: 1fr;
  }
}
</style>
