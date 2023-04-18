package io.github.upmcenterprises.spring.starters.gracefulshutdown;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
public class GracefulShutdownAutoConfiguration {

  @ConditionalOnWebApplication(type = SERVLET)
  @ConditionalOnClass(org.springframework.web.servlet.DispatcherServlet.class)
  @EnableConfigurationProperties(UpmcGracefulShutdownProperties.class)
  @AutoConfiguration
  static class WebMvcGracefulShutdownConfiguration {

    @Bean
    WebMvcPreStopHookEndpoint servletGracefulShutdownEndpoint() {
      return new WebMvcPreStopHookEndpoint();
    }

    @Bean
    WebMvcConfigurer asyncWebMvcConfigurer(UpmcGracefulShutdownProperties properties) {
      return new WebMvcConfigurer() {
        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
          configurer.setDefaultTimeout(properties.getWebMvc().getAsyncTimeout().toMillis());
        }
      };
    }
  }

  @ConditionalOnWebApplication(type = REACTIVE)
  @ConditionalOnClass(org.springframework.web.reactive.config.WebFluxConfigurer.class)
  @AutoConfiguration
  static class ReactiveGracefulShutdownConfiguration {
    @Bean
    WebFluxPreStopHookEndpoint reactiveGracefulShutdownEndpoint() {
      return new WebFluxPreStopHookEndpoint();
    }
  }
}
