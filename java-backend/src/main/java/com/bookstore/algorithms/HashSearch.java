package com.bookstore.algorithms;

import java.util.*;

/**
 * Hash Search Algorithm Implementation
 * Provides O(1) average case search operations using hash tables
 * Used for fast lookups in the Online Bookstore system
 * Time Complexity: O(1) average case, O(n) worst case
 * Space Complexity: O(n)
 */
public class HashSearch<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private static final int RESIZE_FACTOR = 2;
    
    private Entry<K, V>[] buckets;
    private int size;
    private int capacity;
    private double loadFactor;
    private int collisions;
    private int resizeCount;
    
    /**
     * Constructor with default parameters
     */
    public HashSearch() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    
    /**
     * Constructor with custom capacity and load factor
     * @param initialCapacity Initial capacity of hash table
     * @param loadFactor Load factor threshold for resizing
     */
    @SuppressWarnings("unchecked")
    public HashSearch(int initialCapacity, double loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        if (loadFactor <= 0 || loadFactor > 1) {
            throw new IllegalArgumentException("Load factor must be between 0 and 1");
        }
        
        this.capacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.buckets = new Entry[capacity];
        this.size = 0;
        this.collisions = 0;
        this.resizeCount = 0;
    }
    
    /**
     * Insert key-value pair into hash table
     * Time Complexity: O(1) average case, O(n) worst case
     * @param key Key to insert
     * @param value Value to associate with key
     * @return Previous value if key existed, null otherwise
     */
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        if (size >= capacity * loadFactor) {
            resize();
        }
        
        int index = hash(key);
        Entry<K, V> entry = buckets[index];
        
        // Handle collision with chaining
        if (entry == null) {
            buckets[index] = new Entry<>(key, value);
            size++;
            return null;
        }
        
        // Search in chain for existing key
        Entry<K, V> current = entry;
        while (current != null) {
            if (current.key.equals(key)) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            if (current.next == null) break;
            current = current.next;
        }
        
        // Add new entry to chain
        current.next = new Entry<>(key, value);
        size++;
        collisions++;
        
        return null;
    }
    
    /**
     * Search for value by key
     * Time Complexity: O(1) average case, O(n) worst case
     * @param key Key to search for
     * @return Value associated with key, null if not found
     */
    public V get(K key) {
        if (key == null) {
            return null;
        }
        
        int index = hash(key);
        Entry<K, V> entry = buckets[index];
        
        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        
        return null;
    }
    
    /**
     * Check if key exists in hash table
     * Time Complexity: O(1) average case, O(n) worst case
     * @param key Key to check
     * @return true if key exists, false otherwise
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }
    
    /**
     * Remove key-value pair from hash table
     * Time Complexity: O(1) average case, O(n) worst case
     * @param key Key to remove
     * @return Value that was removed, null if key not found
     */
    public V remove(K key) {
        if (key == null) {
            return null;
        }
        
        int index = hash(key);
        Entry<K, V> entry = buckets[index];
        Entry<K, V> prev = null;
        
        while (entry != null) {
            if (entry.key.equals(key)) {
                if (prev == null) {
                    buckets[index] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                size--;
                return entry.value;
            }
            prev = entry;
            entry = entry.next;
        }
        
        return null;
    }
    
    /**
     * Get all keys in hash table
     * Time Complexity: O(n)
     * @return Set of all keys
     */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> entry = bucket;
            while (entry != null) {
                keys.add(entry.key);
                entry = entry.next;
            }
        }
        
        return keys;
    }
    
    /**
     * Get all values in hash table
     * Time Complexity: O(n)
     * @return Collection of all values
     */
    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> entry = bucket;
            while (entry != null) {
                values.add(entry.value);
                entry = entry.next;
            }
        }
        
        return values;
    }
    
    /**
     * Search with custom condition
     * Time Complexity: O(n)
     * @param condition Predicate to match
     * @return First matching entry or null
     */
    public Entry<K, V> findFirst(java.util.function.Predicate<Entry<K, V>> condition) {
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> entry = bucket;
            while (entry != null) {
                if (condition.test(entry)) {
                    return entry;
                }
                entry = entry.next;
            }
        }
        return null;
    }
    
    /**
     * Find all entries matching condition
     * Time Complexity: O(n)
     * @param condition Predicate to match
     * @return List of matching entries
     */
    public List<Entry<K, V>> findAll(java.util.function.Predicate<Entry<K, V>> condition) {
        List<Entry<K, V>> results = new ArrayList<>();
        
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> entry = bucket;
            while (entry != null) {
                if (condition.test(entry)) {
                    results.add(entry);
                }
                entry = entry.next;
            }
        }
        
        return results;
    }
    
    /**
     * Clear all entries from hash table
     * Time Complexity: O(n)
     */
    public void clear() {
        Arrays.fill(buckets, null);
        size = 0;
        collisions = 0;
    }
    
    /**
     * Check if hash table is empty
     * Time Complexity: O(1)
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Get current size of hash table
     * Time Complexity: O(1)
     * @return Number of key-value pairs
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
     * Get current load factor
     * Time Complexity: O(1)
     * @return Current load factor
     */
    public double getCurrentLoadFactor() {
        return (double) size / capacity;
    }
    
    /**
     * Get collision count
     * Time Complexity: O(1)
     * @return Number of collisions
     */
    public int getCollisions() {
        return collisions;
    }
    
    /**
     * Hash function using built-in hashCode
     * @param key Key to hash
     * @return Hash index
     */
    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }
    
    /**
     * Resize hash table when load factor exceeded
     * Time Complexity: O(n)
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldBuckets = buckets;
        
        capacity *= RESIZE_FACTOR;
        buckets = new Entry[capacity];
        size = 0;
        collisions = 0;
        resizeCount++;
        
        // Rehash all entries
        for (Entry<K, V> bucket : oldBuckets) {
            Entry<K, V> entry = bucket;
            while (entry != null) {
                put(entry.key, entry.value);
                entry = entry.next;
            }
        }
    }
    
    /**
     * Get performance metrics
     * @return Performance information
     */
    public PerformanceMetrics getPerformanceMetrics() {
        return new PerformanceMetrics(
            "O(1) average, O(n) worst",
            "O(n)",
            getCurrentLoadFactor(),
            size,
            capacity,
            collisions,
            resizeCount,
            calculateAverageChainLength()
        );
    }
    
    /**
     * Calculate average chain length for collision analysis
     * @return Average chain length
     */
    private double calculateAverageChainLength() {
        if (size == 0) return 0.0;
        
        int totalChainLength = 0;
        int nonEmptyBuckets = 0;
        
        for (Entry<K, V> bucket : buckets) {
            if (bucket != null) {
                nonEmptyBuckets++;
                int chainLength = 0;
                Entry<K, V> entry = bucket;
                while (entry != null) {
                    chainLength++;
                    entry = entry.next;
                }
                totalChainLength += chainLength;
            }
        }
        
        return nonEmptyBuckets > 0 ? (double) totalChainLength / nonEmptyBuckets : 0.0;
    }
    
    /**
     * Entry class for key-value pairs with chaining
     */
    public static class Entry<K, V> {
        public final K key;
        public V value;
        public Entry<K, V> next;
        
        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
        
        @Override
        public String toString() {
            return key + "=" + value;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            
            Entry<?, ?> entry = (Entry<?, ?>) obj;
            return Objects.equals(key, entry.key) && Objects.equals(value, entry.value);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }
    
    /**
     * Performance metrics class
     */
    public static class PerformanceMetrics {
        private final String timeComplexity;
        private final String spaceComplexity;
        private final double loadFactor;
        private final int size;
        private final int capacity;
        private final int collisions;
        private final int resizeCount;
        private final double averageChainLength;
        
        public PerformanceMetrics(String timeComplexity, String spaceComplexity, 
                                double loadFactor, int size, int capacity, 
                                int collisions, int resizeCount, double averageChainLength) {
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
            this.loadFactor = loadFactor;
            this.size = size;
            this.capacity = capacity;
            this.collisions = collisions;
            this.resizeCount = resizeCount;
            this.averageChainLength = averageChainLength;
        }
        
        // Getters
        public String getTimeComplexity() { return timeComplexity; }
        public String getSpaceComplexity() { return spaceComplexity; }
        public double getLoadFactor() { return loadFactor; }
        public int getSize() { return size; }
        public int getCapacity() { return capacity; }
        public int getCollisions() { return collisions; }
        public int getResizeCount() { return resizeCount; }
        public double getAverageChainLength() { return averageChainLength; }
        
        @Override
        public String toString() {
            return String.format("PerformanceMetrics{timeComplexity='%s', spaceComplexity='%s', " +
                               "loadFactor=%.2f, size=%d, capacity=%d, collisions=%d, " +
                               "resizeCount=%d, averageChainLength=%.2f}",
                               timeComplexity, spaceComplexity, loadFactor, size, capacity, 
                               collisions, resizeCount, averageChainLength);
        }
    }
    
    /**
     * Get algorithm information
     * @return Algorithm details
     */
    public static AlgorithmInfo getAlgorithmInfo() {
        return new AlgorithmInfo(
            "Hash Search",
            "Hash Table with Chaining",
            "O(1) average, O(n) worst case",
            "O(n)",
            Arrays.asList(
                "Very fast average case lookups",
                "Constant time insertion and deletion",
                "Flexible key types",
                "Dynamic resizing",
                "Good cache locality"
            ),
            Arrays.asList(
                "Worst case O(n) performance",
                "Memory overhead for sparse data",
                "Hash function quality dependent",
                "No ordering preservation"
            ),
            Arrays.asList(
                "Fast customer lookups",
                "Product catalog indexing",
                "Session management",
                "Caching frequently accessed data",
                "Database indexing"
            )
        );
    }
    
    /**
     * Algorithm information class
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
    
    @Override
    public String toString() {
        return String.format("HashSearch{size=%d, capacity=%d, loadFactor=%.2f, collisions=%d}",
                           size, capacity, getCurrentLoadFactor(), collisions);
    }
}