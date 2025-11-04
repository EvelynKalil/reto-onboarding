package co.com.evelyn.onboardingreactivo.api;

import co.com.evelyn.onboardingreactivo.api.util.error.ApplyErrorHandler;
import co.com.evelyn.onboardingreactivo.model.enums.TechnicalMessage;
import co.com.evelyn.onboardingreactivo.model.exceptions.BusinessException;
import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import static net.logstash.logback.argument.StructuredArguments.kv;

import java.util.UUID;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;
    private final ApplyErrorHandler applyErrorHandler;

    /** POST /users/{id} - Crear usuario (consultar API externa si no existe) */
    public Mono<ServerResponse> createUser(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));

        // Genera un ID único para trazabilidad del request
        String messageId = UUID.randomUUID().toString();
        MDC.put("messageId", messageId);
        log.info("[messageId={}] Iniciando creacion de usuario con ID: {}", messageId, id);

        Mono<ServerResponse> pipeline = userUseCase.createUserById(id)
                .flatMap(user -> {
                    log.info("[messageId={}] Usuario creado exitosamente", kv("email", user.getEmail()));
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(fromValue(user));
                })
                .doFinally(signal -> {
                    log.info("[messageId={}] Finalizando flujo createUser con estado: {}", messageId, signal);
                    MDC.clear();
                });

        return applyErrorHandler.apply(pipeline);
    }

    /** GET /users/{id} - Obtener usuario por ID */
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));
        String messageId = UUID.randomUUID().toString();
        MDC.put("messageId", messageId);

        log.info("[messageId={}] Consultando usuario con ID: {}", messageId, id);

        Mono<ServerResponse> pipeline = userUseCase.getUserById(id)
                .flatMap(user -> {
                    log.info("[messageId={}] Usuario encontrado: {}", messageId, user.getEmail());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(fromValue(user));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("[messageId={}] Usuario con ID {} no encontrado", messageId, id);
                    return ServerResponse.notFound().build();
                }))
                .doFinally(signal -> MDC.clear());

        return applyErrorHandler.apply(pipeline);
    }

    /** GET /users - Obtener todos los usuarios */
    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        String messageId = UUID.randomUUID().toString();
        MDC.put("messageId", messageId);
        log.info("[messageId={}] Consultando todos los usuarios", messageId);

        Mono<ServerResponse> pipeline = ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userUseCase.getAllUsers(), User.class)
                .doFinally(signal -> {
                    log.info("[messageId={}] Finalizando flujo getAllUsers", messageId);
                    MDC.clear();
                });

        return applyErrorHandler.apply(pipeline);
    }

    /** GET /users?name= - Buscar usuarios por nombre */
    public Mono<ServerResponse> getUsersByName(ServerRequest request) {
        String messageId = UUID.randomUUID().toString();
        MDC.put("messageId", messageId);

        log.info("[messageId={}] Buscando usuarios por nombre", messageId);

        Mono<ServerResponse> pipeline = Mono.justOrEmpty(request.queryParam("name"))
                .filter(name -> !name.trim().isEmpty())
                .flatMap(name -> userUseCase.getUsersByName(name)
                        .collectList()
                        .flatMap(users -> {
                            if (users.isEmpty()) {
                                log.warn("[messageId={}] No se encontraron usuarios con el nombre: {}", messageId, name);
                                return Mono.error(new BusinessException(TechnicalMessage.NO_MATCHING_USERS_FOUND));
                            }
                            log.info("[messageId={}] Se encontraron {} usuarios con el nombre: {}", messageId, users.size(), name);
                            return ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(users);
                        })
                )
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_REQUEST)))
                .doFinally(signal -> {
                    log.info("[messageId={}] Finalizando flujo getUsersByName", messageId);
                    MDC.clear();
                });

        return applyErrorHandler.apply(pipeline);
    }
}
