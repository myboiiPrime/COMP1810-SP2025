package com.bookstore.controller;

import com.bookstore.model.Order;
import com.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Create a new order
     */
    @PostMapping
    public ResponseEntity<Object> createOrder(@Valid @RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Order created successfully",
                "order", createdOrder
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", "Failed to create order",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Get all orders with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<Object> getAllOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String search) {
        
        try {
            if (customerId != null && !customerId.trim().isEmpty()) {
                Page<Order> orders = orderService.getOrdersByCustomerId(customerId.trim(), page, size);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", orders.getContent(),
                    "totalPages", orders.getTotalPages(),
                    "totalElements", orders.getTotalElements(),
                    "currentPage", page,
                    "size", size
                ));
            }
            
            if (status != null && !status.trim().isEmpty()) {
                List<Order> orders = orderService.getOrdersByStatus(status.trim());
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", orders,
                    "totalElements", orders.size()
                ));
            }
            
            if (search != null && !search.trim().isEmpty()) {
                List<Order> orders = orderService.searchOrders(search.trim());
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", orders,
                    "totalElements", orders.size()
                ));
            }
            
            if (page != null && size != null) {
                Page<Order> orders = orderService.getAllOrders(page, size, sortBy, sortDir);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", orders.getContent(),
                    "totalPages", orders.getTotalPages(),
                    "totalElements", orders.getTotalElements(),
                    "currentPage", page,
                    "size", size
                ));
            } else {
                List<Order> orders = orderService.getAllOrders();
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", orders,
                    "totalElements", orders.size()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve orders",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable String id) {
        try {
            Optional<Order> order = orderService.getOrderById(id);
            if (order.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "order", order.get()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Order not found"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve order",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Get orders by customer ID
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Object> getOrdersByCustomerId(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        
        try {
            if (size != null && page >= 0 && size > 0) {
                Page<Order> orders = orderService.getOrdersByCustomerId(customerId, page, size);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", orders.getContent(),
                    "totalPages", orders.getTotalPages(),
                    "totalElements", orders.getTotalElements(),
                    "currentPage", page,
                    "size", size
                ));
            } else {
                List<Order> orders = orderService.getOrdersByCustomerId(customerId);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orders", orders,
                    "totalElements", orders.size()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve customer orders",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Update order status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Object> updateOrderStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> statusUpdate) {
        
        try {
            String newStatus = statusUpdate.get("status");
            String notes = statusUpdate.get("notes");
            
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Status is required"
                ));
            }
            
            Order updatedOrder = orderService.updateOrderStatus(id, newStatus.trim(), notes);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order status updated successfully",
                "order", updatedOrder
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to update order status",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Update payment status
     */
    @PutMapping("/{id}/payment-status")
    public ResponseEntity<Object> updatePaymentStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> paymentUpdate) {
        
        try {
            String paymentStatus = paymentUpdate.get("paymentStatus");
            
            if (paymentStatus == null || paymentStatus.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Payment status is required"
                ));
            }
            
            Order updatedOrder = orderService.updatePaymentStatus(id, paymentStatus.trim());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment status updated successfully",
                "order", updatedOrder
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to update payment status",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Cancel order
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Object> cancelOrder(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> cancellationData) {
        
        try {
            String reason = null;
            if (cancellationData != null) {
                reason = cancellationData.get("reason");
            }
            
            Order cancelledOrder = orderService.cancelOrder(id, reason);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order cancelled successfully",
                "order", cancelledOrder
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to cancel order",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Get order by tracking number
     */
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Object> getOrderByTrackingNumber(@PathVariable String trackingNumber) {
        try {
            Optional<Order> order = orderService.getOrderByTrackingNumber(trackingNumber);
            if (order.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "order", order.get()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Order not found with tracking number: " + trackingNumber
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve order",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Get orders by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<Object> getOrdersByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            
            List<Order> orders = orderService.getOrdersByDateRange(start, end);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "orders", orders,
                "totalElements", orders.size(),
                "dateRange", Map.of(
                    "startDate", startDate,
                    "endDate", endDate
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", "Invalid date format. Use ISO format: yyyy-MM-ddTHH:mm:ss",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Get recent orders (last 30 days)
     */
    @GetMapping("/recent")
    public ResponseEntity<Object> getRecentOrders() {
        try {
            List<Order> orders = orderService.getRecentOrders();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "orders", orders,
                "totalElements", orders.size(),
                "period", "Last 30 days"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve recent orders",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Get orders containing a specific book
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<Object> getOrdersContainingBook(@PathVariable String bookId) {
        try {
            List<Order> orders = orderService.getOrdersContainingBook(bookId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "orders", orders,
                "totalElements", orders.size(),
                "bookId", bookId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve orders for book",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Get order statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Object> getOrderStatistics() {
        try {
            OrderService.OrderStatistics stats = orderService.getOrderStatistics();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "statistics", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to retrieve order statistics",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Search orders
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchOrders(@RequestParam String q) {
        try {
            if (q == null || q.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Search query is required"
                ));
            }
            
            List<Order> orders = orderService.searchOrders(q.trim());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "orders", orders,
                "totalElements", orders.size(),
                "searchQuery", q.trim()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to search orders",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Get system status for monitoring
     */
    @GetMapping("/system-status")
    public ResponseEntity<Object> getSystemStatus() {
        try {
            Map<String, Object> systemStatus = Map.of(
                "systemLoad", 45,
                "bottleneckRisk", Map.of("level", "low"),
                "slaCompliance", 98,
                "throughput", 150,
                "averageProcessingTime", 250,
                "processingCenters", List.of(
                    Map.of("id", "Center-A", "isActive", true, "currentLoad", 35),
                    Map.of("id", "Center-B", "isActive", true, "currentLoad", 55),
                    Map.of("id", "Center-C", "isActive", false, "currentLoad", 0)
                )
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", systemStatus
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to get system status",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Optimize system performance
     */
    @PostMapping("/optimize-system")
    public ResponseEntity<Object> optimizeSystem() {
        try {
            // Simulate system optimization
            Map<String, Object> optimizationResult = Map.of(
                "performanceImprovement", 15,
                "loadBalanced", true,
                "bottlenecksResolved", 3
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", optimizationResult
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to optimize system",
                "error", e.getMessage()
            ));
        }
    }
}