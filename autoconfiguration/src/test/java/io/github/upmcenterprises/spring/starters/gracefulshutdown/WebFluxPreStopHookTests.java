package io.github.upmcenterprises.spring.starters.gracefulshutdown;

import io.github.upmcenterprises.spring.starters.gracefulshutdown.GracefulShutdownAutoConfiguration.ReactiveGracefulShutdownConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@DisplayName("Webflux pre-stop hook tests")
class WebFluxPreStopHookTests {
  AnnotationConfigReactiveWebApplicationContext context;

  WebTestClient webClient;

  @BeforeEach
  void setUp() {
    context = new AnnotationConfigReactiveWebApplicationContext();
    context.register(
        EndpointAutoConfiguration.class,
        WebEndpointAutoConfiguration.class,
        ManagementContextAutoConfiguration.class,
        WebFluxAutoConfiguration.class,
        ReactiveGracefulShutdownConfiguration.class);

    TestPropertyValues.of(
            "spring.main.web-application-type:reactive",
            "management.endpoints.web.exposure.include:pre-stop-hook")
        .applyTo(context);
    context.refresh();

    webClient = WebTestClient.bindToApplicationContext(context).build();
  }

  @AfterEach
  void close() {
    context.close();
  }

  @Test
  @DisplayName("/actuator/preStopHook/1 returns an HTTP 200")
  void itExposesAGracefulShutdownEndpoint() {

    webClient
        .get()
        .uri("/actuator/preStopHook/100")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .isEmpty();
  }
}
