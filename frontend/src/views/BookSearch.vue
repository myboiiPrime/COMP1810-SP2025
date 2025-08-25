<template>
  <div class="book-search">
    <div class="container-fluid">
      <!-- Search Header -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="search-header">
            <h1 class="mb-3">📚 Browse Books</h1>
            
            <!-- Advanced Search System -->
            <div class="advanced-search-container">
              <!-- Primary Search Bar -->
              <div class="row mb-3">
                <div class="col-md-8">
                  <div class="input-group">
                    <span class="input-group-text"><i class="fas fa-search"></i></span>
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="Search books by title, author, genre, or ISBN..."
                      v-model="searchQuery"
                      @input="performSearch"
                    >
                    <button class="btn btn-outline-secondary" type="button" @click="clearSearch">
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
                    <select class="form-select" v-model="selectedGenre" @change="filterBooks">
                      <option value="">All Genres</option>
                      <option v-for="genre in availableGenres" :key="genre" :value="genre">{{ genre }}</option>
                    </select>
                  </div>
                  <div class="col-md-3">
                    <label class="form-label">Stock Status</label>
                    <select class="form-select" v-model="stockFilter" @change="filterBooks">
                      <option value="">All Stock Status</option>
                      <option value="inStock">In Stock</option>
                      <option value="outOfStock">Out of Stock</option>
                      <option value="lowStock">Low Stock (< 10)</option>
                    </select>
                  </div>
                  <div class="col-md-3">
                    <label class="form-label">Sort By</label>
                    <select class="form-select" v-model="sortBy" @change="sortBooks">
                      <option value="title">Title (A-Z)</option>
                      <option value="title-desc">Title (Z-A)</option>
                      <option value="author">Author (A-Z)</option>
                      <option value="author-desc">Author (Z-A)</option>
                      <option value="price-low">Price (Low to High)</option>
                      <option value="price-high">Price (High to Low)</option>
                      <option value="rating">Rating (High to Low)</option>
                      <option value="year">Year (Newest First)</option>
                    </select>
                  </div>
                  <div class="col-md-3">
                    <label class="form-label">Items per Page</label>
                    <select class="form-select" v-model="itemsPerPage" @change="updatePagination">
                      <option value="12">12 items</option>
                      <option value="24">24 items</option>
                      <option value="48">48 items</option>
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
                      <button class="btn btn-outline-info btn-sm" @click="exportBooks">
                        <i class="fas fa-download"></i> Export
                      </button>
                      <button class="btn btn-outline-warning btn-sm" @click="resetAllFilters">
                        <i class="fas fa-undo"></i> Reset
                      </button>
                      <button class="btn btn-outline-success btn-sm" @click="savePreset">
                        <i class="fas fa-save"></i> Save Preset
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Results Summary and Pagination -->
      <div class="row mb-3">
        <div class="col-md-6">
          <div class="results-summary">
            <h6 class="mb-1">Search Results</h6>
            <p class="text-muted mb-0">
              Showing {{ startIndex + 1 }}-{{ Math.min(endIndex, filteredBooks.length) }} of {{ filteredBooks.length }} books
              <span v-if="searchQuery"> for "{{ searchQuery }}"</span>
              <span v-if="selectedGenre"> in {{ selectedGenre }}</span>
              <span v-if="stockFilter"> ({{ getStockFilterLabel() }})</span>
            </p>
          </div>
        </div>
        <div class="col-md-6">
          <nav aria-label="Book pagination" v-if="totalPages > 1">
            <ul class="pagination pagination-sm justify-content-end mb-0">
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
              <li 
                v-for="page in visiblePages" 
                :key="page" 
                class="page-item" 
                :class="{ active: page === currentPage }"
              >
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
      
      <!-- View Toggle -->
      <div class="row mb-3">
        <div class="col-12">
          <div class="d-flex justify-content-end">
            <div class="view-toggle">
              <button 
                class="btn btn-sm" 
                :class="viewMode === 'grid' ? 'btn-primary' : 'btn-outline-primary'"
                @click="viewMode = 'grid'"
              >
                <i class="fas fa-th"></i>
              </button>
              <button 
                class="btn btn-sm ms-1" 
                :class="viewMode === 'list' ? 'btn-primary' : 'btn-outline-primary'"
                @click="viewMode = 'list'"
              >
                <i class="fas fa-list"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Books Grid View -->
      <div v-if="viewMode === 'grid'" class="row">
        <div class="col-lg-3 col-md-4 col-sm-6 mb-4" v-for="book in paginatedBooks" :key="book.id">
          <div class="card book-card h-100">
            <div class="card-body d-flex flex-column">
              <div class="book-cover mb-3">
                <i class="fas fa-book fa-3x text-primary"></i>
              </div>
              <h6 class="card-title">{{ book.title }}</h6>
              <p class="card-text text-muted small">by {{ book.author }}</p>
              <div class="book-details mb-3">
                <span class="badge bg-secondary mb-1">{{ book.genre }}</span>
                <div class="rating mb-1">
                  <span v-for="star in 5" :key="star" class="star">
                    <i class="fas fa-star" :class="star <= book.rating ? 'text-warning' : 'text-muted'"></i>
                  </span>
                  <span class="ms-1 small text-muted">({{ book.rating }})</span>
                </div>
                <p class="small text-muted mb-1">Published: {{ book.publishedYear }}</p>
                <p class="small text-muted mb-1">ISBN: {{ book.isbn }}</p>
              </div>
              <div class="mt-auto">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <span class="h5 mb-0 text-primary">${{ book.price }}</span>
                  <span class="small" :class="book.stockQuantity > 0 ? 'text-success' : 'text-danger'">
                    {{ book.stockQuantity > 0 ? `${book.stockQuantity} in stock` : 'Out of stock' }}
                  </span>
                </div>
                <button 
                  class="btn btn-primary w-100" 
                  :disabled="book.stockQuantity === 0 || isInCart(book.id)"
                  @click="addToCart(book)"
                >
                  <i class="fas" :class="isInCart(book.id) ? 'fa-check' : 'fa-cart-plus'"></i>
                  {{ isInCart(book.id) ? 'In Cart' : 'Add to Cart' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Books List View -->
      <div v-if="viewMode === 'list'" class="row">
        <div class="col-12">
          <div class="card">
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-hover">
                  <thead>
                    <tr>
                      <th>Book</th>
                      <th>Author</th>
                      <th>Genre</th>
                      <th>Year</th>
                      <th>Rating</th>
                      <th>Price</th>
                      <th>Stock</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="book in paginatedBooks" :key="book.id">
                      <td>
                        <div>
                          <strong>{{ book.title }}</strong>
                          <br>
                          <small class="text-muted">ISBN: {{ book.isbn }}</small>
                        </div>
                      </td>
                      <td>{{ book.author }}</td>
                      <td>
                        <span class="badge bg-secondary">{{ book.genre }}</span>
                      </td>
                      <td>{{ book.publishedYear }}</td>
                      <td>
                        <div class="rating">
                          <span v-for="star in 5" :key="star" class="star">
                            <i class="fas fa-star" :class="star <= book.rating ? 'text-warning' : 'text-muted'"></i>
                          </span>
                          <span class="ms-1 small">({{ book.rating }})</span>
                        </div>
                      </td>
                      <td class="text-primary fw-bold">${{ book.price }}</td>
                      <td>
                        <span :class="book.stockQuantity > 0 ? 'text-success' : 'text-danger'">
                          {{ book.stockQuantity > 0 ? book.stockQuantity : 'Out of stock' }}
                        </span>
                      </td>
                      <td>
                        <button 
                          class="btn btn-sm btn-primary" 
                          :disabled="book.stockQuantity === 0 || isInCart(book.id)"
                          @click="addToCart(book)"
                        >
                          <i class="fas" :class="isInCart(book.id) ? 'fa-check' : 'fa-cart-plus'"></i>
                          {{ isInCart(book.id) ? 'In Cart' : 'Add' }}
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
      
      <!-- No Results -->
      <div v-if="filteredBooks.length === 0" class="row">
        <div class="col-12">
          <div class="text-center py-5">
            <i class="fas fa-search fa-3x text-muted mb-3"></i>
            <h4 class="text-muted">No books found</h4>
            <p class="text-muted">Try adjusting your search criteria or browse all books.</p>
            <button class="btn btn-primary" @click="clearFilters">Clear Filters</button>
          </div>
        </div>
      </div>
      
      <!-- Shopping Cart Summary -->
      <div v-if="cart.length > 0" class="cart-summary">
        <div class="card">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h6 class="mb-0">🛒 Cart ({{ cart.length }} items)</h6>
                <small class="text-muted">Total: ${{ cartTotal }}</small>
              </div>
              <div>
                <button class="btn btn-outline-primary btn-sm me-2" @click="showCart = true">
                  View Cart
                </button>
                <button class="btn btn-success btn-sm" @click="checkout">
                  Checkout
                </button>
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
import { booksAPI, apiUtils } from '../services/api'

const router = useRouter()

// Reactive data
const searchQuery = ref('')
const selectedGenre = ref('')
const stockFilter = ref('')
const sortBy = ref('title')
const viewMode = ref('grid')
const showCart = ref(false)
const isLoading = ref(false)

// Advanced filters
const showAdvancedFilters = ref(false)
const priceRange = ref({ min: 0, max: 100 })
const minPrice = ref(0)
const maxPrice = ref(100)

// Pagination
const currentPage = ref(1)
const itemsPerPage = ref(12)

const allBooks = ref([])
const cart = ref([])

// Available options
const availableGenres = ref(['Fiction', 'Non-Fiction', 'Romance', 'Mystery', 'Sci-Fi', 'Fantasy', 'Biography', 'History', 'Self-Help', 'Technology'])

// Load books data
const loadBooks = async () => {
  isLoading.value = true
  try {
    let response
    
    // Try real API
    try {
      const apiResponse = await booksAPI.getAll({
        page: 1,
        limit: 50,
        sortBy: 'title',
        sortOrder: 'asc'
      })
      response = apiUtils.formatResponse(apiResponse)
      // Extract books array from the response
      allBooks.value = response.data?.books || []
    } catch (apiError) {
      console.error('Failed to load books from API:', apiError.message)
      allBooks.value = []
    }
    
    // Set price range based on actual book prices
    if (allBooks.value.length > 0) {
      const prices = allBooks.value.map(book => book.price || 0)
      minPrice.value = Math.floor(Math.min(...prices))
      maxPrice.value = Math.ceil(Math.max(...prices))
      priceRange.value = { min: minPrice.value, max: maxPrice.value }
    }
    
  } catch (error) {
    console.error('Error loading books:', error)
    allBooks.value = []
  } finally {
    isLoading.value = false
  }
}

// Computed properties
const filteredBooks = computed(() => {
  let books = allBooks.value
  
  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    books = books.filter(book => 
      book.title.toLowerCase().includes(query) ||
      book.author.toLowerCase().includes(query) ||
      book.genre?.toLowerCase().includes(query) ||
      book.isbn?.toLowerCase().includes(query)
    )
  }
  
  // Filter by genre
  if (selectedGenre.value) {
    books = books.filter(book => book.genre === selectedGenre.value)
  }
  
  // Filter by stock status
  if (stockFilter.value) {
    books = books.filter(book => {
      const stock = book.stockQuantity || 0
      switch (stockFilter.value) {
        case 'inStock': return stock > 0
        case 'outOfStock': return stock === 0
        case 'lowStock': return stock > 0 && stock < 10
        default: return true
      }
    })
  }
  
  // Filter by price range
  books = books.filter(book => {
    const price = book.price || 0
    return price >= priceRange.value.min && price <= priceRange.value.max
  })
  
  // Sort books
  books = [...books].sort((a, b) => {
    switch (sortBy.value) {
      case 'title':
        return a.title.localeCompare(b.title)
      case 'title-desc':
        return b.title.localeCompare(a.title)
      case 'author':
        return a.author.localeCompare(b.author)
      case 'author-desc':
        return b.author.localeCompare(a.author)
      case 'price-low':
        return a.price - b.price
      case 'price-high':
        return b.price - a.price
      case 'rating':
        return (b.rating || 0) - (a.rating || 0)
      case 'year':
        return (b.publishedYear || 0) - (a.publishedYear || 0)
      default:
        return 0
    }
  })
  
  return books
})

// Paginated books for display
const paginatedBooks = computed(() => {
  if (itemsPerPage.value === 'all') {
    return filteredBooks.value
  }
  return filteredBooks.value.slice(startIndex.value, endIndex.value)
})

const cartTotal = computed(() => {
  return cart.value.reduce((total, item) => total + item.price, 0).toFixed(2)
})

// Pagination computed properties
const totalPages = computed(() => {
  if (itemsPerPage.value === 'all') return 1
  return Math.ceil(filteredBooks.value.length / parseInt(itemsPerPage.value))
})

const startIndex = computed(() => {
  if (itemsPerPage.value === 'all') return 0
  return (currentPage.value - 1) * parseInt(itemsPerPage.value)
})

const endIndex = computed(() => {
  if (itemsPerPage.value === 'all') return filteredBooks.value.length
  return startIndex.value + parseInt(itemsPerPage.value)
})

const visiblePages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  
  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) pages.push(i)
      pages.push('...', total)
    } else if (current >= total - 3) {
      pages.push(1, '...')
      for (let i = total - 4; i <= total; i++) pages.push(i)
    } else {
      pages.push(1, '...', current - 1, current, current + 1, '...', total)
    }
  }
  
  return pages.filter(p => p !== '...' || pages.indexOf(p) === pages.lastIndexOf(p))
})

