package com.bookstore.algorithms;

import java.util.*;

/**
 * Merge Sort Algorithm Implementation
 * 
 * Merge Sort is a divide-and-conquer algorithm that divides the input array into two halves,
 * recursively sorts them, and then merges the sorted halves.
 * 
 * Time Complexity: O(n log n) in all cases (best, average, worst)
 * Space Complexity: O(n) - requires additional space for merging
 * 
 * Characteristics:
 * - Stable: Maintains relative order of equal elements
 * - Not in-place: Requires additional memory
 * - Consistent performance: Always O(n log n)
 * - Good for large datasets
 */
public class MergeSort {
    
    /**
     * Sorts an array of integers using merge sort algorithm
     * @param data List of integers to sort
     * @return New sorted list
     */
    public static List<Integer> sort(List<Integer> data) {
        if (data == null || data.size() <= 1) {
            return new ArrayList<>(data != null ? data : Collections.emptyList());
        }
        
        return mergeSortRecursive(new ArrayList<>(data));
    }
    
    /**
     * Recursive merge sort implementation
     * @param data List to sort
     * @return Sorted list
     */
    private static List<Integer> mergeSortRecursive(List<Integer> data) {
        if (data.size() <= 1) {
            return data;
        }
        
        int mid = data.size() / 2;
        List<Integer> left = mergeSortRecursive(data.subList(0, mid));
        List<Integer> right = mergeSortRecursive(data.subList(mid, data.size()));
        
        return merge(left, right);
    }
    
    /**
     * Merges two sorted lists into one sorted list
     * @param left First sorted list
     * @param right Second sorted list
     * @return Merged sorted list
     */
    private static List<Integer> merge(List<Integer> left, List<Integer> right) {
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        
        // Merge elements in sorted order
        while (i < left.size() && j < right.size()) {
            if (left.get(i) <= right.get(j)) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }
        
        // Add remaining elements from left list
        while (i < left.size()) {
            result.add(left.get(i));
            i++;
        }
        
        // Add remaining elements from right list
        while (j < right.size()) {
            result.add(right.get(j));
            j++;
        }
        
        return result;
    }
    
    /**
     * Estimates the number of comparisons for merge sort
     * @param n Size of the array
     * @return Estimated number of comparisons
     */
    public static int estimateComparisons(int n) {
        if (n <= 1) return 0;
        return (int) (n * Math.log(n) / Math.log(2));
    }
    
    /**
     * Sorts an array of strings using merge sort algorithm
     * @param data List of strings to sort
     * @return New sorted list
     */
    public static List<String> sortStrings(List<String> data) {
        if (data == null || data.size() <= 1) {
            return new ArrayList<>(data != null ? data : Collections.emptyList());
        }
        
        return mergeSortStringsRecursive(new ArrayList<>(data));
    }
    
    /**
     * Recursive merge sort implementation for strings
     * @param data List to sort
     * @return Sorted list
     */
    private static List<String> mergeSortStringsRecursive(List<String> data) {
        if (data.size() <= 1) {
            return data;
        }
        
        int mid = data.size() / 2;
        List<String> left = mergeSortStringsRecursive(data.subList(0, mid));
        List<String> right = mergeSortStringsRecursive(data.subList(mid, data.size()));
        
        return mergeStrings(left, right);
    }
    
    /**
     * Merges two sorted string lists into one sorted list
     * @param left First sorted list
     * @param right Second sorted list
     * @return Merged sorted list
     */
    private static List<String> mergeStrings(List<String> left, List<String> right) {
        List<String> result = new ArrayList<>();
        int i = 0, j = 0;
        
        // Merge elements in sorted order (case-insensitive)
        while (i < left.size() && j < right.size()) {
            if (left.get(i).compareToIgnoreCase(right.get(j)) <= 0) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }
        
        // Add remaining elements from left list
        while (i < left.size()) {
            result.add(left.get(i));
            i++;
        }
        
        // Add remaining elements from right list
        while (j < right.size()) {
            result.add(right.get(j));
            j++;
        }
        
        return result;
    }
}