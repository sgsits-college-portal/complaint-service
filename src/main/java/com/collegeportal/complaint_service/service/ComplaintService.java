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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
        complaint.setLocation(request.getLocation());
        
        complaint.setPriority(Complaint.Priority.valueOf(request.getPriority().toUpperCase()));

        Complaint savedComplaint = complaintRepository.save(complaint);

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    FileStorageService.CloudUploadResult uploadResult = fileStorageService.uploadFile(file);
                    
                    ComplaintImage image = new ComplaintImage();
                    image.setImageUrl(uploadResult.url());
                    image.setCloudId(uploadResult.cloudId()); 
                    image.setComplaint(savedComplaint);
                    
                    imageRepository.save(image);
                    savedComplaint.getImages().add(image);
                }
            }
        }
        return savedComplaint;
    }

    @Transactional
    public Complaint assignTechnician(Long complaintId, String adminId, String dispatcherId) {
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new IllegalArgumentException("Technician ID (adminId) cannot be empty.");
        }
        
        Complaint complaint = getComplaintById(complaintId);
        complaint.setAdminId(adminId);
        complaint.setDispatcherId(dispatcherId);
        
        complaint.setStatus(Complaint.Status.IN_PROGRESS); 
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint submitForVerification(Long complaintId, String adminNote) {
        Complaint complaint = getComplaintById(complaintId);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !authentication.getName().equals(complaint.getAdminId())) {
             throw new AccessDeniedException("Access Denied: You can only add notes to complaints explicitly assigned to you.");
        }
        
        complaint.setAdminNote(adminNote);
        complaint.setStatus(Complaint.Status.VERIFICATION_PENDING);
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint approveComplaint(Long complaintId, String hodId, String hodNote) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setHodId(hodId);
        complaint.setHodNote(hodNote);
        complaint.setStatus(Complaint.Status.IN_PROGRESS); 
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint rejectComplaint(Long complaintId, String hodId, String hodNote) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setHodId(hodId);
        complaint.setHodNote(hodNote);
        complaint.setStatus(Complaint.Status.REJECTED); 
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint closeComplaint(Long complaintId) {
        Complaint complaint = getComplaintById(complaintId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !authentication.getName().equals(complaint.getAdminId())) {
             throw new AccessDeniedException("Access Denied: You can only close complaints explicitly assigned to you.");
        }
        complaint.setStatus(Complaint.Status.RESOLVED);
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint toggleVisibility(Long complaintId, boolean isPublic) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setPublic(isPublic); 
        return complaintRepository.save(complaint);
    }

    @Transactional
    public Complaint upvoteComplaint(Long complaintId) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setUpvoteCount(complaint.getUpvoteCount() + 1);
        return complaintRepository.save(complaint);
    }

    public Complaint getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Complaint not found with id: " + id));
                
        if (!complaint.isPublic()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new AccessDeniedException("Access Denied: You must be logged in to view this complaint.");
            }
            
            String currentUsername = authentication.getName(); 
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
            boolean isStaffOrHod = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().contains("TECHNICIAN") || a.getAuthority().contains("HEAD") || a.getAuthority().contains("HOD") || a.getAuthority().contains("STAFF"));
                    
            if (!isAdmin && !isStaffOrHod && !currentUsername.equals(complaint.getUserId())) {
                throw new AccessDeniedException("Access Denied: You do not have permission to view this private ticket.");
            }
        }
        
        return complaint;
    }

    public List<Complaint> getPublicFeed() {
        return complaintRepository.findByIsPublicTrue();
    }
    
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<Complaint> getMyComplaints(String userId) {
        return complaintRepository.findByUserId(userId);
    }
    
    public List<Complaint> getAssignedComplaints(String adminId) {
        return complaintRepository.findByAdminId(adminId);
    }
    
    public List<Complaint> getPendingApprovalComplaints() {
        return complaintRepository.findByStatus(Complaint.Status.VERIFICATION_PENDING);
    }
}