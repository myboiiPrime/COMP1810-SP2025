package com.bookstore.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "customers")
public class Customer {
    
    @Id
    private String id;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private String phone = "";
    
    private Preferences preferences = new Preferences();
    
    @Pattern(regexp = "Bronze|Silver|Gold|Platinum", message = "Invalid membership level")
    private String membershipLevel = "Bronze";
    
    @Min(0)
    private Integer loyaltyPoints = 0;
    
    @Min(0)
    private Double totalSpent = 0.0;
    
    @Min(0)
    private Integer orderCount = 0;
    
    private List<WishlistItem> wishlist = new ArrayList<>();
    
    private List<CartItem> cart = new ArrayList<>();
    
    private List<SearchHistoryItem> searchHistory = new ArrayList<>();
    
    private LocalDateTime lastLogin = LocalDateTime.now();
    
    @CreatedDate
    private LocalDateTime registrationDate;
    
    @Pattern(regexp = "customer|admin", message = "Invalid role")
    private String role = "customer";
    
    private Boolean isActive = true;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Nested classes
    public static class Preferences {
        private List<String> favoriteGenres = new ArrayList<>();
        private List<String> favoriteAuthors = new ArrayList<>();
        private PriceRange priceRange = new PriceRange();
        private String language = "English";
        
        // Getters and setters
        public List<String> getFavoriteGenres() { return favoriteGenres; }
        public void setFavoriteGenres(List<String> favoriteGenres) { this.favoriteGenres = favoriteGenres; }
        
        public List<String> getFavoriteAuthors() { return favoriteAuthors; }
        public void setFavoriteAuthors(List<String> favoriteAuthors) { this.favoriteAuthors = favoriteAuthors; }
        
        public PriceRange getPriceRange() { return priceRange; }
        public void setPriceRange(PriceRange priceRange) { this.priceRange = priceRange; }
        
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }
    
    public static class PriceRange {
        @Min(0)
        private Double min = 0.0;
        @Min(0)
        private Double max = 100.0;
        
        // Getters and setters
        public Double getMin() { return min; }
        public void setMin(Double min) { this.min = min; }
        
        public Double getMax() { return max; }
        public void setMax(Double max) { this.max = max; }
    }
    
    public static class WishlistItem {
        @NotBlank
        private String bookId;
        private LocalDateTime addedDate = LocalDateTime.now();
        @Pattern(regexp = "Low|Medium|High")
        private String priority = "Low";
        
        // Getters and setters
        public String getBookId() { return bookId; }
        public void setBookId(String bookId) { this.bookId = bookId; }
        
        public LocalDateTime getAddedDate() { return addedDate; }
        public void setAddedDate(LocalDateTime addedDate) { this.addedDate = addedDate; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }
    
    public static class CartItem {
        @NotBlank
        private String bookId;
        @Min(1)
        private Integer quantity = 1;
        private LocalDateTime addedDate = LocalDateTime.now();
        
        // Getters and setters
        public String getBookId() { return bookId; }
        public void setBookId(String bookId) { this.bookId = bookId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public LocalDateTime getAddedDate() { return addedDate; }
        public void setAddedDate(LocalDateTime addedDate) { this.addedDate = addedDate; }
    }
    
    public static class SearchHistoryItem {
        @NotBlank
        private String query;
        private LocalDateTime timestamp = LocalDateTime.now();
        @Min(0)
        private Integer resultsCount = 0;
        
        // Getters and setters
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Integer getResultsCount() { return resultsCount; }
        public void setResultsCount(Integer resultsCount) { this.resultsCount = resultsCount; }
    }
    
    // Constructors
    public Customer() {}
    
    public Customer(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }
    
    // Business methods
    public void hashPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(this.password);
    }
    
    public boolean checkPassword(String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, this.password);
    }
    
    public void addToCart(String bookId, Integer quantity) {
        CartItem existingItem = cart.stream()
            .filter(item -> item.getBookId().equals(bookId))
            .findFirst()
            .orElse(null);
            
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setBookId(bookId);
            newItem.setQuantity(quantity);
            cart.add(newItem);
        }
    }
    
    public void addToWishlist(String bookId, String priority) {
        boolean exists = wishlist.stream()
            .anyMatch(item -> item.getBookId().equals(bookId));
            
        if (!exists) {
            WishlistItem item = new WishlistItem();
            item.setBookId(bookId);
            item.setPriority(priority != null ? priority : "Low");
            wishlist.add(item);
        }
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Preferences getPreferences() { return preferences; }
    public void setPreferences(Preferences preferences) { this.preferences = preferences; }
    
    public String getMembershipLevel() { return membershipLevel; }
    public void setMembershipLevel(String membershipLevel) { this.membershipLevel = membershipLevel; }
    
    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    
    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
    
    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
    
    public List<WishlistItem> getWishlist() { return wishlist; }
    public void setWishlist(List<WishlistItem> wishlist) { this.wishlist = wishlist; }
    
    public List<CartItem> getCart() { return cart; }
    public void setCart(List<CartItem> cart) { this.cart = cart; }
    
    public List<SearchHistoryItem> getSearchHistory() { return searchHistory; }
    public void setSearchHistory(List<SearchHistoryItem> searchHistory) { this.searchHistory = searchHistory; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper method to check if user is admin
    public boolean isAdmin() {
        return "admin".equals(this.role);
    }
}