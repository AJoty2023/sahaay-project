package com.example.backend.repository;

import com.example.backend.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    List<EmergencyContact> findByUserId(Long userId);
    
    @Query("SELECT e FROM EmergencyContact e WHERE e.user.id = :userId AND e.notifyOnSos = true")
    List<EmergencyContact> findNotifiableContactsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT e FROM EmergencyContact e WHERE e.user.id = :userId AND e.isPrimary = true")
    List<EmergencyContact> findPrimaryContactsByUserId(@Param("userId") Long userId);
}
