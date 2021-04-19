package io.github.upmcenterprises.spring.starters.gracefulshutdown;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.WebApplicationType.NONE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.ConfigurableEnvironment;

class UpmcEnterprisesGracefulShutdownPostProcessorTests {

  UpmcEnterprisesGracefulShutdownPostProcessor uut;

  @BeforeEach
  void setUp() {
    uut = new UpmcEnterprisesGracefulShutdownPostProcessor();
  }

  @Test
  void defaultAutoConfigurationProperties() {
    SpringApplication springApplication =
        new SpringApplicationBuilder()
            .sources(UpmcEnterprisesGracefulShutdownPostProcessorTests.class)
            .web(NONE)
            .build();

    ConfigurableEnvironment environment = springApplication.run().getEnvironment();

    uut.postProcessEnvironment(environment, springApplication);

    assertThat(environment.getProperty("server.shutdown")).isEqualTo("graceful");
    assertThat(environment.getProperty("spring.lifecycle.timeout-per-shutdown-phase"))
        .isEqualTo("5m");
    assertThat(environment.getProperty("upmc-enterprises.graceful-shutdown.webmvc.async-timeout"))
        .isEqualTo("5m");
  }

  @Test
  void defaultCanBeOverridden() {
    SpringApplication springApplication =
        new SpringApplicationBuilder()
            .sources(UpmcEnterprisesGracefulShutdownPostProcessorTests.class)
            .web(NONE)
            .build();

    ConfigurableEnvironment environment =
        springApplication
            .run(
                "--server.shutdown=immediate",
                "--spring.lifecycle.timeout-per-shutdown-phase=1s",
                "--upmc-enterprises.graceful-shutdown.webmvc.async-timeout=10s")
            .getEnvironment();

    assertThat(environment.getProperty("server.shutdown")).isEqualTo("immediate");

    assertThat(environment.getProperty("spring.lifecycle.timeout-per-shutdown-phase"))
        .isEqualTo("1s");

    assertThat(environment.getProperty("upmc-enterprises.graceful-shutdown.webmvc.async-timeout"))
        .isEqualTo("10s");
  }
}
