package com.renthouse.controller;

import com.renthouse.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/upload")
@Tag(name = "File Upload", description = "File upload APIs")
public class FileUploadController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:5242880}")
    private long maxFileSize; // 5MB default

    @PostMapping("/image")
    @Operation(summary = "Upload image", description = "Upload an image file and return the URL")
    public ResponseEntity<ApiResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("File is empty"));
            }

            // Check file size
            if (file.getSize() > maxFileSize) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("File size exceeds maximum limit of " + (maxFileSize / 1024 / 1024) + "MB"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Only image files are allowed"));
            }

            // Create upload directory if it doesn't exist
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Return the file URL
            String fileUrl = "/api/upload/files/" + filename;
            
            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        }
    }

    @GetMapping("/files/{filename:.+}")
    @Operation(summary = "Get uploaded file", description = "Retrieve an uploaded file by filename")
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = determineContentType(filename);
            
            return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String determineContentType(String filename) {
        String extension = "";
        if (filename.contains(".")) {
            extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        }
        
        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
} 