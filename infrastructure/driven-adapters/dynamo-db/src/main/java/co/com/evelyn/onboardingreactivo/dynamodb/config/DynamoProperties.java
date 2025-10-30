package co.com.evelyn.onboardingreactivo.dynamodb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Lee las propiedades de 'aws.region', 'aws.endpoint'
 * y la propiedad anidada 'aws.dynamodb.table'
 */
@ConfigurationProperties(prefix = "aws")
public record DynamoProperties(
        String region,
        String endpoint,
        DynamoDb dynamodb // Mapea la seccion 'aws.dynamodb'
) {
    /**
     * Mapea 'aws.dynamodb.table'
     */
    public record DynamoDb(String table) {}
}