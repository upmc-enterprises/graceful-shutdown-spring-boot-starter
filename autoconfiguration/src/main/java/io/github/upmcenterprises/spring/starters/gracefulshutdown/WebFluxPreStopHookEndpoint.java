package io.github.upmcenterprises.spring.starters.gracefulshutdown;

import static java.time.Duration.ofMillis;

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

@RestControllerEndpoint(id = "preStopHook")
class WebFluxPreStopHookEndpoint {

  @GetMapping("/{delay}")
  public Mono<Void> preStopHook(@PathVariable("delay") long delay) {
    return Mono.empty().then().delaySubscription(ofMillis(delay));
  }
}
