package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerRegistrationDTO {
    
    @NotNull(message = "Availability hours are required")
    private Map<String, List<String>> availabilityHours;
    
    @Builder.Default
    @Min(value = 1, message = "Maximum distance must be at least 1 km")
    private Integer maxDistanceKm = 10;
    
    private String[] specializations;
    
    private List<Long> skillIds;
}