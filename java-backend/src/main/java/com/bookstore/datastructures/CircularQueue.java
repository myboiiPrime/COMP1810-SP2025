package com.bookstore.datastructures;

/**
 * Circular Queue ADT Implementation
 * Fixed-size circular buffer for order processing in the Online Bookstore system
 * All operations are O(1) time complexity
 * Efficient memory usage with wraparound functionality
 */
public class CircularQueue<T> {
    private Object[] buffer;
    private int capacity;
    private int front;
    private int rear;
    private int count;
    
    /**
     * Constructor with default capacity
     */
    public CircularQueue() {
        this(50);
    }
    
    /**
     * Constructor with specified capacity
     * @param capacity Maximum number of elements
     */
    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.buffer = new Object[capacity];
        this.front = 0;
        this.rear = 0;
        this.count = 0;
    }
    
    /**
     * Enqueue operation - adds element to rear
     * Time Complexity: O(1)
     * @param element Element to add
     * @return true if successful, false if queue is full
     */
    public boolean enqueue(T element) {
        if (isFull()) {
            return false;
        }
        
        buffer[rear] = element;
        rear = (rear + 1) % capacity;
        count++;
        return true;
    }
    
    /**
     * Dequeue operation - removes and returns front element
     * Time Complexity: O(1)
     * @return Front element or null if empty
     */
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        
        T element = (T) buffer[front];
        buffer[front] = null; // Clear reference
        front = (front + 1) % capacity;
        count--;
        return element;
    }
    
    /**
     * Peek operation - returns front element without removing
     * Time Complexity: O(1)
     * @return Front element or null if empty
     */
    @SuppressWarnings("unchecked")
    public T peek() {
        return isEmpty() ? null : (T) buffer[front];
    }
    
    /**
     * Peek rear operation - returns rear element without removing
     * Time Complexity: O(1)
     * @return Rear element or null if empty
     */
    @SuppressWarnings("unchecked")
    public T peekRear() {
        if (isEmpty()) {
            return null;
        }
        int rearIndex = (rear - 1 + capacity) % capacity;
        return (T) buffer[rearIndex];
    }
    
    /**
     * Check if queue is empty
     * Time Complexity: O(1)
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return count == 0;
    }
    
    /**
     * Check if queue is full
     * Time Complexity: O(1)
     * @return true if full, false otherwise
     */
    public boolean isFull() {
        return count == capacity;
    }
    
    /**
     * Get current size of queue
     * Time Complexity: O(1)
     * @return Number of elements in queue
     */
    public int size() {
        return count;
    }
    
    /**
     * Get remaining capacity
     * Time Complexity: O(1)
     * @return Number of available slots
     */
    public int remainingCapacity() {
        return capacity - count;
    }
    
    /**
     * Get total capacity
     * Time Complexity: O(1)
     * @return Total capacity of queue
     */
    public int getCapacity() {
        return capacity;
    }
    
    /**
     * Clear all elements from queue
     * Time Complexity: O(n)
     */
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            buffer[i] = null;
        }
        front = 0;
        rear = 0;
        count = 0;
    }
    
    /**
     * Get utilization percentage
     * Time Complexity: O(1)
     * @return Utilization as percentage (0-100)
     */
    public double getUtilization() {
        return Math.round((double) count / capacity * 100.0);
    }
    
    /**
     * Convert queue to array in order (for debugging)
     * Time Complexity: O(n)
     * @return Array representation of queue elements
     */
    public Object[] toArray() {
        if (isEmpty()) {
            return new Object[0];
        }
        
        Object[] result = new Object[count];
        int index = front;
        
        for (int i = 0; i < count; i++) {
            result[i] = buffer[index];
            index = (index + 1) % capacity;
        }
        
        return result;
    }
    
    /**
     * Get string representation of queue
     * Time Complexity: O(n)
     * @return String representation
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "CircularQueue(0/" + capacity + "): []";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("CircularQueue(").append(count).append("/").append(capacity).append("): [");
        
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
        return new PerformanceMetrics(
            "O(1)", // Time complexity
            "O(k)", // Space complexity (k = capacity)
            getUtilization(),
            count,
            capacity,
            remainingCapacity()
        );
    }
    
    /**
     * Inner class for performance metrics
     */
    public static class PerformanceMetrics {
        private final String timeComplexity;
        private final String spaceComplexity;
        private final double utilization;
        private final int currentSize;
        private final int totalCapacity;
        private final int remainingCapacity;
        
        public PerformanceMetrics(String timeComplexity, String spaceComplexity, 
                                double utilization, int currentSize, 
                                int totalCapacity, int remainingCapacity) {
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
            this.utilization = utilization;
            this.currentSize = currentSize;
            this.totalCapacity = totalCapacity;
            this.remainingCapacity = remainingCapacity;
        }
        
        // Getters
        public String getTimeComplexity() { return timeComplexity; }
        public String getSpaceComplexity() { return spaceComplexity; }
        public double getUtilization() { return utilization; }
        public int getCurrentSize() { return currentSize; }
        public int getTotalCapacity() { return totalCapacity; }
        public int getRemainingCapacity() { return remainingCapacity; }
        
        @Override
        public String toString() {
            return String.format("PerformanceMetrics{timeComplexity='%s', spaceComplexity='%s', " +
                               "utilization=%.1f%%, currentSize=%d, totalCapacity=%d, remainingCapacity=%d}",
                               timeComplexity, spaceComplexity, utilization, 
                               currentSize, totalCapacity, remainingCapacity);
        }
    }
}