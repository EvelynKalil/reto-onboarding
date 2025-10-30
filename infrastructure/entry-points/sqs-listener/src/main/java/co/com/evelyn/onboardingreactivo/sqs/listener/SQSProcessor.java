package co.com.evelyn.onboardingreactivo.sqs.listener;

import co.com.evelyn.onboardingreactivo.dynamodb.DynamoDBTemplateAdapter;
import co.com.evelyn.onboardingreactivo.model.enums.TechnicalMessage;
import co.com.evelyn.onboardingreactivo.model.exceptions.TechnicalException;
import co.com.evelyn.onboardingreactivo.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.io.IOException;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final DynamoDBTemplateAdapter dynamoAdapter;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() -> {
                    log.info("Mensaje recibido desde SQS: {}", message.body());
                    try {
                        return mapper.readValue(message.body(), User.class);
                    } catch (IOException e) {
                        throw new TechnicalException(e, TechnicalMessage.SQS_MESSAGE_PARSING_ERROR);
                    }
                })
                .map(this::transformToUppercase)
                .flatMap(user ->
                        dynamoAdapter.save(user)
                                .doOnSuccess(saved -> log.info("Usuario guardado en DynamoDB: {}", saved))
                                .onErrorMap(err -> new TechnicalException(err, TechnicalMessage.DYNAMODB_SAVE_ERROR))
                )
                .then();
    }

    private User transformToUppercase(User user) {
        user.setEmail(user.getEmail().toUpperCase());
        user.setFirstName(user.getFirstName().toUpperCase());
        user.setLastName(user.getLastName().toUpperCase());
        log.info("Usuario transformado a mayúsculas: {}", user);
        return user;
    }
}
