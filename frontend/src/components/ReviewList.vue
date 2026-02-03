<template>
  <div class="review-list">
    <div class="review-header">
      <h3>Đánh giá sản phẩm</h3>
      
      <!-- Rating Summary -->
      <div v-if="stats" class="rating-summary">
        <div class="average-rating">
          <div class="rating-number">{{ stats.averageRating.toFixed(1) }}</div>
          <div class="stars">
            <span v-for="star in 5" :key="star" class="star">
              {{ star <= Math.round(stats.averageRating) ? '★' : '☆' }}
            </span>
          </div>
          <div class="total-reviews">{{ stats.totalReviews }} đánh giá</div>
        </div>
        
        <!-- Rating Distribution -->
        <div class="rating-bars">
          <div 
            v-for="rating in [5, 4, 3, 2, 1]" 
            :key="rating"
            class="rating-bar-item"
          >
            <span class="rating-label">{{ rating }}★</span>
            <div class="bar-container">
              <div 
                class="bar-fill" 
                :style="{ width: getRatingPercentage(rating) + '%' }"
              ></div>
            </div>
            <span class="rating-count">{{ stats.ratingDistribution[rating] || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Reviews List -->
    <div v-if="loading" class="loading">Đang tải đánh giá...</div>
    
    <div v-else-if="reviews.length === 0" class="no-reviews">
      <p>Chưa có đánh giá nào cho sản phẩm này</p>
    </div>
    
    <div v-else class="reviews">
      <div 
        v-for="review in reviews" 
        :key="review.id"
        class="review-item"
      >
        <div class="review-header-item">
          <div class="user-info">
            <div class="user-avatar">
              {{ getUserInitial(review.user.fullName) }}
            </div>
            <div class="user-details">
              <div class="user-name">{{ review.user.fullName }}</div>
              <div class="review-date">{{ formatDate(review.createdAt) }}</div>
            </div>
          </div>
          
          <div class="review-rating">
            <span v-for="star in 5" :key="star" class="star">
              {{ star <= review.rating ? '★' : '☆' }}
            </span>
          </div>
        </div>
        
        <div v-if="review.comment" class="review-comment">
          {{ review.comment }}
        </div>
        
        <div v-if="review.images && review.images.length > 0" class="review-images">
          <img 
            v-for="image in review.images" 
            :key="image.id"
            :src="image.imageUrl" 
            :alt="'Review image'"
            @click="openImageModal(image.imageUrl)"
          />
        </div>
      </div>
    </div>

    <!-- Image Modal -->
    <div v-if="showImageModal" class="image-modal" @click="closeImageModal">
      <img :src="modalImageUrl" alt="Review image" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import api from '../services/api';

const props = defineProps({
  productId: {
    type: Number,
    required: true
  }
});

const reviews = ref([]);
const stats = ref(null);
const loading = ref(true);
const showImageModal = ref(false);
const modalImageUrl = ref('');

const loadReviews = async () => {
  loading.value = true;
  try {
    const [reviewsData, statsData] = await Promise.all([
      api.getProductReviews(props.productId),
      api.getProductReviewStats(props.productId)
    ]);
    
    reviews.value = reviewsData;
    stats.value = statsData;
  } catch (error) {
    console.error('Error loading reviews:', error);
  } finally {
    loading.value = false;
  }
};

const getRatingPercentage = (rating) => {
  if (!stats.value || stats.value.totalReviews === 0) return 0;
  const count = stats.value.ratingDistribution[rating] || 0;
  return (count / stats.value.totalReviews) * 100;
};

const getUserInitial = (fullName) => {
  if (!fullName) return '?';
  return fullName.charAt(0).toUpperCase();
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  const now = new Date();
  const diffTime = Math.abs(now - date);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  
  if (diffDays === 1) return 'Hôm qua';
  if (diffDays < 7) return `${diffDays} ngày trước`;
  if (diffDays < 30) return `${Math.floor(diffDays / 7)} tuần trước`;
  
  return date.toLocaleDateString('vi-VN');
};

const openImageModal = (imageUrl) => {
  modalImageUrl.value = imageUrl;
  showImageModal.value = true;
};

const closeImageModal = () => {
  showImageModal.value = false;
  modalImageUrl.value = '';
};

// Expose method để component cha có thể refresh
defineExpose({
  loadReviews
});

onMounted(() => {
  loadReviews();
});
</script>

<style scoped>
.review-list {
  margin-top: 30px;
}

.review-header {
  margin-bottom: 20px;
}

.review-header h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 1.3rem;
}

/* Rating Summary */
.rating-summary {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
}

.average-rating {
  text-align: center;
  min-width: 120px;
}

.rating-number {
  font-size: 3rem;
  font-weight: bold;
  color: #f39c12;
  line-height: 1;
}

.average-rating .stars {
  margin: 10px 0;
}

.average-rating .star {
  font-size: 1.2rem;
  color: #f39c12;
}

.total-reviews {
  font-size: 0.9rem;
  color: #666;
}

.rating-bars {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-bar-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rating-label {
  font-size: 0.9rem;
  color: #666;
  min-width: 30px;
}

.bar-container {
  flex: 1;
  height: 8px;
  background: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: #f39c12;
  transition: width 0.3s;
}

.rating-count {
  font-size: 0.9rem;
  color: #666;
  min-width: 30px;
  text-align: right;
}

/* Reviews */
.loading,
.no-reviews {
  text-align: center;
  padding: 40px;
  color: #999;
}

.reviews {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
}

.review-header-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #3498db;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.1rem;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-weight: 600;
  color: #333;
}

.review-date {
  font-size: 0.85rem;
  color: #999;
}

.review-rating .star {
  font-size: 1.1rem;
  color: #f39c12;
}

.review-comment {
  margin-bottom: 15px;
  color: #555;
  line-height: 1.6;
}

.review-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.review-images img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.2s;
}

.review-images img:hover {
  transform: scale(1.05);
}

/* Image Modal */
.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  cursor: pointer;
}

.image-modal img {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
}
</style>
