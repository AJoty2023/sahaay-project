package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissingPersonSightingDTO {
    private Long id;
    private Long missingPersonId;
    private Long reporterId;
    private String sightingLocation;
    private BigDecimal sightingLatitude;
    private BigDecimal sightingLongitude;
    private LocalDate sightingDate;
    private LocalTime sightingTime;
    private String description;
    private String confidenceLevel;
    private Boolean isVerified;
    private Long verifiedById;
    private LocalDateTime createdAt;
}
