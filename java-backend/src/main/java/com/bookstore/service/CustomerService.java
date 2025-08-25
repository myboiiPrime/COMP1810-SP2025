package com.bookstore.service;

import com.bookstore.model.Customer;
import com.bookstore.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new customer
     */
    public Customer createCustomer(Customer customer) {
        // Check if customer with email already exists
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new RuntimeException("Customer with email " + customer.getEmail() + " already exists");
        }

        // Encode password if provided
        if (customer.getPassword() != null) {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        }

        // Set default values
        customer.setRegistrationDate(LocalDateTime.now());
        customer.setLastLogin(LocalDateTime.now());
        customer.setIsActive(true);
        customer.setRole("CUSTOMER");
        
        if (customer.getMembershipLevel() == null) {
            customer.setMembershipLevel("STANDARD");
        }
        
        if (customer.getLoyaltyPoints() == null) {
            customer.setLoyaltyPoints(0);
        }
        
        if (customer.getTotalSpent() == null) {
            customer.setTotalSpent(0.0);
        }

        return customerRepository.save(customer);
    }

    /**
     * Get all customers with pagination
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Page<Customer> getAllCustomers(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return customerRepository.findAll(pageable);
    }

    /**
     * Get customer by ID
     */
    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    /**
     * Get customer by email
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * Update customer
     */
    public Customer updateCustomer(String id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // Update fields
        if (customerDetails.getFullName() != null) {
            customer.setFullName(customerDetails.getFullName());
        }
        if (customerDetails.getEmail() != null && !customerDetails.getEmail().equals(customer.getEmail())) {
            // Check if new email already exists
            if (customerRepository.findByEmail(customerDetails.getEmail()).isPresent()) {
                throw new RuntimeException("Customer with email " + customerDetails.getEmail() + " already exists");
            }
            customer.setEmail(customerDetails.getEmail());
        }
        if (customerDetails.getPhone() != null) {
            customer.setPhone(customerDetails.getPhone());
        }
        // Note: dateOfBirth and address fields not available in current Customer model
        if (customerDetails.getPreferences() != null) {
            customer.setPreferences(customerDetails.getPreferences());
        }
        if (customerDetails.getMembershipLevel() != null) {
            customer.setMembershipLevel(customerDetails.getMembershipLevel());
        }

        return customerRepository.save(customer);
    }

    /**
     * Update customer password
     */
    public void updateCustomerPassword(String id, String newPassword) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }

    /**
     * Update customer loyalty points
     */
    public Customer updateLoyaltyPoints(String customerId, int points) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
        
        // Update membership level based on loyalty points
        updateMembershipLevel(customer);
        
        return customerRepository.save(customer);
    }

    /**
     * Update customer total spent
     */
    public Customer updateTotalSpent(String customerId, double amount) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        customer.setTotalSpent(customer.getTotalSpent() + amount);
        
        // Update membership level based on total spent
        updateMembershipLevel(customer);
        
        return customerRepository.save(customer);
    }

    /**
     * Update customer last login
     */
    public void updateLastLogin(String customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        customer.setLastLogin(LocalDateTime.now());
        customerRepository.save(customer);
    }

    /**
     * Deactivate customer
     */
    public void deactivateCustomer(String id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        customer.setIsActive(false);
        customerRepository.save(customer);
    }

    /**
     * Activate customer
     */
    public void activateCustomer(String id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        customer.setIsActive(true);
        customerRepository.save(customer);
    }

    /**
     * Delete customer
     */
    public void deleteCustomer(String id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    /**
     * Search customers by name or email
     */
    public List<Customer> searchCustomers(String query) {
        return customerRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            query, query);
    }

    /**
     * Get customers by membership level
     */
    public List<Customer> getCustomersByMembershipLevel(String membershipLevel) {
        return customerRepository.findByMembershipLevel(membershipLevel);
    }

    /**
     * Get active customers
     */
    public List<Customer> getActiveCustomers() {
        return customerRepository.findByIsActiveTrue();
    }

    /**
     * Get customers registered within date range
     */
    public List<Customer> getCustomersByRegistrationDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return customerRepository.findByRegistrationDateBetween(startDate, endDate);
    }

    /**
     * Get top customers by total spent
     */
    public List<Customer> getTopCustomersBySpending(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("totalSpent").descending());
        return customerRepository.findAll(pageable).getContent();
    }

    /**
     * Get customer statistics
     */
    public CustomerStats getCustomerStatistics() {
        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.countByIsActiveTrue();
        long inactiveCustomers = totalCustomers - activeCustomers;
        
        // Get customers registered today
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        long newCustomersToday = customerRepository.countByRegistrationDateBetween(startOfDay, endOfDay);
        
        return new CustomerStats(totalCustomers, activeCustomers, inactiveCustomers, newCustomersToday);
    }

    /**
     * Update membership level based on total spent and loyalty points
     */
    private void updateMembershipLevel(Customer customer) {
        double totalSpent = customer.getTotalSpent();
        int loyaltyPoints = customer.getLoyaltyPoints();
        
        if (totalSpent >= 10000 || loyaltyPoints >= 5000) {
            customer.setMembershipLevel("PLATINUM");
        } else if (totalSpent >= 5000 || loyaltyPoints >= 2500) {
            customer.setMembershipLevel("GOLD");
        } else if (totalSpent >= 1000 || loyaltyPoints >= 500) {
            customer.setMembershipLevel("SILVER");
        } else {
            customer.setMembershipLevel("STANDARD");
        }
    }

    /**
     * Customer statistics inner class
     */
    public static class CustomerStats {
        private final long totalCustomers;
        private final long activeCustomers;
        private final long inactiveCustomers;
        private final long newCustomersToday;

        public CustomerStats(long totalCustomers, long activeCustomers, long inactiveCustomers, long newCustomersToday) {
            this.totalCustomers = totalCustomers;
            this.activeCustomers = activeCustomers;
            this.inactiveCustomers = inactiveCustomers;
            this.newCustomersToday = newCustomersToday;
        }

        // Getters
        public long getTotalCustomers() { return totalCustomers; }
        public long getActiveCustomers() { return activeCustomers; }
        public long getInactiveCustomers() { return inactiveCustomers; }
        public long getNewCustomersToday() { return newCustomersToday; }
    }
}