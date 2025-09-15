package com.example.backend.service;

import com.example.backend.dto.SOSAlertDTO;
// Ensure that the SOSAlertDTO class exists at com.example.backend.dto.SOSAlertDTO
import com.example.backend.entity.SOSAlert;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.SOSAlertRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.websocket.SOSWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SOSAlertService {
    
    private final SOSAlertRepository sosAlertRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final LocationService locationService;
    private SOSWebSocketHandler webSocketHandler;
    
    public SOSAlertDTO createSOSAlert(SOSAlertDTO alertDTO) {
        log.info("Creating SOS alert for user: {}", alertDTO.getUserId());
        
        User user = userRepository.findById(alertDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Get location address from coordinates
        String locationAddress = locationService.getAddressFromCoordinates(
            alertDTO.getLatitude(), alertDTO.getLongitude());
        
        SOSAlert sosAlert = SOSAlert.builder()
                .user(user)
                .alertType(SOSAlert.AlertType.valueOf(alertDTO.getAlertType()))
                .latitude(alertDTO.getLatitude())
                .longitude(alertDTO.getLongitude())
                .locationAddress(locationAddress)
                .alertMessage(alertDTO.getAlertMessage())
                .audioFileUrl(alertDTO.getAudioFileUrl())
                .status(SOSAlert.Status.ACTIVE)
                .priorityLevel(SOSAlert.PriorityLevel.valueOf(alertDTO.getPriorityLevel()))
                .isVoiceActivated(alertDTO.getIsVoiceActivated())
                .build();
        
        // AI Analysis (if configured)
        if (alertDTO.getAlertMessage() != null) {
            Map<String, Object> aiAnalysis = analyzeAlert(alertDTO.getAlertMessage());
            sosAlert.setAiAnalysis(aiAnalysis);
        }
        
        SOSAlert savedAlert = sosAlertRepository.save(sosAlert);
        
        // Send notifications to nearby users and emergency contacts
        notifyNearbyUsers(savedAlert);
        notifyEmergencyContacts(savedAlert);
        
        // Broadcast via WebSocket for real-time updates
        if (webSocketHandler != null) {
            webSocketHandler.broadcastSOSAlert(convertToDTO(savedAlert));
        }
        
        log.info("SOS alert created successfully with ID: {}", savedAlert.getId());
        
        return convertToDTO(savedAlert);
    }
    
    public SOSAlertDTO respondToAlert(Long alertId, Long responderId) {
        log.info("User {} responding to SOS alert {}", responderId, alertId);
        
        SOSAlert alert = sosAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("SOS Alert not found"));
        
        User responder = userRepository.findById(responderId)
                .orElseThrow(() -> new ResourceNotFoundException("Responder not found"));
        
        alert.setStatus(SOSAlert.Status.RESPONDED);
        alert.setRespondedBy(responder);
        alert.setResponseTime(LocalDateTime.now());
        
        SOSAlert updatedAlert = sosAlertRepository.save(alert);
        
        // Notify the person who raised the alert
        notificationService.sendNotification(
            alert.getUser().getId(),
            "SOS Response",
            responder.getFullName() + " is responding to your SOS alert",
            "SOS_ALERT",
            alertId
        );
        
        return convertToDTO(updatedAlert);
    }
    
    public SOSAlertDTO resolveAlert(Long alertId) {
        log.info("Resolving SOS alert {}", alertId);
        
        SOSAlert alert = sosAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("SOS Alert not found"));
        
        alert.setStatus(SOSAlert.Status.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        
        SOSAlert updatedAlert = sosAlertRepository.save(alert);
        
        return convertToDTO(updatedAlert);
    }
    
    @Transactional(readOnly = true)
    public List<SOSAlertDTO> getActiveAlerts() {
        List<SOSAlert> alerts = sosAlertRepository.findActiveAlerts();
        return alerts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<SOSAlertDTO> getAlertsNearLocation(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        List<SOSAlert> alerts = sosAlertRepository.findActiveAlertsWithinRadius(latitude, longitude, radiusKm);
        return alerts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<SOSAlertDTO> getUserAlerts(Long userId) {
        List<SOSAlert> alerts = sosAlertRepository.findByUserId(userId);
        return alerts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private void notifyNearbyUsers(SOSAlert alert) {
        // Find users within 5km radius
        List<User> nearbyUsers = userRepository.findUsersWithinRadius(
            alert.getLatitude(), alert.getLongitude(), 5.0);
        
        for (User user : nearbyUsers) {
            if (!user.getId().equals(alert.getUser().getId())) {
                notificationService.sendNotification(
                    user.getId(),
                    "Emergency SOS Alert Nearby",
                    "An SOS alert has been raised near your location",
                    "SOS_ALERT",
                    alert.getId()
                );
            }
        }
    }
    
    private void notifyEmergencyContacts(SOSAlert alert) {
        // This would integrate with EmergencyContactService
        // Send SMS/Email to emergency contacts
        log.info("Notifying emergency contacts for user: {}", alert.getUser().getId());
    }
    
    private Map<String, Object> analyzeAlert(String message) {
        Map<String, Object> analysis = new HashMap<>();
        // Placeholder for AI analysis
        analysis.put("sentiment", "urgent");
        analysis.put("keywords", List.of("help", "emergency"));
        analysis.put("riskLevel", "high");
        return analysis;
    }
    
    private SOSAlertDTO convertToDTO(SOSAlert alert) {
        return SOSAlertDTO.builder()
                .id(alert.getId())
                .userId(alert.getUser().getId())
                .userName(alert.getUser().getFullName())
                .alertType(alert.getAlertType().name())
                .latitude(alert.getLatitude())
                .longitude(alert.getLongitude())
                .locationAddress(alert.getLocationAddress())
                .alertMessage(alert.getAlertMessage())
                .audioFileUrl(alert.getAudioFileUrl())
                .status(alert.getStatus().name())
                .priorityLevel(alert.getPriorityLevel().name())
                .isVoiceActivated(alert.getIsVoiceActivated())
                .respondedById(alert.getRespondedBy() != null ? alert.getRespondedBy().getId() : null)
                .responseTime(alert.getResponseTime())
                .resolvedAt(alert.getResolvedAt())
                .aiAnalysis(alert.getAiAnalysis())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
