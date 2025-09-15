package com.example.backend.controller;

// import com.example.backend.dto.SOSAlertDTO;
import com.example.backend.dto.SOSAlertDTO;
import com.example.backend.service.SOSAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/sos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "SOS Alerts", description = "Emergency SOS alert management")
@SecurityRequirement(name = "Bearer Authentication")
public class SOSController {
    
    private final SOSAlertService sosAlertService;
    
    @PostMapping("/alert")
    @Operation(summary = "Create SOS alert", description = "Create a new emergency SOS alert")
    public ResponseEntity<SOSAlertDTO> createSOSAlert(@Valid @RequestBody SOSAlertDTO alertDTO) {
        log.info("Creating SOS alert for user: {}", alertDTO.getUserId());
        SOSAlertDTO createdAlert = sosAlertService.createSOSAlert(alertDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAlert);
    }
    
    @PostMapping("/alert/voice")
    @Operation(summary = "Create voice SOS alert", description = "Create SOS alert with voice recording")
    public ResponseEntity<SOSAlertDTO> createVoiceSOSAlert(
            @RequestParam Long userId,
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(required = false) MultipartFile audioFile) {
        log.info("Creating voice-activated SOS alert for user: {}", userId);
        
        SOSAlertDTO alertDTO = SOSAlertDTO.builder()
                .userId(userId)
                .latitude(latitude)
                .longitude(longitude)
                .alertType("EMERGENCY")
                .priorityLevel("HIGH")
                .isVoiceActivated(true)
                .build();
        
        // Handle audio file upload if provided
        if (audioFile != null && !audioFile.isEmpty()) {
            // TODO: Upload audio file and set URL
            log.info("Processing audio file: {}", audioFile.getOriginalFilename());
        }
        
        SOSAlertDTO createdAlert = sosAlertService.createSOSAlert(alertDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAlert);
    }
    
    @PutMapping("/alert/{alertId}/respond")
    @Operation(summary = "Respond to SOS alert", description = "Mark yourself as responding to an SOS alert")
    public ResponseEntity<SOSAlertDTO> respondToAlert(
            @PathVariable Long alertId,
            @RequestParam Long responderId) {
        log.info("User {} responding to SOS alert {}", responderId, alertId);
        SOSAlertDTO updatedAlert = sosAlertService.respondToAlert(alertId, responderId);
        return ResponseEntity.ok(updatedAlert);
    }
    
    @PutMapping("/alert/{alertId}/resolve")
    @Operation(summary = "Resolve SOS alert", description = "Mark an SOS alert as resolved")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VOLUNTEER')")
    public ResponseEntity<SOSAlertDTO> resolveAlert(@PathVariable Long alertId) {
        log.info("Resolving SOS alert: {}", alertId);
        SOSAlertDTO resolvedAlert = sosAlertService.resolveAlert(alertId);
        return ResponseEntity.ok(resolvedAlert);
    }
    
    @GetMapping("/alerts/active")
    @Operation(summary = "Get active alerts", description = "Get all currently active SOS alerts")
    public ResponseEntity<List<SOSAlertDTO>> getActiveAlerts() {
        log.info("Fetching active SOS alerts");
        List<SOSAlertDTO> alerts = sosAlertService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/alerts/nearby")
    @Operation(summary = "Get nearby alerts", description = "Get SOS alerts within specified radius")
    public ResponseEntity<List<SOSAlertDTO>> getNearbyAlerts(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "5") double radiusKm) {
        log.info("Fetching SOS alerts near: {}, {} within {}km", latitude, longitude, radiusKm);
        List<SOSAlertDTO> alerts = sosAlertService.getAlertsNearLocation(latitude, longitude, radiusKm);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/alerts/user/{userId}")
    @Operation(summary = "Get user alerts", description = "Get all SOS alerts created by a specific user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<SOSAlertDTO>> getUserAlerts(@PathVariable Long userId) {
        log.info("Fetching SOS alerts for user: {}", userId);
        List<SOSAlertDTO> alerts = sosAlertService.getUserAlerts(userId);
        return ResponseEntity.ok(alerts);
    }
}
