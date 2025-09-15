package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "blood_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"requester"})
@ToString(exclude = {"requester"})
public class BloodRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @JsonIgnore
    private User requester;
    
    @Column(name = "patient_name", nullable = false, length = 100)
    private String patientName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", nullable = false, length = 5)
    private BloodDonor.BloodType bloodType;
    
    @Column(name = "units_needed")
    @Builder.Default
    private Integer unitsNeeded = 1;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Urgency urgency = Urgency.MEDIUM;
    
    @Column(name = "hospital_name", length = 200)
    private String hospitalName;
    
    @Column(name = "hospital_address", columnDefinition = "TEXT")
    private String hospitalAddress;
    
    @Column(name = "hospital_latitude", precision = 10, scale = 8)
    private BigDecimal hospitalLatitude;
    
    @Column(name = "hospital_longitude", precision = 11, scale = 8)
    private BigDecimal hospitalLongitude;
    
    @Column(name = "contact_person", length = 100)
    private String contactPerson;
    
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
    @Column(name = "needed_by_date")
    private LocalDate neededByDate;
    
    @Column(name = "needed_by_time")
    private LocalTime neededByTime;
    
    @Column(name = "additional_requirements", columnDefinition = "TEXT")
    private String additionalRequirements;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.ACTIVE;
    
    @Column(name = "fulfilled_units")
    @Builder.Default
    private Integer fulfilledUnits = 0;
    
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
    
    public enum Urgency {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    public enum Status {
        ACTIVE, PARTIALLY_FULFILLED, FULFILLED, EXPIRED
    }
}
