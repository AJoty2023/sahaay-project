package com.example.backend.util;

import java.math.BigDecimal;

public class LocationUtil {
    
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    /**
     * Calculate distance between two points using Haversine formula
     * @return distance in kilometers
     */
    public static double calculateDistance(BigDecimal lat1, BigDecimal lon1, 
                                          BigDecimal lat2, BigDecimal lon2) {
        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double deltaLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                  Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                  Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Check if a point is within a radius from a center point
     */
    public static boolean isWithinRadius(BigDecimal centerLat, BigDecimal centerLon,
                                        BigDecimal pointLat, BigDecimal pointLon,
                                        double radiusKm) {
        double distance = calculateDistance(centerLat, centerLon, pointLat, pointLon);
        return distance <= radiusKm;
    }
    
    /**
     * Calculate bounding box for a given center and radius
     */
    public static BoundingBox getBoundingBox(BigDecimal centerLat, BigDecimal centerLon, 
                                            double radiusKm) {
        double lat = centerLat.doubleValue();
        double lon = centerLon.doubleValue();
        
        // Convert radius to degrees
        double latDelta = radiusKm / 111.32; // 1 degree latitude = ~111.32 km
        double lonDelta = radiusKm / (111.32 * Math.cos(Math.toRadians(lat)));
        
        return new BoundingBox(
            BigDecimal.valueOf(lat - latDelta),
            BigDecimal.valueOf(lat + latDelta),
            BigDecimal.valueOf(lon - lonDelta),
            BigDecimal.valueOf(lon + lonDelta)
        );
    }
    
    public static class BoundingBox {
        public final BigDecimal minLat;
        public final BigDecimal maxLat;
        public final BigDecimal minLon;
        public final BigDecimal maxLon;
        
        public BoundingBox(BigDecimal minLat, BigDecimal maxLat, 
                          BigDecimal minLon, BigDecimal maxLon) {
            this.minLat = minLat;
            this.maxLat = maxLat;
            this.minLon = minLon;
            this.maxLon = maxLon;
        }
    }
}

