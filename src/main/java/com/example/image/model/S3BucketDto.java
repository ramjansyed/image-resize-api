package com.example.image.model;

import lombok.Getter;
import software.amazon.awssdk.services.s3.model.Bucket;

@Getter
public class S3BucketDto {
    private String name;
    private String creationDate;

    public S3BucketDto(Bucket bucket) {
        this.name = bucket.name();
        this.creationDate = bucket.creationDate().toString();
    }
}
