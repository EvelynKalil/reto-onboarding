package co.com.evelyn.onboardingreactivo.usecase.user;

import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    // Crear usuario (traer de API si no existe)
    public Mono<User> createUserById(Integer id) {
        return userRepository.findById(id)
                .switchIfEmpty(
                        userRepository.fetchFromApi(id)
                                .flatMap(userRepository::save)
                );
    }

    // Obtener usuario por ID
    public Mono<User> getUserById(Integer id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

    // Listar
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Filtrar por nombre
    public Flux<User> getUsersByName(String name) {
        return userRepository.findByName(name);
    }
}
