package eu.aboutdev.localstack.awscloud.transfer;

public record BucketUpload(String bucketName, String objectKey, String content) {
}
