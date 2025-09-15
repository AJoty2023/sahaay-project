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
public class MissingPersonDTO {
    private Long id;
    private Long reporterId;
    private String missingPersonName;
    private Integer age;
    private String gender;
    private String height;
    private String weight;
    private String hairColor;
    private String eyeColor;
    private String distinctiveFeatures;
    private String photoUrl;
    private String lastSeenLocation;
    private BigDecimal lastSeenLatitude;
    private BigDecimal lastSeenLongitude;
    private LocalDate lastSeenDate;
    private LocalTime lastSeenTime;
    private String clothingDescription;
    private String medicalConditions;
    private String contactPersonName;
    private String contactPersonPhone;
    private String policeReportNumber;
    private String status;
    private Integer searchRadiusKm;
    private LocalDateTime foundDate;
    private String foundLocation;
    private LocalDateTime createdAt;
}

