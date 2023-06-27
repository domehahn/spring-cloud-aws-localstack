package eu.aboutdev.localstack.awscloud.controller;

import eu.aboutdev.localstack.awscloud.transfer.BucketUpload;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class AwsLocalstackS3Controller {

    private final S3Client s3Client;

    public AwsLocalstackS3Controller(final S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @GetMapping("/createBucket/{bucketName}")
    public String createBucket(@PathVariable String bucketName) {
        final CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();
        s3Client.createBucket(createBucketRequest);
        return String.format("Bucket %s created successfully.", bucketName);
    }

    @GetMapping("/uploadFile")
    public String uploadFile(@org.springframework.web.bind.annotation.RequestBody BucketUpload bucketUpload) {
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketUpload.bucketName())
                .key(bucketUpload.objectKey())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromString(bucketUpload.content()));
        return String.format("File with key %s uploaded successfully.", bucketUpload.objectKey());
    }

    @GetMapping("/getObjectContent/{bucketName}/{objectKey}")
    public String getObjectContent(@PathVariable String bucketName, @PathVariable String objectKey) {
        final GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        final ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

        final StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(objectBytes.asInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            // Handle the exception
        }

        return content.toString();
    }

    @DeleteMapping("/deleteBucket/{bucketName}")
    public String deleteBucket(@PathVariable String bucketName) {
        final DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();
        s3Client.deleteBucket(deleteBucketRequest);
        return String.format("Bucket %s deleted successfully.", bucketName);
    }

    @DeleteMapping("/deleteObject/{bucketName}/{objectKey}")
    public String deleteObject(@PathVariable String bucketName, @PathVariable String objectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        return String.format("Object with key %s deleted successfully.", objectKey);
    }
}
