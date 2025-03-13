package com.example.image.controller;

import com.example.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String key = imageService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + key);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadImage(String fileName) {
        try {
            String downloadedPath = imageService.downloadFile(fileName);
            return ResponseEntity.ok("File uploaded successfully: " + downloadedPath);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(String fileName) {
        try {
            String key = imageService.deleteFile(fileName);
            return ResponseEntity.ok("File deleted successfully: " + key);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    @PutMapping("/resize")
    public ResponseEntity<String> resizeImage(@RequestParam("file") MultipartFile file) {

        boolean resizeImageAndStoreToS3 ;

        try {
            resizeImageAndStoreToS3 = imageService.resizeImageAndStoreToS3(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!resizeImageAndStoreToS3) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error resizing file");
        }

        return ResponseEntity.ok("File resized successfully");
    }


}
