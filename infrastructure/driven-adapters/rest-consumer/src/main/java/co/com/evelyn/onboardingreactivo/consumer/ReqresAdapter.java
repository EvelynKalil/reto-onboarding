package co.com.evelyn.onboardingreactivo.consumer;

import co.com.evelyn.onboardingreactivo.model.enums.TechnicalMessage;
import co.com.evelyn.onboardingreactivo.model.exceptions.BusinessException;
import co.com.evelyn.onboardingreactivo.model.exceptions.TechnicalException;
import co.com.evelyn.onboardingreactivo.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReqresAdapter {

    private final WebClient.Builder webClientBuilder;

    @Value("${adapter.restconsumer.url}")
    private String baseUrl;

    @Value("${adapter.restconsumer.apikey}")
    private String apiKey;

    public Mono<User> fetchUserById(Integer id) {
        WebClient client = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();

        return client.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(ReqresUserResponse.class)
                .map(ReqresUserResponse::getData)
                .map(data -> User.builder()
                        .id(data.getId())
                        .email(data.getEmail())
                        .firstName(data.getFirst_name())
                        .lastName(data.getLast_name())
                        .avatar(data.getAvatar())
                        .build())
                .doOnNext(u -> log.info("✅ Usuario obtenido de Reqres.in: {}", u))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    int status = ex.getStatusCode().value();
                    if (status == HttpStatus.NOT_FOUND.value()) {
                        log.warn("⚠️ Usuario {} no encontrado en Reqres.in", id);
                        return Mono.error(new BusinessException(TechnicalMessage.USER_NOT_FOUND));
                    }
                    log.error("❌ Error HTTP {} desde Reqres.in", status, ex);
                    return Mono.error(new TechnicalException(ex, TechnicalMessage.INTERNAL_ERROR));
                })
                .onErrorResume(ex -> {
                    log.error("🌐 Error inesperado al consumir Reqres.in", ex);
                    return Mono.error(new TechnicalException(ex, TechnicalMessage.INTERNAL_ERROR));
                });
    }
}
