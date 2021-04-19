package io.github.upmcenterprises.spring.starters.gracefulshutdown;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Slf4j
public class UpmcEnterprisesGracefulShutdownPostProcessor implements EnvironmentPostProcessor {
  public static final String GRACEFUL_SHUTDOWN_CONFIGURATION =
      "/META-INF/upmc-enterprises-graceful-shutdown.yml";

  private final YamlPropertySourceLoader loader;

  public UpmcEnterprisesGracefulShutdownPostProcessor() {
    this.loader = new YamlPropertySourceLoader();
  }

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {

    PropertySource<?> propertySource = loadYaml();
    environment.getPropertySources().addLast(propertySource);
  }

  private PropertySource<?> loadYaml() {
    Resource resource = new ClassPathResource(GRACEFUL_SHUTDOWN_CONFIGURATION);

    if (!resource.exists()) {
      throw new IllegalArgumentException("Resource " + resource + " does not exist");
    }
    try {
      return this.loader.load(GRACEFUL_SHUTDOWN_CONFIGURATION, resource).get(0);
    } catch (IOException ex) {
      throw new IllegalStateException("Failed to load yaml configuration from " + resource, ex);
    }
  }
}
