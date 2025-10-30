package co.com.evelyn.onboardingreactivo.sqs.sender;

import co.com.evelyn.onboardingreactivo.model.events.gateways.EventPublisher;
import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.sqs.sender.config.SQSSenderProperties;
import lombok.extern.slf4j.Slf4j;
// Asegurate de que no haya import de @Qualifier
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Service
public class SQSSender implements EventPublisher {

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;

    public SQSSender(
            SQSSenderProperties properties,
            SqsAsyncClient client // CAMBIO: Sin @Qualifier
    ) {
        this.properties = properties;
        this.client = client; // Esto inyectara el bean @Primary
    }

    @Override
    public Mono<Void> publishUserCreated(User user) {
        String message = String.format(
                "{\"id\":%d,\"email\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\",\"avatar\":\"%s\"}",
                user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getAvatar()
        );

        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Mensaje enviado a SQS con ID: {}", response.messageId()))
                .doOnError(error -> log.error("Error al enviar mensaje SQS", error))
                .then();
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }
}