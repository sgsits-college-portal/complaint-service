package com.collegeportal.complaint_service.service;

import com.collegeportal.complaint_service.domain.Complaint;
import com.collegeportal.complaint_service.domain.ComplaintCategory;
import com.collegeportal.complaint_service.domain.ComplaintPriority;
import com.collegeportal.complaint_service.domain.ComplaintStatus;
import com.collegeportal.complaint_service.dto.ComplaintDtos.AssignComplaintRequest;
import com.collegeportal.complaint_service.dto.ComplaintDtos.RaiseComplaintRequest;
import com.collegeportal.complaint_service.dto.ComplaintDtos.ReopenComplaintRequest;
import com.collegeportal.complaint_service.dto.ComplaintDtos.ResolveComplaintRequest;
import com.collegeportal.complaint_service.dto.ComplaintDtos.UpdateComplaintRequest;
import com.collegeportal.complaint_service.repository.ComplaintRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public Complaint raise(RaiseComplaintRequest request) {
        Complaint complaint = new Complaint();
        complaint.setUserId(request.userId());
        complaint.setTitle(request.title());
        complaint.setDescription(request.description());
        complaint.setCategory(request.category());
        complaint.setPriority(request.priority());
        complaint.setAttachmentUrl(request.attachmentUrl());
        complaint.setRaisedByRole(request.raisedByRole() != null ? request.raisedByRole().toUpperCase() : "STUDENT");
        return complaintRepository.save(complaint);
    }

    public List<Complaint> all() {
        return complaintRepository.findAll();
    }

    public Complaint byId(Long id) {
        return findById(id);
    }

    public List<Complaint> byUser(Long userId) {
        return complaintRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Complaint> byCategory(ComplaintCategory category) {
        return complaintRepository.findByCategoryOrderByCreatedAtDesc(category);
    }

    public List<Complaint> byStatus(ComplaintStatus status) {
        return complaintRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public List<Complaint> byPriority(ComplaintPriority priority) {
        return complaintRepository.findByPriorityOrderByCreatedAtDesc(priority);
    }

    public List<Complaint> byAssignee(Long assignedTo) {
        return complaintRepository.findByAssignedToOrderByCreatedAtDesc(assignedTo);
    }

    public Complaint update(Long id, UpdateComplaintRequest request) {
        Complaint complaint = findById(id);
        complaint.setStatus(request.status());
        complaint.setResolution(request.resolution());
        if (request.status() == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(Instant.now());
        }
        return complaintRepository.save(complaint);
    }

    public Complaint assign(Long id, AssignComplaintRequest request) {
        Complaint complaint = findById(id);
        complaint.setAssignedTo(request.assignedTo());
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        return complaintRepository.save(complaint);
    }

    public Complaint progress(Long id) {
        Complaint complaint = findById(id);
        if (complaint.getAssignedTo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assign complaint before marking in progress");
        }
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        return complaintRepository.save(complaint);
    }

    public Complaint resolve(Long id, ResolveComplaintRequest request) {
        Complaint complaint = findById(id);
        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setResolution(request.resolution());
        complaint.setResolvedAt(Instant.now());
        return complaintRepository.save(complaint);
    }

    public Complaint close(Long id) {
        Complaint complaint = findById(id);
        if (complaint.getStatus() != ComplaintStatus.RESOLVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only resolved complaints can be closed");
        }
        complaint.setStatus(ComplaintStatus.CLOSED);
        return complaintRepository.save(complaint);
    }

    public Complaint reopen(Long id, ReopenComplaintRequest request) {
        Complaint complaint = findById(id);
        if (complaint.getStatus() != ComplaintStatus.RESOLVED && complaint.getStatus() != ComplaintStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only resolved or closed complaints can be reopened");
        }
        complaint.setStatus(ComplaintStatus.REOPENED);
        // Preserve the original resolution text; store the reopen reason separately
        complaint.setReopenReason(request.reason());
        complaint.setResolvedAt(null);
        return complaintRepository.save(complaint);
    }

    public void delete(Long id) {
        complaintRepository.deleteById(id);
    }

    private Complaint findById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
    }
}
