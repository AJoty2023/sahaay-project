package com.example.backend.service;

import com.example.backend.util.LocationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {
    
    @Value("${app.location.geocoding.api-key:}")
    private String googleMapsApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public String getAddressFromCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (googleMapsApiKey == null || googleMapsApiKey.isEmpty()) {
            log.warn("Google Maps API key not configured, returning coordinates as address");
            return String.format("%.6f, %.6f", latitude, longitude);
        }
        
        try {
            String url = String.format(
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s",
                latitude, longitude, googleMapsApiKey
            );
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) restTemplate.getForObject(url, Map.class);
            
            if (response != null && "OK".equals(response.get("status"))) {
                Object resultsObj = response.get("results");
                if (resultsObj instanceof java.util.List<?>) {
                    java.util.List<?> resultsList = (java.util.List<?>) resultsObj;
                    if (!resultsList.isEmpty() && resultsList.get(0) instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> firstResult = (Map<String, Object>) resultsList.get(0);
                        return (String) firstResult.get("formatted_address");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to get address from coordinates: {}", e.getMessage());
        }
        
        return String.format("%.6f, %.6f", latitude, longitude);
    }
    
    public Map<String, BigDecimal> getCoordinatesFromAddress(String address) {
        Map<String, BigDecimal> coordinates = new HashMap<>();
        
        if (googleMapsApiKey == null || googleMapsApiKey.isEmpty()) {
            log.warn("Google Maps API key not configured");
            return coordinates;
        }
        
        try {
            String url = String.format(
                "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
                address.replace(" ", "+"), googleMapsApiKey
            );
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "OK".equals(response.get("status"))) {
                Object resultsObj = response.get("results");
                if (resultsObj instanceof java.util.List<?>) {
                    java.util.List<?> resultsList = (java.util.List<?>) resultsObj;
                    if (!resultsList.isEmpty() && resultsList.get(0) instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> firstResult = (Map<String, Object>) resultsList.get(0);
                        Object geometryObj = firstResult.get("geometry");
                        if (geometryObj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> geometry = (Map<String, Object>) geometryObj;
                            Object locationObj = geometry.get("location");
                            if (locationObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> location = (Map<String, Object>) locationObj;

                                coordinates.put("latitude", new BigDecimal(location.get("lat").toString()));
                                coordinates.put("longitude", new BigDecimal(location.get("lng").toString()));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to get coordinates from address: {}", e.getMessage());
        }
        
        return coordinates;
    }
    
    public double calculateDistance(BigDecimal lat1, BigDecimal lon1, 
                                   BigDecimal lat2, BigDecimal lon2) {
        return LocationUtil.calculateDistance(lat1, lon1, lat2, lon2);
    }
    
    public boolean isWithinRadius(BigDecimal centerLat, BigDecimal centerLon,
                                  BigDecimal pointLat, BigDecimal pointLon,
                                  double radiusKm) {
        double distance = calculateDistance(centerLat, centerLon, pointLat, pointLon);
        return distance <= radiusKm;
    }
    
    public Map<String, Object> getNearbyPlaces(BigDecimal latitude, BigDecimal longitude, 
                                               String placeType, int radius) {
        Map<String, Object> places = new HashMap<>();
        
        if (googleMapsApiKey == null || googleMapsApiKey.isEmpty()) {
            log.warn("Google Maps API key not configured");
            return places;
        }
        
        try {
            String url = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=%s,%s&radius=%d&type=%s&key=%s",
                latitude, longitude, radius, placeType, googleMapsApiKey
            );
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) restTemplate.getForObject(url, Map.class);
            
            if (response != null && "OK".equals(response.get("status"))) {
                places.put("results", response.get("results"));
            }
        } catch (Exception e) {
            log.error("Failed to get nearby places: {}", e.getMessage());
        }
        
        return places;
    }
}
