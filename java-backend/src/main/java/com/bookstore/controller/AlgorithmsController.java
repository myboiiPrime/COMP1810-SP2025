package com.bookstore.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.bookstore.algorithms.MergeSort;
import java.util.*;

@RestController
@RequestMapping("/algorithms")
public class AlgorithmsController {

    // Sample data for demonstrations
    private List<String> sampleBooks = Arrays.asList(
        "Advanced Java Programming", "Algorithms and Data Structures", "Computer Science Fundamentals",
        "Database Management Systems", "Effective Java", "Functional Programming", "Graph Theory",
        "Introduction to Algorithms", "Java: The Complete Reference", "Machine Learning Basics",
        "Network Programming", "Object-Oriented Design", "Python Programming", "Software Engineering",
        "Web Development with Spring"
    );
    
    private List<Integer> sampleNumbers = Arrays.asList(64, 34, 25, 12, 22, 11, 90, 5, 77, 30, 40, 60, 35, 20, 80);

    // Linear Search
    @PostMapping("/search/linear")
    public ResponseEntity<Map<String, Object>> linearSearch(@RequestBody Map<String, Object> request) {
        try {
            String target = (String) request.get("target");
            @SuppressWarnings("unchecked")
            List<String> data = request.containsKey("data") ? 
                (List<String>) request.get("data") : sampleBooks;
            
            if (target == null || target.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Target value is required"
                ));
            }
            
            long startTime = System.nanoTime();
            int index = linearSearchImplementation(data, target.trim());
            long endTime = System.nanoTime();
            
