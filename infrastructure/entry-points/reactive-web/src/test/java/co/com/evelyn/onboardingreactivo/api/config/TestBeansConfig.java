package co.com.evelyn.onboardingreactivo.api.config;

import co.com.evelyn.onboardingreactivo.model.user.gateways.UserRepository;
import co.com.evelyn.onboardingreactivo.usecase.user.UserUseCase;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestBeansConfig {

    @Bean
    public UserUseCase userUseCase() {
        return new UserUseCase(Mockito.mock(UserRepository.class));
    }
}
