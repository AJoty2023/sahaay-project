package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SOSAlertDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String alertType;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String locationAddress;
    private String alertMessage;
    private String audioFileUrl;
    private String status;
    private String priorityLevel;
    private Boolean isVoiceActivated;
    private Long respondedById;
    private LocalDateTime responseTime;
    private LocalDateTime resolvedAt;
    private Map<String, Object> aiAnalysis;
    private LocalDateTime createdAt;
}

