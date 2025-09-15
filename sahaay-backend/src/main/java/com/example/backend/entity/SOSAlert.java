package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "sos_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user", "respondedBy"})
@ToString(exclude = {"user", "respondedBy"})
public class SOSAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", length = 30)
    @Builder.Default
    private AlertType alertType = AlertType.EMERGENCY;
    
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(name = "location_address", columnDefinition = "TEXT")
    private String locationAddress;
    
    @Column(name = "alert_message", columnDefinition = "TEXT")
    private String alertMessage;
    
    @Column(name = "audio_file_url")
    private String audioFileUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.ACTIVE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", length = 10)
    @Builder.Default
    private PriorityLevel priorityLevel = PriorityLevel.HIGH;
    
    @Column(name = "is_voice_activated")
    @Builder.Default
    private Boolean isVoiceActivated = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responded_by")
    @JsonIgnore
    private User respondedBy;
    
    @Column(name = "response_time")
    private LocalDateTime responseTime;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_analysis", columnDefinition = "jsonb")
    private Map<String, Object> aiAnalysis;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum AlertType {
        EMERGENCY, MEDICAL, SAFETY, FIRE, ACCIDENT
    }
    
    public enum Status {
        ACTIVE, RESPONDED, RESOLVED, FALSE_ALARM
    }
    
    public enum PriorityLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
