package co.com.evelyn.onboardingreactivo.api;

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

    // 🔹 Crear usuario (por ID desde reqres.in)
    public Mono<ServerResponse> createUser(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));
        return userUseCase.createUserById(id)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(user)));
    }

    // 🔹 Obtener usuario por ID
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));
        return userUseCase.getUserById(id)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(user)))
                .onErrorResume(e -> ServerResponse.notFound().build());
    }

    // 🔹 Listar todos los usuarios
    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userUseCase.getAllUsers(), User.class);
    }

    // 🔹 Filtrar usuarios por nombre
    public Mono<ServerResponse> getUsersByName(ServerRequest request) {
        String name = request.queryParam("name").orElse("");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userUseCase.getUsersByName(name), User.class);
    }
}
