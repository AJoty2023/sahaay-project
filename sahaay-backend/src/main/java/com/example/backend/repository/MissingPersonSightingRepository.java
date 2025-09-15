package com.example.backend.repository;

import com.example.backend.entity.MissingPersonSighting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissingPersonSightingRepository extends JpaRepository<MissingPersonSighting, Long> {
    List<MissingPersonSighting> findByMissingPersonId(Long missingPersonId);
    
    @Query("SELECT s FROM MissingPersonSighting s WHERE s.missingPerson.id = :personId AND s.isVerified = true")
    List<MissingPersonSighting> findVerifiedSightings(@Param("personId") Long missingPersonId);
    
    @Query("SELECT s FROM MissingPersonSighting s WHERE s.missingPerson.id = :personId ORDER BY s.sightingDate DESC, s.sightingTime DESC")
    List<MissingPersonSighting> findByMissingPersonIdOrderByDateDesc(@Param("personId") Long missingPersonId);
}