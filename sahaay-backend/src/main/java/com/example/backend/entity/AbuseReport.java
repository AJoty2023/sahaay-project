package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Entity
@Table(name = "abuse_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"reporter", "assignedAdmin"})
@ToString(exclude = {"reporter", "assignedAdmin"})
public class AbuseReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    @JsonIgnore
    private User reporter;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 30)
    private ReportType reportType;
    
    @Column(name = "is_anonymous")
    @Builder.Default
    private Boolean isAnonymous = false;
    
    @Column(name = "victim_name", length = 100)
    private String victimName;
    
    @Column(name = "victim_age")
    private Integer victimAge;
    
    @Column(name = "victim_gender", length = 20)
    private String victimGender;
    
    @Column(name = "incident_location", columnDefinition = "TEXT")
    private String incidentLocation;
    
    @Column(name = "incident_date")
    private LocalDate incidentDate;
    
    @Column(name = "incident_time")
    private LocalTime incidentTime;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "evidence_files", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] evidenceFiles;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", length = 20)
    @Builder.Default
    private UrgencyLevel urgencyLevel = UrgencyLevel.MEDIUM;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.SUBMITTED;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    @JsonIgnore
    private User assignedAdmin;
    
    @Column(name = "police_report_number", length = 100)
    private String policeReportNumber;
    
    @Column(name = "follow_up_required")
    @Builder.Default
    private Boolean followUpRequired = true;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_risk_assessment", columnDefinition = "jsonb")
    private Map<String, Object> aiRiskAssessment;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ReportType {
        DOMESTIC_VIOLENCE, HARASSMENT, TRAFFICKING, CHILD_ABUSE, ELDER_ABUSE, OTHER
    }
    
    public enum UrgencyLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    public enum Status {
        SUBMITTED, UNDER_REVIEW, INVESTIGATING, RESOLVED, CLOSED
    }
}
