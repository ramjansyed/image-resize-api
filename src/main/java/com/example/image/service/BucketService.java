package com.example.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BucketService {

    private final S3Client s3Client;

    public List<Bucket> listBuckets() {

        List<Bucket> bucketList = new ArrayList<>();

        try {
            bucketList = s3Client.listBuckets().buckets();

            bucketList.forEach(bucket -> {
                System.out.println("Bucket Name: " + bucket.name());
            });
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return bucketList;
    }

    public boolean createBucket(String name) {

        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder().bucket(name).build();

        try {
            s3Client.createBucket(createBucketRequest);
            return true;
        } catch (AwsServiceException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean deleteBucket(String name) {

        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(name).build();

        try {
            s3Client.deleteBucket(deleteBucketRequest);
            return true;
        } catch (AwsServiceException e) {
            throw new RuntimeException(e);
        }
    }
}
