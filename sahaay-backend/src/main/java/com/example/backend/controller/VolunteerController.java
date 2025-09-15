package com.example.backend.controller;

import com.example.backend.dto.VolunteerDTO;
import com.example.backend.dto.VolunteerRegistrationDTO;
import com.example.backend.service.VolunteerService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/volunteers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Volunteer Management", description = "Volunteer registration and management")
@SecurityRequirement(name = "Bearer Authentication")
public class VolunteerController {
    
    private final VolunteerService volunteerService;
    
    @PostMapping("/register")
    @Operation(summary = "Register as volunteer", description = "Register user as a volunteer")
    public ResponseEntity<VolunteerDTO> registerVolunteer(
            @RequestParam Long userId,
            @Valid @RequestBody VolunteerRegistrationDTO registrationDTO) {
        log.info("Registering volunteer for user: {}", userId);
        VolunteerDTO volunteer = volunteerService.registerVolunteer(userId, registrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(volunteer);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get volunteer by ID", description = "Get volunteer details by ID")
    public ResponseEntity<VolunteerDTO> getVolunteerById(@PathVariable Long id) {
        log.info("Fetching volunteer with ID: {}", id);
        VolunteerDTO volunteer = volunteerService.getVolunteerById(id);
        return ResponseEntity.ok(volunteer);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get volunteer by user ID", description = "Get volunteer details by user ID")
    public ResponseEntity<VolunteerDTO> getVolunteerByUserId(@PathVariable Long userId) {
        log.info("Fetching volunteer for user ID: {}", userId);
        VolunteerDTO volunteer = volunteerService.getVolunteerByUserId(userId);
        return ResponseEntity.ok(volunteer);
    }
    
    @PutMapping("/{id}/availability")
    @Operation(summary = "Update availability", description = "Update volunteer availability status")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<VolunteerDTO> updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean isAvailable) {
        log.info("Updating volunteer {} availability to: {}", id, isAvailable);
        VolunteerDTO volunteer = volunteerService.updateAvailability(id, isAvailable);
        return ResponseEntity.ok(volunteer);
    }
    
    @PutMapping("/{id}/verify")
    @Operation(summary = "Verify volunteer", description = "Verify and approve a volunteer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VolunteerDTO> verifyVolunteer(@PathVariable Long id) {
        log.info("Verifying volunteer: {}", id);
        VolunteerDTO volunteer = volunteerService.verifyVolunteer(id);
        return ResponseEntity.ok(volunteer);
    }
    
    @GetMapping("/available")
    @Operation(summary = "Get available volunteers", description = "Get all available volunteers")
    public ResponseEntity<List<VolunteerDTO>> getAvailableVolunteers() {
        log.info("Fetching available volunteers");
        List<VolunteerDTO> volunteers = volunteerService.getAvailableVolunteers();
        return ResponseEntity.ok(volunteers);
    }
    
    @GetMapping("/nearby")
    @Operation(summary = "Get nearby volunteers", description = "Get volunteers near a location")
    public ResponseEntity<List<VolunteerDTO>> getNearbyVolunteers(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude) {
        log.info("Fetching volunteers near: {}, {}", latitude, longitude);
        List<VolunteerDTO> volunteers = volunteerService.getVolunteersNearLocation(latitude, longitude);
        return ResponseEntity.ok(volunteers);
    }
    
    @GetMapping("/skills")
    @Operation(summary = "Get volunteers by skills", description = "Get volunteers with specific skills")
    public ResponseEntity<List<VolunteerDTO>> getVolunteersBySkills(@RequestParam List<Long> skillIds) {
        log.info("Fetching volunteers with skills: {}", skillIds);
        List<VolunteerDTO> volunteers = volunteerService.getVolunteersBySkills(skillIds);
        return ResponseEntity.ok(volunteers);
    }
}

