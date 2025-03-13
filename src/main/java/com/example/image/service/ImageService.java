package com.example.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        log.info("Uploading file into bucket" + bucketName);
        String key = "uploads/" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return key;
    }

    public String deleteFile(String fileName) throws IOException {

        log.info("Deleting file {} from bucket {}", fileName, bucketName);

        String key = "uploads/" + fileName;

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        return key;
    }

    public String downloadFile(String fileName) throws IOException {
        String key = "uploads/" + fileName;
        String downloadPath = "local-image.jpg";


        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.getObject(getObjectRequest, Path.of(downloadPath));

        return downloadPath;
    }

    public boolean resizeImageAndStoreToS3(MultipartFile file ) throws IOException {

        // Define desired aspect ratios
        int[][] aspectRatios = {
                {16, 9},  // 16:9
                {4, 3},   // 4:3
                {1, 1},   // 1:1
                {3, 2},   // 3:2
                {21, 9}   // 21:9
        };

        // Read the original image from MultipartFile
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        // Process each aspect ratio
        for (int[] ratio : aspectRatios) {
            try {
                int newWidth = 800;  // Set fixed width
                int newHeight = (newWidth * ratio[1]) / ratio[0];  // Maintain aspect ratio

                log.info("Resizing file to newWidth {} and {}", newWidth, newHeight);

                // Resize the image
                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                outputImage.getGraphics().drawImage(scaledImage, 0, 0, null);

                // Convert BufferedImage to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(outputImage, "jpg", baos);
                byte[] imageBytes = baos.toByteArray();

                // Upload the resized image to S3 with a unique filename
                String outputFilePath = "resized_images/" + ratio[0] + "x" + ratio[1] + "_" + file.getOriginalFilename();
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(outputFilePath)
                        .contentType("image/jpeg")
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

            } catch (Exception e) {
                System.err.println("Error processing image for ratio " + ratio[0] + ":" + ratio[1] + " - " + e.getMessage());
                return false;
            }
        }

        return true;  // Return true only after all images are uploaded successfully
    }

}