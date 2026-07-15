package com.collegeportal.complaint_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
@Getter
@Setter
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tracking the People involved (Virtual links to Team 1's Auth DB)
    @Column(nullable = false)
    private String userId;
    
    private String dispatcherId; // The central admin who routed it
    private String adminId;      // Assigned Technician
    private String hodId;        // HOD who verified the fix

    // Core Complaint Data
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM; 

    private String location;
    
    private int upvoteCount = 0;

    @Column(nullable = false)
    private boolean isPublic = false; // Default is private until Admin approves

    // The QA State Machine
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN; 

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    @Column(columnDefinition = "TEXT")
    private String hodNote;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // One Complaint can have many Images
    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComplaintImage> images = new ArrayList<>();

    // --- ENUMS ---
    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum Status {
        OPEN, IN_PROGRESS, VERIFICATION_PENDING, RESOLVED, REJECTED
    }
}