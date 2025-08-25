<template>
  <div class="order-history">
    <div class="container-fluid">
      <!-- Header -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="page-header">
            <h1 class="mb-3">📦 My Order History</h1>
            <p class="text-muted">Track and manage your book orders</p>
          </div>
        </div>
      </div>
      
      <!-- Order Statistics -->
      <div class="row mb-4">
        <div class="col-md-3">
          <div class="card bg-primary text-white">
            <div class="card-body text-center">
              <i class="fas fa-shopping-cart fa-2x mb-2"></i>
              <h4>{{ orderStats.total }}</h4>
              <p class="mb-0">Total Orders</p>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-success text-white">
            <div class="card-body text-center">
              <i class="fas fa-check-circle fa-2x mb-2"></i>
              <h4>{{ orderStats.completed }}</h4>
              <p class="mb-0">Completed</p>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-warning text-white">
            <div class="card-body text-center">
              <i class="fas fa-clock fa-2x mb-2"></i>
              <h4>{{ orderStats.pending }}</h4>
              <p class="mb-0">Pending</p>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-info text-white">
            <div class="card-body text-center">
              <i class="fas fa-dollar-sign fa-2x mb-2"></i>
              <h4>${{ orderStats.totalSpent }}</h4>
              <p class="mb-0">Total Spent</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Filters -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="card">
            <div class="card-body">
              <div class="row">
                <div class="col-md-4">
                  <label class="form-label">Filter by Status</label>
                  <select class="form-select" v-model="statusFilter" @change="filterOrders">
                    <option value="">All Orders</option>
                    <option value="pending">Pending</option>
                    <option value="processing">Processing</option>
                    <option value="confirmed">Confirmed</option>
                    <option value="shipped">Shipped</option>
                    <option value="completed">Completed</option>
                    <option value="delivered">Delivered</option>
                    <option value="cancelled">Cancelled</option>
                    <option value="refunded">Refunded</option>
                  </select>
                </div>
                <div class="col-md-4">
                  <label class="form-label">Date Range</label>
                  <select class="form-select" v-model="dateFilter" @change="filterOrders">
                    <option value="">All Time</option>
                    <option value="7">Last 7 days</option>
                    <option value="30">Last 30 days</option>
                    <option value="90">Last 3 months</option>
                    <option value="365">Last year</option>
                  </select>
                </div>
                <div class="col-md-4">
                  <label class="form-label">Search Orders</label>
                  <input 
                    type="text" 
                    class="form-control" 
                    placeholder="Search by order number, book title, author, or customer name..."
                    v-model="searchQuery"
                    @input="filterOrders"
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Orders List -->
      <div class="row">
        <div class="col-12">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h5 class="mb-0">Orders ({{ filteredOrders.length }})</h5>
              <div class="d-flex gap-2">
                <button class="btn btn-outline-primary btn-sm" @click="exportOrders">
                  <i class="fas fa-download"></i> Export
                </button>
                <button class="btn btn-primary btn-sm" @click="refreshOrders">
                  <i class="fas fa-sync-alt"></i> Refresh
                </button>
              </div>
            </div>
            <div class="card-body">
              <!-- Loading State -->
              <div v-if="loading" class="text-center py-5">
                <div class="spinner-border text-primary" role="status">
                  <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-3 text-muted">Loading your orders...</p>
              </div>
              
              <!-- Error State -->
              <div v-else-if="error" class="text-center py-5">
                <i class="fas fa-exclamation-triangle fa-3x text-danger mb-3"></i>
                <h4 class="text-danger">Error Loading Orders</h4>
                <p class="text-muted">{{ error }}</p>
                <button class="btn btn-primary" @click="loadOrders">
                  <i class="fas fa-sync-alt"></i> Try Again
                </button>
              </div>
              
              <!-- No Orders State -->
              <div v-else-if="filteredOrders.length === 0 && !loading" class="text-center py-5">
                <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                <h4 class="text-muted">No orders found</h4>
                <p class="text-muted">You haven't placed any orders yet or no orders match your filters.</p>
                <router-link to="/books" class="btn btn-primary">
                  <i class="fas fa-shopping-cart"></i> Start Shopping
                </router-link>
              </div>
              
              <div v-else>
                <div class="order-item" v-for="order in filteredOrders" :key="order.id">
                  <div class="card mb-3">
                    <div class="card-header">
                      <div class="row align-items-center">
                        <div class="col-md-3">
                          <h6 class="mb-0">Order #{{ order.orderNumber || order.id }}</h6>
                          <small class="text-muted">{{ formatDate(order.orderDate) }}</small>
                        </div>
                        <div class="col-md-2">
                          <span class="badge" :class="getOrderStatusBadgeClass(order.status)">
                            {{ order.status.toUpperCase() }}
                          </span>
                        </div>
                        <div class="col-md-2">
                          <strong class="text-primary">${{ (order.total || order.totalAmount || 0).toFixed(2) }}</strong>
                        </div>
                        <div class="col-md-2">
                          <small class="text-muted">{{ order.items.length }} item(s)</small>
                        </div>
                        <div class="col-md-3 text-end">
                          <button 
                            class="btn btn-sm btn-outline-info me-1" 
                            @click="toggleOrderDetails(order.id)"
                          >
                            <i class="fas" :class="order.showDetails ? 'fa-chevron-up' : 'fa-chevron-down'"></i>
                            {{ order.showDetails ? 'Hide' : 'Details' }}
                          </button>
                          <button 
                            v-if="order.status === 'pending'"
                            class="btn btn-sm btn-outline-danger"
                            @click="cancelOrder(order)"
                          >
                            <i class="fas fa-times"></i> Cancel
                          </button>
                          <button 
                            v-if="order.status === 'completed'"
                            class="btn btn-sm btn-outline-primary"
                            @click="reorderItems(order)"
                          >
                            <i class="fas fa-redo"></i> Reorder
                          </button>
                        </div>
                      </div>
                    </div>
                    
                    <!-- Order Details -->
                    <div v-if="order.showDetails" class="card-body">
                      <div class="row">
                        <div class="col-md-8">
                          <h6>Order Items:</h6>
                          <div class="table-responsive">
                            <table class="table table-sm">
                              <thead>
                                <tr>
                                  <th>Book</th>
                                  <th>Author</th>
                                  <th>Quantity</th>
                                  <th>Price</th>
                                  <th>Subtotal</th>
                                </tr>
                              </thead>
                              <tbody>
                                <tr v-for="item in order.items" :key="item.id">
                                  <td>{{ item.title }}</td>
                                  <td>{{ item.author }}</td>
                                  <td>{{ item.quantity }}</td>
                                  <td>${{ item.price }}</td>
                                  <td>${{ (item.price * item.quantity).toFixed(2) }}</td>
                                </tr>
                              </tbody>
                            </table>
                          </div>
                        </div>
                        <div class="col-md-4">
                          <h6>Order Summary:</h6>
                          <div class="order-summary">
                            <div class="d-flex justify-content-between">
                              <span>Subtotal:</span>
                              <span>${{ order.subtotal?.toFixed(2) || '0.00' }}</span>
                            </div>
                            <div class="d-flex justify-content-between">
                              <span>Shipping:</span>
                              <span>${{ (order.shippingCost || 0).toFixed(2) }}</span>
                            </div>
                            <div class="d-flex justify-content-between">
                              <span>Tax:</span>
                              <span>${{ (order.tax || 0).toFixed(2) }}</span>
                            </div>
                            <div class="d-flex justify-content-between" v-if="order.discount && order.discount > 0">
                              <span>Discount:</span>
                              <span>-${{ order.discount.toFixed(2) }}</span>
                            </div>
                            <hr>
                            <div class="d-flex justify-content-between fw-bold">
                              <span>Total:</span>
                              <span>${{ (order.total || order.totalAmount || 0).toFixed(2) }}</span>
                            </div>
                          </div>
                          
                          <div class="mt-3" v-if="order.shippingAddress">
                            <h6>Shipping Address:</h6>
                            <address class="small">
                              {{ order.shippingAddress.firstName }} {{ order.shippingAddress.lastName }}<br>
                              {{ order.shippingAddress.street }}<br>
                              {{ order.shippingAddress.city }}, {{ order.shippingAddress.state }} {{ order.shippingAddress.zipCode }}<br>
                              {{ order.shippingAddress.country }}
                              <span v-if="order.shippingAddress.phone"><br>{{ order.shippingAddress.phone }}</span>
                            </address>
                          </div>
                          
                          <div class="mt-3" v-if="order.trackingNumber">
                            <h6>Tracking:</h6>
                            <p class="small">
                              <strong>{{ order.trackingNumber }}</strong>
                            </p>
                          </div>
                          
                          <div class="mt-3" v-if="order.paymentInfo">
                            <h6>Payment Info:</h6>
                            <p class="small">
                              <strong>Method:</strong> {{ order.paymentInfo.method }}<br>
                              <strong>Status:</strong> {{ order.paymentStatus }}<br>
                              <span v-if="order.paymentInfo.cardLastFour">
                                <strong>Card:</strong> **** **** **** {{ order.paymentInfo.cardLastFour }}
                                <span v-if="order.paymentInfo.cardBrand"> ({{ order.paymentInfo.cardBrand }})</span>
                              </span>
                            </p>
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
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { customersAPI } from '@/services/api'

