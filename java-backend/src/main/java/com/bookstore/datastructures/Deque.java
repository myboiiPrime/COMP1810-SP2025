package com.bookstore.datastructures;

import java.util.Arrays;

/**
 * Deque (Double-ended Queue) ADT Implementation
 * Supports insertion and deletion at both ends
 * Used for flexible order processing and buffering in the Online Bookstore system
 * All operations are O(1) amortized time complexity
 */
public class Deque<T> {
    private Object[] items;
    private int frontIndex;
    private int backIndex;
    private int size;
    private int capacity;
    private static final int DEFAULT_CAPACITY = 16;
    private static final int GROWTH_FACTOR = 2;
    
    /**
     * Constructor with default capacity
     */
    public Deque() {
        this(DEFAULT_CAPACITY);
    }
    
    /**
     * Constructor with specified initial capacity
     * @param initialCapacity Initial capacity
     */
    public Deque(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.capacity = initialCapacity;
        this.items = new Object[capacity];
        this.frontIndex = 0;
        this.backIndex = 0;
        this.size = 0;
    }
    
    /**
     * Add element to front of deque
     * Time Complexity: O(1) amortized
     * @param element Element to add
     */
    public void addFront(T element) {
        if (size == capacity) {
            resize();
        }
        
        if (size > 0) {
            frontIndex = (frontIndex - 1 + capacity) % capacity;
        }
        items[frontIndex] = element;
        size++;
        
        if (size == 1) {
            backIndex = frontIndex;
        }
    }
    
    /**
     * Add element to back of deque
     * Time Complexity: O(1) amortized
     * @param element Element to add
     */
    public void addBack(T element) {
        if (size == capacity) {
            resize();
        }
        
        if (size > 0) {
            backIndex = (backIndex + 1) % capacity;
        }
        items[backIndex] = element;
        size++;
        
        if (size == 1) {
            frontIndex = backIndex;
        }
    }
    
    /**
     * Remove and return element from front
     * Time Complexity: O(1)
     * @return Front element or null if empty
     */
    @SuppressWarnings("unchecked")
    public T removeFront() {
        if (isEmpty()) {
            return null;
        }
        
        T element = (T) items[frontIndex];
        items[frontIndex] = null; // Clear reference
        
        if (size == 1) {
            frontIndex = 0;
            backIndex = 0;
        } else {
            frontIndex = (frontIndex + 1) % capacity;
        }
        size--;
        
        return element;
    }
    
    /**
     * Remove and return element from back
     * Time Complexity: O(1)
     * @return Back element or null if empty
     */
    @SuppressWarnings("unchecked")
    public T removeBack() {
        if (isEmpty()) {
            return null;
        }
        
        T element = (T) items[backIndex];
        items[backIndex] = null; // Clear reference
        
        if (size == 1) {
            frontIndex = 0;
            backIndex = 0;
        } else {
            backIndex = (backIndex - 1 + capacity) % capacity;
        }
        size--;
        
        return element;
    }
    
    /**
     * Peek at front element without removing
     * Time Complexity: O(1)
     * @return Front element or null if empty
     */
    @SuppressWarnings("unchecked")
    public T peekFront() {
        return isEmpty() ? null : (T) items[frontIndex];
    }
    
    /**
     * Peek at back element without removing
     * Time Complexity: O(1)
     * @return Back element or null if empty
     */
    @SuppressWarnings("unchecked")
    public T peekBack() {
        return isEmpty() ? null : (T) items[backIndex];
    }
    
    /**
     * Check if deque is empty
     * Time Complexity: O(1)
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Get current size of deque
     * Time Complexity: O(1)
     * @return Number of elements in deque
     */
    public int size() {
        return size;
    }
    
    /**
     * Get current capacity
     * Time Complexity: O(1)
     * @return Current capacity
     */
    public int getCapacity() {
        return capacity;
    }
    
    /**
     * Clear all elements from deque
     * Time Complexity: O(n)
     */
    public void clear() {
        Arrays.fill(items, null);
        frontIndex = 0;
        backIndex = 0;
        size = 0;
    }
    
