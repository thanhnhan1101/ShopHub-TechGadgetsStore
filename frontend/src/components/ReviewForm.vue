<template>
  <div class="review-form">
    <h3>Đánh giá sản phẩm</h3>
    
    <!-- Rating stars -->
    <div class="rating-section">
      <label>Đánh giá của bạn: <span class="required">*</span></label>
      <div class="stars">
        <span 
          v-for="star in 5" 
          :key="star"
          class="star"
          :class="{ active: star <= rating }"
          @click="setRating(star)"
          @mouseover="hoverRating = star"
          @mouseleave="hoverRating = 0"
        >
          {{ (hoverRating >= star || rating >= star) ? '★' : '☆' }}
        </span>
      </div>
      <p class="rating-text">{{ getRatingText(rating) }}</p>
    </div>

    <!-- Comment -->
    <div class="form-group">
      <label>Nhận xét:</label>
      <textarea 
        v-model="comment"
        placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm..."
        rows="4"
        maxlength="1000"
      ></textarea>
      <p class="char-count">{{ comment.length }}/1000</p>
    </div>

    <!-- Image upload -->
    <div class="form-group">
      <label>Thêm ảnh (tùy chọn):</label>
      <MultiImageUpload 
        v-model="imageUrls"
        :max-images="5"
      />
    </div>

    <!-- Actions -->
    <div class="form-actions">
      <button @click="cancel" class="btn-cancel">Hủy</button>
      <button 
        @click="submitReview" 
        class="btn-submit"
        :disabled="!rating || submitting"
      >
        {{ submitting ? 'Đang gửi...' : 'Gửi đánh giá' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import MultiImageUpload from './MultiImageUpload.vue';
import { useNotification } from '../composables/useNotification';

const props = defineProps({
  productId: {
    type: Number,
    required: true
  },
  orderId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['submitted', 'cancel']);

const { showNotification } = useNotification();

const rating = ref(0);
const hoverRating = ref(0);
const comment = ref('');
const imageUrls = ref([]);
const submitting = ref(false);

const setRating = (value) => {
  rating.value = value;
};

const getRatingText = (value) => {
  const texts = {
    1: 'Rất tệ',
    2: 'Tệ',
    3: 'Bình thường',
    4: 'Tốt',
    5: 'Tuyệt vời'
  };
  return texts[value] || 'Chọn đánh giá';
};

const submitReview = async () => {
  if (!rating.value) {
    showNotification('Vui lòng chọn số sao đánh giá', 'error');
    return;
  }

  submitting.value = true;

  try {
    const reviewData = {
      productId: props.productId,
      orderId: props.orderId,
      rating: rating.value,
      comment: comment.value,
      imageUrls: imageUrls.value
    };

    emit('submitted', reviewData);
  } catch (error) {
    console.error('Error submitting review:', error);
    showNotification('Có lỗi xảy ra, vui lòng thử lại', 'error');
  } finally {
    submitting.value = false;
  }
};

const cancel = () => {
  emit('cancel');
};
</script>

<style scoped>
.review-form {
  background: linear-gradient(135deg, #ffffff 0%, #fafbff 100%);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.12);
  border: 1px solid rgba(102, 126, 234, 0.08);
  position: relative;
  overflow: hidden;
  animation: slideIn 0.4s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.review-form::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.05), transparent 70%);
  pointer-events: none;
}

.review-form h3 {
  margin-top: 0;
  margin-bottom: 28px;
  color: #1e293b;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.5px;
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.review-form h3::before {
  content: '⭐';
  font-size: 28px;
  filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.3));
}

.rating-section {
  margin-bottom: 28px;
  padding: 24px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.04) 0%, rgba(118, 75, 162, 0.04) 100%);
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.1);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.rating-section:hover {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.08);
  transform: translateY(-2px);
}

.rating-section label {
  display: block;
  margin-bottom: 16px;
  font-weight: 700;
  color: #1e293b;
  font-size: 16px;
  letter-spacing: 0.3px;
}

.required {
  color: #ef4444;
  font-weight: 800;
}

.stars {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  justify-content: center;
  padding: 16px 0;
}

.star {
  font-size: 3rem;
  cursor: pointer;
  color: #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
  position: relative;
}

.star:hover {
  transform: scale(1.2) rotate(10deg);
  filter: drop-shadow(0 4px 8px rgba(245, 158, 11, 0.4));
}

.star.active {
  color: #f59e0b;
  animation: starPop 0.3s ease-out;
}

@keyframes starPop {
  0% { transform: scale(1); }
  50% { transform: scale(1.3) rotate(15deg); }
  100% { transform: scale(1); }
}

.rating-text {
  margin: 12px 0 0;
  font-size: 17px;
  font-weight: 700;
  text-align: center;
  min-height: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.form-group {
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.form-group label {
  display: block;
  margin-bottom: 12px;
  font-weight: 700;
  color: #1e293b;
  font-size: 16px;
  letter-spacing: 0.3px;
}

.form-group textarea {
  width: 100%;
  padding: 16px;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 14px;
  font-family: inherit;
  font-size: 15px;
  resize: vertical;
  transition: all 0.3s ease;
  background: white;
  color: #1e293b;
  line-height: 1.6;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.form-group textarea::placeholder {
  color: #94a3b8;
  font-weight: 500;
}

.char-count {
  text-align: right;
  font-size: 13px;
  color: #64748b;
  margin-top: 8px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 32px;
  position: relative;
  z-index: 1;
}

.btn-cancel,
.btn-submit {
  padding: 14px 32px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: 0.5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.btn-cancel::before,
.btn-submit::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.5s;
}

.btn-cancel:hover::before,
.btn-submit:hover::before {
  left: 100%;
}

.btn-cancel {
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #475569;
  border: 2px solid #cbd5e0;
}

.btn-cancel:hover {
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e0 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  color: #1e293b;
}

.btn-cancel:active {
  transform: translateY(0);
}

.btn-submit {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.btn-submit:hover:not(:disabled) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 28px rgba(102, 126, 234, 0.4);
}

.btn-submit:active:not(:disabled) {
  transform: translateY(-1px) scale(1.01);
}

.btn-submit:disabled {
  background: linear-gradient(135deg, #cbd5e0 0%, #94a3b8 100%);
  cursor: not-allowed;
  opacity: 0.6;
  transform: none;
  border-color: transparent;
}

/* Loading animation for submit button */
.btn-submit:disabled::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 16px;
  height: 16px;
  margin: -8px 0 0 -8px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 576px) {
  .review-form {
    padding: 24px;
    border-radius: 16px;
  }

  .review-form h3 {
    font-size: 20px;
    margin-bottom: 20px;
  }

  .rating-section {
    padding: 20px;
  }

  .stars {
    gap: 6px;
  }

  .star {
    font-size: 2.5rem;
  }

  .form-actions {
    flex-direction: column-reverse;
    gap: 10px;
  }

  .btn-cancel,
  .btn-submit {
    width: 100%;
    padding: 14px 24px;
  }
}
</style>
