package co.com.evelyn.onboardingreactivo.api.util.error.handlers;

import co.com.evelyn.onboardingreactivo.api.util.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UnexpectedErrorHandler {
    public Mono<ServerResponse> handle(Throwable ex) {
        log.error("Unexpected error: ", ex);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ErrorDTO.from(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ""));
    }
}
