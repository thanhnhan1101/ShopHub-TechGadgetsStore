<template>
  <div id="app">
    <Header v-if="!isAuthPage" />
    <main v-if="!isAuthPage" class="container">
      <router-view />
    </main>
    <router-view v-else />
    <Footer v-if="!isAuthPage" />
    <NotificationContainer />
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'
import NotificationContainer from './components/NotificationContainer.vue'
import './assets/global.css'

export default {
  name: 'App',
  components: {
    Header,
    Footer,
    NotificationContainer
  },
  setup() {
    const route = useRoute()
    
    const isAuthPage = computed(() => {
      return route.name === 'Login' || route.name === 'Register'
    })
    
    const isAdminPage = computed(() => {
      return route.path?.startsWith('/admin')
    })
    
    const shouldHideLayout = computed(() => {
      return isAuthPage.value || isAdminPage.value
    })
    
    return {
      isAuthPage: shouldHideLayout
    }
  }
}
</script>

<style>
#app {
  min-height: 100vh;
}

main.container {
  flex: 1;
  padding: 40px 30px;
  animation: slideIn 0.5s ease;
}
</style>
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover {
  background: #0056b3;
}
<style>
main.container {
  flex: 1;
  padding: 40px 30px;
  animation: slideIn 0.5s ease;
}
</style>