const router = useRouter()

// Reactive data
const statusFilter = ref('')
const dateFilter = ref('')
const searchQuery = ref('')
const allOrders = ref([])
const loading = ref(false)
const error = ref('')

const orderStats = reactive({
  total: 0,
  completed: 0,
  pending: 0,
  totalSpent: 0
})

// Load orders data from API
const loadOrders = async () => {
  try {
    loading.value = true
    error.value = ''
    
    // Get user ID from localStorage
    const userId = localStorage.getItem('userId')
    if (!userId) {
      error.value = 'User not logged in'
      router.push('/login')
      return
    }
    
    console.log('Loading orders for user:', userId)
    const response = await customersAPI.getOrders(userId)
    console.log('Orders API response:', response.data)
    
    if (response.data) {
      // Handle paginated response structure
      const ordersData = response.data.content || response.data.orders || response.data
      
      // Process the orders data to match our component expectations
      allOrders.value = ordersData.map(order => ({
        ...order,
        showDetails: false,
        totalAmount: order.total,
        shipping: order.shippingCost || 0,
        // Map status to lowercase for consistency
        status: order.status.toLowerCase()
      }))
      
      // Calculate stats
      calculateOrderStats()
    } else {
      allOrders.value = []
      resetOrderStats()
    }
  } catch (err) {
    console.error('Error loading orders:', err)
    error.value = 'Failed to load orders. Please try again.'
    allOrders.value = []
    resetOrderStats()
  } finally {
    loading.value = false
  }
}

