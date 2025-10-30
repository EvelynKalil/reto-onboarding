package co.com.evelyn.onboardingreactivo.dynamodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

    /**
     * Cliente DynamoDB para ambiente LOCAL (usa credenciales del perfil por defecto y endpoint localstack)
     */
    @Bean(name = "localDynamoClient")
    @Profile("local")
    public DynamoDbAsyncClient localDynamoDbAsyncClient(
            @Value("${aws.dynamodb.endpoint}") String endpoint,
            @Value("${aws.region}") String region,
            MetricPublisher publisher
    ) {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create("default"))
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .build();
    }

    /**
     * Cliente DynamoDB para entornos DEV, CER, y PDN (usa Web Identity Token)
     */
    @Bean(name = "remoteDynamoClient")
    @Profile({"dev", "cer", "pdn"})
    public DynamoDbAsyncClient remoteDynamoDbAsyncClient(
            MetricPublisher publisher,
            @Value("${aws.region}") String region
    ) {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .region(Region.of(region))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .build();
    }

    /**
     * Cliente DynamoDB por defecto — usado si no hay perfil activo.
     * Esto evita el error "No qualifying bean of type DynamoDbAsyncClient found".
     */
    @Bean(name = "defaultDynamoClient")
    public DynamoDbAsyncClient defaultDynamoDbAsyncClient(
            @Value("${aws.region:us-east-1}") String region
    ) {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(region))
                .build();
    }

    /**
     * Cliente mejorado DynamoDB Enhanced (usa el cliente que esté disponible)
     */
    @Bean
    public DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient(DynamoDbAsyncClient client) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(client)
                .build();
    }
}
