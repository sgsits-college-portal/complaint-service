package com.collegeportal.complaint_service.service;

import com.collegeportal.complaint_service.dto.ComplaintRequestDTO;
import com.collegeportal.complaint_service.entity.Complaint;
import com.collegeportal.complaint_service.entity.ComplaintImage;
import com.collegeportal.complaint_service.repository.ComplaintImageRepository;
import com.collegeportal.complaint_service.repository.ComplaintRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintImageRepository imageRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public Complaint createComplaint(ComplaintRequestDTO request, MultipartFile[] files) throws IOException {
        Complaint complaint = new Complaint();
        complaint.setUserId(request.getUserId());
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());
        complaint.setPriority(request.getPriority());
        complaint.setLocation(request.getLocation());

        Complaint savedComplaint = complaintRepository.save(complaint);

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = fileStorageService.saveImage(file);
                    ComplaintImage image = new ComplaintImage();
                    image.setImageUrl(imageUrl);
                    image.setComplaint(savedComplaint);
                    imageRepository.save(image);
                    savedComplaint.getImages().add(image);
                }
            }
        }
        return savedComplaint;
    }

    @Transactional
    public Complaint assignTechnician(Long complaintId, Long adminId, Long dispatcherId) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setAdminId(adminId);
        complaint.setDispatcherId(dispatcherId);
        complaint.setStatus("IN_PROGRESS");
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint submitForVerification(Long complaintId, String adminNote) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setAdminNote(adminNote);
        complaint.setStatus("VERIFICATION_PENDING");
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint resolveComplaint(Long complaintId, Long hodId, String hodNote) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setHodId(hodId);
        complaint.setHodNote(hodNote);
        complaint.setStatus("RESOLVED");
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint toggleVisibility(Long complaintId, boolean isPublic) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setPublic(isPublic); // Corrected from setIsPublic to setPublic
        return complaintRepository.save(complaint);
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found with id: " + id));
    }

    public List<Complaint> getPublicFeed() {
        return complaintRepository.findByIsPublicTrue();
    }
    // ADMIN: Get all complaints in the system
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<Complaint> getMyComplaints(Long userId) {
        return complaintRepository.findByUserId(userId);
    }
}