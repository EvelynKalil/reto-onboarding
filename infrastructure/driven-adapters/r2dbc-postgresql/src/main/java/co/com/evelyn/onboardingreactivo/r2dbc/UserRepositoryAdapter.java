package co.com.evelyn.onboardingreactivo.r2dbc;

import co.com.evelyn.onboardingreactivo.consumer.ReqresAdapter;
import co.com.evelyn.onboardingreactivo.model.enums.TechnicalMessage;
import co.com.evelyn.onboardingreactivo.model.exceptions.BusinessException;
import co.com.evelyn.onboardingreactivo.model.exceptions.ProcessorException;
import co.com.evelyn.onboardingreactivo.model.exceptions.TechnicalException;
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

    private final UserR2dbcRepository userRepository;
    private final ReqresAdapter reqresAdapter;
    private final RedisRepositoryAdapter redisRepositoryAdapter;

    public UserRepositoryAdapter(UserR2dbcRepository userRepository,
                                 ReqresAdapter reqresAdapter,
                                 ObjectMapper mapper,
                                 RedisRepositoryAdapter redisRepositoryAdapter) {
        super(userRepository, mapper, d -> mapper.map(d, User.class));
        this.userRepository = userRepository;
        this.reqresAdapter = reqresAdapter;
        this.redisRepositoryAdapter = redisRepositoryAdapter;
    }

    /** Buscar usuario por ID (con cache y manejo de errores) */
    @Override
    public Mono<User> findById(Integer id) {
        return redisRepositoryAdapter.getUserFromCache(id)
                .switchIfEmpty(
                        userRepository.findById(id)
                                .map(this::toEntity)
                                .flatMap(user ->
                                        redisRepositoryAdapter.saveUserToCache(user)
                                                .thenReturn(user)
                                )
                )
                .onErrorResume(ex -> ex instanceof ProcessorException
                        ? Mono.error(ex)
                        : Mono.error(new TechnicalException(ex, TechnicalMessage.DATABASE_ERROR))
                );
    }

    /** Listar todos los usuarios */
    @Override
    public Flux<User> findAll() {
        return repository.findAll()
                .map(this::toEntity)
                .onErrorResume(ex -> ex instanceof ProcessorException
                        ? Flux.error(ex)
                        : Flux.error(new TechnicalException(ex, TechnicalMessage.DATABASE_ERROR))
                );
    }

    /** Filtrar usuarios por nombre */
    @Override
    public Flux<User> findByName(String name) {
        return repository.findByName(name)
                .map(this::toEntity)
                .onErrorResume(ex -> ex instanceof ProcessorException
                        ? Flux.error(ex)
                        : Flux.error(new TechnicalException(ex, TechnicalMessage.DATABASE_ERROR))
                );
    }

    /** Guardar usuario (insert/update + cache) */
    @Override
    public Mono<User> save(User user) {
        return repository.existsById(user.getId())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                                ? repository.save(toData(user))
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
                )
                .onErrorResume(ex -> ex instanceof ProcessorException
                        ? Mono.error(ex)
                        : Mono.error(new TechnicalException(ex, TechnicalMessage.DATABASE_ERROR))
                );
    }

    /** Consultar usuario en la API externa Reqres */
    @Override
    public Mono<User> fetchFromApi(Integer id) {
        return reqresAdapter.fetchUserById(id)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.USER_NOT_FOUND)))
                .onErrorResume(ex -> ex instanceof ProcessorException
                        ? Mono.error(ex)
                        : Mono.error(new TechnicalException(ex, TechnicalMessage.INTERNAL_ERROR))
                );
    }
}
