package co.com.evelyn.onboardingreactivo.api.util.error;

import co.com.evelyn.onboardingreactivo.api.util.error.handlers.*;
import co.com.evelyn.onboardingreactivo.model.exceptions.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplyErrorHandler {

    private final BusinessErrorHandler businessHandler;
    private final TechnicalErrorHandler technicalHandler;
    private final ProcessorErrorHandler processorHandler;
    private final GroupedErrorHandler groupedHandler;
    private final UnexpectedErrorHandler unexpectedHandler;

    private final Map<HttpStatus, Set<Class<? extends Throwable>>> exceptionGroups = new ConcurrentHashMap<>();

    @PostConstruct
    void initDefaultGroups() {
        register(HttpStatus.BAD_REQUEST, IllegalArgumentException.class, ServerWebInputException.class, DecodingException.class);
        register(HttpStatus.NOT_FOUND, NoSuchElementException.class);
    }

    @SafeVarargs
    public final void register(HttpStatus status, Class<? extends Throwable>... types) {
        exceptionGroups.computeIfAbsent(status, k -> ConcurrentHashMap.newKeySet())
                .addAll(Arrays.asList(types));
    }

    public Mono<ServerResponse> apply(Mono<ServerResponse> pipeline) {
        return pipeline
                .onErrorResume(BusinessException.class, businessHandler::handle)
                .onErrorResume(TechnicalException.class, technicalHandler::handle)
                .onErrorResume(ProcessorException.class, processorHandler::handle)
                .onErrorResume(isGrouped(), ex -> groupedHandler.handle(ex, resolve(ex)))
                .onErrorResume(unexpectedHandler::handle);
    }

    private Predicate<Throwable> isGrouped() {
        return ex -> exceptionGroups.values().stream()
                .anyMatch(set -> set.stream().anyMatch(type -> type.isAssignableFrom(ex.getClass())));
    }

    private HttpStatus resolve(Throwable ex) {
        return exceptionGroups.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(t -> t.isAssignableFrom(ex.getClass())))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(HttpStatus.BAD_REQUEST);
    }
}
