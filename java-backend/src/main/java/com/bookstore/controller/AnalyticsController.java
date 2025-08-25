package com.bookstore.controller;

import com.bookstore.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Get dashboard overview statistics
     * GET /api/analytics/dashboard?period=30
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(
            @RequestParam(defaultValue = "30") int period) {
        try {
            AnalyticsService.DashboardOverview overview = analyticsService.getDashboardOverview(period);
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to get dashboard data: " + e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get sales analytics
     * GET /api/analytics/sales?period=30&groupBy=day
     */
    @GetMapping("/sales")
    public ResponseEntity<AnalyticsService.SalesAnalytics> getSalesAnalytics(
            @RequestParam(defaultValue = "30") int period,
            @RequestParam(defaultValue = "day") String groupBy) {
        try {
            AnalyticsService.SalesAnalytics salesAnalytics = analyticsService.getSalesAnalytics(period, groupBy);
            return ResponseEntity.ok(salesAnalytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get customer analytics
     * GET /api/analytics/customers?period=30
     */
    @GetMapping("/customers")
    public ResponseEntity<AnalyticsService.CustomerAnalytics> getCustomerAnalytics(
            @RequestParam(defaultValue = "30") int period) {
        try {
            AnalyticsService.CustomerAnalytics customerAnalytics = analyticsService.getCustomerAnalytics(period);
            return ResponseEntity.ok(customerAnalytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get inventory analytics
     * GET /api/analytics/inventory
     */
    @GetMapping("/inventory")
    public ResponseEntity<AnalyticsService.InventoryAnalytics> getInventoryAnalytics() {
        try {
            AnalyticsService.InventoryAnalytics inventoryAnalytics = analyticsService.getInventoryAnalytics();
            return ResponseEntity.ok(inventoryAnalytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get algorithm performance metrics
     * GET /api/analytics/algorithms
     */
    @GetMapping("/algorithms")
    public ResponseEntity<AnalyticsService.AlgorithmMetrics> getAlgorithmMetrics() {
        try {
            AnalyticsService.AlgorithmMetrics algorithmMetrics = analyticsService.getAlgorithmMetrics();
            return ResponseEntity.ok(algorithmMetrics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Export data in CSV or JSON format
     * GET /api/analytics/export/sales?format=csv&period=30
     * GET /api/analytics/export/customers?format=json
     * GET /api/analytics/export/inventory?format=csv
     */
    @GetMapping("/export/{type}")
    public ResponseEntity<?> exportData(
            @PathVariable String type,
            @RequestParam(defaultValue = "json") String format,
            @RequestParam(defaultValue = "30") int period) {
        try {
            List<?> data = analyticsService.getExportData(type, period);
            
            if ("csv".equalsIgnoreCase(format)) {
                // For CSV format, return a simple response indicating CSV export
                Map<String, Object> response = new HashMap<>();
                response.put("message", "CSV export functionality would be implemented here");
                response.put("type", type);
                response.put("format", format);
                response.put("recordCount", data.size());
                response.put("period", period);
                return ResponseEntity.ok(response);
            } else {
                // Return JSON data
                Map<String, Object> response = new HashMap<>();
                response.put("type", type);
                response.put("format", format);
                response.put("data", data);
                response.put("recordCount", data.size());
                response.put("period", period);
                return ResponseEntity.ok(response);
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid export type: " + type);
            error.put("validTypes", "sales, customers, inventory");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Export failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Get analytics summary for quick overview
     * GET /api/analytics/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
        try {
            Map<String, Object> summary = new HashMap<>();
            
            // Get basic dashboard data
            AnalyticsService.DashboardOverview dashboard = analyticsService.getDashboardOverview(7);
            summary.put("totalBooks", dashboard.getTotalBooks());
            summary.put("totalCustomers", dashboard.getTotalCustomers());
            summary.put("totalOrders", dashboard.getTotalOrders());
            summary.put("totalRevenue", dashboard.getTotalRevenue());
            summary.put("lowStockBooks", dashboard.getLowStockBooks());
            summary.put("pendingOrders", dashboard.getPendingOrders());
            
            // Get recent performance
            summary.put("recentOrders", dashboard.getRecentOrders());
            summary.put("recentRevenue", dashboard.getRecentRevenue());
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get analytics summary: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Health check endpoint for analytics service
     * GET /api/analytics/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Analytics Service");
        health.put("timestamp", System.currentTimeMillis());
        health.put("endpoints", new String[]{
            "/dashboard", "/sales", "/customers", "/inventory", "/algorithms", "/export/{type}", "/summary"
        });
        return ResponseEntity.ok(health);
    }
}