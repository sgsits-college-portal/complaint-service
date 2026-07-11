package com.collegeportal.complaint_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
@Data // Lombok automatically creates getters, setters, and constructors
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tracking the People involved (Virtual links to Team 1's Auth DB)
    @Column(nullable = false)
    private Long userId;
    
    private Long dispatcherId; // The central admin who routed it
    private Long adminId;      // Assigned Technician
    private Long hodId;        // HOD who verified the fix

    // Core Complaint Data
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    private String location;
    
    private int upvoteCount = 0;

    @Column(nullable = false)
    private boolean isPublic = false; // Default is private until Admin approves

    // The QA State Machine
    @Column(nullable = false)
    private String status = "OPEN"; // OPEN, IN_PROGRESS, VERIFICATION_PENDING, RESOLVED, REJECTED

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    @Column(columnDefinition = "TEXT")
    private String hodNote;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // One Complaint can have many Images
    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComplaintImage> images = new ArrayList<>();
}