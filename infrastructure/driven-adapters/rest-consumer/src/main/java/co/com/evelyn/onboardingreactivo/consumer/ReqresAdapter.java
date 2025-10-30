package co.com.evelyn.onboardingreactivo.consumer;

import co.com.evelyn.onboardingreactivo.model.enums.TechnicalMessage;
import co.com.evelyn.onboardingreactivo.model.exceptions.BusinessException;
import co.com.evelyn.onboardingreactivo.model.exceptions.TechnicalException;
import co.com.evelyn.onboardingreactivo.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReqresAdapter {

    private final WebClient.Builder webClientBuilder;

    @Value("${adapter.restconsumer.url}")
    private String baseUrl;

    @Value("${adapter.restconsumer.apikey}")
    private String apiKey;

    /** Llama a la API Reqres.in y traduce los errores a excepciones del dominio */
    public Mono<User> fetchUserById(Integer id) {
        WebClient client = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .build();

        return client.get()
                .uri("/users/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.statusCode().value() == 404
                                ? Mono.error(new BusinessException(TechnicalMessage.USER_NOT_FOUND))
                                : Mono.error(new TechnicalException(TechnicalMessage.INVALID_REQUEST))
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new TechnicalException(TechnicalMessage.INTERNAL_ERROR))
                )
                .bodyToMono(ReqresUserResponse.class)
                .map(ReqresUserResponse::getData)
                .map(data -> User.builder()
                        .id(data.getId())
                        .email(data.getEmail())
                        .firstName(data.getFirst_name())
                        .lastName(data.getLast_name())
                        .avatar(data.getAvatar())
                        .build()
                )
                .onErrorMap(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().value() == 404) {
                        return new BusinessException(TechnicalMessage.USER_NOT_FOUND);
                    }
                    return new TechnicalException(ex, TechnicalMessage.INTERNAL_ERROR);
                });
    }
}
