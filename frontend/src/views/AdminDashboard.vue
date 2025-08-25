<template>
  <div class="admin-dashboard">
    <div class="container-fluid">
      <div class="row">
        <div class="col-12">
          <div class="admin-header mb-4">
            <div class="d-flex align-items-center">
              <span class="admin-badge me-3">
                <i class="fas fa-crown"></i> ADMIN MODE
              </span>
              <h1 class="mb-0">Dashboard</h1>
            </div>
            <p class="text-muted mb-0">System Administration & Management</p>
          </div>
        </div>
      </div>
      
      <!-- Statistics Cards -->
      <div class="row mb-4">
        <div class="col-md-3">
          <div class="card bg-primary text-white">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h4>{{ stats.totalBooks }}</h4>
                  <p class="mb-0">Total Books</p>
                </div>
                <div class="align-self-center">
                  <i class="fas fa-book fa-2x"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-success text-white">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h4>{{ stats.totalCustomers }}</h4>
                  <p class="mb-0">Total Customers</p>
                </div>
                <div class="align-self-center">
                  <i class="fas fa-users fa-2x"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-warning text-white">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h4>{{ stats.totalOrders }}</h4>
                  <p class="mb-0">Total Orders</p>
                </div>
                <div class="align-self-center">
                  <i class="fas fa-shopping-cart fa-2x"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-info text-white">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h4>${{ stats.totalRevenue }}</h4>
                  <p class="mb-0">Total Revenue</p>
                </div>
                <div class="align-self-center">
                  <i class="fas fa-dollar-sign fa-2x"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Management Tabs -->
      <div class="row">
        <div class="col-12">
          <ul class="nav nav-tabs" id="adminTabs" role="tablist">
            <li class="nav-item" role="presentation">
              <button class="nav-link active" id="books-tab" data-bs-toggle="tab" data-bs-target="#books" type="button" role="tab">
                📚 Manage Books
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" id="customers-tab" data-bs-toggle="tab" data-bs-target="#customers" type="button" role="tab">
                👥 Manage Customers
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" id="orders-tab" data-bs-toggle="tab" data-bs-target="#orders" type="button" role="tab">
                🛒 Manage Orders
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button class="nav-link" id="system-tab" data-bs-toggle="tab" data-bs-target="#system" type="button" role="tab">
                ⚙️ System Monitor
              </button>
            </li>
          </ul>
          
          <div class="tab-content" id="adminTabsContent">
            <!-- Books Management -->
            <div class="tab-pane fade show active" id="books" role="tabpanel">
              <div class="card mt-3">
                <div class="card-header d-flex justify-content-between align-items-center">
                  <h5 class="mb-0">Books Management</h5>
                  <button class="btn btn-primary btn-sm" @click="showAddBookModal = true">
                    <i class="fas fa-plus"></i> Add New Book
                  </button>
                </div>
                <div class="card-body">
                  <!-- Advanced Search System -->
                  <div class="advanced-search-container mb-4">
                    <!-- Primary Search Bar -->
                    <div class="row mb-3">
                      <div class="col-md-8">
                        <div class="input-group">
                          <span class="input-group-text"><i class="fas fa-search"></i></span>
                          <input 
                            type="text" 
                            class="form-control" 
                            placeholder="Search books by title, author, genre, or ISBN..."
                            v-model="bookSearchQuery"
                            @input="searchBooks"
                          >
                          <button class="btn btn-outline-secondary" type="button" @click="clearBookSearch">
                            <i class="fas fa-times"></i>
                          </button>
                        </div>
                      </div>
                      <div class="col-md-4">
                        <button 
                          class="btn btn-outline-primary w-100" 
                          type="button" 
                          @click="toggleAdvancedFilters"
                        >
                          <i class="fas fa-filter"></i> Advanced Filters
                          <i class="fas" :class="showAdvancedFilters ? 'fa-chevron-up' : 'fa-chevron-down'"></i>
                        </button>
                      </div>
                    </div>
                    
                    <!-- Advanced Filters Panel -->
                    <div v-show="showAdvancedFilters" class="advanced-filters-panel">
                      <div class="row mb-3">
                        <div class="col-md-3">
                          <label class="form-label">Genre</label>
                          <select class="form-select" v-model="bookGenreFilter" @change="filterBooks">
                            <option value="">All Genres</option>
                            <option v-for="genre in availableGenres" :key="genre" :value="genre">{{ genre }}</option>
                          </select>
                        </div>
                        <div class="col-md-3">
                          <label class="form-label">Stock Status</label>
                          <select class="form-select" v-model="bookStockFilter" @change="filterBooks">
                            <option value="">All Stock Status</option>
                            <option value="inStock">In Stock</option>
                            <option value="outOfStock">Out of Stock</option>
                            <option value="lowStock">Low Stock (< 10)</option>
                          </select>
                        </div>
                        <div class="col-md-3">
                          <label class="form-label">Sort By</label>
                          <select class="form-select" v-model="bookSortBy" @change="sortBooks">
                            <option value="title">Title (A-Z)</option>
                            <option value="title-desc">Title (Z-A)</option>
                            <option value="author">Author (A-Z)</option>
                            <option value="author-desc">Author (Z-A)</option>
                            <option value="price">Price (Low to High)</option>
                            <option value="price-desc">Price (High to Low)</option>
                            <option value="stock">Stock (Low to High)</option>
                            <option value="stock-desc">Stock (High to Low)</option>
                          </select>
                        </div>
                        <div class="col-md-3">
                          <label class="form-label">Items per View</label>
                          <select class="form-select" v-model="itemsPerPage" @change="updatePagination">
                            <option value="10">10 items</option>
                            <option value="25">25 items</option>
                            <option value="50">50 items</option>
                            <option value="100">100 items</option>
                            <option value="all">All items</option>
                          </select>
                        </div>
                      </div>
                      
                      <!-- Price Range Filter -->
                      <div class="row mb-3">
                        <div class="col-md-6">
                          <label class="form-label">Price Range: ${{ priceRange.min }} - ${{ priceRange.max }}</label>
                          <div class="d-flex align-items-center">
                            <input 
                              type="range" 
                              class="form-range me-2" 
                              :min="minPrice" 
                              :max="maxPrice" 
                              v-model="priceRange.min"
                              @input="filterBooks"
                            >
                            <input 
                              type="range" 
                              class="form-range" 
                              :min="minPrice" 
                              :max="maxPrice" 
                              v-model="priceRange.max"
                              @input="filterBooks"
                            >
                          </div>
                        </div>
                        <div class="col-md-6">
                          <label class="form-label">Quick Actions</label>
                          <div class="btn-group w-100" role="group">
                            <button class="btn btn-outline-info btn-sm" @click="exportFilteredBooks">
                              <i class="fas fa-download"></i> Export
                            </button>
                            <button class="btn btn-outline-warning btn-sm" @click="resetAllFilters">
                              <i class="fas fa-undo"></i> Reset
                            </button>
                            <button class="btn btn-outline-success btn-sm" @click="saveSearchPreset">
                              <i class="fas fa-save"></i> Save Preset
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <!-- Results Summary & Pagination Controls -->
                  <div class="d-flex justify-content-between align-items-center mb-3">
                    <div>
                      <small class="text-muted">
                        Showing {{ paginatedBooks.length }} of {{ filteredBooks.length }} books
                        <span v-if="filteredBooks.length !== books.length">
                          ({{ filteredBooks.length }} filtered from {{ books.length }} total)
                        </span>
                      </small>
                      <div v-if="activeFiltersCount > 0" class="mt-1">
                        <span class="badge bg-info me-1">{{ activeFiltersCount }} filter(s) active</span>
                        <button class="btn btn-link btn-sm p-0" @click="resetAllFilters">
                          Clear all
                        </button>
                      </div>
                    </div>
                    
                    <!-- Pagination Controls -->
                    <div v-if="totalPages > 1" class="pagination-controls">
                      <nav>
                        <ul class="pagination pagination-sm mb-0">
                          <li class="page-item" :class="{ disabled: currentPage === 1 }">
                            <button class="page-link" @click="goToPage(1)" :disabled="currentPage === 1">
                              <i class="fas fa-angle-double-left"></i>
                            </button>
                          </li>
                          <li class="page-item" :class="{ disabled: currentPage === 1 }">
                            <button class="page-link" @click="goToPage(currentPage - 1)" :disabled="currentPage === 1">
                              <i class="fas fa-angle-left"></i>
                            </button>
                          </li>
                          
                          <li v-for="page in visiblePages" :key="page" class="page-item" :class="{ active: page === currentPage }">
                            <button class="page-link" @click="goToPage(page)">{{ page }}</button>
                          </li>
                          
                          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                            <button class="page-link" @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages">
                              <i class="fas fa-angle-right"></i>
                            </button>
                          </li>
                          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                            <button class="page-link" @click="goToPage(totalPages)" :disabled="currentPage === totalPages">
                              <i class="fas fa-angle-double-right"></i>
                            </button>
                          </li>
                        </ul>
                      </nav>
                    </div>
                  </div>
                  
                  <!-- Scrollable Book List -->
                  <div class="book-list-container" style="height: 400px; overflow-y: auto; border: 1px solid #dee2e6; border-radius: 0.375rem;">
                    <table class="table table-striped table-hover mb-0">
                      <thead class="table-dark sticky-top">
                        <tr>
                          <th>Title</th>
                          <th>Author</th>
                          <th>Genre</th>
                          <th>Price</th>
                          <th>Stock</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr v-for="book in paginatedBooks" :key="book.id">
                          <td>
                            <div class="fw-bold">{{ book.title }}</div>
                            <small class="text-muted">ISBN: {{ book.isbn }}</small>
                          </td>
                          <td>{{ book.author }}</td>
                          <td>
                            <span class="badge bg-secondary">{{ book.genre }}</span>
                          </td>
                          <td>
                            <span class="fw-bold text-success">${{ book.price }}</span>
                          </td>
                          <td>
                            <span 
                              :class="{
                                'badge bg-success': book.stockQuantity > 10,
                                'badge bg-warning': book.stockQuantity > 0 && book.stockQuantity <= 10,
                                'badge bg-danger': book.stockQuantity === 0
                              }"
                            >
                              {{ book.stockQuantity }}
                            </span>
                          </td>
                          <td>
                            <div class="btn-group" role="group">
                              <button class="btn btn-sm btn-outline-primary" @click="editBook(book)" title="Edit Book">
                                <i class="fas fa-edit"></i>
                              </button>
                              <button class="btn btn-sm btn-outline-danger" @click="deleteBook(book.id)" title="Delete Book">
                                <i class="fas fa-trash"></i>
                              </button>
                            </div>
                          </td>
                        </tr>
                        <tr v-if="paginatedBooks.length === 0 && filteredBooks.length === 0">
                          <td colspan="6" class="text-center text-muted py-4">
                            <i class="fas fa-search fa-2x mb-2"></i>
                            <div>No books found matching your criteria</div>
                            <small>Try adjusting your search or filters</small>
                          </td>
                        </tr>
                        <tr v-else-if="paginatedBooks.length === 0 && filteredBooks.length > 0">
                          <td colspan="6" class="text-center text-muted py-4">
                            <i class="fas fa-list fa-2x mb-2"></i>
                            <div>No books on this page</div>
                            <small>Navigate to a different page or adjust items per page</small>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Customers Management -->
            <div class="tab-pane fade" id="customers" role="tabpanel">
              <div class="card mt-3">
                <div class="card-header">
                  <h5 class="mb-0">Customers Management</h5>
                </div>
                <div class="card-body">
                  <div class="table-responsive">
                    <table class="table table-striped">
                      <thead>
                        <tr>
                          <th>Name</th>
                          <th>Email</th>
                          <th>Membership</th>
                          <th>Phone</th>
                          <th>Status</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr v-for="customer in customers" :key="customer.id">
                          <td>{{ customer.name }}</td>
                          <td>{{ customer.email }}</td>
                          <td>
                            <span class="badge" :class="getMembershipBadgeClass(customer.membershipLevel)">
                              {{ customer.membershipLevel }}
                            </span>
                          </td>
                          <td>{{ customer.phone || 'N/A' }}</td>
                          <td>
                            <span class="badge" :class="customer.isActive ? 'bg-success' : 'bg-danger'">
                              {{ customer.isActive ? 'Active' : 'Inactive' }}
                            </span>
                          </td>
                          <td>
                            <button class="btn btn-sm btn-outline-primary me-1" @click="editCustomer(customer)">
                              <i class="fas fa-edit"></i>
                            </button>
                            <button 
                              class="btn btn-sm me-1" 
                              :class="customer.isActive ? 'btn-outline-warning' : 'btn-outline-success'"
                              @click="toggleCustomerStatus(customer)"
                            >
                              <i class="fas" :class="customer.isActive ? 'fa-pause' : 'fa-play'"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger" @click="deleteCustomer(customer)">
                              <i class="fas fa-trash"></i>
                            </button>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Orders Management -->
            <div class="tab-pane fade" id="orders" role="tabpanel">
              <div class="card mt-3">
                <div class="card-header">
                  <h5 class="mb-0">Orders Management</h5>
                </div>
                <div class="card-body">
                  <div class="table-responsive">
                    <table class="table table-striped">
                      <thead>
                        <tr>
                          <th>Order ID</th>
                          <th>Customer</th>
                          <th>Date</th>
                          <th>Total</th>
                          <th>Status</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr v-for="order in orders" :key="order.id">
                          <td>#{{ order.id }}</td>
                          <td>{{ order.customerName }}</td>
                          <td>{{ formatDate(order.orderDate) }}</td>
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
                            <button class="btn btn-sm btn-outline-primary me-1" @click="updateOrderStatus(order)">
                              <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger" @click="deleteOrder(order)">
                              <i class="fas fa-trash"></i>
                            </button>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- System Monitoring -->
            <div class="tab-pane fade" id="system" role="tabpanel">
              <div class="row mt-3">
                <!-- System Status Cards -->
                <div class="col-md-6">
                  <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                      <h5 class="mb-0">🔧 System Status</h5>
                      <button class="btn btn-sm btn-outline-primary" @click="refreshSystemStatus">
                        <i class="fas fa-sync-alt"></i> Refresh
                      </button>
                    </div>
                    <div class="card-body">
                      <div v-if="systemStatus">
                        <div class="row mb-3">
                          <div class="col-6">
                            <div class="text-center">
                              <h4 class="text-primary">{{ systemStatus.systemLoad }}%</h4>
                              <small class="text-muted">System Load</small>
                            </div>
                          </div>
                          <div class="col-6">
                            <div class="text-center">
                              <h4 :class="getBottleneckRiskClass(systemStatus.bottleneckRisk.level)">{{ systemStatus.bottleneckRisk.level }}</h4>
                              <small class="text-muted">Bottleneck Risk</small>
                            </div>
                          </div>
                        </div>
                        <div class="row mb-3">
                          <div class="col-6">
                            <div class="text-center">
                              <h4 class="text-success">{{ systemStatus.slaCompliance }}%</h4>
                              <small class="text-muted">SLA Compliance</small>
                            </div>
                          </div>
                          <div class="col-6">
                            <div class="text-center">
                              <h4 class="text-info">{{ systemStatus.throughput }}</h4>
                              <small class="text-muted">Throughput/min</small>
                            </div>
                          </div>
                        </div>
                        <div class="text-center">
                          <p class="mb-1"><strong>Avg Processing Time:</strong> {{ systemStatus.averageProcessingTime }}ms</p>
                          <p class="mb-0"><strong>Active Centers:</strong> {{ systemStatus.processingCenters.filter(c => c.isActive).length }}/{{ systemStatus.processingCenters.length }}</p>
                        </div>
                      </div>
                      <div v-else class="text-center text-muted">
                        <i class="fas fa-spinner fa-spin"></i> Loading system status...
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Processing Centers -->
                <div class="col-md-6">
                  <div class="card">
                    <div class="card-header">
                      <h5 class="mb-0">🏭 Processing Centers</h5>
                    </div>
                    <div class="card-body">
                      <div v-if="systemStatus && systemStatus.processingCenters">
                        <div v-for="center in systemStatus.processingCenters" :key="center.id" class="mb-3">
                          <div class="d-flex justify-content-between align-items-center">
                            <div>
                              <strong>{{ center.id }}</strong>
                              <span :class="center.isActive ? 'badge bg-success ms-2' : 'badge bg-secondary ms-2'">
                                {{ center.isActive ? 'Active' : 'Inactive' }}
                              </span>
                            </div>
                            <div class="text-end">
                              <small class="text-muted">Load: {{ center.currentLoad }}%</small>
                            </div>
                          </div>
                          <div class="progress mt-1" style="height: 6px;">
                            <div class="progress-bar" :class="getLoadProgressClass(center.currentLoad)" 
                                 :style="{ width: center.currentLoad + '%' }"></div>
                          </div>
                        </div>
                      </div>
                      <div v-else class="text-center text-muted">
                        <i class="fas fa-spinner fa-spin"></i> Loading centers...
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- System Optimization -->
              <div class="row mt-3">
                <div class="col-12">
                  <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                      <h5 class="mb-0">⚡ System Optimization</h5>
                      <button class="btn btn-warning" @click="optimizeSystem" :disabled="optimizing">
                        <i class="fas fa-cog" :class="{ 'fa-spin': optimizing }"></i>
                        {{ optimizing ? 'Optimizing...' : 'Optimize System' }}
                      </button>
                    </div>
                    <div class="card-body">
                      <div v-if="optimizationResult" class="alert alert-success">
                        <h6><i class="fas fa-check-circle"></i> Optimization Complete</h6>
                        <p class="mb-1"><strong>Performance Improvement:</strong> {{ optimizationResult.performanceImprovement }}%</p>
                        <p class="mb-1"><strong>Load Balanced:</strong> {{ optimizationResult.loadBalanced ? 'Yes' : 'No' }}</p>
                        <p class="mb-0"><strong>Bottlenecks Resolved:</strong> {{ optimizationResult.bottlenecksResolved }}</p>
                      </div>
                      <div v-else>
                        <p class="text-muted mb-0">Click "Optimize System" to improve performance and resolve bottlenecks.</p>
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
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ordersAPI, analyticsAPI, booksAPI, customersAPI } from '../services/api'

