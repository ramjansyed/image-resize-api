package com.example.image.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.springframework.http.MediaType;

import java.util.Arrays;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PACKAGE)
public enum ImageEnum {
    JPG("jpg", MediaType.IMAGE_JPEG),
    JPEG("jpeg", MediaType.IMAGE_JPEG),
    TXT("txt", MediaType.TEXT_PLAIN),
    PNG("png", MediaType.IMAGE_PNG),
    PDF("pdf", MediaType.APPLICATION_PDF);

    private final String extension;

    // Media type associated with the file extension
    private final MediaType mediaType;

    // Method to get MediaType based on the filename's extension
    public static MediaType fromFilename(String fileName) {
        val dotIndex = fileName.lastIndexOf('.');
        val fileExtension = (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);

        // Finding matching enum constant for the file extension
        // Default to octet-stream if no matching media type found
        return Arrays.stream(values())
                .filter(e -> e.getExtension().equals(fileExtension))
                .findFirst()
                .map(ImageEnum::getMediaType)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
    }
}
