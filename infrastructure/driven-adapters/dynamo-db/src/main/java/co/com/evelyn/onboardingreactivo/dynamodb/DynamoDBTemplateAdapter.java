package co.com.evelyn.onboardingreactivo.dynamodb;

import co.com.evelyn.onboardingreactivo.dynamodb.config.DynamoProperties; // ◀️ 1. IMPORTAR
import co.com.evelyn.onboardingreactivo.dynamodb.helper.TemplateAdapterOperations;
import co.com.evelyn.onboardingreactivo.model.user.User;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class DynamoDBTemplateAdapter
        extends TemplateAdapterOperations<User, String, ModelEntity> {

    public DynamoDBTemplateAdapter(
            DynamoDbEnhancedAsyncClient connectionFactory,
            ObjectMapper mapper,
            DynamoProperties properties
    ) {
        super(connectionFactory, mapper, d -> mapper.map(d, User.class), properties.dynamodb().table());
    }
}