// Reactive data
const stats = reactive({
  totalBooks: 0,
  totalCustomers: 0,
  totalOrders: 0,
  totalRevenue: 0
})

const books = ref([])
const customers = ref([])
const orders = ref([])
const showAddBookModal = ref(false)

// Advanced Book filtering and search
const bookSearchQuery = ref('')
const bookGenreFilter = ref('')
const bookStockFilter = ref('')
const bookSortBy = ref('title')
const availableGenres = ref([])
const filteredBooks = ref([])
const paginatedBooks = ref([])

// Advanced filters
const showAdvancedFilters = ref(false)
const priceRange = ref({ min: 0, max: 100 })
const minPrice = ref(0)
const maxPrice = ref(100)

// Pagination
const currentPage = ref(1)
const itemsPerPage = ref(25)
const totalPages = ref(1)

// Search presets
const searchPresets = ref([])
const currentPreset = ref('')

// System monitoring data
const systemStatus = ref(null)
const optimizing = ref(false)
const optimizationResult = ref(null)



// Utility functions
const getMembershipBadgeClass = (level) => {
  const classes = {
    bronze: 'bg-warning',
    silver: 'bg-secondary',
    gold: 'bg-warning',
    platinum: 'bg-dark'
  }
  return classes[level] || 'bg-primary'
}

