package com.bookstore.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PerformanceAnalyzer Utility
 * Provides comprehensive performance tracking and analysis capabilities
 * Used for monitoring algorithm performance in the Online Bookstore system
 * Tracks execution time, memory usage, operation counts, and system metrics
 */
public class PerformanceAnalyzer {
    
    private static final PerformanceAnalyzer INSTANCE = new PerformanceAnalyzer();
    private final Map<String, OperationMetrics> operationMetrics;
    private final Map<String, List<Long>> executionTimes;
    private final MemoryMXBean memoryBean;
    private final AtomicLong totalOperations;
    
    /**
     * Private constructor for singleton pattern
     */
    private PerformanceAnalyzer() {
        this.operationMetrics = new ConcurrentHashMap<>();
        this.executionTimes = new ConcurrentHashMap<>();
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.totalOperations = new AtomicLong(0);
    }
    
    /**
     * Get singleton instance
     * @return PerformanceAnalyzer instance
     */
    public static PerformanceAnalyzer getInstance() {
        return INSTANCE;
    }
    
    /**
     * Start performance measurement for an operation
     * @param operationName Name of the operation
     * @return PerformanceContext for tracking
     */
    public PerformanceContext startMeasurement(String operationName) {
        return new PerformanceContext(operationName);
    }
    
    /**
     * Record operation completion
     * @param context Performance context
     */
    public void recordOperation(PerformanceContext context) {
        String operationName = context.getOperationName();
        long executionTime = context.getExecutionTime();
        long memoryUsed = context.getMemoryUsed();
        
        // Update operation metrics
        operationMetrics.compute(operationName, (key, existing) -> {
            if (existing == null) {
                return new OperationMetrics(operationName, executionTime, memoryUsed);
            } else {
                existing.addMeasurement(executionTime, memoryUsed);
                return existing;
            }
        });
        
        // Store execution time for trend analysis
        executionTimes.computeIfAbsent(operationName, k -> new ArrayList<>()).add(executionTime);
        
        totalOperations.incrementAndGet();
    }
    
    /**
     * Get metrics for specific operation
     * @param operationName Name of the operation
     * @return Operation metrics or null if not found
     */
    public OperationMetrics getOperationMetrics(String operationName) {
        return operationMetrics.get(operationName);
    }
    
    /**
     * Get all operation metrics
     * @return Map of all operation metrics
     */
    public Map<String, OperationMetrics> getAllMetrics() {
        return new HashMap<>(operationMetrics);
    }
    
    /**
     * Get system performance summary
     * @return System performance information
     */
    public SystemPerformance getSystemPerformance() {
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();
        
        return new SystemPerformance(
            heapMemory.getUsed(),
            heapMemory.getMax(),
            nonHeapMemory.getUsed(),
            nonHeapMemory.getMax(),
            totalOperations.get(),
            operationMetrics.size()
        );
    }
    
    /**
     * Get performance trends for an operation
     * @param operationName Name of the operation
     * @return Performance trend analysis
     */
    public PerformanceTrend getPerformanceTrend(String operationName) {
        List<Long> times = executionTimes.get(operationName);
        if (times == null || times.isEmpty()) {
            return null;
        }
        
        return new PerformanceTrend(operationName, new ArrayList<>(times));
    }
    
    /**
     * Compare performance between operations
     * @param operation1 First operation name
     * @param operation2 Second operation name
     * @return Performance comparison
     */
    public PerformanceComparison compareOperations(String operation1, String operation2) {
        OperationMetrics metrics1 = operationMetrics.get(operation1);
        OperationMetrics metrics2 = operationMetrics.get(operation2);
        
        if (metrics1 == null || metrics2 == null) {
            return null;
        }
        
        return new PerformanceComparison(metrics1, metrics2);
    }
    
