package co.com.evelyn.onboardingreactivo.api;

import co.com.evelyn.onboardingreactivo.api.config.TestBeansConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@Disabled("Generated test — not applicable to current implementation")
@Import(TestBeansConfig.class)
@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testListenGETUseCase() {
        webTestClient.get()
                .uri("/api/usecase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenGETOtherUseCase() {
        webTestClient.get()
                .uri("/api/otherusercase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenPOSTUseCase() {
        webTestClient.post()
                .uri("/api/usecase/otherpath")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }
}
