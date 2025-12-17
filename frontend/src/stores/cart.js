import { defineStore } from 'pinia'
import api from '../services/api'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [],
    userId: 1 // Mock user ID - in production, get from auth
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
      try {
        const response = await api.getCart(this.userId)
        this.items = response.data
      } catch (error) {
        console.error('Error fetching cart:', error)
      }
    },

    async addToCart(product, quantity = 1) {
      try {
        const cartItem = {
          user: { id: this.userId },
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
