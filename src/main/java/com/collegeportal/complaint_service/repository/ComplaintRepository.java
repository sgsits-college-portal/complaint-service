package com.collegeportal.complaint_service.repository;

import com.collegeportal.complaint_service.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    List<Complaint> findByIsPublicTrue();
    
    List<Complaint> findByUserId(Long userId);
    
    // Updated from String to our strict Enum
    List<Complaint> findByStatus(Complaint.Status status);
}