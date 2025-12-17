import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export default {
  // Categories
  getCategories() {
    return api.get('/categories')
  },
  getCategoryById(id) {
    return api.get(`/categories/${id}`)
  },
  createCategory(category) {
    return api.post('/categories', category)
  },
  updateCategory(id, category) {
    return api.put(`/categories/${id}`, category)
  },
  deleteCategory(id) {
    return api.delete(`/categories/${id}`)
  },

  // Products
  getProducts(categoryId = null) {
    const params = categoryId ? { categoryId } : {}
    return api.get('/products', { params })
  },
  getProductById(id) {
    return api.get(`/products/${id}`)
  },
  createProduct(product) {
    return api.post('/products', product)
  },
  updateProduct(id, product) {
    return api.put(`/products/${id}`, product)
  },
  deleteProduct(id) {
    return api.delete(`/products/${id}`)
  },

  // Cart
  getCart(userId) {
    return api.get(`/cart/${userId}`)
  },
  addToCart(cartItem) {
    return api.post('/cart', cartItem)
  },
  updateCartItem(id, cartItem) {
    return api.put(`/cart/${id}`, cartItem)
  },
  removeFromCart(id) {
    return api.delete(`/cart/${id}`)
  },
  clearCart(userId) {
    return api.delete(`/cart/user/${userId}`)
  },

  // Orders
  getOrders(status = null) {
    const params = status ? { status } : {}
    return api.get('/orders', { params })
  },
  getOrderById(id) {
    return api.get(`/orders/${id}`)
  },
  getOrdersByUser(userId) {
    return api.get(`/orders/user/${userId}`)
  },
  createOrder(order) {
    return api.post('/orders', order)
  },
  updateOrderStatus(id, status) {
    return api.put(`/orders/${id}/status`, null, { params: { status } })
  },
  cancelOrder(id) {
    return api.delete(`/orders/${id}`)
  }
}
