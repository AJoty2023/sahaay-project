package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BloodRequestDTO {
    private Long id;
    
    @NotNull(message = "Requester ID is required")
    private Long requesterId;
    private String requesterName;
    
    @NotBlank(message = "Patient name is required")
    private String patientName;
    
    @NotBlank(message = "Blood type is required")
    private String bloodType;
    
    @Min(value = 1, message = "At least 1 unit is required")
    @Builder.Default
    private Integer unitsNeeded = 1;

    @Builder.Default
    private String urgency = "MEDIUM";
    private String hospitalName;
    private String hospitalAddress;
    private BigDecimal hospitalLatitude;
    private BigDecimal hospitalLongitude;
    private String contactPerson;
    private String contactPhone;
    private LocalDate neededByDate;
    private LocalTime neededByTime;
    private String additionalRequirements;
    private String status;
    @Builder.Default
    private Integer fulfilledUnits = 0;
    private LocalDateTime createdAt;
} 