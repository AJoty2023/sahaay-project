package com.example.backend.controller;

import com.example.backend.entity.MissingPerson;
import com.example.backend.entity.MissingPersonSighting;
import com.example.backend.repository.MissingPersonRepository;
import com.example.backend.repository.MissingPersonSightingRepository;
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
@RequestMapping("/missing-persons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Missing Persons", description = "Missing person reports and sightings")
@SecurityRequirement(name = "Bearer Authentication")
public class MissingPersonController {
    
    private final MissingPersonRepository missingPersonRepository;
    private final MissingPersonSightingRepository sightingRepository;
    
    @PostMapping
    @Operation(summary = "Report missing person", description = "Create a new missing person report")
    public ResponseEntity<MissingPerson> reportMissingPerson(@Valid @RequestBody MissingPerson missingPerson) {
        log.info("Creating missing person report for: {}", missingPerson.getMissingPersonName());
        MissingPerson saved = missingPersonRepository.save(missingPerson);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PostMapping("/{personId}/sightings")
    @Operation(summary = "Report sighting", description = "Report a sighting of a missing person")
    public ResponseEntity<MissingPersonSighting> reportSighting(
            @PathVariable Long personId,
            @Valid @RequestBody MissingPersonSighting sighting) {
        log.info("Reporting sighting for missing person ID: {}", personId);
        MissingPersonSighting saved = sightingRepository.save(sighting);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active cases", description = "Get all active missing person cases")
    public ResponseEntity<List<MissingPerson>> getActiveCases() {
        log.info("Fetching active missing person cases");
        List<MissingPerson> cases = missingPersonRepository.findActiveCases();
        return ResponseEntity.ok(cases);
    }
    
    @GetMapping("/nearby")
    @Operation(summary = "Get nearby cases", description = "Get missing person cases near a location")
    public ResponseEntity<List<MissingPerson>> getNearbyCases(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude) {
        log.info("Fetching missing person cases near: {}, {}", latitude, longitude);
        List<MissingPerson> cases = missingPersonRepository.findActiveCasesNearLocation(latitude, longitude);
        return ResponseEntity.ok(cases);
    }
    
    @GetMapping("/{personId}/sightings")
    @Operation(summary = "Get sightings", description = "Get all sightings for a missing person")
    public ResponseEntity<List<MissingPersonSighting>> getSightings(@PathVariable Long personId) {
        log.info("Fetching sightings for missing person ID: {}", personId);
        List<MissingPersonSighting> sightings = sightingRepository.findByMissingPersonId(personId);
        return ResponseEntity.ok(sightings);
    }
    
    @PutMapping("/{personId}/found")
    @Operation(summary = "Mark as found", description = "Mark a missing person as found")
    public ResponseEntity<MissingPerson> markAsFound(@PathVariable Long personId) {
        log.info("Marking missing person {} as found", personId);
        MissingPerson person = missingPersonRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Missing person not found"));
        person.setStatus(MissingPerson.Status.FOUND);
        MissingPerson updated = missingPersonRepository.save(person);
        return ResponseEntity.ok(updated);
    }
}

