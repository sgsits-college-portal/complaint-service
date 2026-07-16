package com.collegeportal.complaint_service.controller;

import com.collegeportal.complaint_service.domain.Complaint;
import com.collegeportal.complaint_service.domain.ComplaintCategory;
import com.collegeportal.complaint_service.domain.ComplaintPriority;
import com.collegeportal.complaint_service.domain.ComplaintStatus;
import com.collegeportal.complaint_service.dto.ComplaintDtos.AssignComplaintRequest;
import com.collegeportal.complaint_service.dto.ComplaintDtos.RaiseComplaintRequest;
import com.collegeportal.complaint_service.dto.ComplaintDtos.ReopenComplaintRequest;
import com.collegeportal.complaint_service.dto.ComplaintDtos.ResolveComplaintRequest;
import com.collegeportal.complaint_service.dto.ComplaintDtos.UpdateComplaintRequest;
import com.collegeportal.complaint_service.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Slash-agnostic: accepts both /api/complaint and /api/complaint/
    // Students, teachers, and admins can all raise complaints
    @PostMapping(value = {"/raise", "/raise/"})
    @PreAuthorize("hasAuthority('ROLE_STUDENT') or hasAuthority('ROLE_FACULTY') or hasAuthority('ROLE_ADMIN')")
    public Complaint raise(@Valid @RequestBody RaiseComplaintRequest request) {
        return complaintService.raise(request);
    }

    // Admin / staff can list all complaints
    @GetMapping(value = {"", "/"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STAFF')")
    public List<Complaint> all() {
        return complaintService.all();
    }

    @GetMapping(value = {"/{id}", "/{id}/"})
    @PreAuthorize("isAuthenticated()")
    public Complaint byId(@PathVariable Long id) {
        return complaintService.byId(id);
    }

    // Any authenticated user can fetch their own complaints
    @GetMapping(value = {"/user/{userId}", "/user/{userId}/"})
    @PreAuthorize("isAuthenticated()")
    public List<Complaint> byUser(@PathVariable Long userId) {
        return complaintService.byUser(userId);
    }

    @GetMapping(value = {"/category/{category}", "/category/{category}/"})
    @PreAuthorize("isAuthenticated()")
    public List<Complaint> byCategory(@PathVariable ComplaintCategory category) {
        return complaintService.byCategory(category);
    }

    @GetMapping(value = {"/status/{status}", "/status/{status}/"})
    @PreAuthorize("isAuthenticated()")
    public List<Complaint> byStatus(@PathVariable ComplaintStatus status) {
        return complaintService.byStatus(status);
    }

    @GetMapping(value = {"/priority/{priority}", "/priority/{priority}/"})
    @PreAuthorize("isAuthenticated()")
    public List<Complaint> byPriority(@PathVariable ComplaintPriority priority) {
        return complaintService.byPriority(priority);
    }

    @GetMapping(value = {"/assigned/{assignedTo}", "/assigned/{assignedTo}/"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STAFF')")
    public List<Complaint> byAssignee(@PathVariable Long assignedTo) {
        return complaintService.byAssignee(assignedTo);
    }

    // Status updates are admin/staff only
    @PutMapping(value = {"/update/{id}", "/update/{id}/"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STAFF')")
    public Complaint update(@PathVariable Long id, @Valid @RequestBody UpdateComplaintRequest request) {
        return complaintService.update(id, request);
    }

    @PutMapping(value = {"/{id}/assign", "/{id}/assign/"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('SUB_HOD')")
    public Complaint assign(@PathVariable Long id, @Valid @RequestBody AssignComplaintRequest request) {
        return complaintService.assign(id, request);
    }

    @PutMapping(value = {"/{id}/progress", "/{id}/progress/"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STAFF')")
    public Complaint progress(@PathVariable Long id) {
        return complaintService.progress(id);
    }

    @PutMapping(value = {"/{id}/resolve", "/{id}/resolve/"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STAFF')")
    public Complaint resolve(@PathVariable Long id, @Valid @RequestBody ResolveComplaintRequest request) {
        return complaintService.resolve(id, request);
    }

    @PutMapping(value = {"/{id}/close", "/{id}/close/"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Complaint close(@PathVariable Long id) {
        return complaintService.close(id);
    }

    @PutMapping(value = {"/{id}/reopen", "/{id}/reopen/"})
    @PreAuthorize("hasAuthority('ROLE_STUDENT') or hasAuthority('ROLE_FACULTY') or hasAuthority('ROLE_ADMIN')")
    public Complaint reopen(@PathVariable Long id, @RequestBody ReopenComplaintRequest request) {
        return complaintService.reopen(id, request);
    }

    @DeleteMapping(value = {"/{id}", "/{id}/"})
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable Long id) {
        complaintService.delete(id);
    }
}
