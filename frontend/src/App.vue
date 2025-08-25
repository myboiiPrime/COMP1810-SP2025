<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const isAuthenticated = ref(!!localStorage.getItem('userToken'))
const userRole = ref(localStorage.getItem('userRole'))

const isAdmin = computed(() => userRole.value === 'admin')
const isCustomer = computed(() => userRole.value === 'customer')

// Update authentication state when route changes
watch(route, () => {
  isAuthenticated.value = !!localStorage.getItem('userToken')
  userRole.value = localStorage.getItem('userRole')
})

// Listen for storage changes (when login happens in another tab)
const handleStorageChange = () => {
  isAuthenticated.value = !!localStorage.getItem('userToken')
  userRole.value = localStorage.getItem('userRole')
}

onMounted(() => {
  // Update state on mount
  isAuthenticated.value = !!localStorage.getItem('userToken')
  userRole.value = localStorage.getItem('userRole')
  
  // Listen for storage changes
  window.addEventListener('storage', handleStorageChange)
})

const logout = () => {
  localStorage.removeItem('userToken')
  localStorage.removeItem('userRole')
  localStorage.removeItem('userId')
  isAuthenticated.value = false
  userRole.value = null
  router.push('/login')
}
</script>

<template>
  <div id="app">
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark" v-if="isAuthenticated">
      <div class="container">
        <router-link class="navbar-brand" to="/">
          📚 Online Bookstore
        </router-link>
        
        <div class="navbar-nav ms-auto">
          <!-- Admin Status Indicator -->
          <span v-if="isAdmin" class="navbar-text me-3">
            <span class="badge bg-danger me-2">
              <i class="fas fa-crown"></i> ADMIN
            </span>
          </span>
          
          <router-link v-if="isAdmin" class="nav-link" to="/admin">Admin Dashboard</router-link>
          <router-link v-if="isCustomer" class="nav-link" to="/customer">Dashboard</router-link>
          <router-link v-if="isCustomer" class="nav-link" to="/books">Browse Books</router-link>
          <router-link v-if="isCustomer" class="nav-link" to="/orders">My Orders</router-link>

          <button class="btn btn-outline-light btn-sm ms-2" @click="logout">Logout</button>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="container-fluid">
      <router-view />
    </main>
  </div>
</template>

<style>
#app {
  min-height: 100vh;
  background-color: #f8f9fa;
}

body {
  margin: 0;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.container-fluid {
  padding: 20px;
}

.navbar-brand {
  font-weight: bold;
  font-size: 1.5rem;
}
</style>
