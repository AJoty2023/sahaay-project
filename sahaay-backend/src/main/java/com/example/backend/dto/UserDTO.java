package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDate dateOfBirth;
    private String gender;
    private String profilePictureUrl;
    private String userType;
    private Boolean isVerified;
    private Boolean isActive;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String medicalConditions;
    private String accessibilityNeeds;
    private String preferredLanguage;
    private LocalDateTime createdAt;
}