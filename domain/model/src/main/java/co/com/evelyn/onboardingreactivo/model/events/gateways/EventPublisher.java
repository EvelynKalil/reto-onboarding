package co.com.evelyn.onboardingreactivo.model.events.gateways;

import co.com.evelyn.onboardingreactivo.model.user.User;
import reactor.core.publisher.Mono;

public interface EventPublisher {
    Mono<Void> publishUserCreated(User user);
}
