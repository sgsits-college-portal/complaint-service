package com.collegeportal.complaint_service.dto;

import com.collegeportal.complaint_service.domain.ComplaintCategory;
import com.collegeportal.complaint_service.domain.ComplaintPriority;
import com.collegeportal.complaint_service.domain.ComplaintStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ComplaintDtos {

    public record RaiseComplaintRequest(
            @NotNull Long userId,
            @NotBlank String title,
            @NotBlank String description,
            @NotNull ComplaintCategory category,
            @NotNull ComplaintPriority priority,
            String attachmentUrl,
            String raisedByRole
    ) {}

    public record UpdateComplaintRequest(
            @NotNull ComplaintStatus status,
            String resolution
    ) {}

    public record AssignComplaintRequest(
            @NotNull Long assignedTo
    ) {}

    public record ResolveComplaintRequest(
            @NotBlank String resolution
    ) {}

    public record ReopenComplaintRequest(
            String reason
    ) {}
}
