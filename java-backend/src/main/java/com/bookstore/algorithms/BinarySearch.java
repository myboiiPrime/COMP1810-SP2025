package com.bookstore.algorithms;

import java.util.*;

/**
 * Binary Search Algorithm Implementation
 * Provides O(log n) search operations on sorted data
 * Used for efficient searching in the Online Bookstore system
 * Time Complexity: O(log n) for search operations
 * Space Complexity: O(1) for iterative implementation
 */
public class BinarySearch {
    
    private static int comparisons = 0;
    
    /**
     * Reset comparison counter
     */
    private static void resetComparisons() {
        comparisons = 0;
    }
    
    /**
     * Get number of comparisons made in last search
     * @return Number of comparisons
     */
    public static int getComparisons() {
        return comparisons;
    }
    
    /**
     * Standard binary search for exact match
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * @param array Sorted array to search
     * @param target Target element to find
     * @return Index of target element, or -1 if not found
     */
    public static <T extends Comparable<T>> int search(T[] array, T target) {
        resetComparisons();
        
        if (array == null || target == null) {
            return -1;
        }
        
        int left = 0;
        int right = array.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2; // Avoid overflow
            comparisons++;
            
            int comparison = array[mid].compareTo(target);
            
            if (comparison == 0) {
                return mid; // Found target
            } else if (comparison < 0) {
                left = mid + 1; // Search right half
            } else {
                right = mid - 1; // Search left half
            }
        }
        
        return -1; // Target not found
    }
    
    /**
     * Binary search with custom comparator
     * @param array Sorted array to search
     * @param target Target element to find
     * @param comparator Custom comparator function
     * @return Index of target element, or -1 if not found
     */
    public static <T> int search(T[] array, T target, Comparator<T> comparator) {
        resetComparisons();
        
        if (array == null || target == null || comparator == null) {
            return -1;
        }
        
        int left = 0;
        int right = array.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            comparisons++;
            
            int comparison = comparator.compare(array[mid], target);
            
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
    
    /**
     * Find first occurrence of target (leftmost)
     * @param array Sorted array to search
     * @param target Target element to find
     * @return Index of first occurrence, or -1 if not found
     */
    public static <T extends Comparable<T>> int findFirst(T[] array, T target) {
        resetComparisons();
        
        if (array == null || target == null) {
            return -1;
        }
        
        int left = 0;
        int right = array.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            comparisons++;
            
            int comparison = array[mid].compareTo(target);
            
            if (comparison == 0) {
                result = mid;
                right = mid - 1; // Continue searching left
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    /**
     * Find last occurrence of target (rightmost)
     * @param array Sorted array to search
     * @param target Target element to find
     * @return Index of last occurrence, or -1 if not found
     */
    public static <T extends Comparable<T>> int findLast(T[] array, T target) {
        resetComparisons();
        
        if (array == null || target == null) {
            return -1;
        }
        
        int left = 0;
        int right = array.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            comparisons++;
            
            int comparison = array[mid].compareTo(target);
            
            if (comparison == 0) {
                result = mid;
                left = mid + 1; // Continue searching right
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    /**
     * Range search - find all elements within a range
     * Time Complexity: O(log n + k) where k is the number of results
     * @param array Sorted array to search
     * @param minValue Minimum value (inclusive)
     * @param maxValue Maximum value (inclusive)
     * @return List of elements within the range
     */
    public static <T extends Comparable<T>> List<T> rangeSearch(T[] array, T minValue, T maxValue) {
        List<T> results = new ArrayList<>();
        
        if (array == null || minValue == null || maxValue == null) {
            return results;
        }
        
        // Find first occurrence of minValue or first element >= minValue
        int startIndex = findInsertionPoint(array, minValue, true);
        
        // Find last occurrence of maxValue or last element <= maxValue
        int endIndex = findInsertionPoint(array, maxValue, false) - 1;
        
        // Collect results in range
        for (int i = startIndex; i <= endIndex && i < array.length; i++) {
            if (array[i].compareTo(minValue) >= 0 && array[i].compareTo(maxValue) <= 0) {
                results.add(array[i]);
            }
        }
        
        return results;
    }
    
    /**
     * Find insertion point for a value
     * @param array Sorted array
     * @param target Target value
     * @param leftmost True for leftmost insertion, false for rightmost
     * @return Insertion index
     */
    private static <T extends Comparable<T>> int findInsertionPoint(T[] array, T target, boolean leftmost) {
        int left = 0;
        int right = array.length;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            comparisons++;
            
            int comparison = array[mid].compareTo(target);
            
            if (leftmost) {
                if (comparison < 0) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            } else {
                if (comparison <= 0) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }
        }
        
        return left;
    }
    
    /**
     * Verify that array is sorted
     * @param array Array to check
     * @return true if sorted in ascending order
     */
    public static <T extends Comparable<T>> boolean isSorted(T[] array) {
        if (array == null || array.length <= 1) {
            return true;
        }
        
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1].compareTo(array[i]) > 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get algorithm information
     * @return Algorithm details
     */
    public static Map<String, Object> getAlgorithmInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Binary Search");
        info.put("type", "Divide and Conquer");
        info.put("timeComplexity", "O(log n)");
        info.put("spaceComplexity", "O(1)");
        info.put("prerequisites", "Sorted array");
        
        List<String> advantages = Arrays.asList(
            "Very fast for large datasets",
            "Logarithmic time complexity",
            "Memory efficient",
            "Supports range queries"
        );
        info.put("advantages", advantages);
        
        List<String> disadvantages = Arrays.asList(
            "Requires sorted data",
            "Not suitable for frequently changing data",
            "Only works with comparable elements"
        );
        info.put("disadvantages", disadvantages);
        
        return info;
    }
}