const getOrderStatusBadgeClass = (status) => {
  const classes = {
    pending: 'bg-warning',
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
const editBook = (book) => {
  alert(`Edit book: ${book.title}`)
  // In a real app, this would open an edit modal
}

const deleteBook = async (bookId) => {
  // Find the book to get its title for confirmation
  const book = books.value.find(b => b.id === bookId)
  const bookTitle = book ? book.title : 'this book'
  
  if (confirm(`Are you sure you want to delete "${bookTitle}"?\n\nThis action cannot be undone.`)) {
    try {
      // Call the API to delete the book
      await booksAPI.delete(bookId)
      
      // Remove from local array after successful API call
      books.value = books.value.filter(book => book.id !== bookId)
      stats.totalBooks--
      alert(`"${bookTitle}" has been deleted successfully.`)
    } catch (error) {
      console.error('Failed to delete book:', error)
      alert('Failed to delete book. Please try again.')
    }
  }
}

const editCustomer = (customer) => {
  alert(`Edit customer: ${customer.name}`)
  // In a real app, this would open an edit modal
}

const toggleCustomerStatus = (customer) => {
  customer.isActive = !customer.isActive
  alert(`Customer ${customer.name} is now ${customer.isActive ? 'active' : 'inactive'}`)
}

const deleteCustomer = async (customer) => {
  if (confirm(`Are you sure you want to delete customer: ${customer.name}?\n\nThis action cannot be undone.`)) {
    try {
      // Call the API to delete the customer
      await customersAPI.delete(customer.id)
      
      // Remove from local array after successful API call
      customers.value = customers.value.filter(c => c.id !== customer.id)
      stats.totalCustomers--
      alert(`Customer ${customer.name} has been deleted successfully.`)
    } catch (error) {
      console.error('Failed to delete customer:', error)
      alert('Failed to delete customer. Please try again.')
    }
  }
}

const viewOrderDetails = (order) => {
  alert(`View details for order #${order.id}`)
  // In a real app, this would open a details modal
}

const updateOrderStatus = (order) => {
  const statuses = ['pending', 'processing', 'shipped', 'completed', 'cancelled']
  const currentIndex = statuses.indexOf(order.status)
  const nextIndex = (currentIndex + 1) % statuses.length
  order.status = statuses[nextIndex]
  alert(`Order #${order.id} status updated to: ${order.status}`)
}

const deleteOrder = async (order) => {
  if (confirm(`Are you sure you want to cancel order #${order.id}?\n\nCustomer: ${order.customerName}\nTotal: $${order.totalAmount}\n\nThis will cancel the order and cannot be undone.`)) {
    try {
      // Call the API to cancel the order (which is the proper way to "delete" orders)
      await ordersAPI.cancel(order.id)
      
      // Update order status in local array after successful API call
      const orderIndex = orders.value.findIndex(o => o.id === order.id)
      if (orderIndex !== -1) {
        orders.value[orderIndex].status = 'cancelled'
      }
      alert(`Order #${order.id} has been cancelled successfully.`)
    } catch (error) {
      console.error('Failed to cancel order:', error)
      alert('Failed to cancel order. Please try again.')
    }
  }
}

// System monitoring functions
const refreshSystemStatus = async () => {
  try {
    const response = await ordersAPI.getSystemStatus()
    systemStatus.value = response.data.data
  } catch (error) {
    console.error('Failed to fetch system status:', error)
    systemStatus.value = null
  }
}

const optimizeSystem = async () => {
  optimizing.value = true
  optimizationResult.value = null
  
  try {
    const response = await ordersAPI.optimizeSystem()
    optimizationResult.value = response.data.data
    // Refresh system status after optimization
    await refreshSystemStatus()
  } catch (error) {
    console.error('Failed to optimize system:', error)
    optimizationResult.value = null
  } finally {
    optimizing.value = false
  }
}

const getBottleneckRiskClass = (level) => {
  switch (level) {
    case 'low': return 'text-success'
    case 'medium': return 'text-warning'
    case 'high': return 'text-danger'
    default: return 'text-muted'
  }
}

const getLoadProgressClass = (load) => {
  if (load < 50) return 'bg-success'
  if (load < 80) return 'bg-warning'
  return 'bg-danger'
}

// Load real data from API calls
const loadRealData = async () => {
  try {
    console.log('Loading real data from API...')
    
    // Fetch dashboard statistics
    const dashboardResponse = await analyticsAPI.getDashboard(30)
    console.log('Dashboard response:', dashboardResponse)
    if (dashboardResponse.data) {
      // Extract stats directly from the response data
      const data = dashboardResponse.data.data || dashboardResponse.data
      stats.totalBooks = data.totalBooks || 0
      stats.totalCustomers = data.totalCustomers || 0
      stats.totalOrders = data.totalOrders || 0
      stats.totalRevenue = data.totalRevenue || 0
    }
    
    // Fetch books data
    const booksResponse = await booksAPI.getAll()
    console.log('Books response:', booksResponse)
    if (booksResponse.data) {
      // Extract books array from the response
      const data = booksResponse.data.data || booksResponse.data
      books.value = data.books || []
    }
    
    // Fetch customers data
    const customersResponse = await customersAPI.getAll()
    console.log('Customers response:', customersResponse)
    if (customersResponse.data) {
      // Extract customers array from the response
      const data = customersResponse.data.data || customersResponse.data
      customers.value = data.customers || []
    }
    
    // Fetch orders data
    const ordersResponse = await ordersAPI.getAll()
    console.log('Orders response:', ordersResponse)
    if (ordersResponse.data) {
      // Extract orders array from the response
      const data = ordersResponse.data.data || ordersResponse.data
      orders.value = data.orders || []
    }
    
    console.log('Successfully loaded real data from API')
    
    // Initialize book filtering
    initializeBookFiltering()
  } catch (error) {
    console.error('Failed to load real data:', error)
    console.error('Failed to load data from API. Please check your backend connection.')
  }
}

// Advanced Book search and filtering functions
const initializeBookFiltering = () => {
  // Extract unique genres
  const genres = [...new Set(books.value.map(book => book.genre).filter(Boolean))]
  availableGenres.value = genres.sort()
  
  // Set price range based on actual book prices
  if (books.value.length > 0) {
    const prices = books.value.map(book => parseFloat(book.price)).filter(price => !isNaN(price))
    minPrice.value = Math.floor(Math.min(...prices))
    maxPrice.value = Math.ceil(Math.max(...prices))
    priceRange.value = { min: minPrice.value, max: maxPrice.value }
  }
  
  // Initialize filtered books
  filteredBooks.value = [...books.value]
  updatePagination()
}

const searchBooks = () => {
  filterBooks()
}

const filterBooks = () => {
  let filtered = [...books.value]
  
  // Apply search query filter
  if (bookSearchQuery.value.trim()) {
    const query = bookSearchQuery.value.toLowerCase().trim()
    filtered = filtered.filter(book => 
      book.title.toLowerCase().includes(query) ||
      book.author.toLowerCase().includes(query) ||
      book.genre.toLowerCase().includes(query) ||
      (book.isbn && book.isbn.toLowerCase().includes(query))
    )
  }
  
  // Apply genre filter
  if (bookGenreFilter.value) {
    filtered = filtered.filter(book => book.genre === bookGenreFilter.value)
  }
  
  // Apply stock filter
  if (bookStockFilter.value) {
    switch (bookStockFilter.value) {
      case 'inStock':
        filtered = filtered.filter(book => book.stockQuantity > 0)
        break
      case 'outOfStock':
        filtered = filtered.filter(book => book.stockQuantity === 0)
        break
      case 'lowStock':
        filtered = filtered.filter(book => book.stockQuantity > 0 && book.stockQuantity < 10)
        break
    }
  }
  
  // Apply price range filter
  filtered = filtered.filter(book => {
    const price = parseFloat(book.price)
    return price >= priceRange.value.min && price <= priceRange.value.max
  })
  
  filteredBooks.value = filtered
  sortBooks()
  updatePagination()
}

const sortBooks = () => {
  const sorted = [...filteredBooks.value]
  
  switch (bookSortBy.value) {
    case 'title':
      sorted.sort((a, b) => a.title.localeCompare(b.title))
      break
    case 'title-desc':
      sorted.sort((a, b) => b.title.localeCompare(a.title))
      break
    case 'author':
      sorted.sort((a, b) => a.author.localeCompare(b.author))
      break
    case 'author-desc':
      sorted.sort((a, b) => b.author.localeCompare(a.author))
      break
    case 'price':
      sorted.sort((a, b) => parseFloat(a.price) - parseFloat(b.price))
      break
    case 'price-desc':
      sorted.sort((a, b) => parseFloat(b.price) - parseFloat(a.price))
      break
    case 'stock':
      sorted.sort((a, b) => a.stockQuantity - b.stockQuantity)
      break
    case 'stock-desc':
      sorted.sort((a, b) => b.stockQuantity - a.stockQuantity)
      break
  }
  
  filteredBooks.value = sorted
  updatePagination()
}

const clearBookSearch = () => {
  bookSearchQuery.value = ''
  resetAllFilters()
}

const resetAllFilters = () => {
  bookSearchQuery.value = ''
  bookGenreFilter.value = ''
  bookStockFilter.value = ''
  bookSortBy.value = 'title'
  priceRange.value = { min: minPrice.value, max: maxPrice.value }
  filteredBooks.value = [...books.value]
  currentPage.value = 1
  updatePagination()
}

const toggleAdvancedFilters = () => {
  showAdvancedFilters.value = !showAdvancedFilters.value
}

// Pagination functions
const updatePagination = () => {
  if (itemsPerPage.value === 'all') {
    totalPages.value = 1
    paginatedBooks.value = filteredBooks.value
  } else {
    const itemsPerPageNum = parseInt(itemsPerPage.value)
    totalPages.value = Math.ceil(filteredBooks.value.length / itemsPerPageNum)
    
    // Ensure current page is valid
    if (currentPage.value > totalPages.value) {
      currentPage.value = Math.max(1, totalPages.value)
    }
    
    const startIndex = (currentPage.value - 1) * itemsPerPageNum
    const endIndex = startIndex + itemsPerPageNum
    paginatedBooks.value = filteredBooks.value.slice(startIndex, endIndex)
  }
}

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    updatePagination()
  }
}

