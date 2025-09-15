package com.example.backend.controller;

import com.example.backend.dto.UserDTO;
import com.example.backend.dto.UserUpdateDTO;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User profile and management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user profile information")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("Updating user with ID: {}", id);
        UserDTO updatedUser = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/nearby")
    @Operation(summary = "Find nearby users", description = "Find users within specified radius")
    public ResponseEntity<List<UserDTO>> getNearbyUsers(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "10") double radiusKm) {
        log.info("Finding users near coordinates: {}, {}", latitude, longitude);
        List<UserDTO> users = userService.getUsersNearLocation(latitude, longitude, radiusKm);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/type/{userType}")
    @Operation(summary = "Get users by type", description = "Get all users of a specific type")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersByType(@PathVariable String userType) {
        log.info("Fetching users of type: {}", userType);
        List<UserDTO> users = userService.getUsersByType(User.UserType.valueOf(userType));
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/{id}/verify")
    @Operation(summary = "Verify user", description = "Mark user account as verified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verifyUser(@PathVariable Long id) {
        log.info("Verifying user with ID: {}", id);
        userService.verifyUser(id);
        return ResponseEntity.ok().build();
    }
}

