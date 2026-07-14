package com.collegeportal.complaint_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    // Constructor injection pulling from your application.properties
    public FileStorageService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true // Forces HTTPS for secure image delivery
        ));
    }

    // A clean Java Record to return both the URL and the ID simultaneously
    public record CloudUploadResult(String url, String cloudId) {}

    public CloudUploadResult uploadFile(MultipartFile file) throws IOException {
        // 1. Upload the raw bytes to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        
        // 2. Extract the secure viewing URL and the unique deletion ID
        String url = uploadResult.get("secure_url").toString();
        String cloudId = uploadResult.get("public_id").toString(); 
        
        return new CloudUploadResult(url, cloudId);
    }

    public void deleteFile(String cloudId) {
        try {
            // 3. Destroy the file on Cloudinary using the unique ID
            cloudinary.uploader().destroy(cloudId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            System.err.println("Failed to delete image from Cloudinary: " + cloudId);
        }
    }
}