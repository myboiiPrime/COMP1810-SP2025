<template>
  <div class="customer-dashboard">
    <div class="container-fluid">
      <!-- Welcome Section -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="welcome-card">
            <h1 class="mb-2">Welcome back, {{ customerName }}! 👋</h1>
            <p class="text-muted mb-0">Your membership level: 
              <span class="badge" :class="getMembershipBadgeClass(membershipLevel)">
                {{ membershipLevel.toUpperCase() }}
              </span>
            </p>
          </div>
        </div>
      </div>
      
      <!-- Quick Stats -->
      <div class="row mb-4">
        <div class="col-md-3">
          <div class="card bg-primary text-white">
            <div class="card-body text-center">
              <i class="fas fa-shopping-cart fa-2x mb-2"></i>
              <h4>{{ customerStats.totalOrders }}</h4>
              <p class="mb-0">Total Orders</p>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-success text-white">
            <div class="card-body text-center">
              <i class="fas fa-book fa-2x mb-2"></i>
              <h4>{{ customerStats.booksOrdered }}</h4>
              <p class="mb-0">Books Ordered</p>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-info text-white">
            <div class="card-body text-center">
              <i class="fas fa-dollar-sign fa-2x mb-2"></i>
              <h4>${{ customerStats.totalSpent }}</h4>
              <p class="mb-0">Total Spent</p>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-warning text-white">
            <div class="card-body text-center">
              <i class="fas fa-star fa-2x mb-2"></i>
              <h4>{{ customerStats.loyaltyPoints }}</h4>
              <p class="mb-0">Loyalty Points</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Quick Actions -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">🚀 Quick Actions</h5>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-3 mb-3">
                  <router-link to="/books" class="btn btn-outline-primary w-100 h-100 d-flex flex-column align-items-center justify-content-center">
                    <i class="fas fa-search fa-2x mb-2"></i>
                    <span>Browse Books</span>
                  </router-link>
                </div>
                <div class="col-md-3 mb-3">
                  <router-link to="/orders" class="btn btn-outline-success w-100 h-100 d-flex flex-column align-items-center justify-content-center">
                    <i class="fas fa-history fa-2x mb-2"></i>
                    <span>Order History</span>
                  </router-link>
                </div>
                <div class="col-md-3 mb-3">
                  <button class="btn btn-outline-warning w-100 h-100 d-flex flex-column align-items-center justify-content-center" @click="showRecommendations = true">
                    <i class="fas fa-magic fa-2x mb-2"></i>
                    <span>Recommendations</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Recent Orders -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h5 class="mb-0">📦 Recent Orders</h5>
              <router-link to="/orders" class="btn btn-sm btn-outline-primary">View All</router-link>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-hover">
                  <thead>
                    <tr>
                      <th>Order ID</th>
                      <th>Date</th>
                      <th>Items</th>
                      <th>Total</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="order in recentOrders" :key="order.id">
                      <td>#{{ order.id }}</td>
                      <td>{{ formatDate(order.orderDate) }}</td>
                      <td>{{ order.itemCount }} items</td>
                      <td>${{ order.totalAmount }}</td>
                      <td>
                        <span class="badge" :class="getOrderStatusBadgeClass(order.status)">
                          {{ order.status }}
                        </span>
                      </td>
                      <td>
                        <button class="btn btn-sm btn-outline-info me-1" @click="viewOrderDetails(order)">
                          <i class="fas fa-eye"></i>
                        </button>
                        <button 
                          v-if="order.status === 'pending'"
                          class="btn btn-sm btn-outline-danger" 
                          @click="cancelOrder(order)"
                        >
                          <i class="fas fa-times"></i>
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Recommended Books -->
      <div class="row" v-if="showRecommendations">
        <div class="col-12">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h5 class="mb-0">✨ Recommended for You</h5>
              <button class="btn btn-sm btn-outline-secondary" @click="showRecommendations = false">
                <i class="fas fa-times"></i>
              </button>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-4 mb-3" v-for="book in recommendedBooks" :key="book.id">
                  <div class="card h-100">
                    <div class="card-body">
                      <h6 class="card-title">{{ book.title }}</h6>
                      <p class="card-text text-muted">by {{ book.author }}</p>
                      <p class="card-text">
                        <span class="badge bg-secondary">{{ book.genre }}</span>
                        <span class="float-end fw-bold">${{ book.price }}</span>
                      </p>
                      <button class="btn btn-primary btn-sm w-100" @click="addToCart(book)">
                        <i class="fas fa-cart-plus"></i> Add to Cart
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { customersAPI } from '@/services/api'

const router = useRouter()

// Reactive data
const customerName = ref('John Doe')
const membershipLevel = ref('gold')
const showRecommendations = ref(false)

const customerStats = reactive({
  totalOrders: 0,
  booksOrdered: 0,
  totalSpent: 0,
  loyaltyPoints: 0
})

const recentOrders = ref([])
const recommendedBooks = ref([])