// Methods
const performSearch = async () => {
  if (!searchQuery.value.trim()) {
    loadBooks() // Reload all books if search is empty
    return
  }
  
  isLoading.value = true
  try {
    let response
    
    // Try real API search first
    try {
      const apiResponse = await booksAPI.search(searchQuery.value, {
        genre: selectedGenre.value !== '' ? selectedGenre.value : undefined
      })
      response = apiUtils.formatResponse(apiResponse)
      // Extract books array from the search response
      allBooks.value = response.data?.books || []
    } catch (apiError) {
      console.error('Failed to search books from API:', apiError.message)
      allBooks.value = []
    }
    
  } catch (error) {
    console.error('Search error:', error)
  } finally {
    isLoading.value = false
  }
}

const filterBooks = () => {
  // Filtering is reactive through computed property
}

const sortBooks = () => {
  // Sorting is reactive through computed property
}

const clearFilters = () => {
  searchQuery.value = ''
  selectedGenre.value = ''
  sortBy.value = 'title'
}

const isInCart = (bookId) => {
  return cart.value.some(item => item.id === bookId)
}

const addToCart = (book) => {
  if (!isInCart(book.id) && book.stockQuantity > 0) {
    cart.value.push({ ...book, quantity: 1 })
    // Decrease stock quantity
    const bookIndex = allBooks.value.findIndex(b => b.id === book.id)
    if (bookIndex !== -1) {
      allBooks.value[bookIndex].stockQuantity--
    }
    alert(`"${book.title}" has been added to your cart!`)
  }
}

