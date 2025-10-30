package co.com.evelyn.onboardingreactivo.api.util.error.handlers;

import co.com.evelyn.onboardingreactivo.api.util.ErrorDTO;
import co.com.evelyn.onboardingreactivo.model.exceptions.TechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TechnicalErrorHandler {
    public Mono<ServerResponse> handle(TechnicalException ex) {
        var tm = ex.getTechnicalMessage();
        var status = HttpStatus.valueOf(Integer.parseInt(tm.getCode()));
        log.error("Technical error occurred", ex);
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ErrorDTO.from(status, tm.getMessage(), tm.getParam()));
    }
}
