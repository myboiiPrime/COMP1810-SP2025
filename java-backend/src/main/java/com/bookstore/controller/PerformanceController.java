package com.bookstore.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/performance")
public class PerformanceController {

    // In-memory storage for performance metrics
    private List<Map<String, Object>> performanceMetrics = new ArrayList<>();
    private Map<String, Object> systemMetrics = new HashMap<>();
    
    // Initialize with some sample data
    public PerformanceController() {
        initializeSampleMetrics();
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        try {
            // Update system metrics
            updateSystemMetrics();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "metrics", performanceMetrics,
                "systemMetrics", systemMetrics,
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error retrieving performance metrics: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/record")
    public ResponseEntity<Map<String, Object>> recordPerformanceMetric(@RequestBody Map<String, Object> request) {
        try {
            String operation = (String) request.get("operation");
            String algorithm = (String) request.get("algorithm");
            Double executionTime = ((Number) request.get("executionTime")).doubleValue();
            Integer dataSize = request.containsKey("dataSize") ? 
                ((Number) request.get("dataSize")).intValue() : 0;
            
            if (operation == null || algorithm == null || executionTime == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Operation, algorithm, and executionTime are required"
                ));
            }
            
            Map<String, Object> metric = new HashMap<>();
            metric.put("id", UUID.randomUUID().toString());
            metric.put("operation", operation);
            metric.put("algorithm", algorithm);
            metric.put("executionTime", executionTime);
            metric.put("dataSize", dataSize);
            metric.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            metric.put("memoryUsage", getMemoryUsage());
            
            performanceMetrics.add(metric);
            
            // Keep only last 100 metrics to prevent memory issues
            if (performanceMetrics.size() > 100) {
                performanceMetrics.remove(0);
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Performance metric recorded",
                "metric", metric
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error recording performance metric: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/analysis")
    public ResponseEntity<Map<String, Object>> getPerformanceAnalysis() {
        try {
            if (performanceMetrics.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "No performance data available",
                    "analysis", Map.of()
                ));
            }
            
            Map<String, Object> analysis = new HashMap<>();
            
            // Group metrics by algorithm
            Map<String, List<Map<String, Object>>> algorithmMetrics = new HashMap<>();
            for (Map<String, Object> metric : performanceMetrics) {
                String algorithm = (String) metric.get("algorithm");
                algorithmMetrics.computeIfAbsent(algorithm, k -> new ArrayList<>()).add(metric);
            }
            
            // Calculate statistics for each algorithm
            Map<String, Map<String, Object>> algorithmStats = new HashMap<>();
            for (Map.Entry<String, List<Map<String, Object>>> entry : algorithmMetrics.entrySet()) {
                String algorithm = entry.getKey();
                List<Map<String, Object>> metrics = entry.getValue();
                
                List<Double> executionTimes = new ArrayList<>();
                for (Map<String, Object> metric : metrics) {
                    executionTimes.add(((Number) metric.get("executionTime")).doubleValue());
                }
                
                Map<String, Object> stats = new HashMap<>();
                stats.put("count", metrics.size());
                stats.put("averageTime", calculateAverage(executionTimes));
                stats.put("minTime", Collections.min(executionTimes));
                stats.put("maxTime", Collections.max(executionTimes));
                stats.put("totalTime", executionTimes.stream().mapToDouble(Double::doubleValue).sum());
                
                algorithmStats.put(algorithm, stats);
            }
            
            analysis.put("algorithmStatistics", algorithmStats);
            analysis.put("totalOperations", performanceMetrics.size());
            analysis.put("timeRange", getTimeRange());
            analysis.put("mostUsedAlgorithm", getMostUsedAlgorithm(algorithmMetrics));
            analysis.put("fastestAlgorithm", getFastestAlgorithm(algorithmStats));
            analysis.put("performanceTrends", getPerformanceTrends());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "analysis", analysis
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error analyzing performance: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/comparison")
    public ResponseEntity<Map<String, Object>> getAlgorithmComparison() {
        try {
            Map<String, Object> comparison = new HashMap<>();
            
            // Search algorithms comparison
            Map<String, Object> searchComparison = new HashMap<>();
            searchComparison.put("linearSearch", Map.of(
                "timeComplexity", "O(n)",
                "spaceComplexity", "O(1)",
                "pros", Arrays.asList("Simple implementation", "Works on unsorted data", "No extra space needed"),
                "cons", Arrays.asList("Slow for large datasets", "Not efficient for repeated searches")
            ));
            
            searchComparison.put("binarySearch", Map.of(
                "timeComplexity", "O(log n)",
                "spaceComplexity", "O(1)",
                "pros", Arrays.asList("Very fast for large datasets", "Efficient for repeated searches"),
                "cons", Arrays.asList("Requires sorted data", "More complex implementation")
            ));
            
            searchComparison.put("hashSearch", Map.of(
                "timeComplexity", "O(1) average",
                "spaceComplexity", "O(n)",
                "pros", Arrays.asList("Fastest average case", "Constant time lookup"),
                "cons", Arrays.asList("Extra memory overhead", "Worst case O(n) with collisions")
            ));
            
            // Sort algorithms comparison
            Map<String, Object> sortComparison = new HashMap<>();

            
            sortComparison.put("mergeSort", Map.of(
                "timeComplexity", "O(n log n)",
                "spaceComplexity", "O(n)",
                "pros", Arrays.asList("Stable sorting", "Consistent O(n log n) performance", "Good for large datasets"),
                "cons", Arrays.asList("Requires extra space", "Not in-place", "Higher memory usage")
            ));
            
            comparison.put("searchAlgorithms", searchComparison);
            comparison.put("sortAlgorithms", sortComparison);
            
            // Performance recommendations
            Map<String, Object> recommendations = new HashMap<>();
            recommendations.put("smallDatasets", "Linear search is acceptable for small datasets (< 100 elements)");
            recommendations.put("largeDatasets", "Use binary search for sorted data or hash search for unsorted data");
            recommendations.put("sortingChoice", "Use mergesort for stable sorting and consistent O(n log n) performance");
            recommendations.put("memoryConstrained", "Consider heap sort for in-place sorting with O(n log n) guarantee");
            recommendations.put("realTimeApplications", "Hash-based structures provide most consistent performance");
            comparison.put("recommendations", recommendations);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "comparison", comparison
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error getting algorithm comparison: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearPerformanceMetrics() {
        try {
            performanceMetrics.clear();
            initializeSampleMetrics();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Performance metrics cleared and reset with sample data"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error clearing performance metrics: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        try {
            updateSystemMetrics();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "systemMetrics", systemMetrics
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error getting system metrics: " + e.getMessage()
            ));
        }
    }

    // Helper methods
    private void initializeSampleMetrics() {
        performanceMetrics.clear();
        
        // Add sample performance data
        String[] algorithms = {"Linear Search", "Binary Search", "Hash Search", "Merge Sort", "Heap Sort"};
        String[] operations = {"search", "search", "search", "sort", "sort"};
        double[] sampleTimes = {150.5, 45.2, 12.8, 890.3, 1200.7};
        int[] sampleSizes = {1000, 1000, 1000, 500, 500};
        
        for (int i = 0; i < algorithms.length; i++) {
            Map<String, Object> metric = new HashMap<>();
            metric.put("id", UUID.randomUUID().toString());
            metric.put("operation", operations[i]);
            metric.put("algorithm", algorithms[i]);
            metric.put("executionTime", sampleTimes[i]);
            metric.put("dataSize", sampleSizes[i]);
            metric.put("timestamp", LocalDateTime.now().minusMinutes(i * 5).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            metric.put("memoryUsage", getMemoryUsage());
            
            performanceMetrics.add(metric);
        }
    }

    private void updateSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        systemMetrics.put("totalMemory", totalMemory / (1024 * 1024) + " MB");
        systemMetrics.put("usedMemory", usedMemory / (1024 * 1024) + " MB");
        systemMetrics.put("freeMemory", freeMemory / (1024 * 1024) + " MB");
        systemMetrics.put("maxMemory", maxMemory / (1024 * 1024) + " MB");
        systemMetrics.put("memoryUsagePercentage", String.format("%.2f%%", (double) usedMemory / maxMemory * 100));
        systemMetrics.put("availableProcessors", runtime.availableProcessors());
        systemMetrics.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private Map<String, Object> getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        return Map.of(
            "used", usedMemory / (1024 * 1024) + " MB",
            "total", totalMemory / (1024 * 1024) + " MB",
            "percentage", String.format("%.2f%%", (double) usedMemory / totalMemory * 100)
        );
    }

    private double calculateAverage(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private Map<String, Object> getTimeRange() {
        if (performanceMetrics.isEmpty()) {
            return Map.of();
        }
        
        String earliest = performanceMetrics.stream()
            .map(m -> (String) m.get("timestamp"))
            .min(String::compareTo)
            .orElse("");
            
        String latest = performanceMetrics.stream()
            .map(m -> (String) m.get("timestamp"))
            .max(String::compareTo)
            .orElse("");
            
        return Map.of(
            "earliest", earliest,
            "latest", latest
        );
    }

    private String getMostUsedAlgorithm(Map<String, List<Map<String, Object>>> algorithmMetrics) {
        return algorithmMetrics.entrySet().stream()
            .max(Map.Entry.comparingByValue((a, b) -> Integer.compare(a.size(), b.size())))
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }

    private String getFastestAlgorithm(Map<String, Map<String, Object>> algorithmStats) {
        return algorithmStats.entrySet().stream()
            .min(Map.Entry.comparingByValue((a, b) -> 
                Double.compare(
                    ((Number) a.get("averageTime")).doubleValue(),
                    ((Number) b.get("averageTime")).doubleValue()
                )
            ))
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }

    private Map<String, Object> getPerformanceTrends() {
        if (performanceMetrics.size() < 2) {
            return Map.of("trend", "Insufficient data");
        }
        
        // Simple trend analysis based on recent vs older metrics
        int halfSize = performanceMetrics.size() / 2;
        List<Map<String, Object>> olderMetrics = performanceMetrics.subList(0, halfSize);
        List<Map<String, Object>> recentMetrics = performanceMetrics.subList(halfSize, performanceMetrics.size());
        
        double olderAvg = olderMetrics.stream()
            .mapToDouble(m -> ((Number) m.get("executionTime")).doubleValue())
            .average().orElse(0.0);
            
        double recentAvg = recentMetrics.stream()
            .mapToDouble(m -> ((Number) m.get("executionTime")).doubleValue())
            .average().orElse(0.0);
        
        String trend = recentAvg < olderAvg ? "Improving" : 
                      recentAvg > olderAvg ? "Degrading" : "Stable";
        
        double changePercentage = olderAvg != 0 ? 
            ((recentAvg - olderAvg) / olderAvg) * 100 : 0;
        
        return Map.of(
            "trend", trend,
            "changePercentage", String.format("%.2f%%", changePercentage),
            "olderAverage", olderAvg,
            "recentAverage", recentAvg
        );
    }
}