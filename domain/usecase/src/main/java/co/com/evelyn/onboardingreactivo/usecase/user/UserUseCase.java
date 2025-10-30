package co.com.evelyn.onboardingreactivo.usecase.user;

import co.com.evelyn.onboardingreactivo.model.enums.TechnicalMessage;
import co.com.evelyn.onboardingreactivo.model.events.gateways.EventPublisher;
import co.com.evelyn.onboardingreactivo.model.exceptions.BusinessException;
import co.com.evelyn.onboardingreactivo.model.exceptions.ProcessorException;
import co.com.evelyn.onboardingreactivo.model.exceptions.TechnicalException;
import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    /** Crear usuario (traer de API si no existe) */
    public Mono<User> createUserById(Integer id) {
        return userRepository.findById(id)
                .switchIfEmpty(
                        userRepository.fetchFromApi(id)
                                .flatMap(userRepository::save)
                                .flatMap(savedUser ->
                                        eventPublisher.publishUserCreated(savedUser)
                                                .thenReturn(savedUser)
                                )
                )
                .onErrorResume(ex -> ex instanceof ProcessorException
                        ? Mono.error(ex)
                        : Mono.error(new TechnicalException(ex, TechnicalMessage.INTERNAL_ERROR))
                );
    }

    /** Obtener usuario por ID */
    public Mono<User> getUserById(Integer id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.USER_NOT_FOUND)))
                .onErrorResume(ex -> ex instanceof ProcessorException
                        ? Mono.error(ex)
                        : Mono.error(new TechnicalException(ex, TechnicalMessage.INTERNAL_ERROR))
                );
    }

    /** Listar todos los usuarios */
    public Flux<User> getAllUsers() {
        return userRepository.findAll()
                .onErrorResume(ex -> ex instanceof ProcessorException
                        ? Flux.error(ex)
                        : Flux.error(new TechnicalException(ex, TechnicalMessage.DATABASE_ERROR))
                );
    }

    /** Filtrar por nombre */
    public Flux<User> getUsersByName(String name) {
        return userRepository.findByName(name)
                .onErrorResume(ex -> ex instanceof ProcessorException
                        ? Flux.error(ex)
                        : Flux.error(new TechnicalException(ex, TechnicalMessage.DATABASE_ERROR))
                );
    }
}
