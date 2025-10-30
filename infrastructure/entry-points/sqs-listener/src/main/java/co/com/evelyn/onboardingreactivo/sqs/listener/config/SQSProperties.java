package co.com.evelyn.onboardingreactivo.sqs.listener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "entrypoint.sqs")
public record SQSProperties(
        String region,
        String endpoint,
        String queueUrl,
        Integer waitTimeSeconds,
        Integer maxNumberOfMessages,
        Integer visibilityTimeoutSeconds,
        Integer numberOfThreads
) {}
