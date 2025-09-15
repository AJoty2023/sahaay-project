package com.example.backend.repository;

import com.example.backend.entity.BloodRequest;
import com.example.backend.entity.BloodDonor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    List<BloodRequest> findByRequesterId(Long requesterId);
    List<BloodRequest> findByStatus(BloodRequest.Status status);
    List<BloodRequest> findByBloodType(BloodDonor.BloodType bloodType);
    
    @Query("SELECT r FROM BloodRequest r WHERE r.status = 'ACTIVE' AND r.bloodType = :bloodType")
    List<BloodRequest> findActiveRequestsByBloodType(@Param("bloodType") BloodDonor.BloodType bloodType);
    
    @Query("SELECT r FROM BloodRequest r WHERE r.status = 'ACTIVE' AND r.neededByDate <= :date")
    List<BloodRequest> findUrgentRequests(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM BloodRequest r WHERE r.urgency = 'CRITICAL' AND r.status = 'ACTIVE'")
    List<BloodRequest> findCriticalRequests();
}