const checkout = () => {
  if (cart.value.length > 0) {
    // Save cart to localStorage for checkout page
    localStorage.setItem('bookstore_cart', JSON.stringify(cart.value))
    // Navigate to checkout page
    router.push('/checkout')
  } else {
    alert('Your cart is empty. Please add some books before checkout.')
  }
}

// Advanced filter methods
const toggleAdvancedFilters = () => {
  showAdvancedFilters.value = !showAdvancedFilters.value
}

const clearSearch = () => {
  searchQuery.value = ''
  loadBooks()
}

const resetAllFilters = () => {
  searchQuery.value = ''
  selectedGenre.value = ''
  stockFilter.value = ''
  sortBy.value = 'title'
  priceRange.value = { min: minPrice.value, max: maxPrice.value }
  currentPage.value = 1
  loadBooks()
}

const updatePagination = () => {
  currentPage.value = 1
}

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const getStockFilterLabel = () => {
  switch (stockFilter.value) {
    case 'inStock': return 'In Stock'
    case 'outOfStock': return 'Out of Stock'
    case 'lowStock': return 'Low Stock'
    default: return ''
  }
}

const exportBooks = () => {
  const dataStr = JSON.stringify(filteredBooks.value, null, 2)
  const dataBlob = new Blob([dataStr], { type: 'application/json' })
  const url = URL.createObjectURL(dataBlob)
  const link = document.createElement('a')
  link.href = url
  link.download = 'books-export.json'
  link.click()
  URL.revokeObjectURL(url)
}