// Calculate order statistics
const calculateOrderStats = () => {
  orderStats.total = allOrders.value.length
  
  // Count completed orders (including delivered, completed, shipped)
  orderStats.completed = allOrders.value.filter(order => {
    const status = order.status.toLowerCase()
    return status === 'completed' || status === 'delivered' || status === 'shipped'
  }).length
  
  // Count pending orders (including pending, processing, confirmed)
  orderStats.pending = allOrders.value.filter(order => {
    const status = order.status.toLowerCase()
    return status === 'pending' || status === 'processing' || status === 'confirmed'
  }).length
  
  // Calculate total spent (only from completed orders, excluding cancelled)
  orderStats.totalSpent = allOrders.value
    .filter(order => {
      const status = order.status.toLowerCase()
      return status !== 'cancelled' && status !== 'refunded'
    })
    .reduce((total, order) => total + (order.total || order.totalAmount || 0), 0)
    .toFixed(2)
}

// Reset order statistics
const resetOrderStats = () => {
  orderStats.total = 0
  orderStats.completed = 0
  orderStats.pending = 0
  orderStats.totalSpent = 0
}

// Computed properties
const filteredOrders = computed(() => {
  let orders = allOrders.value
  
  // Filter by status
  if (statusFilter.value) {
    orders = orders.filter(order => order.status === statusFilter.value)
  }
  
  // Filter by date range
  if (dateFilter.value) {
    const days = parseInt(dateFilter.value)
    const cutoffDate = new Date()
    cutoffDate.setDate(cutoffDate.getDate() - days)
    orders = orders.filter(order => new Date(order.orderDate) >= cutoffDate)
  }
  
  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    orders = orders.filter(order => {
      // Search in order number, order ID, and items
      const orderNumberMatch = (order.orderNumber || order.id || '').toString().toLowerCase().includes(query)
      const orderIdMatch = order.id.toString().toLowerCase().includes(query)
      
      // Search in order items (title and author)
      const itemsMatch = order.items && order.items.some(item => 
        (item.title || '').toLowerCase().includes(query) ||
        (item.author || '').toLowerCase().includes(query)
      )
      
      // Search in customer/shipping info if available
      const addressMatch = order.shippingAddress && (
        (order.shippingAddress.firstName || '').toLowerCase().includes(query) ||
        (order.shippingAddress.lastName || '').toLowerCase().includes(query)
      )
      
      return orderNumberMatch || orderIdMatch || itemsMatch || addressMatch
    })
  }
  
  // Sort by date (newest first)
  return orders.sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate))
})

