package com.bookstore.service;

import com.bookstore.model.Order;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.CustomerService;
import com.bookstore.util.PriorityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CustomerService customerService;

    /**
     * Create a new order
     */
    public Order createOrder(Order order) {
        // Generate order number
        order.setOrderNumber(generateOrderNumber());
        
        // Generate tracking number
        order.setTrackingNumber(generateTrackingNumber());
        
        // Set initial status
        if (order.getStatus() == null) {
            order.setStatus("Pending");
        }
        
        if (order.getPaymentStatus() == null) {
            order.setPaymentStatus("Pending");
        }
        
        // Calculate and set priority based on customer membership level
        try {
            Optional<Customer> customerOpt = customerService.getCustomerById(order.getCustomerId());
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                int priority = PriorityUtils.getMembershipPriority(customer.getMembershipLevel());
                order.setPriority(priority);
            } else {
                // Default to lowest priority if customer not found
                order.setPriority(1);
            }
        } catch (Exception e) {
            // Default to lowest priority if there's an error
            order.setPriority(1);
        }
        
        // Calculate totals
        calculateOrderTotals(order);
        
        // Validate and update book stock
        validateAndUpdateStock(order);
        
        // Set order date if not provided
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        
        // Add status history
        Order.StatusHistory initialStatus = new Order.StatusHistory();
        initialStatus.setStatus("Pending");
        initialStatus.setTimestamp(LocalDateTime.now());
        initialStatus.setNotes("Order created");
        order.getStatusHistory().add(initialStatus);
        
        return orderRepository.save(order);
    }

    /**
     * Get all orders with pagination
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Page<Order> getAllOrders(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return orderRepository.findAll(pageable);
    }

    /**
     * Get order by ID
     */
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    /**
     * Get orders by customer ID
     */
    public List<Order> getOrdersByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * Get orders by customer ID with pagination
     */
    public Page<Order> getOrdersByCustomerId(String customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        return orderRepository.findByCustomerId(customerId, pageable);
    }

    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Update order status
     */
    public Order updateOrderStatus(String orderId, String newStatus, String notes) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            String oldStatus = order.getStatus();
            order.setStatus(newStatus);
            
            // Add status history
            Order.StatusHistory statusHistory = new Order.StatusHistory();
            statusHistory.setStatus(newStatus);
            statusHistory.setTimestamp(LocalDateTime.now());
            statusHistory.setNotes(notes != null ? notes : "Status updated from " + oldStatus + " to " + newStatus);
            order.getStatusHistory().add(statusHistory);
            
            // Set delivery date if delivered
            if ("Delivered".equals(newStatus)) {
                order.setActualDelivery(LocalDateTime.now());
            }
            
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found with id: " + orderId);
    }

    /**
     * Update payment status
     */
    public Order updatePaymentStatus(String orderId, String paymentStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setPaymentStatus(paymentStatus);
            
            // Add status history
            Order.StatusHistory statusHistory = new Order.StatusHistory();
            statusHistory.setStatus(order.getStatus());
            statusHistory.setTimestamp(LocalDateTime.now());
            statusHistory.setNotes("Payment status updated to: " + paymentStatus);
            order.getStatusHistory().add(statusHistory);
            
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found with id: " + orderId);
    }

    /**
     * Cancel order
     */
    public Order cancelOrder(String orderId, String reason) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            
            if (!order.canBeCancelled()) {
                throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
            }
            
            // Restore book stock
            restoreBookStock(order);
            
            // Update status
            order.setStatus("Cancelled");
            
            // Add status history
            Order.StatusHistory statusHistory = new Order.StatusHistory();
            statusHistory.setStatus("Cancelled");
            statusHistory.setTimestamp(LocalDateTime.now());
            statusHistory.setNotes("Order cancelled. Reason: " + (reason != null ? reason : "No reason provided"));
            order.getStatusHistory().add(statusHistory);
            
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found with id: " + orderId);
    }

    /**
     * Get order by tracking number
     */
    public Optional<Order> getOrderByTrackingNumber(String trackingNumber) {
        return orderRepository.findByTrackingNumber(trackingNumber);
    }

    /**
     * Get orders by date range
     */
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    /**
     * Get recent orders (last 30 days)
     */
    public List<Order> getRecentOrders() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return orderRepository.findRecentOrders(thirtyDaysAgo);
    }

    /**
     * Get orders containing a specific book
     */
    public List<Order> getOrdersContainingBook(String bookId) {
        return orderRepository.findOrdersContainingBook(bookId);
    }

    /**
     * Get order statistics
     */
    public OrderStatistics getOrderStatistics() {
        OrderStatistics stats = new OrderStatistics();
        
        stats.setTotalOrders(orderRepository.count());
        stats.setPendingOrders(orderRepository.countByStatus("Pending"));
        stats.setProcessingOrders(orderRepository.countByStatus("Processing"));
        stats.setShippedOrders(orderRepository.countByStatus("Shipped"));
        stats.setDeliveredOrders(orderRepository.countByStatus("Delivered"));
        stats.setCancelledOrders(orderRepository.countByStatus("Cancelled"));
        
        // Calculate total revenue
        List<Object> revenueResult = orderRepository.calculateTotalRevenue();
        if (!revenueResult.isEmpty()) {
            // Extract revenue from MongoDB aggregation result
            stats.setTotalRevenue(0.0); // Default value, would need proper extraction logic
        }
        
        return stats;
    }

    /**
     * Search orders by customer name or order details
     */
    public List<Order> searchOrders(String searchTerm) {
        // This is a simplified search - in a real application, you might want to implement
        // more sophisticated search using text indexes or external search engines
        return orderRepository.findOrdersByCustomerName(searchTerm);
    }

    // Private helper methods
    
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void calculateOrderTotals(Order order) {
        double subtotal = order.getItems().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
        
        order.setSubtotal(subtotal);
        
        // Calculate tax (assuming 8% tax rate)
        double tax = subtotal * 0.08;
        order.setTax(tax);
        
        // Set shipping cost if not provided
        if (order.getShippingCost() == null) {
            order.setShippingCost(subtotal > 50 ? 0.0 : 9.99); // Free shipping over $50
        }
        
        // Set discount if not provided
        if (order.getDiscount() == null) {
            order.setDiscount(0.0);
        }
        
        // Calculate total
        double total = subtotal + tax + order.getShippingCost() - order.getDiscount();
        order.setTotal(total);
        
        // Update item subtotals
        order.getItems().forEach(item -> {
            if (item.getSubtotal() == null) {
                item.setSubtotal(item.getPrice() * item.getQuantity());
            }
        });
    }

    private void validateAndUpdateStock(Order order) {
        for (Order.OrderItem item : order.getItems()) {
            Optional<Book> bookOpt = bookRepository.findById(item.getBookId());
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                if (book.getStockQuantity() < item.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for book: " + book.getTitle() + 
                        ". Available: " + book.getStockQuantity() + ", Requested: " + item.getQuantity());
                }
                
                // Update stock
                book.updateStock(-item.getQuantity());
                bookRepository.save(book);
                
                // Update item details from book
                item.setTitle(book.getTitle());
                item.setAuthor(book.getAuthor());
                item.setIsbn(book.getIsbn());
                if (item.getPrice() == null) {
                    item.setPrice(book.getPrice());
                }
            } else {
                throw new RuntimeException("Book not found with id: " + item.getBookId());
            }
        }
    }

    private void restoreBookStock(Order order) {
        for (Order.OrderItem item : order.getItems()) {
            Optional<Book> bookOpt = bookRepository.findById(item.getBookId());
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                book.updateStock(item.getQuantity());
                bookRepository.save(book);
            }
        }
    }

    // Inner class for order statistics
    public static class OrderStatistics {
        private long totalOrders;
        private long pendingOrders;
        private long processingOrders;
        private long shippedOrders;
        private long deliveredOrders;
        private long cancelledOrders;
        private double totalRevenue;

        // Getters and setters
        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

        public long getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }

        public long getProcessingOrders() { return processingOrders; }
        public void setProcessingOrders(long processingOrders) { this.processingOrders = processingOrders; }

        public long getShippedOrders() { return shippedOrders; }
        public void setShippedOrders(long shippedOrders) { this.shippedOrders = shippedOrders; }

        public long getDeliveredOrders() { return deliveredOrders; }
        public void setDeliveredOrders(long deliveredOrders) { this.deliveredOrders = deliveredOrders; }

        public long getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }

        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    }
}