<template>
  <div class="login-container">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-4">
        <div class="card shadow">
          <div class="card-body">
            <h2 class="card-title text-center mb-4">📚 Login</h2>
            
            <form @submit.prevent="handleLogin">
              <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input 
                  type="email" 
                  class="form-control" 
                  id="email" 
                  v-model="form.email" 
                  :class="{ 'is-invalid': errors.email }"
                  required
                >
                <div class="invalid-feedback" v-if="errors.email">
                  {{ errors.email }}
                </div>
              </div>
              
              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="password" 
                  v-model="form.password" 
                  :class="{ 'is-invalid': errors.password }"
                  required
                >
                <div class="invalid-feedback" v-if="errors.password">
                  {{ errors.password }}
                </div>
              </div>
              

              
              <div class="alert alert-danger" v-if="loginError">
                {{ loginError }}
              </div>
              
              <button 
                type="submit" 
                class="btn btn-primary w-100 mb-3" 
                :disabled="isLoading"
              >
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                {{ isLoading ? 'Logging in...' : 'Login' }}
              </button>
            </form>
            
            <div class="text-center">
              <p class="mb-0">Don't have an account? 
                <router-link to="/register" class="text-decoration-none">Register here</router-link>
              </p>
            </div>
          </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI, apiUtils } from '../services/api'

const router = useRouter()
const isLoading = ref(false)
const loginError = ref('')

const form = reactive({
  email: '',
  password: ''
})

const errors = reactive({
  email: '',
  password: ''
})

const validateForm = () => {
  // Reset errors
  Object.keys(errors).forEach(key => errors[key] = '')
  
  let isValid = true
  
  if (!form.email) {
    errors.email = 'Email is required'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'Please enter a valid email'
    isValid = false
  }
  
  if (!form.password) {
    errors.password = 'Password is required'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = 'Password must be at least 6 characters'
    isValid = false
  }
  
  return isValid
}

const handleLogin = async () => {
  if (!validateForm()) return
  
  isLoading.value = true
  loginError.value = ''
  
  try {
    let response
    
    // Try real API first
    try {
      response = await authAPI.login({
        email: form.email,
        password: form.password
      })
      response = apiUtils.formatResponse(response)
    } catch (apiError) {
      console.error('Authentication API not available:', apiError.message)
      loginError.value = 'Unable to connect to authentication service. Please try again later.'
      return
    }
    
    if (response.success) {
      const { token, customer, redirectTo } = response.data
      
      // Store authentication data
      apiUtils.setAuth(token, customer.role, customer.id)
      
      // Use server-provided redirect or fallback to role-based routing
      if (redirectTo) {
        router.push(redirectTo)
      } else if (customer.role === 'admin') {
        router.push('/admin')
      } else {
        router.push('/customer')
      }
    } else {
      loginError.value = response.error || 'Login failed'
    }
  } catch (error) {
    const errorInfo = apiUtils.handleError(error)
    loginError.value = errorInfo.error
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

.card {
  border: none;
  border-radius: 15px;
  max-width: 400px;
  width: 100%;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.card-title {
  color: #333;
  font-weight: 600;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  padding: 12px;
  font-weight: 500;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.form-control, .form-select {
  border-radius: 8px;
  border: 1px solid #ddd;
  padding: 12px;
}

.form-control:focus, .form-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
}
</style>