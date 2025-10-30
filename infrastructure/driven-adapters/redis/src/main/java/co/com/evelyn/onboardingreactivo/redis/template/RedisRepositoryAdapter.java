package co.com.evelyn.onboardingreactivo.redis.template;

import co.com.evelyn.onboardingreactivo.model.user.User;
import co.com.evelyn.onboardingreactivo.redis.template.helper.ReactiveTemplateAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
public class RedisRepositoryAdapter
        extends ReactiveTemplateAdapterOperations<User, String, User> {

    private static final String CACHE_PREFIX = "user:";

    public RedisRepositoryAdapter(ReactiveRedisConnectionFactory connectionFactory, ObjectMapper mapper) {
        super(connectionFactory, mapper, d -> mapper.map(d, User.class));
    }

    public Mono<User> getUserFromCache(Integer id) {
        return findById(CACHE_PREFIX + id);
    }

    public Mono<User> saveUserToCache(User user) {
        return save(CACHE_PREFIX + user.getId(), user)
                .flatMap(u -> expireKey(CACHE_PREFIX + user.getId(), Duration.ofMinutes(10))
                        .thenReturn(u));
    }

    private Mono<Boolean> expireKey(String key, Duration ttl) {
        return getTemplate().expire(key, ttl);
    }
}
