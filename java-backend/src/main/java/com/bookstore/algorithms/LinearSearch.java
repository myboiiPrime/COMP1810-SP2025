package com.bookstore.algorithms;

import java.util.*;
import java.util.function.Predicate;

/**
 * Linear Search Algorithm Implementation
 * Provides sequential searching capabilities for unsorted data
 * Used for flexible searching in the Online Bookstore system
 * Time Complexity: O(n) for all operations
 * Space Complexity: O(1) for basic search, O(k) for multiple results
 */
public class LinearSearch {
    
    /**
     * Basic linear search for exact match
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param array Array to search in
     * @param target Target element to find
     * @return Index of first occurrence, -1 if not found
     */
    public static <T> int search(T[] array, T target) {
        if (array == null || target == null) {
            return -1;
        }
        
        for (int i = 0; i < array.length; i++) {
            if (target.equals(array[i])) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Linear search in a list
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param list List to search in
     * @param target Target element to find
     * @return Index of first occurrence, -1 if not found
     */
    public static <T> int search(List<T> list, T target) {
        if (list == null || target == null) {
            return -1;
        }
        
        for (int i = 0; i < list.size(); i++) {
            if (target.equals(list.get(i))) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Find all occurrences of target element
     * Time Complexity: O(n)
     * Space Complexity: O(k) where k is number of matches
     * @param array Array to search in
     * @param target Target element to find
     * @return List of indices where target is found
     */
    public static <T> List<Integer> findAll(T[] array, T target) {
        List<Integer> indices = new ArrayList<>();
        
        if (array == null || target == null) {
            return indices;
        }
        
        for (int i = 0; i < array.length; i++) {
            if (target.equals(array[i])) {
                indices.add(i);
            }
        }
        
        return indices;
    }
    
    /**
     * Find all occurrences in a list
     * Time Complexity: O(n)
     * Space Complexity: O(k) where k is number of matches
     * @param list List to search in
     * @param target Target element to find
     * @return List of indices where target is found
     */
    public static <T> List<Integer> findAll(List<T> list, T target) {
        List<Integer> indices = new ArrayList<>();
        
        if (list == null || target == null) {
            return indices;
        }
        
        for (int i = 0; i < list.size(); i++) {
            if (target.equals(list.get(i))) {
                indices.add(i);
            }
        }
        
        return indices;
    }
    
    /**
     * Search with custom predicate condition
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param array Array to search in
     * @param predicate Condition to match
     * @return Index of first match, -1 if not found
     */
    public static <T> int searchWithCondition(T[] array, Predicate<T> predicate) {
        if (array == null || predicate == null) {
            return -1;
        }
        
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null && predicate.test(array[i])) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Search with custom predicate in list
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param list List to search in
     * @param predicate Condition to match
     * @return Index of first match, -1 if not found
     */
    public static <T> int searchWithCondition(List<T> list, Predicate<T> predicate) {
        if (list == null || predicate == null) {
            return -1;
        }
        
        for (int i = 0; i < list.size(); i++) {
            T element = list.get(i);
            if (element != null && predicate.test(element)) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Find all elements matching predicate
     * Time Complexity: O(n)
     * Space Complexity: O(k) where k is number of matches
     * @param array Array to search in
     * @param predicate Condition to match
     * @return List of indices matching condition
     */
    public static <T> List<Integer> findAllWithCondition(T[] array, Predicate<T> predicate) {
        List<Integer> indices = new ArrayList<>();
        
        if (array == null || predicate == null) {
            return indices;
        }
        
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null && predicate.test(array[i])) {
                indices.add(i);
            }
        }
        
        return indices;
    }
    
    /**
     * Find all elements matching predicate in list
     * Time Complexity: O(n)
     * Space Complexity: O(k) where k is number of matches
     * @param list List to search in
     * @param predicate Condition to match
     * @return List of indices matching condition
     */
    public static <T> List<Integer> findAllWithCondition(List<T> list, Predicate<T> predicate) {
        List<Integer> indices = new ArrayList<>();
        
        if (list == null || predicate == null) {
            return indices;
        }
        
        for (int i = 0; i < list.size(); i++) {
            T element = list.get(i);
            if (element != null && predicate.test(element)) {
                indices.add(i);
            }
        }
        
        return indices;
    }
    
    /**
     * Search for minimum element
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param array Array to search in
     * @param comparator Comparator for element comparison
     * @return Index of minimum element, -1 if array is empty
     */
    public static <T> int findMin(T[] array, Comparator<T> comparator) {
        if (array == null || array.length == 0 || comparator == null) {
            return -1;
        }
        
        int minIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] != null && array[minIndex] != null && 
                comparator.compare(array[i], array[minIndex]) < 0) {
                minIndex = i;
            }
        }
        
        return minIndex;
    }
    
    /**
     * Search for maximum element
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param array Array to search in
     * @param comparator Comparator for element comparison
     * @return Index of maximum element, -1 if array is empty
     */
    public static <T> int findMax(T[] array, Comparator<T> comparator) {
        if (array == null || array.length == 0 || comparator == null) {
            return -1;
        }
        
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] != null && array[maxIndex] != null && 
                comparator.compare(array[i], array[maxIndex]) > 0) {
                maxIndex = i;
            }
        }
        
        return maxIndex;
    }
    
    /**
     * Count occurrences of target element
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param array Array to search in
     * @param target Target element to count
     * @return Number of occurrences
     */
    public static <T> int count(T[] array, T target) {
        if (array == null || target == null) {
            return 0;
        }
        
        int count = 0;
        for (T element : array) {
            if (target.equals(element)) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Count elements matching predicate
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param array Array to search in
     * @param predicate Condition to match
     * @return Number of matching elements
     */
    public static <T> int countWithCondition(T[] array, Predicate<T> predicate) {
        if (array == null || predicate == null) {
            return 0;
        }
        
        int count = 0;
        for (T element : array) {
            if (element != null && predicate.test(element)) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Check if array contains target element
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param array Array to search in
     * @param target Target element to find
     * @return true if found, false otherwise
     */
    public static <T> boolean contains(T[] array, T target) {
        return search(array, target) != -1;
    }
    
    /**
     * Check if any element matches predicate
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param array Array to search in
     * @param predicate Condition to match
     * @return true if any element matches, false otherwise
     */
    public static <T> boolean anyMatch(T[] array, Predicate<T> predicate) {
        return searchWithCondition(array, predicate) != -1;
    }
    
    /**
     * Check if all elements match predicate
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param array Array to search in
     * @param predicate Condition to match
     * @return true if all elements match, false otherwise
     */
    public static <T> boolean allMatch(T[] array, Predicate<T> predicate) {
        if (array == null || predicate == null) {
            return false;
        }
        
        for (T element : array) {
            if (element == null || !predicate.test(element)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get algorithm information
     * @return Algorithm details
     */
    public static AlgorithmInfo getAlgorithmInfo() {
        return new AlgorithmInfo(
            "Linear Search",
            "Sequential Scan",
            "O(n)",
            "O(1) basic, O(k) for multiple results",
            Arrays.asList(
                "Works on unsorted data",
                "Simple implementation",
                "Flexible with custom predicates",
                "No preprocessing required",
                "Stable for equal elements"
            ),
            Arrays.asList(
                "Slow for large datasets",
                "No early termination optimization",
                "Not suitable for frequent searches"
            ),
            Arrays.asList(
                "Small datasets",
                "Unsorted collections",
                "One-time searches",
                "Custom search conditions",
                "Finding all occurrences"
            )
        );
    }
    
    /**
     * Inner class for algorithm information
     */
    public static class AlgorithmInfo {
        private final String name;
        private final String implementation;
        private final String timeComplexity;
        private final String spaceComplexity;
        private final List<String> advantages;
        private final List<String> disadvantages;
        private final List<String> useCases;
        
        public AlgorithmInfo(String name, String implementation, String timeComplexity, 
                           String spaceComplexity, List<String> advantages, 
                           List<String> disadvantages, List<String> useCases) {
            this.name = name;
            this.implementation = implementation;
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
            this.advantages = advantages;
            this.disadvantages = disadvantages;
            this.useCases = useCases;
        }
        
        // Getters
        public String getName() { return name; }
        public String getImplementation() { return implementation; }
        public String getTimeComplexity() { return timeComplexity; }
        public String getSpaceComplexity() { return spaceComplexity; }
        public List<String> getAdvantages() { return advantages; }
        public List<String> getDisadvantages() { return disadvantages; }
        public List<String> getUseCases() { return useCases; }
    }
    
    /**
     * Performance metrics for search operations
     */
    public static class SearchMetrics {
        private final int comparisons;
        private final long executionTimeNs;
        private final boolean found;
        private final int resultIndex;
        
        public SearchMetrics(int comparisons, long executionTimeNs, boolean found, int resultIndex) {
            this.comparisons = comparisons;
            this.executionTimeNs = executionTimeNs;
            this.found = found;
            this.resultIndex = resultIndex;
        }
        
        // Getters
        public int getComparisons() { return comparisons; }
        public long getExecutionTimeNs() { return executionTimeNs; }
        public boolean isFound() { return found; }
        public int getResultIndex() { return resultIndex; }
        
        @Override
        public String toString() {
            return String.format("SearchMetrics{comparisons=%d, executionTimeNs=%d, found=%s, resultIndex=%d}",
                               comparisons, executionTimeNs, found, resultIndex);
        }
    }
    
    /**
     * Search with performance tracking
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * @param array Array to search in
     * @param target Target element to find
     * @return Search metrics including performance data
     */
    public static <T> SearchMetrics searchWithMetrics(T[] array, T target) {
        long startTime = System.nanoTime();
        int comparisons = 0;
        int resultIndex = -1;
        
        if (array != null && target != null) {
            for (int i = 0; i < array.length; i++) {
                comparisons++;
                if (target.equals(array[i])) {
                    resultIndex = i;
                    break;
                }
            }
        }
        
        long endTime = System.nanoTime();
        return new SearchMetrics(comparisons, endTime - startTime, resultIndex != -1, resultIndex);
    }
}