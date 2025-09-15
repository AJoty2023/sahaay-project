package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String fullName;
    private String phone;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDate dateOfBirth;
    private String gender;
    private String profilePictureUrl;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String medicalConditions;
    private String accessibilityNeeds;
    private String preferredLanguage;
}
