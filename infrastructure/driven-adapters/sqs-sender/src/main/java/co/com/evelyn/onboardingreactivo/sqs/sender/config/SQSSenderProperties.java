package co.com.evelyn.onboardingreactivo.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.sqs")
public record SQSSenderProperties(
        String region,
        String queueUrl,
        String endpoint
) { }