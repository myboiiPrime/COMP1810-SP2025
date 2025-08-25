package com.bookstore.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "orders")
public class Order {
    
    @Id
    private String id;
    
    @NotBlank(message = "Customer ID is required")
    @Indexed
    private String customerId;
    
    @Indexed(unique = true)
    private String orderNumber;
    
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItem> items = new ArrayList<>();
    
    @NotNull(message = "Shipping address is required")
    private ShippingAddress shippingAddress;
    
    @NotNull(message = "Payment info is required")
    private PaymentInfo paymentInfo;
    
    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", message = "Subtotal must be non-negative")
    private Double subtotal;
    
    @DecimalMin(value = "0.0", message = "Tax must be non-negative")
    private Double tax = 0.0;
    
    @DecimalMin(value = "0.0", message = "Shipping cost must be non-negative")
    private Double shippingCost = 0.0;
    
    @DecimalMin(value = "0.0", message = "Discount must be non-negative")
    private Double discount = 0.0;
    
    @NotNull(message = "Total is required")
    @DecimalMin(value = "0.0", message = "Total must be non-negative")
    private Double total;
    
    @Pattern(regexp = "Pending|Processing|Shipped|Delivered|Cancelled|Returned", message = "Invalid order status")
    @Indexed
    private String status = "Pending";
    
    @Pattern(regexp = "Pending|Completed|Failed|Refunded", message = "Invalid payment status")
    private String paymentStatus = "Pending";
    
    private String trackingNumber;
    
    private LocalDateTime estimatedDelivery;
    
    private LocalDateTime actualDelivery;
    
    private String notes;
    
    private List<StatusHistory> statusHistory = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime orderDate;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Nested classes
    public static class OrderItem {
        @NotBlank(message = "Book ID is required")
        private String bookId;
        
        @NotBlank(message = "Title is required")
        private String title;
        
        @NotBlank(message = "Author is required")
        private String author;
        
        @NotBlank(message = "ISBN is required")
        private String isbn;
        
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must be non-negative")
        private Double price;
        
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
        
        @NotNull(message = "Subtotal is required")
        @DecimalMin(value = "0.0", message = "Subtotal must be non-negative")
        private Double subtotal;
        
        // Constructors
        public OrderItem() {}
        
        public OrderItem(String bookId, String title, String author, String isbn, Double price, Integer quantity) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.isbn = isbn;
            this.price = price;
            this.quantity = quantity;
            this.subtotal = price * quantity;
        }
        
        // Getters and setters
        public String getBookId() { return bookId; }
        public void setBookId(String bookId) { this.bookId = bookId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    }
    
    public static class ShippingAddress {
        @NotBlank(message = "First name is required")
        private String firstName;
        
        @NotBlank(message = "Last name is required")
        private String lastName;
        
        @NotBlank(message = "Street is required")
        private String street;
        
        @NotBlank(message = "City is required")
        private String city;
        
        @NotBlank(message = "State is required")
        private String state;
        
        @NotBlank(message = "Zip code is required")
        private String zipCode;
        
        @NotBlank(message = "Country is required")
        private String country = "USA";
        
        private String phone;
        
        // Constructors
        public ShippingAddress() {}
        
        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
    
    public static class PaymentInfo {
        @NotBlank(message = "Payment method is required")
        @Pattern(regexp = "Credit Card|Debit Card|PayPal|Bank Transfer|Cash on Delivery", message = "Invalid payment method")
        private String method;
        
        @NotBlank(message = "Transaction ID is required")
        private String transactionId;
        
        @Pattern(regexp = "\\d{4}|^$", message = "Card last four digits must be exactly 4 digits or empty")
        private String cardLastFour;
        
        private String cardBrand;
        
        private LocalDateTime processedAt;
        
        // Constructors
        public PaymentInfo() {}
        
        // Getters and setters
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getCardLastFour() { return cardLastFour; }
        public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }
        
        public String getCardBrand() { return cardBrand; }
        public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }
        
        public LocalDateTime getProcessedAt() { return processedAt; }
        public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    }
    
    public static class StatusHistory {
        @NotBlank(message = "Status is required")
        private String status;
        
        private String notes;
        
        private LocalDateTime timestamp = LocalDateTime.now();
        
        private String updatedBy;
        
        // Constructors
        public StatusHistory() {}
        
        public StatusHistory(String status, String notes, String updatedBy) {
            this.status = status;
            this.notes = notes;
            this.updatedBy = updatedBy;
        }
        
        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public String getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    }
    
    // Constructors
    public Order() {}
    
    public Order(String customerId, List<OrderItem> items, ShippingAddress shippingAddress, PaymentInfo paymentInfo) {
        this.customerId = customerId;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.paymentInfo = paymentInfo;
        calculateTotals();
    }
    
    // Business methods
    public void calculateTotals() {
        this.subtotal = items.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
        this.total = subtotal + tax + shippingCost - discount;
    }
    
    public void updateStatus(String newStatus, String notes, String updatedBy) {
        StatusHistory history = new StatusHistory(this.status, notes, updatedBy);
        statusHistory.add(history);
        this.status = newStatus;
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        calculateTotals();
    }
    
    public void removeItem(String bookId) {
        items.removeIf(item -> item.getBookId().equals(bookId));
        calculateTotals();
    }
    
    public boolean canBeCancelled() {
        return "Pending".equals(status) || "Processing".equals(status);
    }
    
    public boolean isDelivered() {
        return "Delivered".equals(status);
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public ShippingAddress getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddress shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public PaymentInfo getPaymentInfo() { return paymentInfo; }
    public void setPaymentInfo(PaymentInfo paymentInfo) { this.paymentInfo = paymentInfo; }
    
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    
    public Double getTax() { return tax; }
    public void setTax(Double tax) { this.tax = tax; }
    
    public Double getShippingCost() { return shippingCost; }
    public void setShippingCost(Double shippingCost) { this.shippingCost = shippingCost; }
    
    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }
    
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    
    public LocalDateTime getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    
    public LocalDateTime getActualDelivery() { return actualDelivery; }
    public void setActualDelivery(LocalDateTime actualDelivery) { this.actualDelivery = actualDelivery; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<StatusHistory> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<StatusHistory> statusHistory) { this.statusHistory = statusHistory; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}