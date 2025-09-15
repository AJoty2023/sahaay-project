package com.example.backend.repository;

import com.example.backend.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByUserId(Long userId);
    
    @Query("SELECT v FROM Volunteer v WHERE v.isAvailable = true AND v.verificationStatus = 'VERIFIED'")
    List<Volunteer> findAvailableVolunteers();
    
    @Query("SELECT v FROM Volunteer v JOIN v.skills s WHERE s.id IN :skillIds " +
           "AND v.isAvailable = true AND v.verificationStatus = 'VERIFIED'")
    List<Volunteer> findVolunteersBySkills(@Param("skillIds") List<Long> skillIds);
    
    @Query(value = "SELECT v.* FROM volunteers v " +
           "JOIN users u ON v.user_id = u.id " +
           "WHERE v.is_available = true AND v.verification_status = 'VERIFIED' " +
           "AND (6371 * acos(cos(radians(:lat)) * cos(radians(u.latitude)) * " +
           "cos(radians(u.longitude) - radians(:lng)) + sin(radians(:lat)) * " +
           "sin(radians(u.latitude)))) < v.max_distance_km", nativeQuery = true)
    List<Volunteer> findVolunteersNearLocation(@Param("lat") BigDecimal latitude, 
                                               @Param("lng") BigDecimal longitude);
}

