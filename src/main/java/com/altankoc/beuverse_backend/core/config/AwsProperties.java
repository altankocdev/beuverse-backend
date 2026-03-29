package com.altankoc.beuverse_backend.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
public record AwsProperties(
        S3Properties s3,
        String region
) {
    public record S3Properties(
            String bucket
    ) {}
}