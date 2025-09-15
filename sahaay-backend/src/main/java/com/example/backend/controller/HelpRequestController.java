package com.example.backend.controller;

import com.example.backend.dto.HelpRequestDTO;
import com.example.backend.service.HelpRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/help-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Help Requests", description = "Community help request management")
@SecurityRequirement(name = "Bearer Authentication")
public class HelpRequestController {
    
    private final HelpRequestService helpRequestService;
    
    @PostMapping
    @Operation(summary = "Create help request", description = "Create a new help request")
    public ResponseEntity<HelpRequestDTO> createHelpRequest(@Valid @RequestBody HelpRequestDTO requestDTO) {
        log.info("Creating help request: {}", requestDTO.getTitle());
        HelpRequestDTO createdRequest = helpRequestService.createHelpRequest(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }
    
    @PutMapping("/{requestId}/assign")
    @Operation(summary = "Assign volunteer", description = "Assign a volunteer to a help request")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VOLUNTEER')")
    public ResponseEntity<HelpRequestDTO> assignVolunteer(
            @PathVariable Long requestId,
            @RequestParam Long volunteerId) {
        log.info("Assigning volunteer {} to request {}", volunteerId, requestId);
        HelpRequestDTO updatedRequest = helpRequestService.assignVolunteer(requestId, volunteerId);
        return ResponseEntity.ok(updatedRequest);
    }
    
    @PutMapping("/{requestId}/status")
    @Operation(summary = "Update request status", description = "Update the status of a help request")
    public ResponseEntity<HelpRequestDTO> updateStatus(
            @PathVariable Long requestId,
            @RequestParam String status) {
        log.info("Updating request {} status to: {}", requestId, status);
        HelpRequestDTO updatedRequest = helpRequestService.updateRequestStatus(requestId, status);
        return ResponseEntity.ok(updatedRequest);
    }
    
    @GetMapping("/open")
    @Operation(summary = "Get open requests", description = "Get all open help requests")
    public ResponseEntity<List<HelpRequestDTO>> getOpenRequests() {
        log.info("Fetching open help requests");
        List<HelpRequestDTO> requests = helpRequestService.getOpenRequests();
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/nearby")
    @Operation(summary = "Get nearby requests", description = "Get help requests near a location")
    public ResponseEntity<List<HelpRequestDTO>> getNearbyRequests(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude) {
        log.info("Fetching help requests near: {}, {}", latitude, longitude);
        List<HelpRequestDTO> requests = helpRequestService.getRequestsNearLocation(latitude, longitude);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user requests", description = "Get all help requests by a user")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<HelpRequestDTO>> getUserRequests(@PathVariable Long userId) {
        log.info("Fetching help requests for user: {}", userId);
        List<HelpRequestDTO> requests = helpRequestService.getUserRequests(userId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/volunteer/{volunteerId}")
    @Operation(summary = "Get volunteer assignments", description = "Get help requests assigned to a volunteer")
    @PreAuthorize("hasRole('ADMIN') or #volunteerId == authentication.principal.id")
    public ResponseEntity<List<HelpRequestDTO>> getVolunteerAssignments(@PathVariable Long volunteerId) {
        log.info("Fetching assignments for volunteer: {}", volunteerId);
        List<HelpRequestDTO> requests = helpRequestService.getVolunteerAssignments(volunteerId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/scheduled")
    @Operation(summary = "Get scheduled requests", description = "Get help requests scheduled for a specific date")
    public ResponseEntity<List<HelpRequestDTO>> getScheduledRequests(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Fetching help requests scheduled for: {}", date);
        List<HelpRequestDTO> requests = helpRequestService.getScheduledRequestsForDate(date);
        return ResponseEntity.ok(requests);
    }
}