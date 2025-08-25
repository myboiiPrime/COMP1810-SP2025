<template>
  <div class="checkout-container">
    <div class="checkout-header">
      <h1>Checkout</h1>
      <p>Complete your order</p>
    </div>

    <div class="checkout-content">
      <!-- Order Summary -->
      <div class="order-summary">
        <h2>Order Summary</h2>
        <div class="cart-items">
          <div v-for="item in cart" :key="item.id" class="cart-item">
            <div class="item-image book-symbol">
              📚
            </div>
            <div class="item-details">
              <h3>{{ item.title }}</h3>
              <p>by {{ item.author }}</p>
              <p class="quantity">Quantity: {{ item.quantity || 1 }}</p>
            </div>
            <div class="item-price">
              <span class="price">${{ ((item.price || 0) * (item.quantity || 1)).toFixed(2) }}</span>
            </div>
          </div>
        </div>
        
        <div class="order-totals">
          <div class="total-line">
            <span>Subtotal:</span>
            <span>${{ subtotal.toFixed(2) }}</span>
          </div>
          <div class="total-line">
            <span>Shipping:</span>
            <span>${{ shipping.toFixed(2) }}</span>
          </div>
          <div class="total-line">
            <span>Tax:</span>
            <span>${{ tax.toFixed(2) }}</span>
          </div>
          <div class="total-line total">
            <span>Total:</span>
            <span>${{ total.toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- Checkout Form -->
      <div class="checkout-form">
        <form @submit.prevent="submitOrder">
          <!-- Shipping Address -->
          <div class="form-section">
            <h2>Shipping Address</h2>
            <div class="form-row">
              <div class="form-group">
                <label for="fullName">Full Name *</label>
                <input 
                  type="text" 
                  id="fullName" 
                  v-model="shippingAddress.fullName" 
                  required
                  placeholder="Enter your full name"
                >
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="addressLine1">Address Line 1 *</label>
                <input 
                  type="text" 
                  id="addressLine1" 
                  v-model="shippingAddress.addressLine1" 
                  required
                  placeholder="Street address"
                >
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="addressLine2">Address Line 2</label>
                <input 
                  type="text" 
                  id="addressLine2" 
                  v-model="shippingAddress.addressLine2" 
                  placeholder="Apartment, suite, etc. (optional)"
                >
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group half">
                <label for="city">City *</label>
                <input 
                  type="text" 
                  id="city" 
                  v-model="shippingAddress.city" 
                  required
                  placeholder="City"
                >
              </div>
              <div class="form-group half">
                <label for="state">State *</label>
                <input 
                  type="text" 
                  id="state" 
                  v-model="shippingAddress.state" 
                  required
                  placeholder="State"
                >
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group half">
                <label for="postalCode">Postal Code *</label>
                <input 
                  type="text" 
                  id="postalCode" 
                  v-model="shippingAddress.postalCode" 
                  required
                  placeholder="Postal code"
                >
              </div>
              <div class="form-group half">
                <label for="country">Country *</label>
                <input 
                  type="text" 
                  id="country" 
                  v-model="shippingAddress.country" 
                  required
                  placeholder="Country"
                >
              </div>
            </div>
          </div>

          <!-- Payment Method -->
          <div class="form-section">
            <h2>Payment Method</h2>
            <div class="payment-options">
              <div class="payment-option">
                <input 
                  type="radio" 
                  id="cash" 
                  value="Cash" 
                  v-model="paymentMethod" 
                  required
                >
                <label for="cash" class="payment-label">
                  <div class="payment-icon">💵</div>
                  <div class="payment-text">
                    <strong>Cash on Delivery</strong>
                    <p>Pay when your order arrives</p>
                  </div>
                </label>
              </div>
              
              <div class="payment-option">
                <input 
                  type="radio" 
                  id="bank" 
                  value="Bank Transfer" 
                  v-model="paymentMethod" 
                  required
                >
                <label for="bank" class="payment-label">
                  <div class="payment-icon">🏦</div>
                  <div class="payment-text">
                    <strong>Bank Transfer</strong>
                    <p>Transfer payment to our bank account</p>
                  </div>
                </label>
              </div>
            </div>
          </div>

          <!-- Submit Button -->
          <div class="form-actions">
            <button type="button" @click="goBack" class="btn-secondary">
              Back to Cart
            </button>
            <button type="submit" :disabled="isSubmitting" class="btn-primary">
              <span v-if="isSubmitting">Processing...</span>
              <span v-else>Place Order</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ordersAPI, apiUtils } from '../services/api'

export default {
  name: 'Checkout',
  setup() {
    const router = useRouter()
    const cart = ref([])
    const isSubmitting = ref(false)
    const paymentMethod = ref('')
    
    const shippingAddress = reactive({
      fullName: '',
      addressLine1: '',
      addressLine2: '',
      city: '',
      state: '',
      postalCode: '',
      country: ''
    })

    // Load cart from localStorage
    const loadCart = () => {
      const savedCart = localStorage.getItem('bookstore_cart')
      if (savedCart) {
        cart.value = JSON.parse(savedCart)
      } else {
        // If no cart, redirect back to books
        router.push('/books')
      }
    }

    // Calculate totals
    const subtotal = computed(() => {
      return cart.value.reduce((sum, item) => sum + ((item.price || 0) * (item.quantity || 1)), 0)
    })

    const shipping = computed(() => {
      return subtotal.value > 50 ? 0 : 9.99
    })

    const tax = computed(() => {
      return subtotal.value * 0.08
    })

    const total = computed(() => {
      return subtotal.value + shipping.value + tax.value
    })

    // Submit order
    const submitOrder = async () => {
      if (!paymentMethod.value) {
        alert('Please select a payment method')
        return
      }
      
      // Validate shipping address
      console.log('Validating shipping address:', shippingAddress)
      if (!shippingAddress.fullName || !shippingAddress.addressLine1 || !shippingAddress.city || !shippingAddress.state || !shippingAddress.postalCode || !shippingAddress.country) {
        console.log('Validation failed - missing fields:', {
          fullName: !shippingAddress.fullName,
          addressLine1: !shippingAddress.addressLine1,
          city: !shippingAddress.city,
          state: !shippingAddress.state,
          postalCode: !shippingAddress.postalCode,
          country: !shippingAddress.country
        })
        alert('Please fill in all shipping address fields')
        return
      }
      console.log('Shipping address validation passed')

      isSubmitting.value = true

      try {
        // Log shipping address for debugging
        console.log('Shipping address data:', shippingAddress)
        
        // Get customer ID from authentication
        const customerId = apiUtils.getUserId() || '1'
        console.log('Customer ID:', customerId)
        
        // Create order items
        const orderItems = cart.value.map(item => ({
          bookId: item.id,
          title: item.title,
          author: item.author || 'Unknown Author',
          isbn: item.isbn || 'N/A',
          quantity: item.quantity || 1,
          price: item.price || 0,
          subtotal: (item.price || 0) * (item.quantity || 1)
        }))

        // Create order data
        const orderData = {
          customerId: customerId,
          items: orderItems,
          subtotal: subtotal.value,
          shippingCost: shipping.value,
          tax: tax.value,
          total: total.value,
          status: 'Pending',
          paymentStatus: 'Completed',
          shippingAddress: {
            firstName: shippingAddress.fullName.split(' ')[0] || '',
            lastName: shippingAddress.fullName.split(' ').slice(1).join(' ') || '',
            street: shippingAddress.addressLine1,
            city: shippingAddress.city,
            state: shippingAddress.state,
            zipCode: shippingAddress.postalCode,
            country: shippingAddress.country
          },
          paymentInfo: {
            method: paymentMethod.value,
            transactionId: 'TXN-' + Date.now()
          }
        }

        // Log order data for debugging
        console.log('Sending order data:', JSON.stringify(orderData, null, 2))
        console.log('Order data shippingAddress:', orderData.shippingAddress)
        console.log('Order data status:', orderData.status)
        
        // Submit order to API
        console.log('About to call ordersAPI.create...')
        const response = await ordersAPI.create(orderData)
        console.log('API response:', response)
        
        if (response.data.success) {
          // Clear cart
          localStorage.removeItem('bookstore_cart')
          
          // Show success message
          alert(`Order placed successfully! Order number: ${response.data.order.orderNumber}`)
          
          // Redirect to order history
          router.push('/orders')
        } else {
          throw new Error(response.data.message || 'Failed to create order')
        }
      } catch (error) {
        console.error('Order submission error:', error)
        console.error('Error response:', error.response?.data)
        console.error('Error status:', error.response?.status)
        alert('Failed to place order. Please try again.')
      } finally {
        isSubmitting.value = false
      }
    }

    const goBack = () => {
      router.push('/books')
    }

    onMounted(() => {
      loadCart()
    })

    return {
      cart,
      shippingAddress,
      paymentMethod,
      isSubmitting,
      subtotal,
      shipping,
      tax,
      total,
      submitOrder,
      goBack
    }
  }
}
</script>

<style scoped>
.checkout-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.checkout-header {
  text-align: center;
  margin-bottom: 30px;
}

.checkout-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.checkout-header p {
  color: #7f8c8d;
  font-size: 16px;
}

.checkout-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: start;
}

