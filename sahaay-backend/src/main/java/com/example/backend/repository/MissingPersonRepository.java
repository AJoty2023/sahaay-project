package com.example.backend.repository;

import com.example.backend.entity.MissingPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MissingPersonRepository extends JpaRepository<MissingPerson, Long> {
    List<MissingPerson> findByReporterId(Long reporterId);
    List<MissingPerson> findByStatus(MissingPerson.Status status);
    
    @Query("SELECT m FROM MissingPerson m WHERE m.status = 'ACTIVE' ORDER BY m.createdAt DESC")
    List<MissingPerson> findActiveCases();
    
    @Query(value = "SELECT * FROM missing_persons WHERE status = 'ACTIVE' " +
           "AND (6371 * acos(cos(radians(:lat)) * cos(radians(last_seen_latitude)) * " +
           "cos(radians(last_seen_longitude) - radians(:lng)) + sin(radians(:lat)) * " +
           "sin(radians(last_seen_latitude)))) < search_radius_km", nativeQuery = true)
    List<MissingPerson> findActiveCasesNearLocation(@Param("lat") BigDecimal latitude, 
                                                    @Param("lng") BigDecimal longitude);
}
