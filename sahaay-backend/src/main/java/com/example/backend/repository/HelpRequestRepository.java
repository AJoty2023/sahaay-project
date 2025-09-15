package com.example.backend.repository; 

import com.example.backend.entity.HelpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HelpRequestRepository extends JpaRepository<HelpRequest, Long> {
    List<HelpRequest> findByRequesterId(Long requesterId);
    List<HelpRequest> findByStatus(HelpRequest.Status status);
    List<HelpRequest> findByCategory(String category);
    
    @Query("SELECT h FROM HelpRequest h WHERE h.status = 'OPEN' ORDER BY h.urgency DESC, h.createdAt DESC")
    List<HelpRequest> findOpenRequests();
    
    @Query("SELECT h FROM HelpRequest h WHERE h.assignedVolunteer.id = :volunteerId")
    List<HelpRequest> findByAssignedVolunteerId(@Param("volunteerId") Long volunteerId);
    
    @Query("SELECT h FROM HelpRequest h WHERE h.scheduledDate = :date AND h.status IN ('OPEN', 'ASSIGNED')")
    List<HelpRequest> findScheduledRequestsForDate(@Param("date") LocalDate date);
    
    @Query(value = "SELECT * FROM help_requests WHERE status = 'OPEN' " +
           "AND (6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * " +
           "cos(radians(longitude) - radians(:lng)) + sin(radians(:lat)) * " +
           "sin(radians(latitude)))) < max_distance_km", nativeQuery = true)
    List<HelpRequest> findOpenRequestsNearLocation(@Param("lat") BigDecimal latitude, 
                                                   @Param("lng") BigDecimal longitude);
}
