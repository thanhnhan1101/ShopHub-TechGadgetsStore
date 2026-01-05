import { ref } from 'vue'

const notifications = ref([])
let idCounter = 0

export function useNotification() {
  const notify = ({ message, type = 'info', duration = 3000 }) => {
    const id = ++idCounter
    
    notifications.value.push({
      id,
      message,
      type, // 'success', 'error', 'warning', 'info'
      duration
    })

    if (duration > 0) {
      setTimeout(() => {
        removeNotification(id)
      }, duration)
    }

    return id
  }

  const removeNotification = (id) => {
    const index = notifications.value.findIndex(n => n.id === id)
    if (index > -1) {
      notifications.value.splice(index, 1)
    }
  }

  const success = (message, duration = 3000) => {
    return notify({ message, type: 'success', duration })
  }

  const error = (message, duration = 4000) => {
    return notify({ message, type: 'error', duration })
  }

  const warning = (message, duration = 3500) => {
    return notify({ message, type: 'warning', duration })
  }

  const info = (message, duration = 3000) => {
    return notify({ message, type: 'info', duration })
  }

  return {
    notifications,
    notify,
    removeNotification,
    success,
    error,
    warning,
    info
  }
}
