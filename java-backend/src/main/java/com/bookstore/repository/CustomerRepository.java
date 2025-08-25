package com.bookstore.repository;

import com.bookstore.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    
    // Find customer by email
    Optional<Customer> findByEmail(String email);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find customers by membership level
    List<Customer> findByMembershipLevel(String membershipLevel);
    
    // Find active customers
    List<Customer> findByIsActiveTrue();
    
    // Find customers by registration date range
    List<Customer> findByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Count customers by registration date range
    long countByRegistrationDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Search customers by full name or email (case-insensitive)
    List<Customer> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String fullName, String email);
    
    // Find customers by role
    List<Customer> findByRole(String role);
    
    // Find customers with total spent greater than amount
    List<Customer> findByTotalSpentGreaterThan(Double amount);
    
    // Find customers by loyalty points range
    List<Customer> findByLoyaltyPointsBetween(Integer minPoints, Integer maxPoints);
    
    // Find customers who registered after a certain date
    List<Customer> findByRegistrationDateAfter(LocalDateTime date);
    
    // Find customers who logged in recently
    List<Customer> findByLastLoginAfter(LocalDateTime date);
    
    // Find customers by favorite genre
    @Query("{'preferences.favoriteGenres': ?0}")
    List<Customer> findByFavoriteGenre(String genre);
    
    // Find customers by favorite author
    @Query("{'preferences.favoriteAuthors': ?0}")
    List<Customer> findByFavoriteAuthor(String author);
    
    // Find top customers by total spent
    List<Customer> findTop10ByOrderByTotalSpentDesc();
    
    // Find customers with items in cart
    @Query("{'cart': {$exists: true, $not: {$size: 0}}}")
    List<Customer> findCustomersWithItemsInCart();
    
    // Find customers with items in wishlist
    @Query("{'wishlist': {$exists: true, $not: {$size: 0}}}")
    List<Customer> findCustomersWithItemsInWishlist();
    
    // Search customers by name (case-insensitive)
    @Query("{'fullName': {$regex: ?0, $options: 'i'}}")
    List<Customer> findByFullNameContainingIgnoreCase(String name);
    
    // Find customers by phone number
    Optional<Customer> findByPhone(String phone);
    
    // Count customers by membership level
    long countByMembershipLevel(String membershipLevel);
    
    // Count active customers
    long countByIsActiveTrue();
    
    // Find customers with order count greater than
    List<Customer> findByOrderCountGreaterThan(Integer orderCount);
    
    // Find customers by price range preference
    @Query("{'preferences.priceRange.min': {$lte: ?0}, 'preferences.priceRange.max': {$gte: ?1}}")
    List<Customer> findByPriceRangePreference(Double minPrice, Double maxPrice);
    
    // Find customers by language preference
    @Query("{'preferences.language': ?0}")
    List<Customer> findByLanguagePreference(String language);
}