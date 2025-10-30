package co.com.evelyn.onboardingreactivo.api;

import co.com.evelyn.onboardingreactivo.api.util.error.ApplyErrorHandler;
import co.com.evelyn.onboardingreactivo.model.enums.TechnicalMessage;
import co.com.evelyn.onboardingreactivo.model.exceptions.BusinessException;
import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final ApplyErrorHandler applyErrorHandler;

    /** POST /users/{id} */
    public Mono<ServerResponse> createUser(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));

        Mono<ServerResponse> pipeline =
                userUseCase.createUserById(id)
                        .flatMap(user -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(user)));

        return applyErrorHandler.apply(pipeline);
    }

    /** GET /users/{id} */
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));

        Mono<ServerResponse> pipeline =
                userUseCase.getUserById(id)
                        .flatMap(user -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(user)));

        return applyErrorHandler.apply(pipeline);
    }

    /** GET /users */
    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        // reference for Sonar (avoid unused warning)
        Mono<ServerResponse> pipeline =
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(userUseCase.getAllUsers(), User.class);

        return applyErrorHandler.apply(pipeline);
    }

    /** GET /users?name= */
    public Mono<ServerResponse> getUsersByName(ServerRequest request) {

        Mono<ServerResponse> pipeline = Mono.justOrEmpty(request.queryParam("name"))
                .filter(name -> !name.trim().isEmpty())
                .flatMap(name ->
                        userUseCase.getUsersByName(name)
                                .collectList()
                                .flatMap(users -> {
                                    if (users.isEmpty()) {
                                        return Mono.error(new BusinessException(TechnicalMessage.NO_MATCHING_USERS_FOUND));
                                    }
                                    return ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(users);
                                })
                )
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_REQUEST)));

        return applyErrorHandler.apply(pipeline);
    }

}
