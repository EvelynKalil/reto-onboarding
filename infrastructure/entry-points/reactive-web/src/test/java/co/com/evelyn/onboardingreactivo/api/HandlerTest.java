package co.com.evelyn.onboardingreactivo.api;

import co.com.evelyn.onboardingreactivo.api.util.error.ApplyErrorHandler;
import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

class HandlerTest {

    private WebTestClient webTestClient;
    private UserUseCase userUseCase;
    private ApplyErrorHandler applyErrorHandler;
    private Handler handler;

    @BeforeEach
    void setup() {
        userUseCase = Mockito.mock(UserUseCase.class);
        applyErrorHandler = Mockito.mock(ApplyErrorHandler.class);
        handler = new Handler(userUseCase, applyErrorHandler);

        // Simula que el error handler no modifica el flujo
        Mockito.lenient().when(applyErrorHandler.apply(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        // Crea el router manualmente igual que en RouterRest
        RouterFunction<ServerResponse> routerFunction = RouterFunctions.route()
                .POST("/users/{id}", handler::createUser)
                .GET("/users/{id}", handler::getUserById)
                .GET("/users", req -> {
                    if (req.queryParam("name").isPresent()) {
                        return handler.getUsersByName(req);
                    }
                    return handler.getAllUsers(req);
                })
                .build();

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void createUser_shouldReturnCreatedUser() {
        // Arrange
        User mockUser = new User(1, "test@test.com", "Test", "User", "avatar.png");
        Mockito.when(userUseCase.createUserById(1)).thenReturn(Mono.just(mockUser));

        // Act & Assert
        webTestClient.post()
                .uri("/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo("test@test.com")
                .jsonPath("$.firstName").isEqualTo("Test");
    }
}
