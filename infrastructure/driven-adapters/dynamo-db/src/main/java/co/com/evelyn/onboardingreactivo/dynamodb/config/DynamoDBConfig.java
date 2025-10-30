package co.com.evelyn.onboardingreactivo.dynamodb.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(DynamoProperties.class) // Habilita la clase del Paso 1
public class DynamoDBConfig {

    /**
     * Crea el cliente @Primary de DynamoDB que apunta a LocalStack
     */
    @Bean
    @Primary
    public DynamoDbAsyncClient dynamoDbAsyncClient(DynamoProperties properties) {

        String regionValue = !"us-east-1".equals(properties.region())
                ? properties.region()
                : "us-east-1";

        return DynamoDbAsyncClient.builder()
                .region(Region.of(regionValue))

                // 1. Apunta al endpoint de LocalStack (leido de aws.endpoint)
                .endpointOverride(URI.create(properties.endpoint()))

                // 2. Usa las mismas credenciales "dummy" que SQS
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                )
                .build();
    }

    /**
     * Cliente mejorado DynamoDB Enhanced (usa el cliente @Primary de arriba)
     */
    @Bean
    public DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient(DynamoDbAsyncClient client) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(client)
                .build();
    }
}