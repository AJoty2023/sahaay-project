package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "volunteers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user", "skills", "assignedHelpRequests", "helpRequestResponses"})
@ToString(exclude = {"user", "skills", "assignedHelpRequests", "helpRequestResponses"})
public class Volunteer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 20)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    
    @Column(name = "background_check_status", length = 20)
    @Builder.Default
    private String backgroundCheckStatus = "PENDING";
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "availability_hours", columnDefinition = "jsonb")
    private Map<String, List<String>> availabilityHours;
    
    @Column(name = "max_distance_km")
    @Builder.Default
    private Integer maxDistanceKm = 10;
    
    @Column(name = "volunteer_since")
    @Builder.Default
    private LocalDate volunteerSince = LocalDate.now();
    
    @Column(name = "total_completed_tasks")
    @Builder.Default
    private Integer totalCompletedTasks = 0;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] specializations;
    
    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToMany
    @JoinTable(
        name = "volunteer_skills",
        joinColumns = @JoinColumn(name = "volunteer_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills;
    
    @OneToMany(mappedBy = "assignedVolunteer", fetch = FetchType.LAZY)
    private List<HelpRequest> assignedHelpRequests;
    
    @OneToMany(mappedBy = "volunteer", fetch = FetchType.LAZY)
    private List<HelpRequestResponse> helpRequestResponses;
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }
}
