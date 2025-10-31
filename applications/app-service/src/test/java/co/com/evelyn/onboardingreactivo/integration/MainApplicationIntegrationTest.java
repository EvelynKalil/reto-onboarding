package co.com.evelyn.onboardingreactivo.integration;

import co.com.evelyn.onboardingreactivo.api.Handler;
import co.com.evelyn.onboardingreactivo.api.RouterRest;
import co.com.evelyn.onboardingreactivo.api.util.error.ApplyErrorHandler;
import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import({RouterRest.class, Handler.class, MainApplicationIntegrationTest.MockConfig.class})
class MainApplicationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserUseCase userUseCase;

    @Autowired
    private ApplyErrorHandler applyErrorHandler;

    @Test
    void shouldReturnListOfUsers() {
        // simulamos que el manejador de errores no modifica el flujo
        Mockito.when(applyErrorHandler.apply(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        User user = new User(1, "test@test.com", "Evelyn", "Rendon", "avatar.jpg");
        Mockito.when(userUseCase.getAllUsers()).thenReturn(Flux.just(user));

        webTestClient.get()
                .uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .hasSize(1);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        Mockito.when(applyErrorHandler.apply(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));
        Mockito.when(userUseCase.getUserById(999)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/users/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        UserUseCase userUseCase() {
            return Mockito.mock(UserUseCase.class);
        }

        @Bean
        ApplyErrorHandler applyErrorHandler() {
            return Mockito.mock(ApplyErrorHandler.class);
        }
    }
}
