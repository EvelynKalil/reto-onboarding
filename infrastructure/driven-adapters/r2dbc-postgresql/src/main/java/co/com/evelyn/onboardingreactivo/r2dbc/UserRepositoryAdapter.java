package co.com.evelyn.onboardingreactivo.r2dbc;

import co.com.evelyn.onboardingreactivo.consumer.ReqresAdapter;
import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.model.user.gateways.UserRepository;
import co.com.evelyn.onboardingreactivo.r2dbc.entity.UserEntity;
import co.com.evelyn.onboardingreactivo.r2dbc.helper.ReactiveAdapterOperations;
import co.com.evelyn.onboardingreactivo.redis.template.RedisRepositoryAdapter;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity, Integer, UserR2dbcRepository>
        implements UserRepository {

    private final UserR2dbcRepository repository;
    private final ReqresAdapter reqresAdapter;
    private final RedisRepositoryAdapter redisRepositoryAdapter;

    public UserRepositoryAdapter(UserR2dbcRepository repository,
                                 ReqresAdapter reqresAdapter,
                                 ObjectMapper mapper,
                                 RedisRepositoryAdapter redisRepositoryAdapter) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.repository = repository;
        this.reqresAdapter = reqresAdapter;
        this.redisRepositoryAdapter = redisRepositoryAdapter;
    }

    @Override
    public Mono<User> findById(Integer id) {
        return redisRepositoryAdapter.getUserFromCache(id)
                .switchIfEmpty(
                        repository.findById(id)
                                .map(this::toEntity)
                                .flatMap(user ->
                                        redisRepositoryAdapter.saveUserToCache(user)
                                                .thenReturn(user)
                                )
                );
    }

    @Override
    public Flux<User> findAll() {
        return repository.findAll().map(this::toEntity);
    }

    @Override
    public Flux<User> findByName(String name) {
        return repository.findByName(name).map(this::toEntity);
    }

    @Override
    public Mono<User> save(User user) {
        return repository.existsById(user.getId())
                .flatMap(exists -> exists
                                ? repository.save(toData(user)) // update
                                : repository.insert(
                                user.getId(),
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getAvatar()
                        )
                )
                .map(this::toEntity)
                .flatMap(savedUser ->
                        redisRepositoryAdapter.saveUserToCache(savedUser)
                                .thenReturn(savedUser)
                );
    }


    @Override
    public Mono<User> fetchFromApi(Integer id) {
        return reqresAdapter.fetchUserById(id)
                .doOnNext(u -> System.out.println("Usuario obtenido de reqres.in: " + u));
    }
}
