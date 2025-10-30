package co.com.evelyn.onboardingreactivo.api.util.error.handlers;

import co.com.evelyn.onboardingreactivo.api.util.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class GroupedErrorHandler {
    public Mono<ServerResponse> handle(Throwable ex, HttpStatus status) {
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ErrorDTO.from(status, ex.getMessage(), ""));
    }
}
