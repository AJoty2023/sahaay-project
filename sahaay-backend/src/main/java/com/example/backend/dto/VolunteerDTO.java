package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerDTO {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String verificationStatus;
    private String backgroundCheckStatus;
    private Map<String, List<String>> availabilityHours;
    private Integer maxDistanceKm;
    private LocalDate volunteerSince;
    private Integer totalCompletedTasks;
    private BigDecimal averageRating;
    private String[] specializations;
    private Boolean isAvailable;
    private List<String> skills;
    private LocalDateTime createdAt;
}
