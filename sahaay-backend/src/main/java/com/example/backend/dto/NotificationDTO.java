package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long recipientId;
    private String title;
    private String message;
    private String notificationType;
    private Long relatedId;
    private Boolean isRead;
    private String priority;
    private Boolean actionRequired;
    private String actionUrl;
    private LocalDateTime expiresAt;
    private String sentVia;
    private LocalDateTime createdAt;
}