    /**
     * Get top performing operations
     * @param limit Number of operations to return
     * @param sortBy Metric to sort by
     * @return List of top performing operations
     */
    public List<OperationMetrics> getTopPerformers(int limit, SortMetric sortBy) {
        return operationMetrics.values().stream()
            .sorted((m1, m2) -> {
                switch (sortBy) {
                    case AVERAGE_TIME:
                        return Double.compare(m1.getAverageExecutionTime(), m2.getAverageExecutionTime());
                    case TOTAL_TIME:
                        return Long.compare(m1.getTotalExecutionTime(), m2.getTotalExecutionTime());
                    case OPERATION_COUNT:
                        return Long.compare(m2.getOperationCount(), m1.getOperationCount());
                    case MEMORY_USAGE:
                        return Double.compare(m1.getAverageMemoryUsage(), m2.getAverageMemoryUsage());
                    default:
                        return 0;
                }
            })
            .limit(limit)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Clear all metrics
     */
    public void clearMetrics() {
        operationMetrics.clear();
        executionTimes.clear();
        totalOperations.set(0);
    }
    
    /**
     * Clear metrics for specific operation
     * @param operationName Name of the operation
     */
    public void clearOperationMetrics(String operationName) {
        operationMetrics.remove(operationName);
        executionTimes.remove(operationName);
    }
    
    /**
     * Generate performance report
     * @return Formatted performance report
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Performance Analysis Report ===\n\n");
        
        // System overview
        SystemPerformance sysPerf = getSystemPerformance();
        report.append("System Performance:\n");
        report.append(String.format("  Total Operations: %d\n", sysPerf.getTotalOperations()));
        report.append(String.format("  Tracked Operations: %d\n", sysPerf.getTrackedOperations()));
        report.append(String.format("  Heap Memory: %d MB / %d MB\n", 
                     sysPerf.getHeapMemoryUsed() / (1024 * 1024),
                     sysPerf.getHeapMemoryMax() / (1024 * 1024)));
        report.append("\n");
        
        // Top performers
        report.append("Top Performers (by average execution time):\n");
        List<OperationMetrics> topPerformers = getTopPerformers(5, SortMetric.AVERAGE_TIME);
        for (int i = 0; i < topPerformers.size(); i++) {
            OperationMetrics metrics = topPerformers.get(i);
            report.append(String.format("  %d. %s: %.2f ms avg (%.2f ms total, %d ops)\n",
                         i + 1, metrics.getOperationName(),
                         metrics.getAverageExecutionTime() / 1_000_000.0,
                         metrics.getTotalExecutionTime() / 1_000_000.0,
                         metrics.getOperationCount()));
        }
        
        return report.toString();
    }
    
    /**
     * Performance context for tracking individual operations
     */
    public class PerformanceContext {
        private final String operationName;
        private final long startTime;
        private final long startMemory;
        private long endTime;
        private long endMemory;
        private boolean completed;
        
        public PerformanceContext(String operationName) {
            this.operationName = operationName;
            this.startTime = System.nanoTime();
            this.startMemory = getCurrentMemoryUsage();
            this.completed = false;
        }
        
        /**
         * Mark operation as completed
         */
        public void complete() {
            if (!completed) {
                this.endTime = System.nanoTime();
                this.endMemory = getCurrentMemoryUsage();
                this.completed = true;
                recordOperation(this);
            }
        }
        
        /**
         * Get execution time in nanoseconds
         * @return Execution time
         */
        public long getExecutionTime() {
            return completed ? endTime - startTime : System.nanoTime() - startTime;
        }
        
        /**
         * Get memory used in bytes
         * @return Memory usage
         */
        public long getMemoryUsed() {
            return completed ? endMemory - startMemory : getCurrentMemoryUsage() - startMemory;
        }
        
        /**
         * Get operation name
         * @return Operation name
         */
        public String getOperationName() {
            return operationName;
        }
        
        /**
         * Check if operation is completed
         * @return true if completed
         */
        public boolean isCompleted() {
            return completed;
        }
    }
    
    /**
     * Operation metrics class
     */
    public static class OperationMetrics {
        private final String operationName;
        private long totalExecutionTime;
        private long totalMemoryUsage;
        private long operationCount;
        private long minExecutionTime;
        private long maxExecutionTime;
        private long minMemoryUsage;
        private long maxMemoryUsage;
        
        public OperationMetrics(String operationName, long executionTime, long memoryUsage) {
            this.operationName = operationName;
            this.totalExecutionTime = executionTime;
            this.totalMemoryUsage = memoryUsage;
            this.operationCount = 1;
            this.minExecutionTime = executionTime;
            this.maxExecutionTime = executionTime;
            this.minMemoryUsage = memoryUsage;
            this.maxMemoryUsage = memoryUsage;
        }
        
        public synchronized void addMeasurement(long executionTime, long memoryUsage) {
            totalExecutionTime += executionTime;
            totalMemoryUsage += memoryUsage;
            operationCount++;
            
            minExecutionTime = Math.min(minExecutionTime, executionTime);
            maxExecutionTime = Math.max(maxExecutionTime, executionTime);
            minMemoryUsage = Math.min(minMemoryUsage, memoryUsage);
            maxMemoryUsage = Math.max(maxMemoryUsage, memoryUsage);
        }
        
        // Getters
        public String getOperationName() { return operationName; }
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public long getTotalMemoryUsage() { return totalMemoryUsage; }
        public long getOperationCount() { return operationCount; }
        public double getAverageExecutionTime() { return (double) totalExecutionTime / operationCount; }
        public double getAverageMemoryUsage() { return (double) totalMemoryUsage / operationCount; }
        public long getMinExecutionTime() { return minExecutionTime; }
        public long getMaxExecutionTime() { return maxExecutionTime; }
        public long getMinMemoryUsage() { return minMemoryUsage; }
        public long getMaxMemoryUsage() { return maxMemoryUsage; }
        public double getExecutionTime() { return getAverageExecutionTime(); }
        
        @Override
        public String toString() {
            return String.format("OperationMetrics{name='%s', avgTime=%.2fms, totalTime=%.2fms, count=%d}",
                               operationName, getAverageExecutionTime() / 1_000_000.0,
                               totalExecutionTime / 1_000_000.0, operationCount);
        }
    }
    
    /**
     * System performance information
     */
    public static class SystemPerformance {
        private final long heapMemoryUsed;
        private final long heapMemoryMax;
        private final long nonHeapMemoryUsed;
        private final long nonHeapMemoryMax;
        private final long totalOperations;
        private final int trackedOperations;
        
        public SystemPerformance(long heapMemoryUsed, long heapMemoryMax,
                                long nonHeapMemoryUsed, long nonHeapMemoryMax,
                                long totalOperations, int trackedOperations) {
            this.heapMemoryUsed = heapMemoryUsed;
            this.heapMemoryMax = heapMemoryMax;
            this.nonHeapMemoryUsed = nonHeapMemoryUsed;
            this.nonHeapMemoryMax = nonHeapMemoryMax;
            this.totalOperations = totalOperations;
            this.trackedOperations = trackedOperations;
        }
        
        // Getters
        public long getHeapMemoryUsed() { return heapMemoryUsed; }
        public long getHeapMemoryMax() { return heapMemoryMax; }
        public long getNonHeapMemoryUsed() { return nonHeapMemoryUsed; }
        public long getNonHeapMemoryMax() { return nonHeapMemoryMax; }
        public long getTotalOperations() { return totalOperations; }
        public int getTrackedOperations() { return trackedOperations; }
        
        public double getHeapMemoryUtilization() {
            return heapMemoryMax > 0 ? (double) heapMemoryUsed / heapMemoryMax : 0.0;
        }
    }
    
    /**
     * Performance trend analysis
     */
    public static class PerformanceTrend {
        private final String operationName;
        private final List<Long> executionTimes;
        private final double averageTime;
        private final double standardDeviation;
        private final TrendDirection trend;
        
        public PerformanceTrend(String operationName, List<Long> executionTimes) {
            this.operationName = operationName;
            this.executionTimes = executionTimes;
            this.averageTime = calculateAverage(executionTimes);
            this.standardDeviation = calculateStandardDeviation(executionTimes, averageTime);
            this.trend = calculateTrend(executionTimes);
        }
        
        private double calculateAverage(List<Long> times) {
            return times.stream().mapToLong(Long::longValue).average().orElse(0.0);
        }
        
        private double calculateStandardDeviation(List<Long> times, double average) {
            double variance = times.stream()
                .mapToDouble(time -> Math.pow(time - average, 2))
                .average().orElse(0.0);
            return Math.sqrt(variance);
        }
        
        private TrendDirection calculateTrend(List<Long> times) {
            if (times.size() < 2) return TrendDirection.STABLE;
            
            int size = times.size();
            int windowSize = Math.min(10, size / 2);
            
            double firstHalf = times.subList(0, windowSize).stream()
                .mapToLong(Long::longValue).average().orElse(0.0);
            double secondHalf = times.subList(size - windowSize, size).stream()
                .mapToLong(Long::longValue).average().orElse(0.0);
            
            double threshold = averageTime * 0.1; // 10% threshold
            
            if (secondHalf > firstHalf + threshold) {
                return TrendDirection.DEGRADING;
            } else if (secondHalf < firstHalf - threshold) {
                return TrendDirection.IMPROVING;
            } else {
                return TrendDirection.STABLE;
            }
        }
        
        // Getters
        public String getOperationName() { return operationName; }
        public List<Long> getExecutionTimes() { return new ArrayList<>(executionTimes); }
        public double getAverageTime() { return averageTime; }
        public double getStandardDeviation() { return standardDeviation; }
        public TrendDirection getTrend() { return trend; }
    }
    
    /**
     * Performance comparison between two operations
     */
    public static class PerformanceComparison {
        private final OperationMetrics operation1;
        private final OperationMetrics operation2;
        private final double timeRatio;
        private final double memoryRatio;
        
        public PerformanceComparison(OperationMetrics operation1, OperationMetrics operation2) {
            this.operation1 = operation1;
            this.operation2 = operation2;
            this.timeRatio = operation1.getAverageExecutionTime() / operation2.getAverageExecutionTime();
            this.memoryRatio = operation1.getAverageMemoryUsage() / operation2.getAverageMemoryUsage();
        }
        
        // Getters
        public OperationMetrics getOperation1() { return operation1; }
        public OperationMetrics getOperation2() { return operation2; }
        public double getTimeRatio() { return timeRatio; }
        public double getMemoryRatio() { return memoryRatio; }
        
        public String getFasterOperation() {
            return timeRatio < 1.0 ? operation1.getOperationName() : operation2.getOperationName();
        }
        
        public String getMoreMemoryEfficientOperation() {
            return memoryRatio < 1.0 ? operation1.getOperationName() : operation2.getOperationName();
        }
    }
    
    /**
     * Trend direction enumeration
     */
    public enum TrendDirection {
        IMPROVING, STABLE, DEGRADING
    }
    
    /**
     * Sort metric enumeration
     */
    public enum SortMetric {
        AVERAGE_TIME, TOTAL_TIME, OPERATION_COUNT, MEMORY_USAGE
    }
    
    /**
     * Get current memory usage
     * @return Current memory usage in bytes
     */
    private long getCurrentMemoryUsage() {
        return memoryBean.getHeapMemoryUsage().getUsed();
    }
}