// Computed properties for pagination
const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2))
  let end = Math.min(totalPages.value, start + maxVisible - 1)
  
  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1)
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

const activeFiltersCount = computed(() => {
  let count = 0
  if (bookSearchQuery.value.trim()) count++
  if (bookGenreFilter.value) count++
  if (bookStockFilter.value) count++
  if (priceRange.value.min !== minPrice.value || priceRange.value.max !== maxPrice.value) count++
  return count
})

// Export and preset functions
const exportFilteredBooks = () => {
  const csvContent = "data:text/csv;charset=utf-8," + 
    "Title,Author,Genre,Price,Stock\n" +
    filteredBooks.value.map(book => 
      `"${book.title}","${book.author}","${book.genre}",${book.price},${book.stockQuantity}`
    ).join("\n")
  
  const encodedUri = encodeURI(csvContent)
  const link = document.createElement("a")
  link.setAttribute("href", encodedUri)
  link.setAttribute("download", "filtered_books.csv")
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

const saveSearchPreset = () => {
  const presetName = prompt('Enter a name for this search preset:')
  if (presetName) {
    const preset = {
      name: presetName,
      searchQuery: bookSearchQuery.value,
      genreFilter: bookGenreFilter.value,
      stockFilter: bookStockFilter.value,
      sortBy: bookSortBy.value,
      priceRange: { ...priceRange.value }
    }
    searchPresets.value.push(preset)
    alert(`Search preset "${presetName}" saved!`)
  }
}

// Watchers for reactive filtering
watch([bookSearchQuery, bookGenreFilter, bookStockFilter, bookSortBy], () => {
  filterBooks()
})

watch(itemsPerPage, () => {
  currentPage.value = 1
  updatePagination()
})

// Update the onMounted lifecycle
onMounted(() => {
  loadRealData() // Load real data on component mount
  refreshSystemStatus()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px 0;
}

.card {
  border: none;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  border-radius: 8px;
}

.nav-tabs .nav-link {
  border: none;
  color: #666;
  font-weight: 500;
}

.nav-tabs .nav-link.active {
  background-color: #fff;
  border-bottom: 3px solid #007bff;
  color: #007bff;
}

.table th {
  border-top: none;
  font-weight: 600;
  color: #333;
}

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}

.card-header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
}

