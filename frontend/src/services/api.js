import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Interceptor để tự động thêm JWT token vào mọi request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log(`API Request: ${config.method?.toUpperCase()} ${config.url} (with token)`)
    } else {
      console.log(`API Request: ${config.method?.toUpperCase()} ${config.url} (no token)`)
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Interceptor để xử lý lỗi response
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.log('API Error:', error.response?.status, error.config?.url)
    
    if (error.response?.status === 401) {
      // Chỉ logout và redirect khi:
      // 1. Có lỗi 401 (Unauthorized)
      // 2. KHÔNG phải đang ở trang public (login/register/home)
      // 3. Có token trong localStorage (nghĩa là đã từng đăng nhập)
      const currentPath = window.location.pathname
      const hasToken = localStorage.getItem('token')
      const isPublicPage = currentPath.includes('/login') || 
                          currentPath.includes('/register') || 
                          currentPath === '/'
      
      if (hasToken && !isPublicPage) {
        console.warn('Token expired or invalid. Logging out...')
        // Token hết hạn hoặc không hợp lệ - clear storage và redirect
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        delete axios.defaults.headers.common['Authorization']
        
        // Redirect về login với thông báo
        window.location.href = '/login?expired=true'
      }
    }
    
    return Promise.reject(error)
  }
)

export default {
  // Categories
  getCategories() {
    return api.get('/categories') // Admin - lấy tất cả
  },
  getActiveCategories() {
    return api.get('/categories/active') // User - chỉ lấy active
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
    return api.delete(`/categories/${id}`) // Hard delete
  },
  toggleCategoryStatus(id) {
    return api.put(`/categories/${id}/toggle-status`) // Bật/tắt trạng thái
  },

  // Products
  getProducts(categoryId = null, includeInactive = false) {
    const params = {}
    if (categoryId) params.categoryId = categoryId
    if (includeInactive) params.includeInactive = true
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

  // File Upload
  uploadImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    
    return api.post('/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // Upload nhiều ảnh cùng lúc
  uploadMultipleImages(files) {
    const formData = new FormData()
    
    // Thêm tất cả files vào FormData
    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i])
    }
    
    return api.post('/upload/images', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
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
  },

  // Users (Admin only)
  getUsers() {
    return api.get('/users')
  },
  getUserById(id) {
    return api.get(`/users/${id}`)
  },
  createUser(user) {
    return api.post('/users', user)
  },
  updateUser(id, user) {
    return api.put(`/users/${id}`, user)
  },
  deleteUser(id) {
    return api.delete(`/users/${id}`)
  },

  // Reviews
  createReview(reviewData) {
    return api.post('/reviews', reviewData).then(res => res.data)
  },
  getProductReviews(productId) {
    return api.get(`/reviews/product/${productId}`).then(res => res.data)
  },
  getProductReviewStats(productId) {
    return api.get(`/reviews/product/${productId}/stats`).then(res => res.data)
  },
  canUserReview(productId, orderId) {
    return api.get('/reviews/can-review', { 
      params: { productId, orderId } 
    }).then(res => res.data)
  },
  getMyReviews() {
    return api.get('/reviews/my-reviews').then(res => res.data)
  }
}
