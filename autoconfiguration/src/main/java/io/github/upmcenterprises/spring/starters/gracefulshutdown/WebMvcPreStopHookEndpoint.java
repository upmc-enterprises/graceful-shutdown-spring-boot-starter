package io.github.upmcenterprises.spring.starters.gracefulshutdown;

import java.util.concurrent.Callable;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestControllerEndpoint(id = "preStopHook")
class WebMvcPreStopHookEndpoint {
  @GetMapping("/{delay}")
  public Callable<ResponseEntity<Void>> preStopHook(@PathVariable("delay") long delay) {
    return () -> {
      Thread.sleep(delay);
      return ResponseEntity.ok().build();
    };
  }
}
