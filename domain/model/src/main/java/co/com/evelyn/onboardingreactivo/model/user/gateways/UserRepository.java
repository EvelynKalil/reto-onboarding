package co.com.evelyn.onboardingreactivo.model.user.gateways;

import co.com.evelyn.onboardingreactivo.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> findById(Integer id);
    Flux<User> findAll();
    Flux<User> findByName(String name);
    Mono<User> save(User user);
    Mono<User> fetchFromApi(Integer id);
}
