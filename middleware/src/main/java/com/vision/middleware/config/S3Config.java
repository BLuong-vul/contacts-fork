package com.vision.middleware.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Amazon S3 client.
 */
@Configuration
public class S3Config {

    /**
     * AWS Access Key ID value fetched from application properties.
     */
    @Value("${aws.access.key.id}")
    private String accessKeyId;

    /**
     * AWS Secret Access Key fetched from application properties.
     */
    @Value("${aws.secret.access.key}")
    private String secretAccessKey;

    /**
     * AWS S3 region value fetched from application properties.
     */
    @Value("${aws.s3.region}")
    private String region;

    /**
     * Bean definition for Amazon S3 client.
     * This method constructs an Amazon S3 client using the provided AWS credentials and region.
     *
     * @return configured AmazonS3 client instance
     */
    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }
}
