<template>
  <Teleport to="body">
    <div class="notification-container">
      <TransitionGroup name="notification">
        <div
          v-for="notification in notifications"
          :key="notification.id"
          :class="['notification', `notification-${notification.type}`]"
          @click="removeNotification(notification.id)"
        >
          <div class="notification-icon">
            <span v-if="notification.type === 'success'">✅</span>
            <span v-else-if="notification.type === 'error'">❌</span>
            <span v-else-if="notification.type === 'warning'">⚠️</span>
            <span v-else>ℹ️</span>
          </div>
          <div class="notification-content">
            <p>{{ notification.message }}</p>
          </div>
          <button class="notification-close" @click.stop="removeNotification(notification.id)">
            ×
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script>
import { useNotification } from '../composables/useNotification'

export default {
  name: 'NotificationContainer',
  setup() {
    const { notifications, removeNotification } = useNotification()

    return {
      notifications,
      removeNotification
    }
  }
}
</script>

<style scoped>
.notification-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 400px;
  pointer-events: none;
}

.notification {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 20px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  cursor: pointer;
  transition: all 0.3s ease;
  pointer-events: auto;
  min-width: 300px;
  backdrop-filter: blur(10px);
  border-left: 4px solid;
}

.notification:hover {
  transform: translateX(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
}

.notification-success {
  border-left-color: #27ae60;
  background: linear-gradient(135deg, #ffffff 0%, #f0fff4 100%);
}

.notification-error {
  border-left-color: #e74c3c;
  background: linear-gradient(135deg, #ffffff 0%, #fff5f5 100%);
}

.notification-warning {
  border-left-color: #f39c12;
  background: linear-gradient(135deg, #ffffff 0%, #fffbf0 100%);
}

.notification-info {
  border-left-color: #3498db;
  background: linear-gradient(135deg, #ffffff 0%, #f0f9ff 100%);
}

.notification-icon {
  font-size: 24px;
  flex-shrink: 0;
  animation: iconBounce 0.5s ease;
}

@keyframes iconBounce {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-content p {
  margin: 0;
  font-size: 15px;
  font-weight: 500;
  color: #2c3e50;
  line-height: 1.5;
  word-wrap: break-word;
}

.notification-close {
  background: none;
  border: none;
  font-size: 28px;
  line-height: 1;
  color: #95a5a6;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.notification-close:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #2c3e50;
  transform: rotate(90deg);
}

/* Transition Animations */
.notification-enter-active {
  animation: slideInRight 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.notification-leave-active {
  animation: slideOutRight 0.3s ease-out;
}

@keyframes slideInRight {
  from {
    transform: translateX(120%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideOutRight {
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(120%);
    opacity: 0;
  }
}

/* Responsive */
@media (max-width: 768px) {
  .notification-container {
    top: 70px;
    right: 10px;
    left: 10px;
    max-width: none;
  }

  .notification {
    min-width: auto;
  }
}
</style>
