package co.com.evelyn.onboardingreactivo.sqs.listener.config;

import co.com.evelyn.onboardingreactivo.sqs.listener.helper.SQSListener;
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

    @Bean
    public SQSListener sqsListener(
            SqsAsyncClient client, // Inyecta el cliente @Primary
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
    @Primary // 1. Es el bean primario
    public SqsAsyncClient sqsAsyncClient(SQSProperties properties, MetricPublisher publisher) {

        String regionValue = properties.region() != null ? properties.region() : "us-east-1";

        return SqsAsyncClient.builder()

                // 2. Apunta a LocalStack (leido de entrypoint.sqs.endpoint)
                .endpointOverride(resolveEndpoint(properties))

                .region(Region.of(regionValue))

                // 3. Usa credenciales "dummy"
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                )

                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .build();
    }

    protected URI resolveEndpoint(SQSProperties properties) {
        if (properties.endpoint() != null) {
            return URI.create(properties.endpoint());
        }
        return null;
    }
}