const savePreset = () => {
  const preset = {
    selectedGenre: selectedGenre.value,
    stockFilter: stockFilter.value,
    sortBy: sortBy.value,
    priceRange: priceRange.value,
    itemsPerPage: itemsPerPage.value
  }
  localStorage.setItem('bookSearchPreset', JSON.stringify(preset))
  alert('Search preset saved!')
}

const loadPreset = () => {
  const saved = localStorage.getItem('bookSearchPreset')
  if (saved) {
    const preset = JSON.parse(saved)
    selectedGenre.value = preset.selectedGenre || ''
    stockFilter.value = preset.stockFilter || ''
    sortBy.value = preset.sortBy || 'title'
    priceRange.value = preset.priceRange || { min: minPrice.value, max: maxPrice.value }
    itemsPerPage.value = preset.itemsPerPage || 12
  }
}

// Lifecycle
onMounted(() => {
  loadPreset()
  loadBooks()
})
</script>

<style scoped>
.book-search {
  padding: 20px 0;
}

.search-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 15px;
  margin-bottom: 20px;
}

.advanced-search-container {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  padding: 1.5rem;
}

/* Keep Advanced Filters button in hovered state permanently */
.advanced-search-container .btn-outline-primary {
  background-color: #007bff;
  border-color: #007bff;
  color: white;
}

