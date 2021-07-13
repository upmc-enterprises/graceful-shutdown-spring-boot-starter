package io.github.upmcenterprises.spring.starters.gracefulshutdown;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties(prefix = "upmc-enterprises.graceful-shutdown")
@Getter
public class UpmcGracefulShutdownProperties {

  private final WebMvc webMvc = new WebMvc();

  @Getter
  @Setter
  public static class WebMvc {
    /** The amount of time Spring WebMvc's Async support should wait before timing out. */
    @DurationUnit(MINUTES)
    private Duration asyncTimeout = Duration.ofMinutes(5L);
  }
}
