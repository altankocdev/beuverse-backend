package com.altankoc.beuverse_backend.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AwsProperties awsProperties;

    @Bean
    @ConditionalOnProperty(name = "aws.region")
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsProperties.region()))
                .build();
    }
}