package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloodDonorDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String bloodType;
    private Boolean isAvailable;
    private LocalDate lastDonationDate;
    private Boolean medicalEligibility;
    private Integer weightKg;
    private String healthConditions;
    private String[] preferredDonationCenters;
    private Boolean emergencyDonor;
    private String contactPreference;
    private LocalDateTime createdAt;
}
