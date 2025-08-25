<template>
  <div class="register-container">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="card shadow">
          <div class="card-body">
            <h2 class="card-title text-center mb-4">📚 Register</h2>
            
            <form @submit.prevent="handleRegister">
              <div class="mb-3">
                <label for="name" class="form-label">Full Name</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="name" 
                  v-model="form.name" 
                  :class="{ 'is-invalid': errors.name }"
                  required
                >
                <div class="invalid-feedback" v-if="errors.name">
                  {{ errors.name }}
                </div>
              </div>
              
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
              
              <div class="mb-3">
                <label for="confirmPassword" class="form-label">Confirm Password</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="confirmPassword" 
                  v-model="form.confirmPassword" 
                  :class="{ 'is-invalid': errors.confirmPassword }"
                  required
                >
                <div class="invalid-feedback" v-if="errors.confirmPassword">
                  {{ errors.confirmPassword }}
                </div>
              </div>
              
              <div class="mb-3">
                <label for="phone" class="form-label">Phone Number</label>
                <input 
                  type="tel" 
                  class="form-control" 
                  id="phone" 
                  v-model="form.phone" 
                  :class="{ 'is-invalid': errors.phone }"
                  placeholder="+1234567890"
                >
                <div class="invalid-feedback" v-if="errors.phone">
                  {{ errors.phone }}
                </div>
              </div>
              
              <div class="mb-3">
                <label for="membershipLevel" class="form-label">Membership Level</label>
                <select 
                  class="form-select" 
                  id="membershipLevel" 
                  v-model="form.membershipLevel" 
                  :class="{ 'is-invalid': errors.membershipLevel }"
                  required
                >
                  <option value="">Select Membership</option>
                  <option value="bronze">Bronze</option>
                  <option value="silver">Silver</option>
                  <option value="gold">Gold</option>
                  <option value="platinum">Platinum</option>
                </select>
                <div class="invalid-feedback" v-if="errors.membershipLevel">
                  {{ errors.membershipLevel }}
                </div>
              </div>
              
              <div class="alert alert-danger" v-if="registerError">
                {{ registerError }}
              </div>
              
              <div class="alert alert-success" v-if="registerSuccess">
                {{ registerSuccess }}
              </div>
              
              <button 
                type="submit" 
                class="btn btn-success w-100 mb-3" 
                :disabled="isLoading"
              >
                <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                {{ isLoading ? 'Creating Account...' : 'Create Account' }}
              </button>
            </form>
            
            <div class="text-center">
              <p class="mb-0">Already have an account? 
                <router-link to="/login" class="text-decoration-none">Login here</router-link>
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
import axios from 'axios'
import { authAPI, apiUtils } from '../services/api'

const router = useRouter()
const isLoading = ref(false)
const registerError = ref('')
const registerSuccess = ref('')

const form = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  membershipLevel: ''
})

const errors = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  membershipLevel: ''
})

const validateForm = () => {
  // Reset errors
  Object.keys(errors).forEach(key => errors[key] = '')
  
  let isValid = true
  
  if (!form.name.trim()) {
    errors.name = 'Full name is required'
    isValid = false
  } else if (form.name.trim().length < 2) {
    errors.name = 'Name must be at least 2 characters'
    isValid = false
  }
  
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
  
  if (!form.confirmPassword) {
    errors.confirmPassword = 'Please confirm your password'
    isValid = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match'
    isValid = false
  }
  
  if (form.phone && !/^[+]?[1-9]\d{1,14}$/.test(form.phone.replace(/\s/g, ''))) {
    errors.phone = 'Please enter a valid phone number'
    isValid = false
  }
  
  if (!form.membershipLevel) {
    errors.membershipLevel = 'Please select a membership level'
    isValid = false
  }
  
  return isValid
}

const handleRegister = async () => {
  if (!validateForm()) return
  
  isLoading.value = true
  registerError.value = ''
  registerSuccess.value = ''
  
  try {
    let response
    
    const userData = {
      name: form.name,
      email: form.email,
      password: form.password,
      phone: form.phone,
      membershipLevel: form.membershipLevel
    }
    
    // Try real API
    try {
      response = await authAPI.register(userData)
      response = apiUtils.formatResponse(response)
    } catch (apiError) {
      console.error('Registration API not available:', apiError.message)
      registerError.value = 'Unable to connect to registration service. Please try again later.'
      return
    }
    
    if (response.success) {
      registerSuccess.value = response.data.message || 'Account created successfully! You can now login.'
      
      // Reset form
      Object.keys(form).forEach(key => form[key] = '')
      
      // Redirect to login after 2 seconds
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      registerError.value = response.error || 'Registration failed'
    }
    
  } catch (error) {
    const errorInfo = apiUtils.handleError(error)
    registerError.value = errorInfo.error
    console.error('Registration error:', error)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
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
  max-width: 500px;
  width: 100%;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.card-title {
  color: #333;
  font-weight: 600;
}

.btn-success {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border: none;
  border-radius: 8px;
  padding: 12px;
  font-weight: 500;
}

.btn-success:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(17, 153, 142, 0.4);
}

.form-control, .form-select {
  border-radius: 8px;
  border: 1px solid #ddd;
  padding: 12px;
}

.form-control:focus, .form-select:focus {
  border-color: #11998e;
  box-shadow: 0 0 0 0.2rem rgba(17, 153, 142, 0.25);
}
</style>