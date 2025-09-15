package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"recipient"})
@ToString(exclude = {"recipient"})
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @JsonIgnore
    private User recipient;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 30)
    private NotificationType notificationType;
    
    @Column(name = "related_id")
    private Long relatedId;
    
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;
    
    @Column(name = "action_required")
    @Builder.Default
    private Boolean actionRequired = false;
    
    @Column(name = "action_url")
    private String actionUrl;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sent_via", length = 20)
    @Builder.Default
    private SentVia sentVia = SentVia.APP;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum NotificationType {
        SOS_ALERT, HELP_REQUEST, BLOOD_REQUEST, MISSING_PERSON, SYSTEM, REMINDER
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    public enum SentVia {
        APP, EMAIL, SMS, PUSH
    }
}
