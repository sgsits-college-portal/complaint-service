package com.collegeportal.complaint_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.collegeportal.complaint_service.dto.ComplaintRequestDTO;
import com.collegeportal.complaint_service.entity.Complaint;
import com.collegeportal.complaint_service.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ComplaintController {

    private final ComplaintService complaintService;

    // 1. STUDENT: Submit a new complaint
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Complaint> createComplaint(
            @RequestPart("data") String data, 
            @RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException {
        
        // Convert the raw string back into your DTO
        ObjectMapper mapper = new ObjectMapper();
        ComplaintRequestDTO request = mapper.readValue(data, ComplaintRequestDTO.class);

        Complaint newComplaint = complaintService.createComplaint(request, files);
        return new ResponseEntity<>(newComplaint, HttpStatus.CREATED);
    }

    // 2. ADMIN: Assign to a technician
    @PutMapping("/{id}/assign")
    public ResponseEntity<Complaint> assignTechnician(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestParam Long dispatcherId) {
            
        return ResponseEntity.ok(complaintService.assignTechnician(id, adminId, dispatcherId));
    }

    // 3. TECHNICIAN: Submit for HOD Verification
    @PutMapping("/{id}/submit-verification")
    public ResponseEntity<Complaint> submitForVerification(
            @PathVariable Long id,
            @RequestParam String adminNote) {
            
        return ResponseEntity.ok(complaintService.submitForVerification(id, adminNote));
    }

    // 4. HOD: Final Sign-off and Resolution
    @PutMapping("/{id}/resolve")
    public ResponseEntity<Complaint> resolveComplaint(
            @PathVariable Long id,
            @RequestParam Long hodId,
            @RequestParam String hodNote) {
            
        return ResponseEntity.ok(complaintService.resolveComplaint(id, hodId, hodNote));
    }

    // 5. ADMIN: Toggle Public Visibility
    @PutMapping("/{id}/visibility")
    public ResponseEntity<Complaint> toggleVisibility(
            @PathVariable Long id,
            @RequestParam boolean isPublic) {
            
        return ResponseEntity.ok(complaintService.toggleVisibility(id, isPublic));
    }

    // --- FETCH ENDPOINTS ---

    // Get a specific complaint
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaint(@PathVariable Long id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    // Get global public feed
    @GetMapping("/public")
    public ResponseEntity<List<Complaint>> getPublicFeed() {
        return ResponseEntity.ok(complaintService.getPublicFeed());
    }

    // Get a specific user's complaints
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Complaint>> getMyComplaints(@PathVariable Long userId) {
        return ResponseEntity.ok(complaintService.getMyComplaints(userId));
    }
    // ADMIN: Get master list of all complaints
    @GetMapping("/all")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }
}