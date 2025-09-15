package com.example.backend.repository;
import com.example.backend.entity.BloodDonor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BloodDonorRepository extends JpaRepository<BloodDonor, Long> {
    Optional<BloodDonor> findByUserId(Long userId);
    List<BloodDonor> findByBloodType(BloodDonor.BloodType bloodType);
    
    @Query("SELECT d FROM BloodDonor d WHERE d.bloodType = :bloodType AND d.isAvailable = true AND d.medicalEligibility = true")
    List<BloodDonor> findAvailableDonorsByBloodType(@Param("bloodType") BloodDonor.BloodType bloodType);
    
    @Query(value = "SELECT d.* FROM blood_donors d " +
           "JOIN users u ON d.user_id = u.id " +
           "WHERE d.blood_type = :bloodType AND d.is_available = true " +
           "AND d.medical_eligibility = true AND d.emergency_donor = true " +
           "AND (6371 * acos(cos(radians(:lat)) * cos(radians(u.latitude)) * " +
           "cos(radians(u.longitude) - radians(:lng)) + sin(radians(:lat)) * " +
           "sin(radians(u.latitude)))) < :radius", nativeQuery = true)
    List<BloodDonor> findEmergencyDonorsNearLocation(@Param("bloodType") String bloodType,
                                                     @Param("lat") BigDecimal latitude, 
                                                     @Param("lng") BigDecimal longitude, 
                                                     @Param("radius") double radiusKm);
}
