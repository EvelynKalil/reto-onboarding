package co.com.evelyn.onboardingreactivo.sqs.sender.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import java.net.URI;

@Configuration
@EnableConfigurationProperties(SQSSenderProperties.class)
@ConditionalOnMissingBean(SqsAsyncClient.class)
public class SQSSenderConfig {

    @Bean(name = "configSqs")
    public SqsAsyncClient configSqs(SQSSenderProperties properties, MetricPublisher publisher) {
        String regionValue = properties.region() != null ? properties.region() : "us-east-1"; // fallback seguro

        return SqsAsyncClient.builder()
                .endpointOverride(resolveEndpoint(properties))
                .region(Region.of(regionValue))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .credentialsProvider(getProviderChain())
                .build();
    }

    private AwsCredentialsProviderChain getProviderChain() {
        return AwsCredentialsProviderChain.builder()
                .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
                .addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .addCredentialsProvider(ProfileCredentialsProvider.create())
                .addCredentialsProvider(ContainerCredentialsProvider.builder().build())
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
    }

    private URI resolveEndpoint(SQSSenderProperties properties) {
        return properties.endpoint() != null ? URI.create(properties.endpoint()) : null;
    }
}
