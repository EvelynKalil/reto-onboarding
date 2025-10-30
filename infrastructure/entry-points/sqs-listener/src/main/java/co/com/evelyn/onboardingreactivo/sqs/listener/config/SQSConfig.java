package co.com.evelyn.onboardingreactivo.sqs.listener.config;

import co.com.evelyn.onboardingreactivo.sqs.listener.helper.SQSListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.net.URI;
import java.util.function.Function;

@EnableConfigurationProperties(SQSProperties.class)
@Configuration
public class SQSConfig {

    @Value("${aws.credentials.access-key}")
    private String accessKey;

    @Value("${aws.credentials.secret-key}")
    private String secretKey;

    @Bean
    public SQSListener sqsListener(
            SqsAsyncClient client,
            SQSProperties properties,
            Function<Message, Mono<Void>> fn
    ) {
        return SQSListener.builder()
                .client(client)
                .properties(properties)
                .processor(fn)
                .build()
                .start();
    }

    @Bean
    @Primary
    public SqsAsyncClient sqsAsyncClient(SQSProperties properties, MetricPublisher publisher) {
        String regionValue = properties.region() != null ? properties.region() : "us-east-1";

        return SqsAsyncClient.builder()
                .endpointOverride(resolveEndpoint(properties))
                .region(Region.of(regionValue))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .build();
    }

    protected URI resolveEndpoint(SQSProperties properties) {
        return properties.endpoint() != null ? URI.create(properties.endpoint()) : null;
    }
}
