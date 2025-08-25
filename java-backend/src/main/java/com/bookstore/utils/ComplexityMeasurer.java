package com.bookstore.utils;

import java.util.*;
import java.util.function.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * ComplexityMeasurer provides empirical analysis of algorithm time and space complexity
 * through automated testing with varying input sizes and statistical analysis.
 * 
 * Features:
 * - Time complexity measurement with statistical analysis
 * - Space complexity tracking through memory monitoring
 * - Automated complexity classification (O(1), O(log n), O(n), etc.)
 * - Performance regression detection
 * - Detailed reporting and visualization data
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class ComplexityMeasurer {
    
    private static final int DEFAULT_MIN_SIZE = 100;
    private static final int DEFAULT_MAX_SIZE = 10000;
    private static final int DEFAULT_STEP_MULTIPLIER = 2;
    private static final int DEFAULT_ITERATIONS = 5;
    private static final double GROWTH_RATE_THRESHOLD = 0.1;
    
    private final MemoryMXBean memoryBean;
    private final List<MeasurementPoint> measurements;
    private ComplexityConfig config;
    
    public ComplexityMeasurer() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.measurements = new ArrayList<>();
        this.config = new ComplexityConfig();
    }
    
    /**
     * Measures time complexity of an algorithm across different input sizes
     */
    public <T> ComplexityResult measureTimeComplexity(
            Function<Integer, T> inputGenerator,
            Consumer<T> algorithm) {
        return measureTimeComplexity(inputGenerator, algorithm, config);
    }
    
    /**
     * Measures time complexity with custom configuration
     */
    public <T> ComplexityResult measureTimeComplexity(
            Function<Integer, T> inputGenerator,
            Consumer<T> algorithm,
            ComplexityConfig customConfig) {
        
        measurements.clear();
        ComplexityConfig activeConfig = customConfig != null ? customConfig : config;
        
        for (int size = activeConfig.minSize; size <= activeConfig.maxSize; size *= activeConfig.stepMultiplier) {
            MeasurementPoint point = measureSinglePoint(size, inputGenerator, algorithm, activeConfig);
            measurements.add(point);
        }
        
        return analyzeComplexity(measurements, ComplexityType.TIME);
    }
    
    /**
     * Measures space complexity by monitoring memory usage
     */
    public <T> ComplexityResult measureSpaceComplexity(
            Function<Integer, T> inputGenerator,
            Function<T, Object> algorithm) {
        
        measurements.clear();
        
        for (int size = config.minSize; size <= config.maxSize; size *= config.stepMultiplier) {
            MeasurementPoint point = measureSpacePoint(size, inputGenerator, algorithm);
            measurements.add(point);
        }
        
        return analyzeComplexity(measurements, ComplexityType.SPACE);
    }
    
    /**
     * Measures both time and space complexity
     */
    public <T> CombinedComplexityResult measureCombinedComplexity(
            Function<Integer, T> inputGenerator,
            Function<T, Object> algorithm) {
        
        ComplexityResult timeResult = measureTimeComplexity(
            inputGenerator, 
            input -> algorithm.apply(input)
        );
        
        ComplexityResult spaceResult = measureSpaceComplexity(inputGenerator, algorithm);
        
        return new CombinedComplexityResult(timeResult, spaceResult);
    }
    
    private <T> MeasurementPoint measureSinglePoint(
            int size,
            Function<Integer, T> inputGenerator,
            Consumer<T> algorithm,
            ComplexityConfig config) {
        
        List<Long> executionTimes = new ArrayList<>();
        List<Long> memoryUsages = new ArrayList<>();
        
        // Warm up JVM
        for (int i = 0; i < 3; i++) {
            T input = inputGenerator.apply(size);
            algorithm.accept(input);
        }
        
        // Actual measurements
        for (int i = 0; i < config.iterations; i++) {
            System.gc(); // Suggest garbage collection
            
            long memoryBefore = getUsedMemory();
            T input = inputGenerator.apply(size);
            long memoryAfterInput = getUsedMemory();
            
            long startTime = System.nanoTime();
            algorithm.accept(input);
            long endTime = System.nanoTime();
            
            long memoryAfterAlgorithm = getUsedMemory();
            
            executionTimes.add(endTime - startTime);
            memoryUsages.add(Math.max(0, memoryAfterAlgorithm - memoryBefore));
        }
        
        return new MeasurementPoint(
            size,
            calculateMedian(executionTimes),
            calculateMedian(memoryUsages),
            calculateStandardDeviation(executionTimes),
            executionTimes,
            memoryUsages
        );
    }
    
    private <T> MeasurementPoint measureSpacePoint(
            int size,
            Function<Integer, T> inputGenerator,
            Function<T, Object> algorithm) {
        
        List<Long> memoryUsages = new ArrayList<>();
        
        for (int i = 0; i < config.iterations; i++) {
            System.gc();
            
            long memoryBefore = getUsedMemory();
            T input = inputGenerator.apply(size);
            Object result = algorithm.apply(input);
            long memoryAfter = getUsedMemory();
            
            memoryUsages.add(Math.max(0, memoryAfter - memoryBefore));
            
            // Keep reference to prevent premature GC
            if (result != null) {
                result.hashCode();
            }
        }
        
        return new MeasurementPoint(
            size,
            0L, // No time measurement for space-only analysis
            calculateMedian(memoryUsages),
            0.0,
            Collections.emptyList(),
            memoryUsages
        );
    }
    
    private ComplexityResult analyzeComplexity(List<MeasurementPoint> points, ComplexityType type) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 measurement points for analysis");
        }
        
        ComplexityClass detectedClass = detectComplexityClass(points, type);
        double growthRate = calculateGrowthRate(points, type);
        double rSquared = calculateRSquared(points, detectedClass, type);
        
        return new ComplexityResult(
            detectedClass,
            growthRate,
            rSquared,
            new ArrayList<>(points),
            generateComplexityReport(points, detectedClass, type)
        );
    }
    
    private ComplexityClass detectComplexityClass(List<MeasurementPoint> points, ComplexityType type) {
        double[] ratios = calculateGrowthRatios(points, type);
        
        // Analyze growth patterns
        double avgRatio = Arrays.stream(ratios).average().orElse(0.0);
        double variance = calculateVariance(ratios);
        
        // Classification logic based on growth patterns
        if (avgRatio < 1.1 && variance < 0.1) {
            return ComplexityClass.CONSTANT;
        } else if (avgRatio < 1.5 && isLogarithmic(points, type)) {
            return ComplexityClass.LOGARITHMIC;
        } else if (avgRatio >= 1.8 && avgRatio <= 2.2 && variance < 0.2) {
            return ComplexityClass.LINEAR;
        } else if (avgRatio > 2.2 && isLogLinear(points, type)) {
            return ComplexityClass.LINEARITHMIC;
        } else if (isQuadratic(points, type)) {
            return ComplexityClass.QUADRATIC;
        } else if (isExponential(points, type)) {
            return ComplexityClass.EXPONENTIAL;
        } else {
            return ComplexityClass.UNKNOWN;
        }
    }
    
    private double[] calculateGrowthRatios(List<MeasurementPoint> points, ComplexityType type) {
        double[] ratios = new double[points.size() - 1];
        
        for (int i = 1; i < points.size(); i++) {
            double current = type == ComplexityType.TIME ? 
                points.get(i).averageTime : points.get(i).averageMemory;
            double previous = type == ComplexityType.TIME ? 
                points.get(i-1).averageTime : points.get(i-1).averageMemory;
            
            ratios[i-1] = previous > 0 ? current / previous : 0;
        }
        
        return ratios;
    }
    
    private boolean isLogarithmic(List<MeasurementPoint> points, ComplexityType type) {
        // Check if growth follows log(n) pattern
        double correlation = calculateLogCorrelation(points, type);
        return correlation > 0.8;
    }
    
    private boolean isLogLinear(List<MeasurementPoint> points, ComplexityType type) {
        // Check if growth follows n*log(n) pattern
        double correlation = calculateNLogNCorrelation(points, type);
        return correlation > 0.8;
    }
    
    private boolean isQuadratic(List<MeasurementPoint> points, ComplexityType type) {
        // Check if growth follows n^2 pattern
        double correlation = calculateQuadraticCorrelation(points, type);
        return correlation > 0.8;
    }
    
    private boolean isExponential(List<MeasurementPoint> points, ComplexityType type) {
        // Check if growth follows exponential pattern
        double[] ratios = calculateGrowthRatios(points, type);
        double avgRatio = Arrays.stream(ratios).average().orElse(0.0);
        return avgRatio > 3.0;
    }
    
    private double calculateLogCorrelation(List<MeasurementPoint> points, ComplexityType type) {
        double[] x = points.stream().mapToDouble(p -> Math.log(p.inputSize)).toArray();
        double[] y = points.stream().mapToDouble(p -> 
            type == ComplexityType.TIME ? p.averageTime : p.averageMemory).toArray();
        return calculateCorrelation(x, y);
    }
    
    private double calculateNLogNCorrelation(List<MeasurementPoint> points, ComplexityType type) {
        double[] x = points.stream().mapToDouble(p -> p.inputSize * Math.log(p.inputSize)).toArray();
        double[] y = points.stream().mapToDouble(p -> 
            type == ComplexityType.TIME ? p.averageTime : p.averageMemory).toArray();
        return calculateCorrelation(x, y);
    }
    
    private double calculateQuadraticCorrelation(List<MeasurementPoint> points, ComplexityType type) {
        double[] x = points.stream().mapToDouble(p -> p.inputSize * p.inputSize).toArray();
        double[] y = points.stream().mapToDouble(p -> 
            type == ComplexityType.TIME ? p.averageTime : p.averageMemory).toArray();
        return calculateCorrelation(x, y);
    }
    
    private double calculateCorrelation(double[] x, double[] y) {
        if (x.length != y.length || x.length < 2) return 0.0;
        
        double meanX = Arrays.stream(x).average().orElse(0.0);
        double meanY = Arrays.stream(y).average().orElse(0.0);
        
        double numerator = 0.0;
        double sumXSquared = 0.0;
        double sumYSquared = 0.0;
        
        for (int i = 0; i < x.length; i++) {
            double deltaX = x[i] - meanX;
            double deltaY = y[i] - meanY;
            numerator += deltaX * deltaY;
            sumXSquared += deltaX * deltaX;
            sumYSquared += deltaY * deltaY;
        }
        
        double denominator = Math.sqrt(sumXSquared * sumYSquared);
        return denominator > 0 ? numerator / denominator : 0.0;
    }
    
    private double calculateGrowthRate(List<MeasurementPoint> points, ComplexityType type) {
        if (points.size() < 2) return 0.0;
        
        double[] ratios = calculateGrowthRatios(points, type);
        return Arrays.stream(ratios).average().orElse(0.0);
    }
    
    private double calculateRSquared(List<MeasurementPoint> points, ComplexityClass complexityClass, ComplexityType type) {
        // Calculate R-squared for the detected complexity class
        switch (complexityClass) {
            case LOGARITHMIC:
                return Math.pow(calculateLogCorrelation(points, type), 2);
            case LINEARITHMIC:
                return Math.pow(calculateNLogNCorrelation(points, type), 2);
            case QUADRATIC:
                return Math.pow(calculateQuadraticCorrelation(points, type), 2);
            default:
                return calculateLinearRSquared(points, type);
        }
    }
    
    private double calculateLinearRSquared(List<MeasurementPoint> points, ComplexityType type) {
        double[] x = points.stream().mapToDouble(p -> (double) p.inputSize).toArray();
        double[] y = points.stream().mapToDouble(p -> 
            type == ComplexityType.TIME ? p.averageTime : p.averageMemory).toArray();
        return Math.pow(calculateCorrelation(x, y), 2);
    }
    
    private String generateComplexityReport(List<MeasurementPoint> points, ComplexityClass complexityClass, ComplexityType type) {
        StringBuilder report = new StringBuilder();
        report.append("=== Complexity Analysis Report ===\n");
        report.append(String.format("Type: %s Complexity\n", type));
        report.append(String.format("Detected Class: %s\n", complexityClass));
        report.append(String.format("Measurement Points: %d\n", points.size()));
        report.append(String.format("Input Size Range: %d - %d\n", 
            points.get(0).inputSize, points.get(points.size()-1).inputSize));
        
        report.append("\n=== Measurement Data ===\n");
        report.append(String.format("%-10s %-15s %-15s %-15s\n", 
            "Size", "Avg Time (ns)", "Avg Memory (B)", "Std Dev"));
        
        for (MeasurementPoint point : points) {
            report.append(String.format("%-10d %-15.2f %-15.2f %-15.2f\n",
                point.inputSize, 
                (double) point.averageTime,
                (double) point.averageMemory,
                point.standardDeviation));
        }
        
        return report.toString();
    }
    
    // Utility methods
    private long getUsedMemory() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        return heapUsage.getUsed();
    }
    
    private long calculateMedian(List<Long> values) {
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int size = sorted.size();
        return size % 2 == 0 ? 
            (sorted.get(size/2 - 1) + sorted.get(size/2)) / 2 :
            sorted.get(size/2);
    }
    
    private double calculateStandardDeviation(List<Long> values) {
        double mean = values.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double variance = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average().orElse(0.0);
        return Math.sqrt(variance);
    }
    
    private double calculateVariance(double[] values) {
        double mean = Arrays.stream(values).average().orElse(0.0);
        return Arrays.stream(values)
            .map(v -> Math.pow(v - mean, 2))
            .average().orElse(0.0);
    }
    
    // Configuration and result classes
    public static class ComplexityConfig {
        public int minSize = DEFAULT_MIN_SIZE;
        public int maxSize = DEFAULT_MAX_SIZE;
        public int stepMultiplier = DEFAULT_STEP_MULTIPLIER;
        public int iterations = DEFAULT_ITERATIONS;
        
        public ComplexityConfig() {}
        
        public ComplexityConfig(int minSize, int maxSize, int stepMultiplier, int iterations) {
            this.minSize = minSize;
            this.maxSize = maxSize;
            this.stepMultiplier = stepMultiplier;
            this.iterations = iterations;
        }
    }
    
    public static class MeasurementPoint {
        public final int inputSize;
        public final long averageTime;
        public final long averageMemory;
        public final double standardDeviation;
        public final List<Long> allTimeMeasurements;
        public final List<Long> allMemoryMeasurements;
        
        public MeasurementPoint(int inputSize, long averageTime, long averageMemory, 
                              double standardDeviation, List<Long> allTimeMeasurements, 
                              List<Long> allMemoryMeasurements) {
            this.inputSize = inputSize;
            this.averageTime = averageTime;
            this.averageMemory = averageMemory;
            this.standardDeviation = standardDeviation;
            this.allTimeMeasurements = new ArrayList<>(allTimeMeasurements);
            this.allMemoryMeasurements = new ArrayList<>(allMemoryMeasurements);
        }
    }
    
    public static class ComplexityResult {
        public final ComplexityClass complexityClass;
        public final double growthRate;
        public final double rSquared;
        public final List<MeasurementPoint> measurementPoints;
        public final String detailedReport;
        
        public ComplexityResult(ComplexityClass complexityClass, double growthRate, 
                              double rSquared, List<MeasurementPoint> measurementPoints, 
                              String detailedReport) {
            this.complexityClass = complexityClass;
            this.growthRate = growthRate;
            this.rSquared = rSquared;
            this.measurementPoints = measurementPoints;
            this.detailedReport = detailedReport;
        }
        
        @Override
        public String toString() {
            return String.format("Complexity: %s, Growth Rate: %.2f, R²: %.3f", 
                complexityClass, growthRate, rSquared);
        }
    }
    
    public static class CombinedComplexityResult {
        public final ComplexityResult timeComplexity;
        public final ComplexityResult spaceComplexity;
        
        public CombinedComplexityResult(ComplexityResult timeComplexity, ComplexityResult spaceComplexity) {
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
        }
        
        @Override
        public String toString() {
            return String.format("Time: %s, Space: %s", timeComplexity, spaceComplexity);
        }
    }
    
    public enum ComplexityClass {
        CONSTANT("O(1)", "Constant time/space"),
        LOGARITHMIC("O(log n)", "Logarithmic growth"),
        LINEAR("O(n)", "Linear growth"),
        LINEARITHMIC("O(n log n)", "Linearithmic growth"),
        QUADRATIC("O(n²)", "Quadratic growth"),
        CUBIC("O(n³)", "Cubic growth"),
        EXPONENTIAL("O(2^n)", "Exponential growth"),
        FACTORIAL("O(n!)", "Factorial growth"),
        UNKNOWN("O(?)", "Unknown complexity pattern");
        
        private final String notation;
        private final String description;
        
        ComplexityClass(String notation, String description) {
            this.notation = notation;
            this.description = description;
        }
        
        public String getNotation() { return notation; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return notation;
        }
    }
    
    public enum ComplexityType {
        TIME, SPACE
    }
    
    // Setter methods for configuration
    public void setConfig(ComplexityConfig config) {
        this.config = config;
    }
    
    public ComplexityConfig getConfig() {
        return config;
    }
    
    public List<MeasurementPoint> getLastMeasurements() {
        return new ArrayList<>(measurements);
    }
}