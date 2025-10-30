package co.com.evelyn.onboardingreactivo.consumer;

import co.com.evelyn.onboardingreactivo.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                        .build()
                )
                .doOnNext(u -> System.out.println("Usuario obtenido de reqres.in: " + u))
                .onErrorResume(ex -> {
                    System.err.println("Error al obtener usuario " + id + ": " + ex.getMessage());
                    return Mono.empty(); // evita romper el flujo si hay error HTTP
                });
    }
}
