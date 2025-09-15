package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "help_request_responses", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"help_request_id", "volunteer_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"helpRequest", "volunteer"})
@ToString(exclude = {"helpRequest", "volunteer"})
public class HelpRequestResponse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "help_request_id", nullable = false)
    @JsonIgnore
    private HelpRequest helpRequest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false)
    @JsonIgnore
    private Volunteer volunteer;
    
    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;
    
    @Column(name = "availability_confirmed")
    @Builder.Default
    private Boolean availabilityConfirmed = true;
    
    @Column(name = "estimated_arrival_time")
    private Duration estimatedArrivalTime;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.PENDING;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum Status {
        PENDING, ACCEPTED, REJECTED, WITHDRAWN
    }
}
