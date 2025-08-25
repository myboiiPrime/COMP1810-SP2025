package com.bookstore.util;

/**
 * Utility class for handling order priority calculations based on customer membership levels
 */
public class PriorityUtils {
    
    /**
     * Convert customer membership level to numerical priority
     * @param membershipLevel The customer's membership level (BRONZE, SILVER, GOLD, PLATINUM, STANDARD)
     * @return Priority value: Bronze/Standard=1, Silver=2, Gold=3, Platinum=4
     */
    public static int getMembershipPriority(String membershipLevel) {
        if (membershipLevel == null) {
            return 1; // Default to lowest priority
        }
        
        switch (membershipLevel.toUpperCase()) {
            case "PLATINUM":
                return 4;
            case "GOLD":
                return 3;
            case "SILVER":
                return 2;
            case "BRONZE":
            case "STANDARD":
            default:
                return 1;
        }
    }
    
    /**
     * Get priority description for display purposes
     * @param priority The numerical priority value
     * @return Human-readable priority description
     */
    public static String getPriorityDescription(int priority) {
        switch (priority) {
            case 4:
                return "Platinum (Highest Priority)";
            case 3:
                return "Gold (High Priority)";
            case 2:
                return "Silver (Medium Priority)";
            case 1:
            default:
                return "Bronze/Standard (Low Priority)";
        }
    }
    
    /**
     * Check if a priority value is valid
     * @param priority The priority value to validate
     * @return true if priority is between 1 and 4 (inclusive)
     */
    public static boolean isValidPriority(int priority) {
        return priority >= 1 && priority <= 4;
    }
}