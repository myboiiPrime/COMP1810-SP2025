package com.bookstore.service;

import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Order;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CustomerRepository;
import com.bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Get dashboard overview statistics
     */
    public DashboardOverview getDashboardOverview(int periodDays) {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusDays(periodDays);

            // Basic counts with null safety
            long totalBooks = 0;
            long totalCustomers = 0;
            long totalOrders = 0;
            
            try {
                totalBooks = bookRepository.countByInStockTrue();
            } catch (Exception e) {
                System.err.println("Error counting books: " + e.getMessage());
            }
            
            try {
                totalCustomers = customerRepository.countByIsActiveTrue();
            } catch (Exception e) {
                System.err.println("Error counting customers: " + e.getMessage());
            }
            
            try {
                totalOrders = orderRepository.count();
            } catch (Exception e) {
                System.err.println("Error counting orders: " + e.getMessage());
            }

            // Revenue statistics with safer approach
            double totalRevenue = 0.0;
            double averageOrderValue = 0.0;
            
            try {
                List<Order> allOrders = orderRepository.findAll();
                if (allOrders != null && !allOrders.isEmpty()) {
                    totalRevenue = allOrders.stream()
                            .filter(order -> order != null && order.getTotal() != null)
                            .mapToDouble(Order::getTotal)
                            .sum();
                    averageOrderValue = totalRevenue / allOrders.size();
                }
            } catch (Exception e) {
                System.err.println("Error calculating revenue: " + e.getMessage());
            }

            // Recent period statistics
            long recentOrderCount = 0;
            double recentRevenue = 0.0;
            
            try {
                List<Order> recentOrders = orderRepository.findByOrderDateAfter(startDate);
                if (recentOrders != null) {
                    recentOrderCount = recentOrders.size();
                    recentRevenue = recentOrders.stream()
                            .filter(order -> order != null && order.getTotal() != null)
                            .mapToDouble(Order::getTotal)
                            .sum();
                }
            } catch (Exception e) {
                System.err.println("Error calculating recent orders: " + e.getMessage());
            }

            // Low stock books
            long lowStockBooks = 0;
            try {
                lowStockBooks = bookRepository.countByInStockTrueAndStockQuantityLessThan(10);
            } catch (Exception e) {
                System.err.println("Error counting low stock books: " + e.getMessage());
            }

            // Pending orders
            long pendingOrders = 0;
            try {
                pendingOrders = orderRepository.countByStatus("pending");
            } catch (Exception e) {
                System.err.println("Error counting pending orders: " + e.getMessage());
            }

            return new DashboardOverview(
                    totalBooks, totalCustomers, totalOrders, totalRevenue,
                    averageOrderValue, lowStockBooks, pendingOrders,
                    periodDays, recentOrderCount, recentRevenue
            );
        } catch (Exception e) {
            System.err.println("Error in getDashboardOverview: " + e.getMessage());
            e.printStackTrace();
            // Return default values in case of error
            return new DashboardOverview(0, 0, 0, 0.0, 0.0, 0, 0, periodDays, 0, 0.0);
        }
    }

    /**
     * Get sales analytics
     */
    public SalesAnalytics getSalesAnalytics(int periodDays, String groupBy) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(periodDays);
        List<String> completedStatuses = Arrays.asList("completed", "shipped", "delivered");

        // Get sales data grouped by time period
        List<Order> salesOrders = orderRepository.findByOrderDateAfterAndStatusIn(startDate, completedStatuses);
        
        Map<String, SalesTrendData> salesTrend = new HashMap<>();
        for (Order order : salesOrders) {
            String key = formatDateByGrouping(order.getOrderDate(), groupBy);
            salesTrend.computeIfAbsent(key, k -> new SalesTrendData(k, 0.0, 0, 0.0))
                    .addOrder(order.getTotal());
        }

        // Get top selling books
        Map<String, TopSellingBook> bookSales = new HashMap<>();
        for (Order order : salesOrders) {
            for (Order.OrderItem item : order.getItems()) {
                String bookId = item.getBookId();
                bookSales.computeIfAbsent(bookId, k -> {
                    Optional<Book> book = bookRepository.findById(k);
                    return book.map(b -> new TopSellingBook(b.getTitle(), b.getAuthor(), 0, 0.0))
                            .orElse(new TopSellingBook("Unknown", "Unknown", 0, 0.0));
                }).addSale(item.getQuantity(), item.getPrice() * item.getQuantity());
            }
        }

        List<TopSellingBook> topBooks = bookSales.values().stream()
                .sorted((a, b) -> Integer.compare(b.getTotalQuantity(), a.getTotalQuantity()))
                .limit(10)
                .collect(Collectors.toList());

        return new SalesAnalytics(periodDays, groupBy, new ArrayList<>(salesTrend.values()), topBooks);
    }

    /**
     * Get customer analytics
     */
    public CustomerAnalytics getCustomerAnalytics(int periodDays) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(periodDays);

        // Customer registration trend
        List<Customer> newCustomers = customerRepository.findByRegistrationDateAfter(startDate);
        Map<String, Integer> registrationTrend = new HashMap<>();
        for (Customer customer : newCustomers) {
            String key = formatDateByGrouping(customer.getRegistrationDate(), "day");
            registrationTrend.put(key, registrationTrend.getOrDefault(key, 0) + 1);
        }

        // Customer segmentation by membership level
        Map<String, Long> membershipDistribution = new HashMap<>();
        membershipDistribution.put("bronze", customerRepository.countByMembershipLevel("bronze"));
        membershipDistribution.put("silver", customerRepository.countByMembershipLevel("silver"));
        membershipDistribution.put("gold", customerRepository.countByMembershipLevel("gold"));
        membershipDistribution.put("platinum", customerRepository.countByMembershipLevel("platinum"));

        // Top customers by spending
        List<Customer> topCustomers = customerRepository.findTop10ByOrderByTotalSpentDesc();

        // Customer activity metrics
        long activeCustomers = customerRepository.countByIsActiveTrue();
        long totalCustomers = customerRepository.count();
        double activityRate = totalCustomers > 0 ? (double) activeCustomers / totalCustomers : 0;

        return new CustomerAnalytics(
                periodDays, registrationTrend, membershipDistribution,
                topCustomers, activeCustomers, totalCustomers, activityRate
        );
    }

    /**
     * Get inventory analytics
     */
    public InventoryAnalytics getInventoryAnalytics() {
        // Stock overview
        long totalBooks = bookRepository.count();
        long inStockBooks = bookRepository.countByInStockTrue();
        long outOfStockBooks = totalBooks - inStockBooks;
        long lowStockBooks = bookRepository.countByInStockTrueAndStockQuantityLessThan(10);

        List<Book> allBooks = bookRepository.findAll();
        double totalStockValue = allBooks.stream()
                .mapToDouble(book -> book.getStockQuantity() * book.getPrice())
                .sum();
        double averagePrice = allBooks.stream()
                .mapToDouble(Book::getPrice)
                .average()
                .orElse(0.0);

        // Category distribution
        Map<String, CategoryStats> categoryDistribution = new HashMap<>();
        for (Book book : allBooks) {
            String category = book.getCategories().isEmpty() ? book.getGenre() : book.getCategories().get(0);
            categoryDistribution.computeIfAbsent(category, k -> new CategoryStats(k, 0, 0.0, 0.0, 0))
                    .addBook(book);
        }

        // Low stock alerts
        List<Book> lowStockAlerts = bookRepository.findByInStockTrueAndStockQuantityLessThan(10)
                .stream()
                .sorted(Comparator.comparingInt(Book::getStockQuantity))
                .limit(20)
                .collect(Collectors.toList());

        // Price range analysis
        Map<String, PriceRangeStats> priceRanges = new HashMap<>();
        String[] ranges = {"0-10", "10-20", "20-30", "30-50", "50-100", "100+"};
        for (String range : ranges) {
            priceRanges.put(range, new PriceRangeStats(range, 0, 0.0, 0));
        }

        for (Book book : allBooks) {
            String range = getPriceRange(book.getPrice());
            priceRanges.get(range).addBook(book);
        }

        return new InventoryAnalytics(
                totalBooks, inStockBooks, outOfStockBooks, lowStockBooks,
                totalStockValue, averagePrice, new ArrayList<>(categoryDistribution.values()),
                lowStockAlerts, new ArrayList<>(priceRanges.values())
        );
    }

    /**
     * Get algorithm performance metrics
     */
    public AlgorithmMetrics getAlgorithmMetrics() {
        // Generate sample algorithm performance data
        Random random = new Random();

        SearchPerformance searchPerformance = new SearchPerformance(
                5000 + random.nextInt(5000),
                50 + random.nextInt(50),
                Arrays.asList(
                        new SearchTerm("javascript", 245),
                        new SearchTerm("python", 198),
                        new SearchTerm("react", 167),
                        new SearchTerm("nodejs", 134),
                        new SearchTerm("mongodb", 98)
                ),
                0.85 + random.nextDouble() * 0.1
        );

        SortingPerformance sortingPerformance = new SortingPerformance(
                Arrays.asList("mergesort", "heapsort"),
                Map.of(
                        "mergesort", 15 + random.nextInt(45),
                        "heapsort", 12 + random.nextInt(43)
                ),
                Arrays.asList(100, 500, 1000, 5000),
                Arrays.asList(
                        new PerformanceComparison(100, 3, 2.5),
                        new PerformanceComparison(500, 12, 10),
                        new PerformanceComparison(1000, 25, 22),
                        new PerformanceComparison(5000, 140, 125)
                )
        );

        RecommendationPerformance recommendationPerformance = new RecommendationPerformance(
                Arrays.asList("collaborative_filtering", "content_based", "hybrid"),
                Map.of(
                        "collaborative_filtering", 0.75,
                        "content_based", 0.68,
                        "hybrid", 0.82
                ),
                Map.of(
                        "collaborative_filtering", 150,
                        "content_based", 80,
                        "hybrid", 200
                )
        );

        QueuePerformance queuePerformance = new QueuePerformance(
                new QueueStats(random.nextInt(100), random.nextInt(50), random.nextInt(20)),
                new QueueStats(random.nextInt(200), random.nextInt(30), random.nextInt(10))
        );

        return new AlgorithmMetrics(
                searchPerformance, sortingPerformance,
                recommendationPerformance, queuePerformance,
                LocalDateTime.now()
        );
    }

    /**
     * Export data based on type
     */
    public List<?> getExportData(String type, int periodDays) {
        switch (type.toLowerCase()) {
            case "sales":
                LocalDateTime startDate = LocalDateTime.now().minusDays(periodDays);
                return orderRepository.findByOrderDateAfter(startDate);
            case "customers":
                return customerRepository.findAll();
            case "inventory":
                return bookRepository.findAll();
            default:
                throw new IllegalArgumentException("Invalid export type: " + type);
        }
    }

    // Helper methods
    private String formatDateByGrouping(LocalDateTime date, String groupBy) {
        switch (groupBy.toLowerCase()) {
            case "hour":
                return String.format("%d-%02d-%02d %02d:00", 
                        date.getYear(), date.getMonthValue(), date.getDayOfMonth(), date.getHour());
            case "day":
                return String.format("%d-%02d-%02d", 
                        date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            case "week":
                return String.format("%d-W%02d", date.getYear(), date.getDayOfYear() / 7);
            case "month":
                return String.format("%d-%02d", date.getYear(), date.getMonthValue());
            default:
                return String.format("%d-%02d-%02d", 
                        date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
    }

    private String getPriceRange(double price) {
        if (price < 10) return "0-10";
        if (price < 20) return "10-20";
        if (price < 30) return "20-30";
        if (price < 50) return "30-50";
        if (price < 100) return "50-100";
        return "100+";
    }

    // Inner classes for response DTOs
    public static class DashboardOverview {
        private long totalBooks;
        private long totalCustomers;
        private long totalOrders;
        private double totalRevenue;
        private double averageOrderValue;
        private long lowStockBooks;
        private long pendingOrders;
        private int period;
        private long recentOrders;
        private double recentRevenue;

        public DashboardOverview(long totalBooks, long totalCustomers, long totalOrders,
                               double totalRevenue, double averageOrderValue, long lowStockBooks,
                               long pendingOrders, int period, long recentOrders, double recentRevenue) {
            this.totalBooks = totalBooks;
            this.totalCustomers = totalCustomers;
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.averageOrderValue = averageOrderValue;
            this.lowStockBooks = lowStockBooks;
            this.pendingOrders = pendingOrders;
            this.period = period;
            this.recentOrders = recentOrders;
            this.recentRevenue = recentRevenue;
        }

        // Getters
        public long getTotalBooks() { return totalBooks; }
        public long getTotalCustomers() { return totalCustomers; }
        public long getTotalOrders() { return totalOrders; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getAverageOrderValue() { return averageOrderValue; }
        public long getLowStockBooks() { return lowStockBooks; }
        public long getPendingOrders() { return pendingOrders; }
        public int getPeriod() { return period; }
        public long getRecentOrders() { return recentOrders; }
        public double getRecentRevenue() { return recentRevenue; }
    }

    public static class SalesAnalytics {
        private int period;
        private String groupBy;
        private List<SalesTrendData> salesTrend;
        private List<TopSellingBook> topSellingBooks;

        public SalesAnalytics(int period, String groupBy, List<SalesTrendData> salesTrend, List<TopSellingBook> topSellingBooks) {
            this.period = period;
            this.groupBy = groupBy;
            this.salesTrend = salesTrend;
            this.topSellingBooks = topSellingBooks;
        }

        // Getters
        public int getPeriod() { return period; }
        public String getGroupBy() { return groupBy; }
        public List<SalesTrendData> getSalesTrend() { return salesTrend; }
        public List<TopSellingBook> getTopSellingBooks() { return topSellingBooks; }
    }

    public static class SalesTrendData {
        private String period;
        private double totalSales;
        private int orderCount;
        private double averageOrderValue;

        public SalesTrendData(String period, double totalSales, int orderCount, double averageOrderValue) {
            this.period = period;
            this.totalSales = totalSales;
            this.orderCount = orderCount;
            this.averageOrderValue = averageOrderValue;
        }

        public void addOrder(double amount) {
            this.totalSales += amount;
            this.orderCount++;
            this.averageOrderValue = this.totalSales / this.orderCount;
        }

        // Getters
        public String getPeriod() { return period; }
        public double getTotalSales() { return totalSales; }
        public int getOrderCount() { return orderCount; }
        public double getAverageOrderValue() { return averageOrderValue; }
    }

    public static class TopSellingBook {
        private String title;
        private String author;
        private int totalQuantity;
        private double totalRevenue;

        public TopSellingBook(String title, String author, int totalQuantity, double totalRevenue) {
            this.title = title;
            this.author = author;
            this.totalQuantity = totalQuantity;
            this.totalRevenue = totalRevenue;
        }

        public void addSale(int quantity, double revenue) {
            this.totalQuantity += quantity;
            this.totalRevenue += revenue;
        }

        // Getters
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public int getTotalQuantity() { return totalQuantity; }
        public double getTotalRevenue() { return totalRevenue; }
    }

    public static class CustomerAnalytics {
        private int period;
        private Map<String, Integer> registrationTrend;
        private Map<String, Long> membershipDistribution;
        private List<Customer> topCustomers;
        private long activeCustomers;
        private long totalCustomers;
        private double activityRate;

        public CustomerAnalytics(int period, Map<String, Integer> registrationTrend,
                               Map<String, Long> membershipDistribution, List<Customer> topCustomers,
                               long activeCustomers, long totalCustomers, double activityRate) {
            this.period = period;
            this.registrationTrend = registrationTrend;
            this.membershipDistribution = membershipDistribution;
            this.topCustomers = topCustomers;
            this.activeCustomers = activeCustomers;
            this.totalCustomers = totalCustomers;
            this.activityRate = activityRate;
        }

        // Getters
        public int getPeriod() { return period; }
        public Map<String, Integer> getRegistrationTrend() { return registrationTrend; }
        public Map<String, Long> getMembershipDistribution() { return membershipDistribution; }
        public List<Customer> getTopCustomers() { return topCustomers; }
        public long getActiveCustomers() { return activeCustomers; }
        public long getTotalCustomers() { return totalCustomers; }
        public double getActivityRate() { return activityRate; }
    }

    public static class InventoryAnalytics {
        private long totalBooks;
        private long inStockBooks;
        private long outOfStockBooks;
        private long lowStockBooks;
        private double totalStockValue;
        private double averagePrice;
        private List<CategoryStats> categoryDistribution;
        private List<Book> lowStockAlerts;
        private List<PriceRangeStats> priceRangeAnalysis;

        public InventoryAnalytics(long totalBooks, long inStockBooks, long outOfStockBooks,
                                long lowStockBooks, double totalStockValue, double averagePrice,
                                List<CategoryStats> categoryDistribution, List<Book> lowStockAlerts,
                                List<PriceRangeStats> priceRangeAnalysis) {
            this.totalBooks = totalBooks;
            this.inStockBooks = inStockBooks;
            this.outOfStockBooks = outOfStockBooks;
            this.lowStockBooks = lowStockBooks;
            this.totalStockValue = totalStockValue;
            this.averagePrice = averagePrice;
            this.categoryDistribution = categoryDistribution;
            this.lowStockAlerts = lowStockAlerts;
            this.priceRangeAnalysis = priceRangeAnalysis;
        }

        // Getters
        public long getTotalBooks() { return totalBooks; }
        public long getInStockBooks() { return inStockBooks; }
        public long getOutOfStockBooks() { return outOfStockBooks; }
        public long getLowStockBooks() { return lowStockBooks; }
        public double getTotalStockValue() { return totalStockValue; }
        public double getAveragePrice() { return averagePrice; }
        public List<CategoryStats> getCategoryDistribution() { return categoryDistribution; }
        public List<Book> getLowStockAlerts() { return lowStockAlerts; }
        public List<PriceRangeStats> getPriceRangeAnalysis() { return priceRangeAnalysis; }
    }

    public static class CategoryStats {
        private String category;
        private int count;
        private double averagePrice;
        private double totalStockValue;
        private int inStockCount;

        public CategoryStats(String category, int count, double averagePrice, double totalStockValue, int inStockCount) {
            this.category = category;
            this.count = count;
            this.averagePrice = averagePrice;
            this.totalStockValue = totalStockValue;
            this.inStockCount = inStockCount;
        }

        public void addBook(Book book) {
            this.count++;
            this.totalStockValue += book.getStockQuantity() * book.getPrice();
            this.averagePrice = (this.averagePrice * (this.count - 1) + book.getPrice()) / this.count;
            if (book.getInStock()) {
                this.inStockCount++;
            }
        }

        // Getters
        public String getCategory() { return category; }
        public int getCount() { return count; }
        public double getAveragePrice() { return averagePrice; }
        public double getTotalStockValue() { return totalStockValue; }
        public int getInStockCount() { return inStockCount; }
    }

    public static class PriceRangeStats {
        private String range;
        private int count;
        private double averageRating;
        private int totalStock;

        public PriceRangeStats(String range, int count, double averageRating, int totalStock) {
            this.range = range;
            this.count = count;
            this.averageRating = averageRating;
            this.totalStock = totalStock;
        }

        public void addBook(Book book) {
            this.count++;
            this.averageRating = (this.averageRating * (this.count - 1) + book.getRating()) / this.count;
            this.totalStock += book.getStockQuantity();
        }

        // Getters
        public String getRange() { return range; }
        public int getCount() { return count; }
        public double getAverageRating() { return averageRating; }
        public int getTotalStock() { return totalStock; }
    }

    public static class AlgorithmMetrics {
        private SearchPerformance searchPerformance;
        private SortingPerformance sortingPerformance;
        private RecommendationPerformance recommendationPerformance;
        private QueuePerformance queuePerformance;
        private LocalDateTime lastUpdated;

        public AlgorithmMetrics(SearchPerformance searchPerformance, SortingPerformance sortingPerformance,
                              RecommendationPerformance recommendationPerformance, QueuePerformance queuePerformance,
                              LocalDateTime lastUpdated) {
            this.searchPerformance = searchPerformance;
            this.sortingPerformance = sortingPerformance;
            this.recommendationPerformance = recommendationPerformance;
            this.queuePerformance = queuePerformance;
            this.lastUpdated = lastUpdated;
        }

        // Getters
        public SearchPerformance getSearchPerformance() { return searchPerformance; }
        public SortingPerformance getSortingPerformance() { return sortingPerformance; }
        public RecommendationPerformance getRecommendationPerformance() { return recommendationPerformance; }
        public QueuePerformance getQueuePerformance() { return queuePerformance; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
    }

    public static class SearchPerformance {
        private int totalSearches;
        private int averageResponseTime;
        private List<SearchTerm> popularSearchTerms;
        private double searchSuccessRate;

        public SearchPerformance(int totalSearches, int averageResponseTime, List<SearchTerm> popularSearchTerms, double searchSuccessRate) {
            this.totalSearches = totalSearches;
            this.averageResponseTime = averageResponseTime;
            this.popularSearchTerms = popularSearchTerms;
            this.searchSuccessRate = searchSuccessRate;
        }

        // Getters
        public int getTotalSearches() { return totalSearches; }
        public int getAverageResponseTime() { return averageResponseTime; }
        public List<SearchTerm> getPopularSearchTerms() { return popularSearchTerms; }
        public double getSearchSuccessRate() { return searchSuccessRate; }
    }

    public static class SearchTerm {
        private String term;
        private int count;

        public SearchTerm(String term, int count) {
            this.term = term;
            this.count = count;
        }

        // Getters
        public String getTerm() { return term; }
        public int getCount() { return count; }
    }

    public static class SortingPerformance {
        private List<String> algorithmsUsed;
        private Map<String, Integer> averageExecutionTime;
        private List<Integer> dataSetSizes;
        private List<PerformanceComparison> performanceComparison;

        public SortingPerformance(List<String> algorithmsUsed, Map<String, Integer> averageExecutionTime,
                                List<Integer> dataSetSizes, List<PerformanceComparison> performanceComparison) {
            this.algorithmsUsed = algorithmsUsed;
            this.averageExecutionTime = averageExecutionTime;
            this.dataSetSizes = dataSetSizes;
            this.performanceComparison = performanceComparison;
        }

        // Getters
        public List<String> getAlgorithmsUsed() { return algorithmsUsed; }
        public Map<String, Integer> getAverageExecutionTime() { return averageExecutionTime; }
        public List<Integer> getDataSetSizes() { return dataSetSizes; }
        public List<PerformanceComparison> getPerformanceComparison() { return performanceComparison; }
    }

    public static class PerformanceComparison {
        private int size;
        private double mergesort;
        private double heapsort;

        public PerformanceComparison(int size, double mergesort, double heapsort) {
            this.size = size;
            this.mergesort = mergesort;
            this.heapsort = heapsort;
        }

        // Getters
        public int getSize() { return size; }
        public double getMergesort() { return mergesort; }
        public double getHeapsort() { return heapsort; }
    }

    public static class RecommendationPerformance {
        private List<String> algorithmsUsed;
        private Map<String, Double> accuracyRates;
        private Map<String, Integer> responseTime;

        public RecommendationPerformance(List<String> algorithmsUsed, Map<String, Double> accuracyRates, Map<String, Integer> responseTime) {
            this.algorithmsUsed = algorithmsUsed;
            this.accuracyRates = accuracyRates;
            this.responseTime = responseTime;
        }

        // Getters
        public List<String> getAlgorithmsUsed() { return algorithmsUsed; }
        public Map<String, Double> getAccuracyRates() { return accuracyRates; }
        public Map<String, Integer> getResponseTime() { return responseTime; }
    }

    public static class QueuePerformance {
        private QueueStats orderProcessingQueue;
        private QueueStats customerServiceQueue;

        public QueuePerformance(QueueStats orderProcessingQueue, QueueStats customerServiceQueue) {
            this.orderProcessingQueue = orderProcessingQueue;
            this.customerServiceQueue = customerServiceQueue;
        }

        // Getters
        public QueueStats getOrderProcessingQueue() { return orderProcessingQueue; }
        public QueueStats getCustomerServiceQueue() { return customerServiceQueue; }
    }

    public static class QueueStats {
        private int averageWaitTime;
        private int throughput;
        private int currentQueueSize;

        public QueueStats(int averageWaitTime, int throughput, int currentQueueSize) {
            this.averageWaitTime = averageWaitTime;
            this.throughput = throughput;
            this.currentQueueSize = currentQueueSize;
        }

        // Getters
        public int getAverageWaitTime() { return averageWaitTime; }
        public int getThroughput() { return throughput; }
        public int getCurrentQueueSize() { return currentQueueSize; }
    }
}