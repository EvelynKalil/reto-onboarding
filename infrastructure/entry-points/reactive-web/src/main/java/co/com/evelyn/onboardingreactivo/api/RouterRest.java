package co.com.evelyn.onboardingreactivo.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        // reference for Sonar (avoid unused warning)
        return RouterFunctions.route(POST("/users/{id}"), handler::createUser)
                .andRoute(GET("/users/{id}"), handler::getUserById)
                .andRoute(GET("/users").and(queryParam("name", name -> true)), handler::getUsersByName)
                .andRoute(GET("/users"), handler::getAllUsers);
    }
}
