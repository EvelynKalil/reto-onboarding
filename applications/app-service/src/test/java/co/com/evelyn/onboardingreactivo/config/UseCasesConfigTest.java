package co.com.evelyn.onboardingreactivo.config;

import co.com.evelyn.onboardingreactivo.model.user.gateways.UserRepository;
import co.com.evelyn.onboardingreactivo.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {



    @Test
    void testUseCaseBeansExist() {

        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] names = ctx.getBeanDefinitionNames();
            boolean found = false;
            for (String n : names) {
                if (n.endsWith("UseCase")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "No beans ending with 'UseCase' were found");
        }
    }

    @Configuration
    static class TestConfig extends UseCasesConfig {

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }
}