.advanced-search-container .btn-outline-primary:hover,
.advanced-search-container .btn-outline-primary:focus,
.advanced-search-container .btn-outline-primary:active {
  background-color: #0056b3;
  border-color: #0056b3;
  color: white;
}

.advanced-filters-panel {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  padding: 1.5rem;
  margin-top: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.advanced-filters-panel .form-label {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.advanced-filters-panel .form-select,
.advanced-filters-panel .form-range {
  background-color: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.advanced-filters-panel .form-select:focus,
.advanced-filters-panel .form-range:focus {
  background-color: white;
  border-color: #80bdff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

.results-summary h6 {
  color: #495057;
  font-weight: 600;
}

.pagination .page-link {
  border-radius: 6px;
  margin: 0 2px;
  border: 1px solid #dee2e6;
}

.pagination .page-item.active .page-link {
  background-color: #007bff;
  border-color: #007bff;
}

.book-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.book-cover {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 8px;
}

.rating .star {
  font-size: 0.9rem;
}

.cart-summary {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
  min-width: 300px;
}

.view-toggle .btn {
  border-radius: 6px;
}

.form-control, .form-select {
  border-radius: 8px;
  border: 1px solid #ddd;
}

.form-control:focus, .form-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
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
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: #f8f9fa;
}

.table-responsive {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.badge {
  font-size: 0.75rem;
  padding: 0.375rem 0.75rem;
}

/* Advanced filter styling */
.btn-group .btn {
  border-radius: 4px;
}

.form-range {
  height: 1.5rem;
}

.form-range::-webkit-slider-thumb {
  background-color: #007bff;
}

.form-range::-moz-range-thumb {
  background-color: #007bff;
  border: none;
}

/* Responsive design */
@media (max-width: 768px) {
  .cart-summary {
    position: relative;
    bottom: auto;
    right: auto;
    margin-top: 20px;
    min-width: auto;
  }
  
  .search-header {
    padding: 1rem;
  }
  
  .advanced-search-container {
    padding: 1rem;
  }
  
  .advanced-filters-panel {
    padding: 1rem;
  }
  
  .pagination {
    justify-content: center;
  }
  
  .results-summary {
    text-align: center;
    margin-bottom: 1rem;
  }
}

@media (max-width: 576px) {
  .advanced-filters-panel .row .col-md-3,
  .advanced-filters-panel .row .col-md-6 {
    margin-bottom: 1rem;
  }
}
</style>