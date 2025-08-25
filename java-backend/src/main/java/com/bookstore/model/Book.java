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

@Document(collection = "books")
public class Book {
    
    @Id
    private String id;
    
    @NotBlank(message = "Title is required")
    @Indexed
    private String title;
    
    @NotBlank(message = "Author is required")
    @Indexed
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @Indexed(unique = true)
    private String isbn;
    
    @NotBlank(message = "Genre is required")
    @Indexed
    private String genre;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    @Indexed
    private Double price;
    
    @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    @Indexed
    private Double rating = 0.0;
    
    private String description;
    
    @Indexed
    private Integer year;
    
    private String publisher;
    
    @Min(value = 1, message = "Pages must be at least 1")
    private Integer pages;
    
    private String language = "English";
    
    @Indexed
    private Boolean inStock = true;
    
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity = 0;
    
    private List<String> tags = new ArrayList<>();
    
    private String coverImage;
    
    private List<Review> reviews = new ArrayList<>();
    
    private Double averageRating = 0.0;
    
    private Integer totalReviews = 0;
    
    private Integer totalSales = 0;
    
    private Double totalRevenue = 0.0;
    
    private List<String> categories = new ArrayList<>();
    
    private String format = "Paperback";
    
    private String condition = "New";
    
    private String weight;
    
    private String dimensions;
    
    private Boolean featured = false;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Nested Review class
    public static class Review {
        private String userId;
        
        private String userName;
        
        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        private Integer rating;
        
        private String comment;
        
        private LocalDateTime date = LocalDateTime.now();
        
        // Constructors
        public Review() {}
        
        public Review(String userId, Integer rating, String comment) {
            this.userId = userId;
            this.rating = rating;
            this.comment = comment;
        }
        
        // Getters and setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
    }
    
    // Constructors
    public Book() {}
    
    public Book(String title, String author, String isbn, String genre, Double price) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.price = price;
    }
    
    // Business methods
    public void addReview(String userId, Integer rating, String comment) {
        Review review = new Review(userId, rating, comment);
        reviews.add(review);
        updateAverageRating();
    }
    
    public void updateAverageRating() {
        if (reviews.isEmpty()) {
            this.averageRating = 0.0;
            this.totalReviews = 0;
        } else {
            double sum = reviews.stream().mapToInt(Review::getRating).sum();
            this.averageRating = sum / reviews.size();
            this.totalReviews = reviews.size();
        }
        this.rating = this.averageRating;
    }
    
    public void updateStock(Integer quantity) {
        this.stockQuantity = Math.max(0, this.stockQuantity + quantity);
        this.inStock = this.stockQuantity > 0;
    }
    
    public void recordSale(Integer quantity, Double salePrice) {
        this.totalSales += quantity;
        this.totalRevenue += (salePrice * quantity);
        updateStock(-quantity);
    }
    
    public boolean isAvailable() {
        return inStock && stockQuantity > 0;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public Boolean getInStock() { return inStock; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
    
    public Integer getTotalSales() { return totalSales; }
    public void setTotalSales(Integer totalSales) { this.totalSales = totalSales; }
    
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }
    
    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }
    
    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public void updateRating() {
        if (reviews != null && !reviews.isEmpty()) {
            double sum = reviews.stream().mapToDouble(Review::getRating).sum();
            this.averageRating = sum / reviews.size();
            this.totalReviews = reviews.size();
        } else {
            this.averageRating = 0.0;
            this.totalReviews = 0;
        }
    }
    
    public void incrementSales(Integer quantity, Double revenue) {
        this.totalSales = (this.totalSales != null ? this.totalSales : 0) + quantity;
        this.totalRevenue = (this.totalRevenue != null ? this.totalRevenue : 0.0) + revenue;
    }
}