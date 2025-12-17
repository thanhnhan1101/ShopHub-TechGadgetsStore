<template>
  <footer class="footer">
    <div class="container-footer">
      <p>&copy; 2025 ShopHub - Tech Gadgets Store. All rights reserved.</p>
      <p class="tech-stack">Built with Spring Boot + Vue 3 + SQL Server</p>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer'
}
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 0;
  margin-top: 80px;
  text-align: center;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.1);
}

.container-footer {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 30px;
}

.container-footer p {
  font-size: 15px;
  margin: 8px 0;
  opacity: 0.95;
}

.tech-stack {
  margin-top: 12px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
  letter-spacing: 0.5px;
}
</style>
