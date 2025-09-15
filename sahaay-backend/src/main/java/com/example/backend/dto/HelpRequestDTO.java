package com.example.backend.dto;
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
public class HelpRequestDTO {
    private Long id;
    
    @NotNull(message = "Requester ID is required")
    private Long requesterId;
    private String requesterName;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @Builder.Default
    private String urgency = "MEDIUM";
    private Integer[] requiredSkills;
    private String preferredVolunteerGender;
    private String estimatedDuration;
    private String locationAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    @Builder.Default
    private Integer maxDistanceKm = 5;
    @Builder.Default
    private LocalDate scheduledDate = null;
    private LocalTime scheduledTime;
    @Builder.Default
    private Boolean isRecurring = false;
    private String recurringPattern;
    @Builder.Default
    private BigDecimal compensationOffered = BigDecimal.ZERO;
    private String status;
    private Long assignedVolunteerId;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
