package com.collegeportal.complaint_service.dto;

import lombok.Data;

@Data
public class ComplaintRequestDTO {
    private String userId;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String location;
}