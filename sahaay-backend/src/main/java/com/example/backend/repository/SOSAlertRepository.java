package com.example.backend.repository;


import com.example.backend.entity.SOSAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SOSAlertRepository extends JpaRepository<SOSAlert, Long> {
    List<SOSAlert> findByUserId(Long userId);
    List<SOSAlert> findByStatus(SOSAlert.Status status);
    
    @Query("SELECT s FROM SOSAlert s WHERE s.status = 'ACTIVE' ORDER BY s.createdAt DESC")
    List<SOSAlert> findActiveAlerts();
    
    @Query(value = "SELECT * FROM sos_alerts WHERE status = 'ACTIVE' " +
           "AND (6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * " +
           "cos(radians(longitude) - radians(:lng)) + sin(radians(:lat)) * " +
           "sin(radians(latitude)))) < :radius " +
           "ORDER BY created_at DESC", nativeQuery = true)
    List<SOSAlert> findActiveAlertsWithinRadius(@Param("lat") BigDecimal latitude, 
                                                @Param("lng") BigDecimal longitude, 
                                                @Param("radius") double radiusKm);
    
    @Query("SELECT s FROM SOSAlert s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    List<SOSAlert> findAlertsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
}