.order-summary {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.order-summary h2 {
  color: #2c3e50;
  margin-bottom: 20px;
  font-size: 20px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #e9ecef;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 15px;
}

.book-symbol {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
  border: 2px solid #dee2e6;
  font-size: 24px;
  color: #6c757d;
}

.item-details {
  flex: 1;
}

.item-details h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #2c3e50;
}

.item-details p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.quantity {
  font-weight: 500;
  color: #495057 !important;
}

.item-price {
  font-weight: 600;
  color: #2c3e50;
  font-size: 16px;
}

.order-totals {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 2px solid #dee2e6;
}

.total-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 16px;
}

.total-line.total {
  font-weight: 600;
  font-size: 18px;
  color: #2c3e50;
  border-top: 1px solid #dee2e6;
  padding-top: 10px;
  margin-top: 10px;
}

.checkout-form {
  background: white;
  padding: 25px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.form-section {
  margin-bottom: 30px;
}

.form-section h2 {
  color: #2c3e50;
  margin-bottom: 20px;
  font-size: 20px;
  border-bottom: 2px solid #3498db;
  padding-bottom: 10px;
}

.form-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.form-group {
  flex: 1;
}

.form-group.half {
  flex: 0.5;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #2c3e50;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.payment-option {
  position: relative;
}

.payment-option input[type="radio"] {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

.payment-label {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-option input[type="radio"]:checked + .payment-label {
  border-color: #3498db;
  background-color: #f8f9fa;
}

.payment-icon {
  font-size: 24px;
  margin-right: 15px;
}

.payment-text strong {
  display: block;
  color: #2c3e50;
  margin-bottom: 5px;
}

.payment-text p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 30px;
}

.btn-primary, .btn-secondary {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2980b9;
}

.btn-primary:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}

@media (max-width: 768px) {
  .checkout-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 10px;
  }
  
  .form-group.half {
    flex: 1;
  }
  
  .form-actions {
    flex-direction: column;
  }
}
</style>