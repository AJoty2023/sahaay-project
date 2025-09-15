package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "help_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"requester", "assignedVolunteer", "responses"})
@ToString(exclude = {"requester", "assignedVolunteer", "responses"})
public class HelpRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @JsonIgnore
    private User requester;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, length = 50)
    private String category;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Urgency urgency = Urgency.MEDIUM;
    
    @Column(name = "required_skills", columnDefinition = "integer[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Integer[] requiredSkills;
    
    @Column(name = "preferred_volunteer_gender", length = 20)
    private String preferredVolunteerGender;
    
    @Column(name = "estimated_duration", length = 50)
    private String estimatedDuration;
    
    @Column(name = "location_address", columnDefinition = "TEXT")
    private String locationAddress;
    
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Column(name = "max_distance_km")
    @Builder.Default
    private Integer maxDistanceKm = 5;
    
    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;
    
    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;
    
    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;
    
    @Column(name = "recurring_pattern", length = 50)
    private String recurringPattern;
    
    @Column(name = "compensation_offered", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal compensationOffered = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.OPEN;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_volunteer_id")
    @JsonIgnore
    private Volunteer assignedVolunteer;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_recommended_volunteers", columnDefinition = "jsonb")
    private Map<String, Object> aiRecommendedVolunteers;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "helpRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HelpRequestResponse> responses;
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum Urgency {
        LOW, MEDIUM, HIGH, URGENT
    }
    
    public enum Status {
        OPEN, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
