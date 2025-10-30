package co.com.evelyn.onboardingreactivo.api.util.error.handlers;

import co.com.evelyn.onboardingreactivo.api.util.ErrorDTO;
import co.com.evelyn.onboardingreactivo.model.exceptions.ProcessorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ProcessorErrorHandler {
    public Mono<ServerResponse> handle(ProcessorException ex) {
        var tm = ex.getTechnicalMessage();
        var status = HttpStatus.valueOf(Integer.parseInt(tm.getCode()));
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ErrorDTO.from(status, tm.getMessage(), tm.getParam()));
    }
}
