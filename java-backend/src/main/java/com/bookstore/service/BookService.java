package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Page<Book> getAllBooks(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.searchBooks(query);
    }

    public Page<Book> searchBooks(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.searchBooksWithPagination(query, pageable);
    }

    public List<Book> searchBooksNoPagination(String query) {
        return bookRepository.searchBooks(query);
    }

    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenreIgnoreCase(genre);
    }

    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    public List<Book> getBooksByPriceRange(Double minPrice, Double maxPrice) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Book> getBooksByRatingRange(Double minRating, Double maxRating) {
        return bookRepository.findByRatingBetween(minRating, maxRating);
    }

    public List<Book> getTopRatedBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookRepository.findTopRatedBooks(pageable);
    }

    public List<Book> getBestSellingBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookRepository.findBestSellingBooks(pageable);
    }

    public List<Book> getNewestBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookRepository.findNewestBooks(pageable);
    }

    public List<Book> getFeaturedBooks() {
        return bookRepository.findByFeaturedTrue();
    }

    public List<Book> getInStockBooks() {
        return bookRepository.findByInStockTrue();
    }

    public List<Book> getLowStockBooks(int threshold) {
        return bookRepository.findLowStockBooks(threshold);
    }

    public Book saveBook(Book book) {
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        return bookRepository.save(book);
    }

    public Book updateBook(String id, Book bookDetails) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            
            // Update fields
            if (bookDetails.getTitle() != null) book.setTitle(bookDetails.getTitle());
            if (bookDetails.getAuthor() != null) book.setAuthor(bookDetails.getAuthor());
            if (bookDetails.getIsbn() != null) book.setIsbn(bookDetails.getIsbn());
            if (bookDetails.getGenre() != null) book.setGenre(bookDetails.getGenre());
            if (bookDetails.getPrice() != null) book.setPrice(bookDetails.getPrice());
            if (bookDetails.getDescription() != null) book.setDescription(bookDetails.getDescription());
            if (bookDetails.getYear() != null) book.setYear(bookDetails.getYear());
            if (bookDetails.getPublisher() != null) book.setPublisher(bookDetails.getPublisher());
            if (bookDetails.getPages() != null) book.setPages(bookDetails.getPages());
            if (bookDetails.getLanguage() != null) book.setLanguage(bookDetails.getLanguage());
            if (bookDetails.getStockQuantity() != null) {
                book.setStockQuantity(bookDetails.getStockQuantity());
                book.setInStock(bookDetails.getStockQuantity() > 0);
            }
            if (bookDetails.getTags() != null) book.setTags(bookDetails.getTags());
            if (bookDetails.getCoverImage() != null) book.setCoverImage(bookDetails.getCoverImage());
            if (bookDetails.getFormat() != null) book.setFormat(bookDetails.getFormat());
            if (bookDetails.getCondition() != null) book.setCondition(bookDetails.getCondition());
            if (bookDetails.getWeight() != null) book.setWeight(bookDetails.getWeight());
            if (bookDetails.getDimensions() != null) book.setDimensions(bookDetails.getDimensions());
            
            book.setUpdatedAt(LocalDateTime.now());
            return bookRepository.save(book);
        }
        return null;
    }

    public boolean deleteBook(String id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Map<String, Object> addReview(String bookId, String customerId, String customerName, 
                                       Double rating, String comment) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Book> optionalBook = bookRepository.findById(bookId);
            if (optionalBook.isEmpty()) {
                response.put("success", false);
                response.put("message", "Book not found");
                return response;
            }
            
            Book book = optionalBook.get();
            
            // Check if customer already reviewed this book
            boolean alreadyReviewed = book.getReviews().stream()
                .anyMatch(review -> review.getUserId().equals(customerId));
            
            if (alreadyReviewed) {
                response.put("success", false);
                response.put("message", "You have already reviewed this book");
                return response;
            }
            
            // Add new review
            Book.Review review = new Book.Review();
            review.setUserId(customerId);
            review.setUserName(customerName);
            review.setRating(rating.intValue());
            review.setComment(comment);
            review.setDate(LocalDateTime.now());
            
            book.getReviews().add(review);
            
            // Update book rating
            book.updateRating();
            book.setUpdatedAt(LocalDateTime.now());
            
            bookRepository.save(book);
            
            response.put("success", true);
            response.put("message", "Review added successfully");
            response.put("book", book);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add review: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> updateStock(String bookId, Integer quantity) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Book> optionalBook = bookRepository.findById(bookId);
            if (optionalBook.isEmpty()) {
                response.put("success", false);
                response.put("message", "Book not found");
                return response;
            }
            
            Book book = optionalBook.get();
            book.setStockQuantity(quantity);
            book.setInStock(quantity > 0);
            book.setUpdatedAt(LocalDateTime.now());
            
            bookRepository.save(book);
            
            response.put("success", true);
            response.put("message", "Stock updated successfully");
            response.put("book", book);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update stock: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> getBookStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long totalBooks = bookRepository.count();
            long inStockBooks = bookRepository.countByInStockTrue();
            long outOfStockBooks = bookRepository.countByInStockFalse();
            
            stats.put("totalBooks", totalBooks);
            stats.put("inStockBooks", inStockBooks);
            stats.put("outOfStockBooks", outOfStockBooks);
            stats.put("stockPercentage", totalBooks > 0 ? (double) inStockBooks / totalBooks * 100 : 0);
            
            // Get genre distribution
            List<Object> genreStats = bookRepository.countBooksByGenre();
            stats.put("genreDistribution", genreStats);
            
            // Get author statistics
            List<Object> authorStats = bookRepository.countBooksByAuthor();
            stats.put("topAuthors", authorStats);
            
        } catch (Exception e) {
            stats.put("error", "Failed to get statistics: " + e.getMessage());
        }
        
        return stats;
    }

    public List<String> getAllGenres() {
        return bookRepository.findDistinctGenres();
    }

    public List<String> getAllAuthors() {
        return bookRepository.findDistinctAuthors();
    }

    public List<String> getAllPublishers() {
        return bookRepository.findDistinctPublishers();
    }

    public List<String> getAllLanguages() {
        return bookRepository.findDistinctLanguages();
    }
}