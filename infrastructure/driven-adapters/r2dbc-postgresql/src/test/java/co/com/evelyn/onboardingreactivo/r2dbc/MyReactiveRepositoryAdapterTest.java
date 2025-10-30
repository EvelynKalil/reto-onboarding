package co.com.evelyn.onboardingreactivo.r2dbc;

import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Disabled("Generated test — not applicable to current implementation")
@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @InjectMocks
    private UserRepositoryAdapter adapter;

    @Mock
    private UserR2dbcRepository repository;

    @Mock
    private ObjectMapper mapper;

    private final User user = User.builder()
            .id(1)
            .email("eve@example.com")
            .firstName("Evelyn")
            .lastName("Rendon")
            .avatar("https://example.com/avatar.png")
            .build();

    private final UserEntity data = UserEntity.builder()
            .id(1)
            .email("eve@example.com")
            .firstName("Evelyn")
            .lastName("Rendon")
            .avatar("https://example.com/avatar.png")
            .build();

    @Test
    void mustFindValueById() {
        when(repository.findById(1)).thenReturn(Mono.just(data));
        when(mapper.map(data, User.class)).thenReturn(user);

        Mono<User> result = adapter.findById(1);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getEmail().equals("eve@example.com"))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll()).thenReturn(Flux.just(data));
        when(mapper.map(data, User.class)).thenReturn(user);

        Flux<User> result = adapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getFirstName().equals("Evelyn"))
                .verifyComplete();
    }

    @Test
    void mustFindByName() {
        when(repository.findByName("Evelyn")).thenReturn(Flux.just(data));
        when(mapper.map(data, User.class)).thenReturn(user);

        Flux<User> result = adapter.findByName("Evelyn");

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getId() == 1)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(data));
        when(mapper.map(data, User.class)).thenReturn(user);
        when(mapper.map(any(User.class), any())).thenReturn(data);

        Mono<User> result = adapter.save(user);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getEmail().equals("eve@example.com"))
                .verifyComplete();
    }
}
