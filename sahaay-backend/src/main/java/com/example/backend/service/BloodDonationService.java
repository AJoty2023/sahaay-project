package com.example.backend.service;

import com.example.backend.dto.BloodRequestDTO;
import com.example.backend.dto.BloodDonorDTO;
import com.example.backend.entity.BloodDonor;
import com.example.backend.entity.BloodRequest;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.BloodDonorRepository;
import com.example.backend.repository.BloodRequestRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BloodDonationService {
    
    private final BloodDonorRepository bloodDonorRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    
    public BloodDonorDTO registerDonor(Long userId, BloodDonorDTO donorDTO) {
        log.info("Registering blood donor for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if already registered
        if (bloodDonorRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("User is already registered as a blood donor");
        }
        
        BloodDonor donor = BloodDonor.builder()
                .user(user)
                .bloodType(BloodDonor.BloodType.valueOf(donorDTO.getBloodType()))
                .isAvailable(true)
                .lastDonationDate(donorDTO.getLastDonationDate())
                .medicalEligibility(donorDTO.getMedicalEligibility())
                .weightKg(donorDTO.getWeightKg())
                .healthConditions(donorDTO.getHealthConditions())
                .preferredDonationCenters(donorDTO.getPreferredDonationCenters())
                .emergencyDonor(donorDTO.getEmergencyDonor())
                .contactPreference(BloodDonor.ContactPreference.valueOf(donorDTO.getContactPreference()))
                .build();
        
        BloodDonor savedDonor = bloodDonorRepository.save(donor);
        log.info("Blood donor registered successfully with ID: {}", savedDonor.getId());
        
        return convertDonorToDTO(savedDonor);
    }
    
    public BloodRequestDTO createBloodRequest(BloodRequestDTO requestDTO) {
        log.info("Creating blood request for patient: {}", requestDTO.getPatientName());
        
        User requester = userRepository.findById(requestDTO.getRequesterId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        BloodRequest request = BloodRequest.builder()
                .requester(requester)
                .patientName(requestDTO.getPatientName())
                .bloodType(BloodDonor.BloodType.valueOf(requestDTO.getBloodType()))
                .unitsNeeded(requestDTO.getUnitsNeeded())
                .urgency(BloodRequest.Urgency.valueOf(requestDTO.getUrgency()))
                .hospitalName(requestDTO.getHospitalName())
                .hospitalAddress(requestDTO.getHospitalAddress())
                .hospitalLatitude(requestDTO.getHospitalLatitude())
                .hospitalLongitude(requestDTO.getHospitalLongitude())
                .contactPerson(requestDTO.getContactPerson())
                .contactPhone(requestDTO.getContactPhone())
                .neededByDate(requestDTO.getNeededByDate())
                .neededByTime(requestDTO.getNeededByTime())
                .additionalRequirements(requestDTO.getAdditionalRequirements())
                .status(BloodRequest.Status.ACTIVE)
                .build();
        
        BloodRequest savedRequest = bloodRequestRepository.save(request);
        
        // Notify matching donors
        notifyMatchingDonors(savedRequest);
        
        log.info("Blood request created successfully with ID: {}", savedRequest.getId());
        
        return convertRequestToDTO(savedRequest);
    }
    
    public BloodDonorDTO updateDonorAvailability(Long donorId, boolean isAvailable) {
        log.info("Updating donor {} availability to: {}", donorId, isAvailable);
        
        BloodDonor donor = bloodDonorRepository.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood donor not found"));
        
        donor.setIsAvailable(isAvailable);
        
        if (!isAvailable && donor.getLastDonationDate() == null) {
            donor.setLastDonationDate(LocalDate.now());
        }
        
        BloodDonor updatedDonor = bloodDonorRepository.save(donor);
        
        return convertDonorToDTO(updatedDonor);
    }
    
    public BloodRequestDTO updateRequestStatus(Long requestId, String status, Integer fulfilledUnits) {
        log.info("Updating blood request {} status to: {}", requestId, status);
        
        BloodRequest request = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found"));
        
        request.setStatus(BloodRequest.Status.valueOf(status));
        
        if (fulfilledUnits != null) {
            request.setFulfilledUnits(fulfilledUnits);
            if (fulfilledUnits >= request.getUnitsNeeded()) {
                request.setStatus(BloodRequest.Status.FULFILLED);
            } else if (fulfilledUnits > 0) {
                request.setStatus(BloodRequest.Status.PARTIALLY_FULFILLED);
            }
        }
        
        BloodRequest updatedRequest = bloodRequestRepository.save(request);
        
        return convertRequestToDTO(updatedRequest);
    }
    
    @Transactional(readOnly = true)
    public List<BloodDonorDTO> findAvailableDonors(String bloodType) {
        BloodDonor.BloodType type = BloodDonor.BloodType.valueOf(bloodType);
        List<BloodDonor> donors = bloodDonorRepository.findAvailableDonorsByBloodType(type);
        
        return donors.stream()
                .map(this::convertDonorToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BloodDonorDTO> findEmergencyDonorsNearLocation(
            String bloodType, BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        
        List<BloodDonor> donors = bloodDonorRepository.findEmergencyDonorsNearLocation(
            bloodType, latitude, longitude, radiusKm);
        
        return donors.stream()
                .map(this::convertDonorToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BloodRequestDTO> getActiveRequests() {
        List<BloodRequest> requests = bloodRequestRepository.findByStatus(BloodRequest.Status.ACTIVE);
        
        return requests.stream()
                .map(this::convertRequestToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BloodRequestDTO> getUrgentRequests() {
        List<BloodRequest> requests = bloodRequestRepository.findCriticalRequests();
        
        return requests.stream()
                .map(this::convertRequestToDTO)
                .collect(Collectors.toList());
    }
    
    private void notifyMatchingDonors(BloodRequest request) {
        List<BloodDonor> matchingDonors = bloodDonorRepository
            .findAvailableDonorsByBloodType(request.getBloodType());
        
        for (BloodDonor donor : matchingDonors) {
            String urgencyText = request.getUrgency() == BloodRequest.Urgency.CRITICAL ? 
                "URGENT: " : "";
            
            notificationService.sendNotification(
                donor.getUser().getId(),
                urgencyText + "Blood Donation Request",
                "Your blood type " + request.getBloodType().getValue() + 
                " is urgently needed at " + request.getHospitalName(),
                "BLOOD_REQUEST",
                request.getId()
            );
        }
    }
    
    private BloodDonorDTO convertDonorToDTO(BloodDonor donor) {
        return BloodDonorDTO.builder()
                .id(donor.getId())
                .userId(donor.getUser().getId())
                .userName(donor.getUser().getFullName())
                .bloodType(donor.getBloodType().getValue())
                .isAvailable(donor.getIsAvailable())
                .lastDonationDate(donor.getLastDonationDate())
                .medicalEligibility(donor.getMedicalEligibility())
                .weightKg(donor.getWeightKg())
                .healthConditions(donor.getHealthConditions())
                .preferredDonationCenters(donor.getPreferredDonationCenters())
                .emergencyDonor(donor.getEmergencyDonor())
                .contactPreference(donor.getContactPreference().name())
                .createdAt(donor.getCreatedAt())
                .build();
    }
    
    private BloodRequestDTO convertRequestToDTO(BloodRequest request) {
        return BloodRequestDTO.builder()
                .id(request.getId())
                .requesterId(request.getRequester().getId())
                .requesterName(request.getRequester().getFullName())
                .patientName(request.getPatientName())
                .bloodType(request.getBloodType().getValue())
                .unitsNeeded(request.getUnitsNeeded())
                .urgency(request.getUrgency().name())
                .hospitalName(request.getHospitalName())
                .hospitalAddress(request.getHospitalAddress())
                .hospitalLatitude(request.getHospitalLatitude())
                .hospitalLongitude(request.getHospitalLongitude())
                .contactPerson(request.getContactPerson())
                .contactPhone(request.getContactPhone())
                .neededByDate(request.getNeededByDate())
                .neededByTime(request.getNeededByTime())
                .additionalRequirements(request.getAdditionalRequirements())
                .status(request.getStatus().name())
                .fulfilledUnits(request.getFulfilledUnits())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