            double executionTime = (endTime - startTime) / 1000.0;
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "algorithm", "Linear Search",
                "target", target.trim(),
                "found", index != -1,
                "index", index,
                "executionTime", executionTime + " microseconds",
                "comparisons", data.size(), // Worst case for linear search
                "timeComplexity", "O(n)",
                "spaceComplexity", "O(1)",
                "dataSize", data.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error performing linear search: " + e.getMessage()
            ));
        }
    }

    // Binary Search
    @PostMapping("/search/binary")
    public ResponseEntity<Map<String, Object>> binarySearch(@RequestBody Map<String, Object> request) {
        try {
            String target = (String) request.get("target");
            @SuppressWarnings("unchecked")
            List<String> data = request.containsKey("data") ? 
                (List<String>) request.get("data") : new ArrayList<>(sampleBooks);
            
            if (target == null || target.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Target value is required"
                ));
            }
            
            // Sort data for binary search
            Collections.sort(data);
            
            long startTime = System.nanoTime();
            int index = binarySearchImplementation(data, target.trim());
            long endTime = System.nanoTime();
            
            double executionTime = (endTime - startTime) / 1000.0;
            int comparisons = (int) Math.ceil(Math.log(data.size()) / Math.log(2));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("algorithm", "Binary Search");
            response.put("target", target.trim());
            response.put("found", index != -1);
            response.put("index", index);
            response.put("executionTime", executionTime + " microseconds");
            response.put("comparisons", comparisons);
            response.put("timeComplexity", "O(log n)");
            response.put("spaceComplexity", "O(1)");
            response.put("dataSize", data.size());
            response.put("sortedData", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error performing binary search: " + e.getMessage()
            ));
        }
    }

    // Hash Search
    @PostMapping("/search/hash")
    public ResponseEntity<Map<String, Object>> hashSearch(@RequestBody Map<String, Object> request) {
        try {
            String target = (String) request.get("target");
            @SuppressWarnings("unchecked")
            List<String> data = request.containsKey("data") ? 
                (List<String>) request.get("data") : sampleBooks;
            
            if (target == null || target.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Target value is required"
                ));
            }
            
            // Create hash map for O(1) lookup
            Map<String, Integer> hashMap = new HashMap<>();
            for (int i = 0; i < data.size(); i++) {
                hashMap.put(data.get(i), i);
            }
            
            long startTime = System.nanoTime();
            Integer index = hashMap.get(target.trim());
            long endTime = System.nanoTime();
            
            double executionTime = (endTime - startTime) / 1000.0;
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "algorithm", "Hash Search",
                "target", target.trim(),
                "found", index != null,
                "index", index != null ? index : -1,
                "executionTime", executionTime + " microseconds",
                "comparisons", 1, // Hash lookup is O(1)
                "timeComplexity", "O(1) average, O(n) worst case",
                "spaceComplexity", "O(n)",
                "dataSize", data.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error performing hash search: " + e.getMessage()
            ));
        }
    }



    // Merge Sort
    @PostMapping("/sort/merge")
    public ResponseEntity<Map<String, Object>> mergeSort(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> data = request.containsKey("data") ? 
                (List<Integer>) request.get("data") : new ArrayList<>(sampleNumbers);
            
            List<Integer> originalData = new ArrayList<>(data);
            
            long startTime = System.nanoTime();
            List<Integer> sortedData = MergeSort.sort(data);
            long endTime = System.nanoTime();
            
            double executionTime = (endTime - startTime) / 1000.0;
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "algorithm", "Merge Sort",
                "originalData", originalData,
                "sortedData", sortedData,
                "executionTime", executionTime + " microseconds",
                "timeComplexity", "O(n log n)",
                "spaceComplexity", "O(n)",
                "dataSize", data.size(),
                "comparisons", MergeSort.estimateComparisons(data.size())
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error performing merge sort: " + e.getMessage()
            ));
        }
    }

    // Performance Comparison
    @PostMapping("/compare/search")
    public ResponseEntity<Map<String, Object>> compareSearchAlgorithms(@RequestBody Map<String, Object> request) {
        try {
            String target = (String) request.get("target");
            @SuppressWarnings("unchecked")
            List<String> data = request.containsKey("data") ? 
                (List<String>) request.get("data") : sampleBooks;
            
            if (target == null || target.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Target value is required"
                ));
            }
            
            Map<String, Object> results = new HashMap<>();
            
            // Linear Search
            long startTime = System.nanoTime();
            int linearIndex = linearSearchImplementation(data, target.trim());
            long linearTime = System.nanoTime() - startTime;
            
            results.put("linearSearch", Map.of(
                "found", linearIndex != -1,
                "index", linearIndex,
                "executionTime", linearTime / 1000.0 + " microseconds",
                "comparisons", data.size()
            ));
            
            // Binary Search (requires sorted data)
            List<String> sortedData = new ArrayList<>(data);
            Collections.sort(sortedData);
            startTime = System.nanoTime();
            int binaryIndex = binarySearchImplementation(sortedData, target.trim());
            long binaryTime = System.nanoTime() - startTime;
            
            results.put("binarySearch", Map.of(
                "found", binaryIndex != -1,
                "index", binaryIndex,
                "executionTime", binaryTime / 1000.0 + " microseconds",
                "comparisons", (int) Math.ceil(Math.log(data.size()) / Math.log(2))
            ));
            
            // Hash Search
            Map<String, Integer> hashMap = new HashMap<>();
            for (int i = 0; i < data.size(); i++) {
                hashMap.put(data.get(i), i);
            }
            startTime = System.nanoTime();
            Integer hashIndex = hashMap.get(target.trim());
            long hashTime = System.nanoTime() - startTime;
            
            results.put("hashSearch", Map.of(
                "found", hashIndex != null,
                "index", hashIndex != null ? hashIndex : -1,
                "executionTime", hashTime / 1000.0 + " microseconds",
                "comparisons", 1
            ));
            
            // Performance analysis
            double speedupBinaryVsLinear = (double) linearTime / binaryTime;
            double speedupHashVsLinear = (double) linearTime / hashTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("target", target.trim());
            response.put("dataSize", data.size());
            response.put("results", results);
            
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("fastestAlgorithm", hashTime < binaryTime && hashTime < linearTime ? "Hash Search" : 
                                        binaryTime < linearTime ? "Binary Search" : "Linear Search");
            analysis.put("speedupBinaryVsLinear", String.format("%.2fx", speedupBinaryVsLinear));
            analysis.put("speedupHashVsLinear", String.format("%.2fx", speedupHashVsLinear));
            response.put("analysis", analysis);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error comparing search algorithms: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/sort/performance")
    public ResponseEntity<Map<String, Object>> analyzeSortPerformance(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> data = request.containsKey("data") ? 
                (List<Integer>) request.get("data") : new ArrayList<>(sampleNumbers);
            
            // Merge Sort Performance Analysis
            long startTime = System.nanoTime();
            List<Integer> mergeSortData = MergeSort.sort(new ArrayList<>(data));
            long mergeSortTime = System.nanoTime() - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("originalData", data);
            response.put("dataSize", data.size());
            response.put("sortedData", mergeSortData);
            response.put("executionTime", mergeSortTime / 1000.0 + " microseconds");
            response.put("timeComplexity", "O(n log n)");
            response.put("spaceComplexity", "O(n)");
            response.put("comparisons", MergeSort.estimateComparisons(data.size()));
            response.put("algorithm", "Merge Sort");
            response.put("stable", true);
            response.put("inPlace", false);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error analyzing sort performance: " + e.getMessage()
            ));
        }
    }

    // Get complexity analysis
    @GetMapping("/complexity")
    public ResponseEntity<Map<String, Object>> getComplexityAnalysis() {
        try {
            Map<String, Object> complexityData = new HashMap<>();
            
            Map<String, Object> searchAlgorithms = new HashMap<>();
            
            Map<String, Object> linearSearch = new HashMap<>();
            linearSearch.put("timeComplexity", "O(n)");
            linearSearch.put("spaceComplexity", "O(1)");
            linearSearch.put("bestCase", "O(1) - element at first position");
            linearSearch.put("averageCase", "O(n/2)");
            linearSearch.put("worstCase", "O(n) - element at last position or not found");
            searchAlgorithms.put("linearSearch", linearSearch);
            
            Map<String, Object> binarySearch = new HashMap<>();
            binarySearch.put("timeComplexity", "O(log n)");
            binarySearch.put("spaceComplexity", "O(1)");
            binarySearch.put("bestCase", "O(1) - element at middle position");
            binarySearch.put("averageCase", "O(log n)");
            binarySearch.put("worstCase", "O(log n)");
            binarySearch.put("prerequisite", "Data must be sorted");
            searchAlgorithms.put("binarySearch", binarySearch);
            
            Map<String, Object> hashSearch = new HashMap<>();
            hashSearch.put("timeComplexity", "O(1) average, O(n) worst case");
            hashSearch.put("spaceComplexity", "O(n)");
            hashSearch.put("bestCase", "O(1) - no collisions");
            hashSearch.put("averageCase", "O(1)");
            hashSearch.put("worstCase", "O(n) - all elements hash to same bucket");
            searchAlgorithms.put("hashSearch", hashSearch);
            
            complexityData.put("searchAlgorithms", searchAlgorithms);
            
            Map<String, Object> sortAlgorithms = new HashMap<>();
            

            
            Map<String, Object> mergeSort = new HashMap<>();
            mergeSort.put("timeComplexity", "O(n log n)");
            mergeSort.put("spaceComplexity", "O(n)");
            mergeSort.put("bestCase", "O(n log n)");
            mergeSort.put("averageCase", "O(n log n)");
            mergeSort.put("worstCase", "O(n log n)");
            mergeSort.put("stable", true);
            mergeSort.put("inPlace", false);
            sortAlgorithms.put("mergeSort", mergeSort);
            
            complexityData.put("sortAlgorithms", sortAlgorithms);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "complexityAnalysis", complexityData
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error getting complexity analysis: " + e.getMessage()
            ));
        }
    }

    // Helper methods for algorithm implementations
    private int linearSearchImplementation(List<String> data, String target) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equalsIgnoreCase(target)) {
                return i;
            }
        }
        return -1;
    }

    private int binarySearchImplementation(List<String> sortedData, String target) {
        int left = 0;
        int right = sortedData.size() - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int comparison = sortedData.get(mid).compareToIgnoreCase(target);
            
            if (comparison == 0) {
                return mid;
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    // Get sample data
    @GetMapping("/sample-data")
    public ResponseEntity<Map<String, Object>> getSampleData() {
        try {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "sampleBooks", sampleBooks,
                "sampleNumbers", sampleNumbers
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error getting sample data: " + e.getMessage()
            ));
        }
    }
}