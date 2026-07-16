package com.collegeportal.complaint_service.domain;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private Long id;
    private Long userId;
    private String title;
    @Column(length = 2000)
    private String description;
    @Enumerated(EnumType.STRING)
    private ComplaintCategory category;
    @Enumerated(EnumType.STRING)
    private ComplaintPriority priority = ComplaintPriority.MEDIUM;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String attachmentUrl;
    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.OPEN;
    private Long assignedTo;
    private String raisedByRole;   // role of the user who raised the complaint
    @Column(length = 2000)
    private String resolution;
    @Column(length = 1000)
    private String reopenReason;   // reason provided when a resolved/closed complaint is reopened
    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }
    public ComplaintPriority getPriority() { return priority; }
    public void setPriority(ComplaintPriority priority) { this.priority = priority; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }
    public Long getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }
    public String getRaisedByRole() { return raisedByRole; }
    public void setRaisedByRole(String raisedByRole) { this.raisedByRole = raisedByRole; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public String getReopenReason() { return reopenReason; }
    public void setReopenReason(String reopenReason) { this.reopenReason = reopenReason; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
