package com.bookstore.controller;

import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.service.CustomerService;
import com.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private OrderService orderService;

    /**
     * Create a new customer
     */
    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer) {
        try {
            Customer createdCustomer = customerService.createCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Customer created successfully",
                "customer", createdCustomer
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all customers with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "registrationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            if (page != null && size != null) {
                Page<Customer> customers = customerService.getAllCustomers(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "customers", customers.getContent(),
                    "totalElements", customers.getTotalElements(),
                    "totalPages", customers.getTotalPages(),
                    "currentPage", customers.getNumber(),
                    "hasNext", customers.hasNext(),
                    "hasPrevious", customers.hasPrevious()
                ));
            } else {
                List<Customer> customers = customerService.getAllCustomers();
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "customers", customers
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error retrieving customers: " + e.getMessage()
            ));
        }
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> getCustomerById(@PathVariable String id) {
        try {
            Optional<Customer> customer = customerService.getCustomerById(id);
            if (customer.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "customer", customer.get()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Customer not found"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error retrieving customer: " + e.getMessage()
            ));
        }
    }

    /**
     * Get customer by email
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCustomerByEmail(@PathVariable String email) {
        try {
            Optional<Customer> customer = customerService.getCustomerByEmail(email);
            if (customer.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "customer", customer.get()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Customer not found"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error retrieving customer: " + e.getMessage()
            ));
        }
    }

    /**
     * Update customer
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> updateCustomer(@PathVariable String id, @Valid @RequestBody Customer customerDetails) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Customer updated successfully",
                "customer", updatedCustomer
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Update customer password
     */
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> updateCustomerPassword(
            @PathVariable String id, 
            @RequestBody Map<String, String> passwordData) {
        try {
            String newPassword = passwordData.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "New password is required"
                ));
            }
            
            customerService.updateCustomerPassword(id, newPassword);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password updated successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Update customer loyalty points
     */
    @PutMapping("/{id}/loyalty-points")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLoyaltyPoints(
            @PathVariable String id, 
            @RequestBody Map<String, Integer> pointsData) {
        try {
            Integer points = pointsData.get("points");
            if (points == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Points value is required"
                ));
            }
            
            Customer updatedCustomer = customerService.updateLoyaltyPoints(id, points);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Loyalty points updated successfully",
                "customer", updatedCustomer
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Update customer total spent
     */
    @PutMapping("/{id}/total-spent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTotalSpent(
            @PathVariable String id, 
            @RequestBody Map<String, Double> spentData) {
        try {
            Double amount = spentData.get("amount");
            if (amount == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Amount value is required"
                ));
            }
            
            Customer updatedCustomer = customerService.updateTotalSpent(id, amount);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Total spent updated successfully",
                "customer", updatedCustomer
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Update customer last login
     */
    @PutMapping("/{id}/last-login")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLastLogin(@PathVariable String id) {
        try {
            customerService.updateLastLogin(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Last login updated successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Deactivate customer
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateCustomer(@PathVariable String id) {
        try {
            customerService.deactivateCustomer(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Customer deactivated successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Activate customer
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activateCustomer(@PathVariable String id) {
        try {
            customerService.activateCustomer(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Customer activated successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Delete customer
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Customer deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Search customers
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchCustomers(@RequestParam String query) {
        try {
            List<Customer> customers = customerService.searchCustomers(query);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "customers", customers,
                "count", customers.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error searching customers: " + e.getMessage()
            ));
        }
    }

    /**
     * Get customers by membership level
     */
    @GetMapping("/membership/{level}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCustomersByMembershipLevel(@PathVariable String level) {
        try {
            List<Customer> customers = customerService.getCustomersByMembershipLevel(level.toUpperCase());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "customers", customers,
                "count", customers.size(),
                "membershipLevel", level.toUpperCase()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error retrieving customers: " + e.getMessage()
            ));
        }
    }

    /**
     * Get active customers
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getActiveCustomers() {
        try {
            List<Customer> customers = customerService.getActiveCustomers();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "customers", customers,
                "count", customers.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error retrieving active customers: " + e.getMessage()
            ));
        }
    }

    /**
     * Get customers by registration date range
     */
    @GetMapping("/registration-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCustomersByRegistrationDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Customer> customers = customerService.getCustomersByRegistrationDateRange(startDate, endDate);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "customers", customers,
                "count", customers.size(),
                "dateRange", Map.of(
                    "startDate", startDate,
                    "endDate", endDate
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error retrieving customers: " + e.getMessage()
            ));
        }
    }

    /**
     * Get top customers by spending
     */
    @GetMapping("/top-spenders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTopCustomersBySpending(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Customer> customers = customerService.getTopCustomersBySpending(limit);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "customers", customers,
                "count", customers.size(),
                "limit", limit
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error retrieving top customers: " + e.getMessage()
            ));
        }
    }

    /**
     * Get customer statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCustomerStatistics() {
        try {
            CustomerService.CustomerStats stats = customerService.getCustomerStatistics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "statistics", Map.of(
                    "totalCustomers", stats.getTotalCustomers(),
                    "activeCustomers", stats.getActiveCustomers(),
                    "inactiveCustomers", stats.getInactiveCustomers(),
                    "newCustomersToday", stats.getNewCustomersToday()
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error retrieving customer statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * Get orders for a specific customer
     */
    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> getCustomerOrders(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        try {
            // First verify the customer exists
            Optional<Customer> customer = customerService.getCustomerById(id);
            if (!customer.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Customer not found"
                ));
            }

            if (size != null && page >= 0 && size > 0) {
                Page<Order> orders = orderService.getOrdersByCustomerId(id, page, size);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", orders.getContent(),
                    "totalPages", orders.getTotalPages(),
                    "totalElements", orders.getTotalElements(),
                    "currentPage", page,
                    "size", size
                ));
            } else {
                List<Order> orders = orderService.getOrdersByCustomerId(id);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", orders,
                    "totalElements", orders.size()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve customer orders: " + e.getMessage()
            ));
        }
    }

    /**
     * Get customer recommendations
     */
    @GetMapping("/{id}/recommendations")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> getCustomerRecommendations(@PathVariable String id) {
        try {
            // First verify the customer exists
            Optional<Customer> customer = customerService.getCustomerById(id);
            if (!customer.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Customer not found"
                ));
            }

            // For now, return empty recommendations
            // In a real application, this would use a recommendation algorithm
            return ResponseEntity.ok(Map.of(
                "success", true,
                "recommendations", List.of(),
                "books", List.of(),
                "message", "Recommendations feature coming soon"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve customer recommendations: " + e.getMessage()
            ));
        }
    }
}