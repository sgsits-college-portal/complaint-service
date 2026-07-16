package com.collegeportal.complaint_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.collegeportal.complaint_service.dto.ComplaintRequestDTO;
import com.collegeportal.complaint_service.entity.Complaint;
import com.collegeportal.complaint_service.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    // 1. STUDENT: Submit a new complaint
    @PostMapping(value = {"", "/"}, consumes = {"multipart/form-data"}) // THE FIX: Makes the endpoint slash-agnostic
    @PreAuthorize("hasAuthority('ROLE_STUDENT')") 
    public ResponseEntity<Complaint> createComplaint(
            @RequestPart("data") String data, 
            @RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        ComplaintRequestDTO request = mapper.readValue(data, ComplaintRequestDTO.class);

        Complaint newComplaint = complaintService.createComplaint(request, files);
        return new ResponseEntity<>(newComplaint, HttpStatus.CREATED);
    }

    // 2. ADMIN: Assign to a technician
    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Complaint> assignTechnician(
            @PathVariable Long id,
            @RequestParam String adminId,
            @RequestParam String dispatcherId) {
            
        return ResponseEntity.ok(complaintService.assignTechnician(id, adminId, dispatcherId));
    }

    // 3. TECHNICIAN: Submit for HOD Verification
    @PutMapping("/{id}/submit-verification")
    @PreAuthorize("hasAnyAuthority('SUB_TECHNICIAN', 'ROLE_ADMIN', 'SUB_SUPER_ADMIN', 'SUB_SYSTEM_ADMIN')")
    public ResponseEntity<Complaint> submitForVerification(
            @PathVariable Long id,
            @RequestParam String adminNote) {
            
        return ResponseEntity.ok(complaintService.submitForVerification(id, adminNote));
    }

    // 4. HOD: Final Sign-off and Resolution
    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAnyAuthority('ROLE_HOD', 'SUB_HEAD_OF_DEPT', 'SUB_HOD', 'ROLE_ADMIN', 'SUB_SUPER_ADMIN', 'SUB_SYSTEM_ADMIN')")
    public ResponseEntity<Complaint> resolveComplaint(
            @PathVariable Long id,
            @RequestParam String hodId,
            @RequestParam String hodNote) {
            
        return ResponseEntity.ok(complaintService.resolveComplaint(id, hodId, hodNote));
    }

    // 5. ADMIN: Toggle Public Visibility
    @PutMapping("/{id}/visibility")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Complaint> toggleVisibility(
            @PathVariable Long id,
            @RequestParam boolean isPublic) {
            
        return ResponseEntity.ok(complaintService.toggleVisibility(id, isPublic));
    }

    // --- FETCH ENDPOINTS ---

    @GetMapping("/assigned")
    @PreAuthorize("hasAnyAuthority('SUB_TECHNICIAN', 'ROLE_ADMIN', 'SUB_SUPER_ADMIN', 'SUB_SYSTEM_ADMIN')")
    public ResponseEntity<List<Complaint>> getAssignedComplaints(org.springframework.security.core.Authentication authentication) {
        String adminId = authentication.getName();
        return ResponseEntity.ok(complaintService.getAssignedComplaints(adminId));
    }

    @GetMapping("/pending-approval")
    @PreAuthorize("hasAnyAuthority('ROLE_HOD', 'SUB_HEAD_OF_DEPT', 'SUB_HOD', 'ROLE_ADMIN', 'SUB_SUPER_ADMIN', 'SUB_SYSTEM_ADMIN')")
    public ResponseEntity<List<Complaint>> getPendingApprovalComplaints() {
        return ResponseEntity.ok(complaintService.getPendingApprovalComplaints());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<Complaint> getComplaint(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    @GetMapping("/public")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Complaint>> getPublicFeed() {
        return ResponseEntity.ok(complaintService.getPublicFeed());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_FACULTY', 'STUDENT', 'FACULTY') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Complaint>> getMyComplaints(@PathVariable String userId) {
        return ResponseEntity.ok(complaintService.getMyComplaints(userId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") 
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }
}
