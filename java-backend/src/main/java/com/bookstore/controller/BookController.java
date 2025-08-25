package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<Object> getAllBooks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(defaultValue = "false") boolean inStock) {
        
        try {
            if (search != null && !search.trim().isEmpty()) {
                if (page != null && size != null) {
                    Page<Book> books = bookService.searchBooks(search.trim(), page, size);
                    Map<String, Object> response = new HashMap<>();
                    response.put("books", books.getContent());
                    response.put("totalPages", books.getTotalPages());
                    response.put("totalElements", books.getTotalElements());
                    response.put("currentPage", page);
                    response.put("size", size);
                    return ResponseEntity.ok(response);
                } else {
                    // Use non-paginated search - need to implement this in service
                    List<Book> searchBooks = bookService.searchBooksNoPagination(search.trim());
                    return ResponseEntity.ok(Map.of("books", searchBooks));
                }
            }
            
            // Apply filters
            List<Book> books;
            if (genre != null && !genre.trim().isEmpty()) {
                books = bookService.getBooksByGenre(genre.trim());
            } else if (author != null && !author.trim().isEmpty()) {
                books = bookService.getBooksByAuthor(author.trim());
            } else if (minPrice != null && maxPrice != null) {
                books = bookService.getBooksByPriceRange(minPrice, maxPrice);
            } else if (minRating != null && maxRating != null) {
                books = bookService.getBooksByRatingRange(minRating, maxRating);
            } else if (inStock) {
                books = bookService.getInStockBooks();
            } else {
                if (page != null && size != null) {
                    Page<Book> bookPage = bookService.getAllBooks(page, size, sortBy, sortDir);
                    Map<String, Object> response = new HashMap<>();
                    response.put("books", bookPage.getContent());
                    response.put("totalPages", bookPage.getTotalPages());
                    response.put("totalElements", bookPage.getTotalElements());
                    response.put("currentPage", page);
                    response.put("size", size);
                    return ResponseEntity.ok(response);
                } else {
                    // Use non-paginated version
                    books = bookService.getAllBooks();
                    return ResponseEntity.ok(Map.of("books", books));
                }
            }
            
            // Return filtered books
            return ResponseEntity.ok(Map.of("books", books));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch books: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable String id) {
        try {
            Optional<Book> book = bookService.getBookById(id);
            if (book.isPresent()) {
                return ResponseEntity.ok(book.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch book: " + e.getMessage()));
        }
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Object> getBookByIsbn(@PathVariable String isbn) {
        try {
            Optional<Book> book = bookService.getBookByIsbn(isbn);
            if (book.isPresent()) {
                return ResponseEntity.ok(book.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch book: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchBooks(
            @RequestParam String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        
        try {
            if (page != null && size != null) {
                Page<Book> books = bookService.searchBooks(q, page, size);
                Map<String, Object> response = new HashMap<>();
                response.put("books", books.getContent());
                response.put("totalPages", books.getTotalPages());
                response.put("totalElements", books.getTotalElements());
                response.put("currentPage", page);
                response.put("size", size);
                response.put("query", q);
                return ResponseEntity.ok(response);
            } else {
                List<Book> books = bookService.searchBooksNoPagination(q);
                Map<String, Object> response = new HashMap<>();
                response.put("books", books);
                response.put("query", q);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Search failed: " + e.getMessage()));
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<Object> getFeaturedBooks() {
        try {
            List<Book> books = bookService.getFeaturedBooks();
            return ResponseEntity.ok(Map.of("books", books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch featured books: " + e.getMessage()));
        }
    }

    @GetMapping("/top-rated")
    public ResponseEntity<Object> getTopRatedBooks(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Book> books = bookService.getTopRatedBooks(limit);
            return ResponseEntity.ok(Map.of("books", books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch top-rated books: " + e.getMessage()));
        }
    }

    @GetMapping("/best-selling")
    public ResponseEntity<Object> getBestSellingBooks(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Book> books = bookService.getBestSellingBooks(limit);
            return ResponseEntity.ok(Map.of("books", books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch best-selling books: " + e.getMessage()));
        }
    }

    @GetMapping("/newest")
    public ResponseEntity<Object> getNewestBooks(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Book> books = bookService.getNewestBooks(limit);
            return ResponseEntity.ok(Map.of("books", books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch newest books: " + e.getMessage()));
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<Object> getLowStockBooks(
            @RequestParam(defaultValue = "10") int threshold) {
        try {
            List<Book> books = bookService.getLowStockBooks(threshold);
            return ResponseEntity.ok(Map.of("books", books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch low-stock books: " + e.getMessage()));
        }
    }

    @GetMapping("/genres")
    public ResponseEntity<Object> getAllGenres() {
        try {
            List<String> genres = bookService.getAllGenres();
            return ResponseEntity.ok(Map.of("genres", genres));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch genres: " + e.getMessage()));
        }
    }

    @GetMapping("/authors")
    public ResponseEntity<Object> getAllAuthors() {
        try {
            List<String> authors = bookService.getAllAuthors();
            return ResponseEntity.ok(Map.of("authors", authors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch authors: " + e.getMessage()));
        }
    }

    @GetMapping("/publishers")
    public ResponseEntity<Object> getAllPublishers() {
        try {
            List<String> publishers = bookService.getAllPublishers();
            return ResponseEntity.ok(Map.of("publishers", publishers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch publishers: " + e.getMessage()));
        }
    }

    @GetMapping("/languages")
    public ResponseEntity<Object> getAllLanguages() {
        try {
            List<String> languages = bookService.getAllLanguages();
            return ResponseEntity.ok(Map.of("languages", languages));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch languages: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Object> getBookStatistics() {
        try {
            Map<String, Object> stats = bookService.getBookStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch statistics: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> createBook(@Valid @RequestBody Book book) {
        try {
            Book savedBook = bookService.saveBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create book: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBook(@PathVariable String id, @RequestBody Book book) {
        try {
            Book updatedBook = bookService.updateBook(id, book);
            if (updatedBook != null) {
                return ResponseEntity.ok(updatedBook);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update book: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable String id) {
        try {
            boolean deleted = bookService.deleteBook(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete book: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<Object> addReview(
            @PathVariable String id,
            @RequestBody ReviewRequest request,
            @RequestAttribute("customerId") String customerId,
            @RequestAttribute("username") String username) {
        
        try {
            Map<String, Object> response = bookService.addReview(
                id, customerId, username, request.getRating(), request.getComment()
            );
            
            if ((Boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to add review: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Object> updateStock(
            @PathVariable String id,
            @RequestBody StockUpdateRequest request) {
        
        try {
            Map<String, Object> response = bookService.updateStock(id, request.getQuantity());
            
            if ((Boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update stock: " + e.getMessage()));
        }
    }

    // Request DTOs
    public static class ReviewRequest {
        private Double rating;
        private String comment;

        // Getters and setters
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    public static class StockUpdateRequest {
        private Integer quantity;

        // Getters and setters
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}