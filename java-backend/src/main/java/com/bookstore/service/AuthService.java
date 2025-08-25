package com.bookstore.service;

import com.bookstore.model.Customer;
import com.bookstore.repository.CustomerRepository;
import com.bookstore.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> register(Customer customer) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if email already exists
            if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
                response.put("success", false);
                response.put("message", "Email already exists");
                return response;
            }

            // Hash the password
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            
            // Set default values
            customer.setRegistrationDate(LocalDateTime.now());
            customer.setLastLogin(LocalDateTime.now());
            customer.setRole("customer");
            customer.setIsActive(true);
            customer.setMembershipLevel("bronze");
            customer.setLoyaltyPoints(0);
            customer.setTotalSpent(0.0);
            customer.setOrderCount(0);
            
            // Initialize preferences if not set
            if (customer.getPreferences() == null) {
                customer.setPreferences(new Customer.Preferences());
            }
            
            // Save customer
            Customer savedCustomer = customerRepository.save(customer);
            
            // Generate JWT token
            String token = jwtUtil.generateTokenWithCustomerInfo(
                savedCustomer.getEmail(), 
                savedCustomer.getId(), 
                savedCustomer.getRole()
            );
            
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("token", token);
            response.put("customer", createCustomerResponse(savedCustomer));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Customer> customerOpt = customerRepository.findByEmail(email);
            
            if (customerOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return response;
            }
            
            Customer customer = customerOpt.get();
            
            // Check if account is active
            if (!customer.getIsActive()) {
                response.put("success", false);
                response.put("message", "Account is deactivated");
                return response;
            }
            
            // Verify password
            if (!passwordEncoder.matches(password, customer.getPassword())) {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return response;
            }
            
            // Update last login
            customer.setLastLogin(LocalDateTime.now());
            customerRepository.save(customer);
            
            // Generate JWT token
            String token = jwtUtil.generateTokenWithCustomerInfo(
                customer.getEmail(), 
                customer.getId(), 
                customer.getRole()
            );
            
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("customer", createCustomerResponse(customer));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> validateToken(String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractUsername(token);
                String customerId = jwtUtil.extractCustomerId(token);
                
                Optional<Customer> customerOpt = customerRepository.findByEmail(email);
                
                if (customerOpt.isPresent() && customerOpt.get().getIsActive()) {
                    response.put("success", true);
                    response.put("customer", createCustomerResponse(customerOpt.get()));
                } else {
                    response.put("success", false);
                    response.put("message", "Customer not found or inactive");
                }
            } else {
                response.put("success", false);
                response.put("message", "Invalid or expired token");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Token validation failed: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> refreshToken(String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (jwtUtil.canTokenBeRefreshed(token)) {
                String newToken = jwtUtil.refreshToken(token);
                response.put("success", true);
                response.put("token", newToken);
            } else {
                response.put("success", false);
                response.put("message", "Token cannot be refreshed");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Token refresh failed: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> changePassword(String customerId, String currentPassword, String newPassword) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Customer> customerOpt = customerRepository.findById(customerId);
            
            if (customerOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Customer not found");
                return response;
            }
            
            Customer customer = customerOpt.get();
            
            // Verify current password
            if (!passwordEncoder.matches(currentPassword, customer.getPassword())) {
                response.put("success", false);
                response.put("message", "Current password is incorrect");
                return response;
            }
            
            // Update password
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerRepository.save(customer);
            
            response.put("success", true);
            response.put("message", "Password changed successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Password change failed: " + e.getMessage());
        }
        
        return response;
    }

    private Map<String, Object> createCustomerResponse(Customer customer) {
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("id", customer.getId());
        customerData.put("fullName", customer.getFullName());
        customerData.put("email", customer.getEmail());
        customerData.put("phone", customer.getPhone());
        customerData.put("role", customer.getRole());
        customerData.put("membershipLevel", customer.getMembershipLevel());
        customerData.put("loyaltyPoints", customer.getLoyaltyPoints());
        customerData.put("totalSpent", customer.getTotalSpent());
        customerData.put("orderCount", customer.getOrderCount());
        customerData.put("registrationDate", customer.getRegistrationDate());
        customerData.put("lastLogin", customer.getLastLogin());
        customerData.put("preferences", customer.getPreferences());
        customerData.put("cartItemCount", customer.getCart() != null ? customer.getCart().size() : 0);
        customerData.put("wishlistCount", customer.getWishlist() != null ? customer.getWishlist().size() : 0);
        return customerData;
    }
}