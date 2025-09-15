package com.example.backend.service;

import com.example.backend.dto.HelpRequestDTO;
import com.example.backend.entity.HelpRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.Volunteer;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.HelpRequestRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HelpRequestService {
    
    private final HelpRequestRepository helpRequestRepository;
    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final NotificationService notificationService;

    
    public HelpRequestDTO createHelpRequest(HelpRequestDTO requestDTO) {
        log.info("Creating help request for user: {}", requestDTO.getRequesterId());
        
        User requester = userRepository.findById(requestDTO.getRequesterId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        HelpRequest helpRequest = HelpRequest.builder()
                .requester(requester)
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .category(requestDTO.getCategory())
                .urgency(HelpRequest.Urgency.valueOf(requestDTO.getUrgency()))
                .requiredSkills(requestDTO.getRequiredSkills())
                .preferredVolunteerGender(requestDTO.getPreferredVolunteerGender())
                .estimatedDuration(requestDTO.getEstimatedDuration())
                .locationAddress(requestDTO.getLocationAddress())
                .latitude(requestDTO.getLatitude())
                .longitude(requestDTO.getLongitude())
                .maxDistanceKm(requestDTO.getMaxDistanceKm())
                .scheduledDate(requestDTO.getScheduledDate())
                .scheduledTime(requestDTO.getScheduledTime())
                .isRecurring(requestDTO.getIsRecurring())
                .recurringPattern(requestDTO.getRecurringPattern())
                .compensationOffered(requestDTO.getCompensationOffered())
                .status(HelpRequest.Status.OPEN)
                .build();
        
        HelpRequest savedRequest = helpRequestRepository.save(helpRequest);
        
        // Find and notify suitable volunteers
        notifySuitableVolunteers(savedRequest);
        
        log.info("Help request created successfully with ID: {}", savedRequest.getId());
        
        return convertToDTO(savedRequest);
    }
    
    public HelpRequestDTO assignVolunteer(Long requestId, Long volunteerId) {
        log.info("Assigning volunteer {} to help request {}", volunteerId, requestId);
        
        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Help request not found"));
        
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found"));
        
        request.setAssignedVolunteer(volunteer);
        request.setStatus(HelpRequest.Status.ASSIGNED);
        request.setAssignedAt(LocalDateTime.now());
        
        HelpRequest updatedRequest = helpRequestRepository.save(request);
        
        // Notify the requester
        notificationService.sendNotification(
            request.getRequester().getId(),
            "Volunteer Assigned",
            volunteer.getUser().getFullName() + " has been assigned to your help request",
            "HELP_REQUEST",
            requestId
        );
        
        return convertToDTO(updatedRequest);
    }
    
    public HelpRequestDTO updateRequestStatus(Long requestId, String status) {
        log.info("Updating status of help request {} to {}", requestId, status);
        
        HelpRequest request = helpRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Help request not found"));
        
        request.setStatus(HelpRequest.Status.valueOf(status));
        
        if (status.equals("COMPLETED")) {
            request.setCompletedAt(LocalDateTime.now());
            // Update volunteer statistics
            if (request.getAssignedVolunteer() != null) {
                volunteerRepository.findById(request.getAssignedVolunteer().getId())
                    .ifPresent(volunteer -> {
                        volunteer.setTotalCompletedTasks(volunteer.getTotalCompletedTasks() + 1);
                        volunteerRepository.save(volunteer);
                    });
            }
        }
        
        HelpRequest updatedRequest = helpRequestRepository.save(request);
        
        return convertToDTO(updatedRequest);
    }
    
    @Transactional(readOnly = true)
    public List<HelpRequestDTO> getOpenRequests() {
        List<HelpRequest> requests = helpRequestRepository.findOpenRequests();
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HelpRequestDTO> getRequestsNearLocation(BigDecimal latitude, BigDecimal longitude) {
        List<HelpRequest> requests = helpRequestRepository.findOpenRequestsNearLocation(latitude, longitude);
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HelpRequestDTO> getUserRequests(Long userId) {
        List<HelpRequest> requests = helpRequestRepository.findByRequesterId(userId);
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HelpRequestDTO> getVolunteerAssignments(Long volunteerId) {
        List<HelpRequest> requests = helpRequestRepository.findByAssignedVolunteerId(volunteerId);
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HelpRequestDTO> getScheduledRequestsForDate(LocalDate date) {
        List<HelpRequest> requests = helpRequestRepository.findScheduledRequestsForDate(date);
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private void notifySuitableVolunteers(HelpRequest request) {
        // Find volunteers near the location with required skills
        List<Volunteer> volunteers = volunteerRepository.findVolunteersNearLocation(
            request.getLatitude(), request.getLongitude());
        
        for (Volunteer volunteer : volunteers) {
            notificationService.sendNotification(
                volunteer.getUser().getId(),
                "New Help Request",
                "A new help request matching your skills is available",
                "HELP_REQUEST",
                request.getId()
            );
        }
    }
    
    private HelpRequestDTO convertToDTO(HelpRequest request) {
        return HelpRequestDTO.builder()
                .id(request.getId())
                .requesterId(request.getRequester().getId())
                .requesterName(request.getRequester().getFullName())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .urgency(request.getUrgency().name())
                .requiredSkills(request.getRequiredSkills())
                .preferredVolunteerGender(request.getPreferredVolunteerGender())
                .estimatedDuration(request.getEstimatedDuration())
                .locationAddress(request.getLocationAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .maxDistanceKm(request.getMaxDistanceKm())
                .scheduledDate(request.getScheduledDate())
                .scheduledTime(request.getScheduledTime())
                .isRecurring(request.getIsRecurring())
                .recurringPattern(request.getRecurringPattern())
                .compensationOffered(request.getCompensationOffered())
                .status(request.getStatus().name())
                .assignedVolunteerId(request.getAssignedVolunteer() != null ? 
                    request.getAssignedVolunteer().getId() : null)
                .assignedAt(request.getAssignedAt())
                .completedAt(request.getCompletedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}

