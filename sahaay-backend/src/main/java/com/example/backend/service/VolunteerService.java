package com.example.backend.service;

import com.example.backend.dto.VolunteerDTO;
import com.example.backend.dto.VolunteerRegistrationDTO;
import com.example.backend.entity.Skill;
import com.example.backend.entity.User;
import com.example.backend.entity.Volunteer;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.SkillRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VolunteerService {
    
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    
    public VolunteerDTO registerVolunteer(Long userId, VolunteerRegistrationDTO registrationDTO) {
        log.info("Registering user {} as volunteer", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Check if already a volunteer
        if (volunteerRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("User is already registered as a volunteer");
        }
        
        // Update user type
        user.setUserType(User.UserType.VOLUNTEER);
        userRepository.save(user);
        
        // Create volunteer profile
        Volunteer volunteer = Volunteer.builder()
                .user(user)
                .verificationStatus(Volunteer.VerificationStatus.PENDING)
                .backgroundCheckStatus("PENDING")
                .maxDistanceKm(registrationDTO.getMaxDistanceKm())
                .availabilityHours(registrationDTO.getAvailabilityHours())
                .specializations(registrationDTO.getSpecializations())
                .isAvailable(true)
                .build();
        
        // Add skills
        if (registrationDTO.getSkillIds() != null && !registrationDTO.getSkillIds().isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(registrationDTO.getSkillIds());
            volunteer.setSkills(skills);
        }
        
        Volunteer savedVolunteer = volunteerRepository.save(volunteer);
        log.info("Volunteer registered successfully with ID: {}", savedVolunteer.getId());
        
        return convertToDTO(savedVolunteer);
    }
    
    @Transactional(readOnly = true)
    public VolunteerDTO getVolunteerById(Long id) {
        Volunteer volunteer = volunteerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + id));
        return convertToDTO(volunteer);
    }
    
    @Transactional(readOnly = true)
    public VolunteerDTO getVolunteerByUserId(Long userId) {
        Volunteer volunteer = volunteerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found for user id: " + userId));
        return convertToDTO(volunteer);
    }
    
    public VolunteerDTO updateAvailability(Long volunteerId, boolean isAvailable) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId));
        
        volunteer.setIsAvailable(isAvailable);
        Volunteer updatedVolunteer = volunteerRepository.save(volunteer);
        
        log.info("Volunteer {} availability updated to: {}", volunteerId, isAvailable);
        return convertToDTO(updatedVolunteer);
    }
    
    public VolunteerDTO verifyVolunteer(Long volunteerId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId));
        
        volunteer.setVerificationStatus(Volunteer.VerificationStatus.VERIFIED);
        volunteer.setBackgroundCheckStatus("COMPLETED");
        Volunteer updatedVolunteer = volunteerRepository.save(volunteer);
        
        log.info("Volunteer {} verified successfully", volunteerId);
        return convertToDTO(updatedVolunteer);
    }
    
    @Transactional(readOnly = true)
    public List<VolunteerDTO> getAvailableVolunteers() {
        List<Volunteer> volunteers = volunteerRepository.findAvailableVolunteers();
        return volunteers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<VolunteerDTO> getVolunteersBySkills(List<Long> skillIds) {
        List<Volunteer> volunteers = volunteerRepository.findVolunteersBySkills(skillIds);
        return volunteers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<VolunteerDTO> getVolunteersNearLocation(BigDecimal latitude, BigDecimal longitude) {
        List<Volunteer> volunteers = volunteerRepository.findVolunteersNearLocation(latitude, longitude);
        return volunteers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public void incrementCompletedTasks(Long volunteerId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId));
        
        volunteer.setTotalCompletedTasks(volunteer.getTotalCompletedTasks() + 1);
        volunteerRepository.save(volunteer);
        
        log.info("Incremented completed tasks for volunteer: {}", volunteerId);
    }
    
    public void updateRating(Long volunteerId, BigDecimal newRating) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId));
        
        // Calculate new average rating (simplified - in production, you'd track all ratings)
        BigDecimal currentRating = volunteer.getAverageRating();
        int totalTasks = volunteer.getTotalCompletedTasks();
        
        if (totalTasks == 0) {
            volunteer.setAverageRating(newRating);
        } else {
            BigDecimal totalRating = currentRating.multiply(BigDecimal.valueOf(totalTasks));
            BigDecimal averageRating = totalRating.divide(BigDecimal.valueOf(totalTasks + 1), 2, RoundingMode.HALF_UP);
            volunteer.setAverageRating(averageRating);
            volunteer.setAverageRating(averageRating);
        }
        
        volunteerRepository.save(volunteer);
        log.info("Updated rating for volunteer: {}", volunteerId);
    }
    
    private VolunteerDTO convertToDTO(Volunteer volunteer) {
        return VolunteerDTO.builder()
                .id(volunteer.getId())
                .userId(volunteer.getUser().getId())
                .username(volunteer.getUser().getUsername())
                .fullName(volunteer.getUser().getFullName())
                .verificationStatus(volunteer.getVerificationStatus().name())
                .backgroundCheckStatus(volunteer.getBackgroundCheckStatus())
                .availabilityHours(volunteer.getAvailabilityHours())
                .maxDistanceKm(volunteer.getMaxDistanceKm())
                .volunteerSince(volunteer.getVolunteerSince())
                .totalCompletedTasks(volunteer.getTotalCompletedTasks())
                .averageRating(volunteer.getAverageRating())
                .specializations(volunteer.getSpecializations())
                .isAvailable(volunteer.getIsAvailable())
                .skills(volunteer.getSkills() != null ? 
                    volunteer.getSkills().stream()
                        .map(skill -> skill.getSkillName())
                        .collect(Collectors.toList()) : null)
                .createdAt(volunteer.getCreatedAt())
                .build();
    }
}