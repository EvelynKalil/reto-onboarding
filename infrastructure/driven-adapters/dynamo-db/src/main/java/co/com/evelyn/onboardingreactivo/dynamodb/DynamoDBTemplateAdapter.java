package co.com.evelyn.onboardingreactivo.dynamodb;

import co.com.evelyn.onboardingreactivo.dynamodb.helper.TemplateAdapterOperations;
import co.com.evelyn.onboardingreactivo.model.user.User;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class DynamoDBTemplateAdapter
        extends TemplateAdapterOperations<User, String, ModelEntity> {

    public DynamoDBTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        // El nombre de la tabla debe coincidir con la configurada en AWS o LocalStack
        super(connectionFactory, mapper, d -> mapper.map(d, User.class), "users");
    }

    public Mono<User> save(User user) {
        return super.save(user);
    }
}
