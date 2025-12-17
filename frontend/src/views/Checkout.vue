<template>
  <div class="checkout">
    <h1>Checkout</h1>

    <div class="checkout-container">
      <div class="checkout-form">
        <h2>Shipping Information</h2>
        <form @submit.prevent="placeOrder">
          <div class="form-group">
            <label>Full Name *</label>
            <input v-model="form.fullName" type="text" required />
          </div>

          <div class="form-group">
            <label>Phone *</label>
            <input v-model="form.phone" type="tel" required />
          </div>

          <div class="form-group">
            <label>Address *</label>
            <textarea v-model="form.address" rows="3" required></textarea>
          </div>

          <div class="form-group">
            <label>Payment Method *</label>
            <select v-model="form.paymentMethod" required>
              <option value="">Select payment method</option>
              <option value="COD">Cash on Delivery (COD)</option>
              <option value="Credit Card">Credit Card</option>
              <option value="E-wallet">E-wallet</option>
            </select>
          </div>

          <div class="form-group">
            <label>Order Notes</label>
            <textarea v-model="form.notes" rows="2" placeholder="Optional notes"></textarea>
          </div>

          <button type="submit" class="btn btn-success btn-block" :disabled="submitting">
            {{ submitting ? 'Processing...' : 'Place Order' }}
          </button>
        </form>
      </div>

      <div class="order-summary">
        <h2>Order Summary</h2>
        <div v-for="item in cartStore.items" :key="item.id" class="summary-item">
          <span>{{ item.product?.name }} × {{ item.quantity }}</span>
          <span>${{ (item.product?.price * item.quantity).toFixed(2) }}</span>
        </div>
        <div class="summary-total">
          <span>Total:</span>
          <span>${{ cartStore.cartTotal.toFixed(2) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import api from '../services/api'

export default {
  name: 'Checkout',
  setup() {
    const router = useRouter()
    const cartStore = useCartStore()
    const submitting = ref(false)

    const form = ref({
      fullName: '',
      phone: '',
      address: '',
      paymentMethod: '',
      notes: ''
    })

    const placeOrder = async () => {
      if (cartStore.items.length === 0) {
        alert('Your cart is empty')
        return
      }

      submitting.value = true
      try {
        const order = {
          user: { id: cartStore.userId },
          fullName: form.value.fullName,
          phone: form.value.phone,
          address: form.value.address,
          paymentMethod: form.value.paymentMethod,
          notes: form.value.notes,
          totalAmount: cartStore.cartTotal,
          orderItems: cartStore.items.map(item => ({
            product: { id: item.product.id },
            productName: item.product.name,
            price: item.product.price,
            quantity: item.quantity
          }))
        }

        await api.createOrder(order)
        await cartStore.clearCart()
        
        alert('Order placed successfully!')
        router.push('/orders')
      } catch (error) {
        console.error('Error placing order:', error)
        alert('Failed to place order. Please try again.')
      } finally {
        submitting.value = false
      }
    }

    onMounted(() => {
      if (cartStore.items.length === 0) {
        cartStore.fetchCart()
      }
    })

    return {
      form,
      cartStore,
      submitting,
      placeOrder
    }
  }
}
</script>

<style scoped>
.checkout h1 {
  margin-bottom: 30px;
}

.checkout-container {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.checkout-form, .order-summary {
  background: white;
  border-radius: 10px;
  padding: 25px;
}

.checkout-form h2, .order-summary h2 {
  font-size: 22px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

.btn-block {
  width: 100%;
  padding: 15px;
  font-size: 16px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  padding: 20px 0;
  font-size: 24px;
  font-weight: bold;
  border-top: 2px solid #ddd;
  margin-top: 10px;
}

@media (max-width: 768px) {
  .checkout-container {
    grid-template-columns: 1fr;
  }
}
</style>
