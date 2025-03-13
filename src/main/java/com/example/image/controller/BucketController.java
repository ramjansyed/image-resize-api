package com.example.image.controller;

import com.example.image.model.S3BucketDto;
import com.example.image.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bucket")
@RequiredArgsConstructor
public class BucketController {

    private final BucketService bucketService;

    @GetMapping("/lists")
    public ResponseEntity<List<S3BucketDto>> listBuckets() {
        List<S3BucketDto> bucketList = bucketService.listBuckets()
                .stream()
                .map(S3BucketDto::new)
                .collect(Collectors.toList());

        if (bucketList.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }

        return ResponseEntity.ok(bucketList); // HTTP 200 OK
    }

    @PostMapping("/create-bucket")
    public ResponseEntity<Boolean> createBucket(@RequestParam("bucketName") String bucketName) {
        boolean result = bucketService.createBucket(bucketName);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete-bucket")
    public ResponseEntity<Boolean> deleteBucket(@RequestParam("bucketName") String bucketName) {
        boolean result = bucketService.deleteBucket(bucketName);
        return ResponseEntity.ok(result);
    }
}
