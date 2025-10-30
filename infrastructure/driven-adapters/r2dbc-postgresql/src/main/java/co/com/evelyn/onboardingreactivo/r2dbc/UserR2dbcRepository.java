package co.com.evelyn.onboardingreactivo.r2dbc;

import co.com.evelyn.onboardingreactivo.r2dbc.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserR2dbcRepository extends
        ReactiveCrudRepository<UserEntity, Integer>,
        ReactiveQueryByExampleExecutor<UserEntity> {

    /**
     * Buscar usuarios por coincidencia parcial en nombre o apellido (case-insensitive)
     */
    @Query("SELECT * FROM users WHERE LOWER(first_name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(last_name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Flux<UserEntity> findByName(@Param("name") String name);

    /**
     * Inserta un nuevo usuario y devuelve la entidad creada (Postgres RETURNING *)
     */
    @Query("INSERT INTO users (id, email, first_name, last_name, avatar) " +
            "VALUES (:id, :email, :firstName, :lastName, :avatar) " +
            "RETURNING *")
    Mono<UserEntity> insert(@Param("id") Integer id,
                            @Param("email") String email,
                            @Param("firstName") String firstName,
                            @Param("lastName") String lastName,
                            @Param("avatar") String avatar);
}
