package com.example.backend.repository;

import com.example.backend.entity.AbuseReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbuseReportRepository extends JpaRepository<AbuseReport, Long> {
    List<AbuseReport> findByReporterId(Long reporterId);
    List<AbuseReport> findByStatus(AbuseReport.Status status);
    List<AbuseReport> findByReportType(AbuseReport.ReportType reportType);
    
    @Query("SELECT a FROM AbuseReport a WHERE a.urgencyLevel = 'CRITICAL' AND a.status IN ('SUBMITTED', 'UNDER_REVIEW')")
    List<AbuseReport> findCriticalPendingReports();
    
    @Query("SELECT a FROM AbuseReport a WHERE a.assignedAdmin.id = :adminId")
    List<AbuseReport> findByAssignedAdminId(@Param("adminId") Long adminId);
    
    @Query("SELECT a FROM AbuseReport a WHERE a.followUpRequired = true AND a.status != 'CLOSED'")
    List<AbuseReport> findReportsRequiringFollowUp();
}
