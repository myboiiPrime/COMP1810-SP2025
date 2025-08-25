package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    
    // Find book by ISBN
    Optional<Book> findByIsbn(String isbn);
    
    // Check if ISBN exists
    boolean existsByIsbn(String isbn);
    
    // Find books by title (case-insensitive)
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    // Find books by author (case-insensitive)
    @Query("{'author': {$regex: ?0, $options: 'i'}}")
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    // Find books by genre
    List<Book> findByGenre(String genre);
    
    // Find books by genre (case-insensitive)
    @Query("{'genre': {$regex: ?0, $options: 'i'}}")
    List<Book> findByGenreContainingIgnoreCase(String genre);
    
    // Find books in stock
    List<Book> findByInStockTrue();
    
    // Find books by price range
    List<Book> findByPriceBetween(Double minPrice, Double maxPrice);
    
    // Find books by rating range
    List<Book> findByRatingBetween(Double minRating, Double maxRating);
    
    // Find books by year
    List<Book> findByYear(Integer year);
    
    // Find books by year range
    List<Book> findByYearBetween(Integer startYear, Integer endYear);
    
    // Find books by publisher
    @Query("{'publisher': {$regex: ?0, $options: 'i'}}")
    List<Book> findByPublisherContainingIgnoreCase(String publisher);
    
    // Find books by language
    List<Book> findByLanguage(String language);
    
    // Find books by tags
    @Query("{'tags': {$in: [?0]}}")
    List<Book> findByTagsContaining(String tag);
    
    // Find books by multiple tags
    @Query("{'tags': {$in: ?0}}")
    List<Book> findByTagsIn(List<String> tags);
    
    // Find books by format
    List<Book> findByFormat(String format);
    
    // Find books by condition
    List<Book> findByCondition(String condition);
    
    // Search books by multiple criteria (title, author, genre, or ISBN)
    @Query("{ $or: [ {'title': {$regex: ?0, $options: 'i'}}, {'author': {$regex: ?0, $options: 'i'}}, {'genre': {$regex: ?0, $options: 'i'}}, {'isbn': {$regex: ?0, $options: 'i'}} ] }")
    List<Book> searchBooks(String searchTerm);
    
    // Advanced search with pagination (includes ISBN)
    @Query("{ $or: [ {'title': {$regex: ?0, $options: 'i'}}, {'author': {$regex: ?0, $options: 'i'}}, {'genre': {$regex: ?0, $options: 'i'}}, {'isbn': {$regex: ?0, $options: 'i'}}, {'tags': {$regex: ?0, $options: 'i'}} ] }")
    Page<Book> searchBooksWithPagination(String searchTerm, Pageable pageable);
    
    // Find top-rated books
    List<Book> findTop10ByOrderByRatingDesc();
    
    // Find best-selling books
    List<Book> findTop10ByOrderByTotalSalesDesc();
    
    // Find newest books
    List<Book> findTop10ByOrderByCreatedAtDesc();
    
    // Find books with low stock
    List<Book> findByStockQuantityLessThan(Integer threshold);
    
    // Find books with no stock
    List<Book> findByStockQuantityEquals(Integer quantity);
    
    // Find books by price less than
    List<Book> findByPriceLessThan(Double maxPrice);
    
    // Find books by price greater than
    List<Book> findByPriceGreaterThan(Double minPrice);
    
    // Find books by rating greater than
    List<Book> findByRatingGreaterThan(Double minRating);
    
    // Find books with reviews
    @Query("{'reviews': {$exists: true, $not: {$size: 0}}}")
    List<Book> findBooksWithReviews();
    
    // Find books without reviews
    @Query("{ $or: [ {'reviews': {$exists: false}}, {'reviews': {$size: 0}} ] }")
    List<Book> findBooksWithoutReviews();
    
    // Count books by genre
    long countByGenre(String genre);
    
    // Count books in stock
    long countByInStockTrue();
    
    // Count books that are in stock and have low stock quantity
    long countByInStockTrueAndStockQuantityLessThan(Integer threshold);
    
    // Find books that are in stock and have low stock quantity
    List<Book> findByInStockTrueAndStockQuantityLessThan(Integer threshold);
    
    // Count books by author
    long countByAuthor(String author);
    
    // Find books by categories
    @Query("{'categories': {$in: [?0]}}")
    List<Book> findByCategoriesContaining(String category);
    
    // Find books by multiple categories
    @Query("{'categories': {$in: ?0}}")
    List<Book> findByCategoriesIn(List<String> categories);
    
    // Find books with pagination
    Page<Book> findByInStockTrue(Pageable pageable);
    
    // Find books by genre with pagination
    Page<Book> findByGenre(String genre, Pageable pageable);
    
    // Find books by author with pagination
    @Query("{'author': {$regex: ?0, $options: 'i'}}")
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    
    // Find books by price range with pagination
    Page<Book> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
    
    // Get distinct genres
    @Query(value = "{}", fields = "{'genre': 1}")
    List<String> findDistinctGenres();
    
    // Get distinct authors
    @Query(value = "{}", fields = "{'author': 1}")
    List<String> findDistinctAuthors();
    
    // Get distinct publishers
    @Query(value = "{}", fields = "{'publisher': 1}")
    List<String> findDistinctPublishers();
    
    // Get distinct languages
    @Query(value = "{}", fields = "{'language': 1}")
    List<String> findDistinctLanguages();
    
    // Count books out of stock
    long countByInStockFalse();
    
    // Count books by genre (aggregation)
    @Query(value = "{}", count = true)
    List<Object> countBooksByGenre();
    
    // Count books by author (aggregation)
    @Query(value = "{}", count = true)
    List<Object> countBooksByAuthor();
    
    // Find best selling books (ordered by totalSales descending)
    @Query(value = "{}", sort = "{ 'totalSales': -1 }")
    List<Book> findBestSellingBooks(Pageable pageable);
    
    // Find newest books (ordered by createdAt descending)
    @Query(value = "{}", sort = "{ 'createdAt': -1 }")
    List<Book> findNewestBooks(Pageable pageable);
    
    // Find featured books
    List<Book> findByFeaturedTrue();
    
    // Find books with low stock
    @Query("{ 'stockQuantity': { $lte: ?0 } }")
    List<Book> findLowStockBooks(int threshold);
    
    // Search by title or author (case insensitive)
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'author': { $regex: ?1, $options: 'i' } } ] }")
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'author': { $regex: ?1, $options: 'i' } } ] }")
    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author, Pageable pageable);
    
    // Find by genre (case insensitive)
    @Query("{ 'genre': { $regex: ?0, $options: 'i' } }")
    List<Book> findByGenreIgnoreCase(String genre);
    
    // Find top rated books (ordered by averageRating descending)
    @Query(value = "{}", sort = "{ 'averageRating': -1 }")
    List<Book> findTopRatedBooks(Pageable pageable);
}