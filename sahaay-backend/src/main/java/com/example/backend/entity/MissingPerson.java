package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "missing_persons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"reporter", "sightings"})
@ToString(exclude = {"reporter", "sightings"})
public class MissingPerson {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    @JsonIgnore
    private User reporter;
    
    @Column(name = "missing_person_name", nullable = false, length = 100)
    private String missingPersonName;
    
    private Integer age;
    
    @Column(length = 20)
    private String gender;
    
    @Column(length = 20)
    private String height;
    
    @Column(length = 20)
    private String weight;
    
    @Column(name = "hair_color", length = 30)
    private String hairColor;
    
    @Column(name = "eye_color", length = 30)
    private String eyeColor;
    
    @Column(name = "distinctive_features", columnDefinition = "TEXT")
    private String distinctiveFeatures;
    
    @Column(name = "photo_url")
    private String photoUrl;
    
    @Column(name = "last_seen_location", columnDefinition = "TEXT")
    private String lastSeenLocation;
    
    @Column(name = "last_seen_latitude", precision = 10, scale = 8)
    private BigDecimal lastSeenLatitude;
    
    @Column(name = "last_seen_longitude", precision = 11, scale = 8)
    private BigDecimal lastSeenLongitude;
    
    @Column(name = "last_seen_date", nullable = false)
    private LocalDate lastSeenDate;
    
    @Column(name = "last_seen_time")
    private LocalTime lastSeenTime;
    
    @Column(name = "clothing_description", columnDefinition = "TEXT")
    private String clothingDescription;
    
    @Column(name = "medical_conditions", columnDefinition = "TEXT")
    private String medicalConditions;
    
    @Column(name = "contact_person_name", length = 100)
    private String contactPersonName;
    
    @Column(name = "contact_person_phone", length = 20)
    private String contactPersonPhone;
    
    @Column(name = "police_report_number", length = 100)
    private String policeReportNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.ACTIVE;
    
    @Column(name = "search_radius_km")
    @Builder.Default
    private Integer searchRadiusKm = 50;
    
    @Column(name = "found_date")
    private LocalDateTime foundDate;
    
    @Column(name = "found_location", columnDefinition = "TEXT")
    private String foundLocation;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "missingPerson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MissingPersonSighting> sightings;
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum Status {
        ACTIVE, FOUND, CLOSED
    }
}