// Load customer data from API
const loadCustomerData = async () => {
  try {
    // Get customer info from localStorage
    const userEmail = localStorage.getItem('userEmail')
    const userId = localStorage.getItem('userId')
    
    console.log('User ID from localStorage:', userId)
    console.log('User Email from localStorage:', userEmail)
    
    if (userEmail) {
      customerName.value = userEmail.split('@')[0].replace('.', ' ')
    }
    
    if (!userId) {
      console.error('No user ID found in localStorage')
      // Redirect to login if no user ID
      router.push('/login')
      return
    }
    
    if (userId) {
      // Load customer profile data
      const profileResponse = await customersAPI.getProfile(userId)
      console.log('Profile API response:', profileResponse.data)
      
      if (profileResponse.data && profileResponse.data.success) {
        const profile = profileResponse.data.customer
        console.log('Customer profile:', profile)
        
        // Update customer name
        customerName.value = profile.fullName || profile.email.split('@')[0]
        
        // Update membership level
        membershipLevel.value = (profile.membershipLevel || 'bronze').toLowerCase()
        
        // Update customer stats from profile (will be overridden by actual order data)
        customerStats.totalOrders = profile.orderCount || 0
        customerStats.booksOrdered = profile.orderCount || 0
        customerStats.totalSpent = parseFloat(profile.totalSpent || 0).toFixed(2)
        customerStats.loyaltyPoints = profile.loyaltyPoints || 0
      }
      
      // Load customer orders
      const ordersResponse = await customersAPI.getOrders(userId)
      console.log('Orders API response:', ordersResponse.data)
      
      if (ordersResponse.data && ordersResponse.data.success) {
        const orders = ordersResponse.data.orders || []
        console.log('Parsed orders:', orders)
        
        // Map orders to match template expectations and take only recent 5
        recentOrders.value = orders.slice(0, 5).map(order => ({
          id: order.id,
          orderDate: order.orderDate,
          itemCount: order.items ? order.items.length : 0,
          totalAmount: order.total ? order.total.toFixed(2) : '0.00',
          status: order.status || 'pending'
        }))
        
        // Update customer stats with actual order data
        customerStats.totalOrders = orders.length
        customerStats.booksOrdered = orders.reduce((total, order) => {
          return total + (order.items ? order.items.reduce((sum, item) => sum + (item.quantity || 1), 0) : 0)
        }, 0)
        customerStats.totalSpent = orders.reduce((total, order) => total + (order.total || 0), 0).toFixed(2)
        
        console.log('Updated customer stats:', customerStats)
      } else {
        console.warn('No orders data received or API call failed')
      }
      
      // Load book recommendations
      const recommendationsResponse = await customersAPI.getRecommendations(userId)
      if (recommendationsResponse.data) {
        const data = recommendationsResponse.data.data || recommendationsResponse.data
        recommendedBooks.value = data.books || data.recommendations || []
      }
    }
    
    console.log('Customer data loaded successfully')
    
  } catch (error) {
    console.error('Failed to load customer data:', error)
  }
}

// Utility functions
const getMembershipBadgeClass = (level) => {
  const classes = {
    bronze: 'bg-warning text-dark',
    silver: 'bg-secondary',
    gold: 'bg-warning text-dark',
    platinum: 'bg-dark'
  }
  return classes[level] || 'bg-primary'
}

const getOrderStatusBadgeClass = (status) => {
  const classes = {
    pending: 'bg-warning text-dark',
    processing: 'bg-info',
    shipped: 'bg-primary',
    completed: 'bg-success',
    cancelled: 'bg-danger'
  }
  return classes[status] || 'bg-secondary'
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString()
}

// Action functions
const viewOrderDetails = (order) => {
  alert(`Order #${order.id} Details:\n\nDate: ${formatDate(order.orderDate)}\nItems: ${order.itemCount}\nTotal: $${order.totalAmount}\nStatus: ${order.status}`)
}

const cancelOrder = (order) => {
  if (confirm(`Are you sure you want to cancel order #${order.id}?`)) {
    order.status = 'cancelled'
    alert(`Order #${order.id} has been cancelled.`)
  }
}

const addToCart = (book) => {
  alert(`"${book.title}" has been added to your cart!`)
  // In a real app, this would add the book to a cart state/store
}

// Lifecycle
onMounted(() => {
  loadCustomerData()
})
</script>

<style scoped>
.customer-dashboard {
  padding: 20px 0;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 15px;
  margin-bottom: 20px;
}

.card {
  border: none;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  border-radius: 8px;
  transition: transform 0.2s;
}

.card:hover {
  transform: translateY(-2px);
}

.btn {
  border-radius: 8px;
  transition: all 0.2s;
}

.btn:hover {
  transform: translateY(-1px);
}

.table th {
  border-top: none;
  font-weight: 600;
  color: #333;
}

.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}

.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
  font-weight: 600;
}

.fas {
  width: 16px;
  text-align: center;
  line-height: 1;
  display: inline-block;
}

.card-body .fas.fa-2x {
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.quick-action-btn {
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.2s;
}

.quick-action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
</style>