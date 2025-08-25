import axios from 'axios';

// Base API configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('userToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('userToken');
      localStorage.removeItem('userRole');
      localStorage.removeItem('userId');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Authentication API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  getProfile: () => api.get('/auth/profile'),
  refreshToken: () => api.post('/auth/refresh'),
  logout: () => api.post('/auth/logout')
};

// Books API
export const booksAPI = {
  // Public endpoints
  getAll: (params = {}) => api.get('/books', { params }),
  getById: (id) => api.get(`/books/${id}`),
  getByISBN: (isbn) => api.get(`/books/isbn/${isbn}`),
  search: (query, filters = {}) => api.get('/books/search', { 
    params: { q: query, ...filters } 
  }),
  getCategories: () => api.get('/books/categories'),
  getAuthors: () => api.get('/books/authors'),
  
  // Admin endpoints
  create: (bookData) => api.post('/books', bookData),
  update: (id, bookData) => api.put(`/books/${id}`, bookData),
  delete: (id) => api.delete(`/books/${id}`),
  toggleStock: (id) => api.patch(`/books/${id}/toggle-stock`)
};

// Orders API
export const ordersAPI = {
  // Customer endpoints
  getMyOrders: (params = {}) => api.get('/orders/my-orders', { params }),
  getById: (id) => api.get(`/orders/${id}`),
  create: (orderData) => api.post('/orders', orderData),
  cancel: (id) => api.patch(`/orders/${id}/cancel`),
  
  // Admin endpoints
  getAll: (params = {}) => api.get('/orders', { params }),
  updateStatus: (id, status) => api.patch(`/orders/${id}/status`, { status }),
  getStats: (period = '30') => api.get('/orders/stats', { params: { period } }),
  
  // Advanced system management
  getSystemStatus: () => api.get('/orders/system-status'),
  optimizeSystem: () => api.post('/orders/optimize')
};

// Customers API
export const customersAPI = {
  // Profile management
  getProfile: (id) => api.get(`/customers/${id}`),
  updateProfile: (id, data) => api.put(`/customers/${id}`, data),
  getOrders: (id, params = {}) => api.get(`/customers/${id}/orders`, { params }),
  getRecommendations: (id) => api.get(`/customers/${id}/recommendations`),
  
  // Admin endpoints
  getAll: (params = {}) => api.get('/customers', { params }),
  toggleStatus: (id) => api.patch(`/customers/${id}/toggle-status`),
  delete: (id) => api.delete(`/customers/${id}`),
  getStats: (period = '30') => api.get('/customers/stats/summary', { params: { period } })
};

// Analytics API
export const analyticsAPI = {
  getDashboard: (period = '30') => api.get('/analytics/dashboard', { params: { period } }),
  getSales: (period = '30', groupBy = 'day') => api.get('/analytics/sales', { 
    params: { period, groupBy } 
  }),
  getCustomers: (period = '30') => api.get('/analytics/customers', { params: { period } }),
  getInventory: () => api.get('/analytics/inventory'),
  getAlgorithms: () => api.get('/analytics/algorithms'),
  exportData: (type, period = '30', format = 'json') => api.get(`/analytics/export/${type}`, {
    params: { period, format },
    responseType: format === 'csv' ? 'blob' : 'json'
  })
};

// Data Structures and Algorithms API
export const dataStructuresAPI = {
  // Stack operations
  stackPush: (value) => api.post('/data-structures/stack/push', { value }),
  stackPop: () => api.post('/data-structures/stack/pop'),
  stackPeek: () => api.get('/data-structures/stack/peek'),
  
  // Queue operations
  queueEnqueue: (value) => api.post('/data-structures/queue/enqueue', { value }),
  queueDequeue: () => api.post('/data-structures/queue/dequeue'),
  
  // Circular Queue operations
  circularQueueEnqueue: (value) => api.post('/data-structures/circular-queue/enqueue', { value }),
  circularQueueDequeue: () => api.post('/data-structures/circular-queue/dequeue'),
  
  // Deque operations
  dequeAddFront: (value) => api.post('/data-structures/deque/add-front', { value }),
  dequeAddRear: (value) => api.post('/data-structures/deque/add-rear', { value }),
  dequeRemoveFront: () => api.post('/data-structures/deque/remove-front'),
  dequeRemoveRear: () => api.post('/data-structures/deque/remove-rear'),
  
  // Priority Queue operations
  priorityQueueAdd: (value, priority) => api.post('/data-structures/priority-queue/add', { value, priority }),
  priorityQueuePoll: () => api.post('/data-structures/priority-queue/poll'),
  
  // Hash Search operations
  hashSearchPut: (key, value) => api.post('/data-structures/hash-search/put', { key, value }),
  hashSearchGet: (key) => api.get(`/data-structures/hash-search/get/${key}`),
  hashSearchRemove: (key) => api.delete(`/data-structures/hash-search/remove/${key}`)
};

// Algorithms API
export const algorithmsAPI = {
  // Search algorithms
  linearSearch: (array, target) => api.post('/algorithms/search/linear', { array, target }),
  binarySearch: (array, target) => api.post('/algorithms/search/binary', { array, target }),
  hashSearch: (data, key) => api.post('/algorithms/search/hash', { data, key }),
  
  // Sorting algorithms
  quickSort: (array) => api.post('/algorithms/sort/quick', { array }),
  mergeSort: (array) => api.post('/algorithms/sort/merge', { array }),
  
  // Performance analysis
  compareSearchAlgorithms: (array, target) => api.post('/algorithms/compare/search', { array, target }),
  compareSortAlgorithms: (array) => api.post('/algorithms/compare/sort', { array }),
  
  // Complexity analysis
  getComplexityAnalysis: (algorithm) => api.get(`/algorithms/complexity/${algorithm}`),
  getAllComplexityData: () => api.get('/algorithms/complexity/all')
};

// Performance Monitoring API
export const performanceAPI = {
  getMetrics: () => api.get('/performance/metrics'),
  getSystemStatus: () => api.get('/performance/system-status'),
  getAlgorithmPerformance: (algorithm) => api.get(`/performance/algorithm/${algorithm}`),
  getDataStructurePerformance: (dataStructure) => api.get(`/performance/data-structure/${dataStructure}`),
  resetMetrics: () => api.post('/performance/reset')
};

// Utility functions
export const apiUtils = {
  // Handle API errors
  handleError: (error) => {
    if (error.response) {
      // Server responded with error status
      const message = error.response.data?.error || error.response.data?.message || 'Server error';
      return {
        success: false,
        error: message,
        status: error.response.status
      };
    } else if (error.request) {
      // Request was made but no response received
      return {
        success: false,
        error: 'Network error - please check your connection',
        status: 0
      };
    } else {
      // Something else happened
      return {
        success: false,
        error: error.message || 'Unknown error occurred',
        status: 0
      };
    }
  },
  
  // Format API response
  formatResponse: (response) => {
    return {
      success: true,
      data: response.data?.data || response.data,
      message: response.data?.message,
      status: response.status
    };
  },
  
  // Check if user is authenticated
  isAuthenticated: () => {
    return !!localStorage.getItem('userToken');
  },
  
  // Get user role
  getUserRole: () => {
    return localStorage.getItem('userRole');
  },
  
  // Get user ID
  getUserId: () => {
    return localStorage.getItem('userId');
  },
  
  // Clear authentication data
  clearAuth: () => {
    localStorage.removeItem('userToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
  },
  
  // Set authentication data
  setAuth: (token, role, userId) => {
    localStorage.setItem('userToken', token);
    localStorage.setItem('userRole', role);
    localStorage.setItem('userId', userId);
  }
};



export default api;