package com.example.backend.controller;

import com.example.backend.dto.BloodDonorDTO;
import com.example.backend.dto.BloodRequestDTO;
import com.example.backend.service.BloodDonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/blood")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Blood Donation", description = "Blood donation and request management")
@SecurityRequirement(name = "Bearer Authentication")
public class BloodDonationController {
    
    private final BloodDonationService bloodDonationService;
    
    @PostMapping("/donors/register")
    @Operation(summary = "Register as donor", description = "Register user as a blood donor")
    public ResponseEntity<BloodDonorDTO> registerDonor(
            @RequestParam Long userId,
            @Valid @RequestBody BloodDonorDTO donorDTO) {
        log.info("Registering blood donor for user: {}", userId);
        BloodDonorDTO registeredDonor = bloodDonationService.registerDonor(userId, donorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredDonor);
    }
    
    @PostMapping("/requests")
    @Operation(summary = "Create blood request", description = "Create a new blood donation request")
    public ResponseEntity<BloodRequestDTO> createBloodRequest(@Valid @RequestBody BloodRequestDTO requestDTO) {
        log.info("Creating blood request for patient: {}", requestDTO.getPatientName());
        BloodRequestDTO createdRequest = bloodDonationService.createBloodRequest(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }
    
    @PutMapping("/donors/{donorId}/availability")
    @Operation(summary = "Update donor availability", description = "Update blood donor availability status")
    public ResponseEntity<BloodDonorDTO> updateDonorAvailability(
            @PathVariable Long donorId,
            @RequestParam boolean isAvailable) {
        log.info("Updating donor {} availability to: {}", donorId, isAvailable);
        BloodDonorDTO updatedDonor = bloodDonationService.updateDonorAvailability(donorId, isAvailable);
        return ResponseEntity.ok(updatedDonor);
    }
    
    @PutMapping("/requests/{requestId}/status")
    @Operation(summary = "Update request status", description = "Update blood request status")
    public ResponseEntity<BloodRequestDTO> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestParam String status,
            @RequestParam(required = false) Integer fulfilledUnits) {
        log.info("Updating blood request {} status to: {}", requestId, status);
        BloodRequestDTO updatedRequest = bloodDonationService.updateRequestStatus(requestId, status, fulfilledUnits);
        return ResponseEntity.ok(updatedRequest);
    }
    
    @GetMapping("/donors/available")
    @Operation(summary = "Find available donors", description = "Find available donors by blood type")
    public ResponseEntity<List<BloodDonorDTO>> findAvailableDonors(@RequestParam String bloodType) {
        log.info("Finding available donors for blood type: {}", bloodType);
        List<BloodDonorDTO> donors = bloodDonationService.findAvailableDonors(bloodType);
        return ResponseEntity.ok(donors);
    }
    
    @GetMapping("/donors/emergency")
    @Operation(summary = "Find emergency donors", description = "Find emergency donors near a location")
    public ResponseEntity<List<BloodDonorDTO>> findEmergencyDonors(
            @RequestParam String bloodType,
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "10") double radiusKm) {
        log.info("Finding emergency donors for {} near {}, {}", bloodType, latitude, longitude);
        List<BloodDonorDTO> donors = bloodDonationService.findEmergencyDonorsNearLocation(
            bloodType, latitude, longitude, radiusKm);
        return ResponseEntity.ok(donors);
    }
    
    @GetMapping("/requests/active")
    @Operation(summary = "Get active requests", description = "Get all active blood requests")
    public ResponseEntity<List<BloodRequestDTO>> getActiveRequests() {
        log.info("Fetching active blood requests");
        List<BloodRequestDTO> requests = bloodDonationService.getActiveRequests();
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/requests/urgent")
    @Operation(summary = "Get urgent requests", description = "Get urgent/critical blood requests")
    public ResponseEntity<List<BloodRequestDTO>> getUrgentRequests() {
        log.info("Fetching urgent blood requests");
        List<BloodRequestDTO> requests = bloodDonationService.getUrgentRequests();
        return ResponseEntity.ok(requests);
    }
}
