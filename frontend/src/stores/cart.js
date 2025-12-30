import { defineStore } from 'pinia'
import api from '../services/api'
import { useAuthStore } from './auth'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: []
  }),

  getters: {
    cartCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    
    cartTotal: (state) => {
      return state.items.reduce((sum, item) => {
        const price = item.product?.price || 0
        return sum + (price * item.quantity)
      }, 0)
    }
  },

  actions: {
    async fetchCart() {
      const authStore = useAuthStore()
      if (!authStore.isAuthenticated || !authStore.user?.id) {
        this.items = []
        return
      }
      
      try {
        const response = await api.getCart(authStore.user.id)
        this.items = response.data
      } catch (error) {
        console.error('Error fetching cart:', error)
        this.items = []
      }
    },

    async addToCart(product, quantity = 1) {
      const authStore = useAuthStore()
      if (!authStore.isAuthenticated || !authStore.user?.id) {
        console.error('User not authenticated')
        return
      }
      
      try {
        const cartItem = {
          user: { id: authStore.user.id },
          product: { id: product.id },
          quantity
        }
        await api.addToCart(cartItem)
        await this.fetchCart()
      } catch (error) {
        console.error('Error adding to cart:', error)
        throw error
      }
    },

    async updateQuantity(itemId, quantity) {
      try {
        await api.updateCartItem(itemId, { quantity })
        await this.fetchCart()
      } catch (error) {
        console.error('Error updating cart:', error)
      }
    },

    async removeItem(itemId) {
      try {
        await api.removeFromCart(itemId)
        await this.fetchCart()
      } catch (error) {
        console.error('Error removing from cart:', error)
      }
    },

    async clearCart() {
      try {
        await api.clearCart(this.userId)
        this.items = []
      } catch (error) {
        console.error('Error clearing cart:', error)
      }
    }
  }
})