// Utility functions
const getOrderStatusBadgeClass = (status) => {
  const normalizedStatus = status.toLowerCase()
  const classes = {
    pending: 'bg-warning text-dark',
    processing: 'bg-info',
    confirmed: 'bg-info',
    shipped: 'bg-primary',
    completed: 'bg-success',
    delivered: 'bg-success',
    cancelled: 'bg-danger',
    refunded: 'bg-secondary'
  }
  return classes[normalizedStatus] || 'bg-secondary'
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// Action functions
const filterOrders = () => {
  // Filtering is reactive through computed property
}

const toggleOrderDetails = (orderId) => {
  const order = allOrders.value.find(o => o.id === orderId)
  if (order) {
    order.showDetails = !order.showDetails
  }
}

const cancelOrder = async (order) => {
  if (confirm(`Are you sure you want to cancel order #${order.orderNumber || order.id}?`)) {
    try {
      // In a real implementation, you would call the cancel API
      // await ordersAPI.cancel(order.id)
      order.status = 'cancelled'
      calculateOrderStats()
      alert(`Order #${order.orderNumber || order.id} has been cancelled.`)
    } catch (err) {
      console.error('Error cancelling order:', err)
      alert('Failed to cancel order. Please try again.')
    }
  }
}

const reorderItems = (order) => {
  const itemNames = order.items.map(item => item.title).join(', ')
  const total = (order.total || order.totalAmount || 0).toFixed(2)
  if (confirm(`Reorder these items?\n\n${itemNames}\n\nTotal: $${total}`)) {
    alert('Items have been added to your cart!')
    // In a real app, this would add items to cart and redirect to checkout
    router.push('/cart')
  }
}

const exportOrders = () => {
  try {
    // Create CSV content
    const headers = ['Order Number', 'Date', 'Status', 'Items', 'Total']
    const csvContent = [
      headers.join(','),
      ...filteredOrders.value.map(order => [
        order.orderNumber || order.id,
        formatDate(order.orderDate),
        order.status,
        order.items.length,
        (order.total || order.totalAmount || 0).toFixed(2)
      ].join(','))
    ].join('\n')
    
    // Download CSV file
    const blob = new Blob([csvContent], { type: 'text/csv' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `order-history-${new Date().toISOString().split('T')[0]}.csv`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
  } catch (err) {
    console.error('Error exporting orders:', err)
    alert('Failed to export orders. Please try again.')
  }
}

const refreshOrders = () => {
  loadOrders()
}

// Lifecycle
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-history {
  padding: 20px 0;
}

.page-header {
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
}

.order-item .card {
  transition: all 0.2s;
}

.order-item .card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.order-summary {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.order-summary .d-flex {
  margin-bottom: 5px;
}

.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}

.btn {
  border-radius: 6px;
  transition: all 0.2s;
}

.btn:hover {
  transform: translateY(-1px);
}

.form-control, .form-select {
  border-radius: 8px;
  border: 1px solid #ddd;
}

.form-control:focus, .form-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
}

.table th {
  border-top: none;
  font-weight: 600;
  color: #333;
}

.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}

address {
  line-height: 1.4;
  margin-bottom: 0;
}

.fas {
  width: 16px;
  text-align: center;
}
</style>