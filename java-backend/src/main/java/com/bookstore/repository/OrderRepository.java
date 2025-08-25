package com.bookstore.repository;

import com.bookstore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    // Find orders by customer ID
    List<Order> findByCustomerId(String customerId);
    
    // Find orders by customer ID with pagination
    Page<Order> findByCustomerId(String customerId, Pageable pageable);
    
    // Find orders by status
    List<Order> findByStatus(String status);
    
    // Find orders by payment status
    List<Order> findByPaymentStatus(String paymentStatus);
    
    // Find orders by tracking number
    Optional<Order> findByTrackingNumber(String trackingNumber);
    
    // Find orders by date range
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find orders by date range with pagination
    Page<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Find orders by total amount range
    List<Order> findByTotalBetween(Double minTotal, Double maxTotal);
    
    // Find orders created after a certain date
    List<Order> findByCreatedAtAfter(LocalDateTime date);
    
    // Find orders created before a certain date
    List<Order> findByCreatedAtBefore(LocalDateTime date);
    
    // Find orders after a certain date
    List<Order> findByOrderDateAfter(LocalDateTime date);
    
    // Find orders after a certain date with specific statuses
    List<Order> findByOrderDateAfterAndStatusIn(LocalDateTime date, List<String> statuses);
    
    // Find recent orders (last 30 days)
    @Query("{'orderDate': {$gte: ?0}}")
    List<Order> findRecentOrders(LocalDateTime thirtyDaysAgo);
    
    // Find orders by customer and status
    List<Order> findByCustomerIdAndStatus(String customerId, String status);
    
    // Find orders by customer and date range
    List<Order> findByCustomerIdAndOrderDateBetween(String customerId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find pending orders
    List<Order> findByStatusIn(List<String> statuses);
    
    // Find orders with estimated delivery before date
    List<Order> findByEstimatedDeliveryBefore(LocalDateTime date);
    
    // Find orders with estimated delivery after date
    List<Order> findByEstimatedDeliveryAfter(LocalDateTime date);
    
    // Find delivered orders
    List<Order> findByStatusAndActualDeliveryIsNotNull(String status);
    
    // Find orders by payment method
    @Query("{'paymentInfo.method': ?0}")
    List<Order> findByPaymentMethod(String paymentMethod);
    
    // Find orders by shipping city
    @Query("{'shippingAddress.city': {$regex: ?0, $options: 'i'}}")
    List<Order> findByShippingCity(String city);
    
    // Find orders by shipping state
    @Query("{'shippingAddress.state': {$regex: ?0, $options: 'i'}}")
    List<Order> findByShippingState(String state);
    
    // Find orders by shipping country
    @Query("{'shippingAddress.country': {$regex: ?0, $options: 'i'}}")
    List<Order> findByShippingCountry(String country);
    
    // Find orders containing a specific book
    @Query("{'items.bookId': ?0}")
    List<Order> findOrdersContainingBook(String bookId);
    
    // Find orders containing books by author
    @Query("{'items.author': {$regex: ?0, $options: 'i'}}")
    List<Order> findOrdersContainingBooksByAuthor(String author);
    
    // Find orders containing books by title
    @Query("{'items.title': {$regex: ?0, $options: 'i'}}")
    List<Order> findOrdersContainingBooksByTitle(String title);
    
    // Find high-value orders
    List<Order> findByTotalGreaterThan(Double amount);
    
    // Find low-value orders
    List<Order> findByTotalLessThan(Double amount);
    
    // Count orders by status
    long countByStatus(String status);
    
    // Count orders by customer
    long countByCustomerId(String customerId);
    
    // Count orders by date range
    long countByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Count orders by payment status
    long countByPaymentStatus(String paymentStatus);
    
    // Find top customers by order count
    @Query(value = "{ $group: { _id: '$customerId', orderCount: { $sum: 1 }, totalSpent: { $sum: '$total' } } }", 
           sort = "{ orderCount: -1 }")
    List<Object> findTopCustomersByOrderCount();
    
    // Find top customers by total spent
    @Query(value = "{ $group: { _id: '$customerId', orderCount: { $sum: 1 }, totalSpent: { $sum: '$total' } } }", 
           sort = "{ totalSpent: -1 }")
    List<Object> findTopCustomersByTotalSpent();
    
    // Calculate total revenue
    @Query(value = "{ $group: { _id: null, totalRevenue: { $sum: '$total' } } }")
    List<Object> calculateTotalRevenue();
    
    // Calculate total revenue by date range
    @Query(value = "{ $match: { orderDate: { $gte: ?0, $lte: ?1 } } }, { $group: { _id: null, totalRevenue: { $sum: '$total' } } }")
    List<Object> calculateTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find orders with notes
    @Query("{'notes': {$exists: true, $ne: null, $ne: ''}}")
    List<Order> findOrdersWithNotes();
    
    // Find orders without tracking number
    @Query("{ $or: [ {'trackingNumber': {$exists: false}}, {'trackingNumber': null}, {'trackingNumber': ''} ] }")
    List<Order> findOrdersWithoutTrackingNumber();
    
    // Find orders by transaction ID
    @Query("{'paymentInfo.transactionId': ?0}")
    Optional<Order> findByTransactionId(String transactionId);
    
    // Find orders with discount
    List<Order> findByDiscountGreaterThan(Double discount);
    
    // Find orders with free shipping
    List<Order> findByShippingCostEquals(Double shippingCost);
    
    // Find orders by subtotal range
    List<Order> findBySubtotalBetween(Double minSubtotal, Double maxSubtotal);
    
    // Find latest orders
    List<Order> findTop10ByOrderByOrderDateDesc();
    
    // Find orders by status with pagination
    Page<Order> findByStatus(String status, Pageable pageable);
    
    // Find all orders with pagination and sorting
    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
    
    // Search orders by customer name (requires join or denormalization)
    @Query("{ $lookup: { from: 'customers', localField: 'customerId', foreignField: '_id', as: 'customer' } }, { $match: { 'customer.fullName': { $regex: ?0, $options: 'i' } } }")
    List<Order> findOrdersByCustomerName(String customerName);
}