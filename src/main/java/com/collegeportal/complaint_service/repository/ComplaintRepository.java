package com.collegeportal.complaint_service.repository;

import com.collegeportal.complaint_service.domain.Complaint;
import com.collegeportal.complaint_service.domain.ComplaintCategory;
import com.collegeportal.complaint_service.domain.ComplaintPriority;
import com.collegeportal.complaint_service.domain.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Complaint> findByCategoryOrderByCreatedAtDesc(ComplaintCategory category);
    List<Complaint> findByStatusOrderByCreatedAtDesc(ComplaintStatus status);
    List<Complaint> findByPriorityOrderByCreatedAtDesc(ComplaintPriority priority);
    List<Complaint> findByAssignedToOrderByCreatedAtDesc(Long assignedTo);
}
