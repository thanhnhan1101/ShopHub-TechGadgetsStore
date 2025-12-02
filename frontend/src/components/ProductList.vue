<template>
  <div>
    <form @submit.prevent="createProduct" style="margin-bottom: 1rem;">
      <input v-model="form.name" placeholder="Name" required />
      <input v-model="form.price" type="number" step="0.01" placeholder="Price" required />
      <input v-model="form.stock" type="number" placeholder="Stock" required />
      <input v-model="form.description" placeholder="Description" />
      <button type="submit">Add</button>
    </form>

    <div v-if="loading">Loading...</div>

    <ul v-else>
      <li v-for="p in products" :key="p.id" style="margin-bottom: 0.5rem;">
        <strong>{{ p.name }}</strong> — ${{ p.price }} — stock: {{ p.stock }}
        <div style="font-size: 0.9em; color: #444">{{ p.description }}</div>
      </li>
    </ul>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  data() {
    return {
      products: [],
      loading: false,
      form: { name: '', description: '', price: 0, stock: 0 }
    }
  },
  mounted() {
    this.fetch()
  },
  methods: {
    async fetch() {
      this.loading = true
      try {
        const res = await axios.get('http://localhost:8080/api/products')
        this.products = res.data
      } catch (e) {
        console.error(e)
        alert('Cannot load products. Make sure backend is running and CORS is allowed.')
      } finally {
        this.loading = false
      }
    },
    async createProduct() {
      try {
        await axios.post('http://localhost:8080/api/products', this.form)
        this.form = { name: '', description: '', price: 0, stock: 0 }
        this.fetch()
      } catch (e) {
        console.error(e)
        alert('Failed to create product')
      }
    }
  }
}
</script>