    /**
     * Resize the internal array when capacity is exceeded
     * Time Complexity: O(n)
     */
    private void resize() {
        int newCapacity = capacity * GROWTH_FACTOR;
        Object[] newItems = new Object[newCapacity];
        
        // Copy elements in order
        for (int i = 0; i < size; i++) {
            newItems[i] = items[(frontIndex + i) % capacity];
        }
        
        items = newItems;
        frontIndex = 0;
        backIndex = size - 1;
        capacity = newCapacity;
    }
    
    /**
     * Convert deque to array in order (front to back)
     * Time Complexity: O(n)
     * @return Array representation of deque elements
     */
    public Object[] toArray() {
        if (isEmpty()) {
            return new Object[0];
        }
        
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = items[(frontIndex + i) % capacity];
        }
        
        return result;
    }
    
    /**
     * Get string representation of deque
     * Time Complexity: O(n)
     * @return String representation
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "Deque(0): []";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Deque(").append(size).append("): [");
        
        Object[] elements = toArray();
        for (int i = 0; i < Math.min(elements.length, 5); i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[i]);
        }
        
        if (elements.length > 5) {
            sb.append("...");
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Get performance metrics
     * @return Performance information
     */
    public PerformanceMetrics getPerformanceMetrics() {
        double loadFactor = (double) size / capacity;
        return new PerformanceMetrics(
            "O(1) amortized", // Time complexity
            "O(n)", // Space complexity
            loadFactor,
            size,
            capacity,
            capacity - size
        );
    }
    
    /**
     * Inner class for performance metrics
     */
    public static class PerformanceMetrics {
        private final String timeComplexity;
        private final String spaceComplexity;
        private final double loadFactor;
        private final int currentSize;
        private final int totalCapacity;
        private final int remainingCapacity;
        
        public PerformanceMetrics(String timeComplexity, String spaceComplexity, 
                                double loadFactor, int currentSize, 
                                int totalCapacity, int remainingCapacity) {
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
            this.loadFactor = loadFactor;
            this.currentSize = currentSize;
            this.totalCapacity = totalCapacity;
            this.remainingCapacity = remainingCapacity;
        }
        
        // Getters
        public String getTimeComplexity() { return timeComplexity; }
        public String getSpaceComplexity() { return spaceComplexity; }
        public double getLoadFactor() { return loadFactor; }
        public int getCurrentSize() { return currentSize; }
        public int getTotalCapacity() { return totalCapacity; }
        public int getRemainingCapacity() { return remainingCapacity; }
        
        @Override
        public String toString() {
            return String.format("PerformanceMetrics{timeComplexity='%s', spaceComplexity='%s', " +
                               "loadFactor=%.2f, currentSize=%d, totalCapacity=%d, remainingCapacity=%d}",
                               timeComplexity, spaceComplexity, loadFactor, 
                               currentSize, totalCapacity, remainingCapacity);
        }
    }
    
    /**
     * Get algorithm information
     * @return Algorithm details
     */
    public static AlgorithmInfo getAlgorithmInfo() {
        return new AlgorithmInfo(
            "Deque (Double-ended Queue)",
            "Dynamic Array",
            "O(1) amortized",
            "O(n)",
            Arrays.asList(
                "Flexible insertion/deletion at both ends",
                "Amortized O(1) operations",
                "Dynamic resizing",
                "Memory efficient"
            ),
            Arrays.asList(
                "Occasional O(n) resize operations",
                "Memory overhead for sparse arrays",
                "Complex index management"
            ),
            Arrays.asList(
                "Recent customers management",
                "Flexible buffering",
                "Undo/Redo operations",
                "Sliding window algorithms"
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
        private final java.util.List<String> advantages;
        private final java.util.List<String> disadvantages;
        private final java.util.List<String> useCases;
        
        public AlgorithmInfo(String name, String implementation, String timeComplexity, 
                           String spaceComplexity, java.util.List<String> advantages, 
                           java.util.List<String> disadvantages, java.util.List<String> useCases) {
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
        public java.util.List<String> getAdvantages() { return advantages; }
        public java.util.List<String> getDisadvantages() { return disadvantages; }
        public java.util.List<String> getUseCases() { return useCases; }
    }
}