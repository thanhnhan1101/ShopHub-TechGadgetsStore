<template>
  <div class="cart">
    <h1>Shopping Cart</h1>

    <div v-if="cartStore.items.length === 0" class="empty-cart">
      <p>Your cart is empty</p>
      <button class="btn btn-primary" @click="goToShop">Continue Shopping</button>
    </div>

    <div v-else>
      <div class="cart-items">
        <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
          <div class="item-image">
            <img v-if="item.product?.imageUrl" :src="item.product.imageUrl" :alt="item.product?.name" />
            <div v-else class="placeholder">📱</div>
          </div>

          <div class="item-details">
            <h3>{{ item.product?.name }}</h3>
            <p class="item-price">${{ item.product?.price }}</p>
          </div>

          <div class="item-quantity">
            <button @click="decreaseQty(item)" class="btn-qty">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="increaseQty(item)" class="btn-qty">+</button>
          </div>

          <div class="item-subtotal">
            ${{ (item.product?.price * item.quantity).toFixed(2) }}
          </div>

          <button @click="removeItem(item.id)" class="btn-remove">✕</button>
        </div>
      </div>

      <div class="cart-summary">
        <h2>Order Summary</h2>
        <div class="summary-row">
          <span>Items ({{ cartStore.cartCount }}):</span>
          <span>${{ cartStore.cartTotal.toFixed(2) }}</span>
        </div>
        <div class="summary-row total">
          <span>Total:</span>
          <span>${{ cartStore.cartTotal.toFixed(2) }}</span>
        </div>
        <button class="btn btn-success btn-block" @click="goToCheckout">
          Proceed to Checkout
        </button>
        <button class="btn btn-secondary btn-block" @click="goToShop">
          Continue Shopping
        </button>
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
      if (confirm('Remove this item from cart?')) {
        await cartStore.removeItem(itemId)
      }
    }

    const goToCheckout = () => {
      router.push('/checkout')
    }

    const goToShop = () => {
      router.push('/')
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
      goToShop
    }
  }
}
</script>

<style scoped>
.cart h1 {
  margin-bottom: 30px;
}

.empty-cart {
  text-align: center;
  padding: 60px;
}

.empty-cart p {
  font-size: 20px;
  color: #7f8c8d;
  margin-bottom: 20px;
}

.cart-items {
  margin-bottom: 30px;
}

.cart-item {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 15px;
  display: grid;
  grid-template-columns: 100px 1fr 150px 100px 40px;
  gap: 20px;
  align-items: center;
}

.item-image {
  width: 100px;
  height: 100px;
  background: #f8f9fa;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder {
  font-size: 40px;
}

.item-details h3 {
  font-size: 18px;
  margin-bottom: 5px;
}

.item-price {
  color: #27ae60;
  font-weight: 600;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 15px;
}

.btn-qty {
  width: 35px;
  height: 35px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.item-subtotal {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
}

.btn-remove {
  width: 35px;
  height: 35px;
  border: none;
  background: #e74c3c;
  color: white;
  border-radius: 50%;
  cursor: pointer;
  font-size: 18px;
}

.btn-remove:hover {
  background: #c0392b;
}

.cart-summary {
  background: white;
  border-radius: 10px;
  padding: 25px;
  max-width: 400px;
  margin-left: auto;
}

.cart-summary h2 {
  font-size: 22px;
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 16px;
}

.summary-row.total {
  font-size: 24px;
  font-weight: bold;
  padding-top: 15px;
  border-top: 2px solid #ddd;
  margin-top: 20px;
}

.btn-block {
  width: 100%;
  margin-bottom: 10px;
  padding: 15px;
}

@media (max-width: 768px) {
  .cart-item {
    grid-template-columns: 80px 1fr;
    gap: 15px;
  }
  
  .item-quantity, .item-subtotal, .btn-remove {
    grid-column: 1 / -1;
  }
}
</style>
