package com.example.backend.repository;

import com.example.backend.entity.HelpRequestResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HelpRequestResponseRepository extends JpaRepository<HelpRequestResponse, Long> {
    List<HelpRequestResponse> findByHelpRequestId(Long helpRequestId);
    List<HelpRequestResponse> findByVolunteerId(Long volunteerId);
    
    @Query("SELECT r FROM HelpRequestResponse r WHERE r.helpRequest.id = :requestId AND r.volunteer.id = :volunteerId")
    Optional<HelpRequestResponse> findByHelpRequestIdAndVolunteerId(@Param("requestId") Long requestId, 
                                                                    @Param("volunteerId") Long volunteerId);
    
    @Query("SELECT r FROM HelpRequestResponse r WHERE r.status = :status")
    List<HelpRequestResponse> findByStatus(@Param("status") HelpRequestResponse.Status status);
}
