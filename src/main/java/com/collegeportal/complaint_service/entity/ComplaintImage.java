package com.collegeportal.complaint_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_images")
@Getter
@Setter
public class ComplaintImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String imageUrl;

    // NEW FIELD: Required to delete the file from the cloud later
    @Column(nullable = false)
    private String cloudId; 

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    @JsonIgnore // Prevents infinite JSON loops when sending data to the frontend
    private Complaint complaint;
}