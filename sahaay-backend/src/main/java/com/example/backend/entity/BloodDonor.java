package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_donors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
public class BloodDonor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", nullable = false, length = 5)
    private BloodType bloodType;
    
    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;
    
    @Column(name = "last_donation_date")
    private LocalDate lastDonationDate;
    
    @Column(name = "medical_eligibility")
    @Builder.Default
    private Boolean medicalEligibility = true;
    
    @Column(name = "weight_kg")
    private Integer weightKg;
    
    @Column(name = "health_conditions", columnDefinition = "TEXT")
    private String healthConditions;
    
    @Column(name = "preferred_donation_centers", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] preferredDonationCenters;
    
    @Column(name = "emergency_donor")
    @Builder.Default
    private Boolean emergencyDonor = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "contact_preference", length = 20)
    @Builder.Default
    private ContactPreference contactPreference = ContactPreference.PHONE;
    
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
    
    public enum BloodType {
        A_POSITIVE("A+"), A_NEGATIVE("A-"),
        B_POSITIVE("B+"), B_NEGATIVE("B-"),
        AB_POSITIVE("AB+"), AB_NEGATIVE("AB-"),
        O_POSITIVE("O+"), O_NEGATIVE("O-");
        
        private final String value;
        
        BloodType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    public enum ContactPreference {
        PHONE, EMAIL, APP
    }
}
