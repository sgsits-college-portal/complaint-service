package com.collegeportal.complaint_service.repository;

import com.collegeportal.complaint_service.entity.ComplaintImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintImageRepository extends JpaRepository<ComplaintImage, Long> {
}