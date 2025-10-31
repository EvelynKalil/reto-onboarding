package co.com.evelyn.onboardingreactivo.r2dbc;

import co.com.evelyn.onboardingreactivo.consumer.ReqresAdapter;
import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.r2dbc.entity.UserEntity;
import co.com.evelyn.onboardingreactivo.redis.template.RedisRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UserRepositoryAdapterTest {

    private UserR2dbcRepository repository;
    private ReqresAdapter reqresAdapter;
    private RedisRepositoryAdapter redisRepositoryAdapter;
    private ObjectMapper mapper;
    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(UserR2dbcRepository.class);
        reqresAdapter = Mockito.mock(ReqresAdapter.class);
        redisRepositoryAdapter = Mockito.mock(RedisRepositoryAdapter.class);

        mapper = Mockito.mock(ObjectMapper.class);
        Mockito.lenient()
                .when(mapper.map(Mockito.any(UserEntity.class), Mockito.eq(User.class)))
                .thenAnswer(inv -> {
                    UserEntity e = inv.getArgument(0);
                    return new User(e.getId(), e.getEmail(), e.getFirstName(), e.getLastName(), e.getAvatar());
                });
        Mockito.lenient()
                .when(mapper.map(Mockito.any(User.class), Mockito.eq(UserEntity.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    return new UserEntity(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getAvatar());
                });

        adapter = new UserRepositoryAdapter(repository, reqresAdapter, mapper, redisRepositoryAdapter);
    }


    @Test
    void saveUser_shouldReturnSavedUserAndCacheIt() {
        // Arrange
        UserEntity entity = new UserEntity(1, "test@test.com", "Test", "User", "avatar.png");
        User model = new User(1, "test@test.com", "Test", "User", "avatar.png");

        Mockito.when(repository.existsById(1)).thenReturn(Mono.just(true));
        Mockito.when(repository.save(Mockito.any(UserEntity.class))).thenReturn(Mono.just(entity));
        Mockito.when(redisRepositoryAdapter.saveUserToCache(Mockito.any(User.class))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.save(model))
                .expectNextMatches(saved -> saved.getEmail().equals("test@test.com")
                        && saved.getFirstName().equals("Test"))
                .verifyComplete();

        Mockito.verify(repository).save(Mockito.any(UserEntity.class));
        Mockito.verify(redisRepositoryAdapter).saveUserToCache(Mockito.any(User.class));
    }
}
