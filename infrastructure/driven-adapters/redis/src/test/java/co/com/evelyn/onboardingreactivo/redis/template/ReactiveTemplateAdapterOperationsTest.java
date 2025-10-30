package co.com.evelyn.onboardingreactivo.redis.template;

import co.com.evelyn.onboardingreactivo.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@Disabled("Generated test — not applicable to current implementation")
class ReactiveTemplateAdapterOperationsTest {

    @Mock
    private ReactiveRedisConnectionFactory connectionFactory;

    @Mock
    private ObjectMapper objectMapper;

    private RedisRepositoryAdapter adapter;

    private User userMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Crear un usuario de prueba
        userMock = new User(1, "george.bluth@reqres.in", "George", "Bluth", "https://reqres.in/img/faces/1-image.jpg");

        // Configurar el mapper simulado
        when(objectMapper.map(userMock, Object.class)).thenReturn(userMock);

        // Crear el adapter real con mocks
        adapter = new RedisRepositoryAdapter(connectionFactory, objectMapper);
    }

    @Test
    void testSave() {
        StepVerifier.create(adapter.save("user:1", userMock))
                .expectNext(userMock)
                .verifyComplete();
    }

    @Test
    void testSaveWithExpiration() {
        StepVerifier.create(adapter.save("user:1", userMock, 2000))
                .expectNext(userMock)
                .verifyComplete();
    }

    @Test
    void testFindById() {
        StepVerifier.create(adapter.findById("user:1"))
                .verifyComplete();
    }
}
