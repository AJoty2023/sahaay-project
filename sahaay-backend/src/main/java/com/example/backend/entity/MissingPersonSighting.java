package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "missing_person_sightings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"missingPerson", "reporter", "verifiedBy"})
@ToString(exclude = {"missingPerson", "reporter", "verifiedBy"})
public class MissingPersonSighting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missing_person_id", nullable = false)
    @JsonIgnore
    private MissingPerson missingPerson;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    @JsonIgnore
    private User reporter;
    
    @Column(name = "sighting_location", nullable = false, columnDefinition = "TEXT")
    private String sightingLocation;
    
    @Column(name = "sighting_latitude", precision = 10, scale = 8)
    private BigDecimal sightingLatitude;
    
    @Column(name = "sighting_longitude", precision = 11, scale = 8)
    private BigDecimal sightingLongitude;
    
    @Column(name = "sighting_date", nullable = false)
    private LocalDate sightingDate;
    
    @Column(name = "sighting_time")
    private LocalTime sightingTime;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "confidence_level", length = 20)
    @Builder.Default
    private ConfidenceLevel confidenceLevel = ConfidenceLevel.MEDIUM;
    
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    @JsonIgnore
    private User verifiedBy;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum ConfidenceLevel {
        LOW, MEDIUM, HIGH
    }
}