import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'

const pinia = createPinia()
const app = createApp(App)

app.use(pinia)
app.use(router)

// Load user từ localStorage khi app khởi động
const authStore = useAuthStore()
authStore.loadUserFromStorage()

app.mount('#app')