.admin-header {
  background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
  color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.admin-badge {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 0.9rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.admin-badge i {
  margin-right: 5px;
  color: #ffd700;
}

.fas {
  width: 16px;
  text-align: center;
}

/* Fix icon alignment in statistics cards */
.card .fas.fa-2x {
  width: auto;
  text-align: center;
}

/* Advanced Search Styles */
.advanced-search-container {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e9ecef;
}

.advanced-filters-panel {
  background: white;
  border-radius: 6px;
  padding: 20px;
  margin-top: 15px;
  border: 1px solid #dee2e6;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.form-label {
  font-weight: 600;
  color: #495057;
  margin-bottom: 5px;
}

.form-range {
  height: 6px;
}

.form-range::-webkit-slider-thumb {
  background: #007bff;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.pagination-controls .page-link {
  border: none;
  color: #6c757d;
  padding: 0.375rem 0.75rem;
}

.pagination-controls .page-item.active .page-link {
  background-color: #007bff;
  border-color: #007bff;
  color: white;
}

.pagination-controls .page-link:hover {
  background-color: #e9ecef;
  color: #495057;
}

.book-list-container {
  border: 2px solid #dee2e6;
  border-radius: 8px;
  background: white;
}

.book-list-container .table {
  margin-bottom: 0;
}

.book-list-container .table thead th {
  position: sticky;
  top: 0;
  z-index: 10;
  background: #343a40;
  color: white;
  border: none;
}

.book-list-container .table tbody tr:hover {
  background-color: #f8f9fa;
}

.btn-group .btn {
  border-radius: 0;
}

.btn-group .btn:first-child {
  border-top-left-radius: 0.375rem;
  border-bottom-left-radius: 0.375rem;
}

.btn-group .btn:last-child {
  border-top-right-radius: 0.375rem;
  border-bottom-right-radius: 0.375rem;
}

.input-group-text {
  background-color: #e9ecef;
  border-color: #ced4da;
  color: #6c757d;
}

/* Animation for advanced filters */
.advanced-filters-panel {
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .advanced-search-container {
    padding: 15px;
  }
  
  .advanced-filters-panel {
    padding: 15px;
  }
  
  .pagination-controls {
    margin-top: 10px;
  }
  
  .book-list-container {
    height: 300px !important;
  }
}
</style>