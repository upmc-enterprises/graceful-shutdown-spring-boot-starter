package io.github.upmcenterprises.spring.starters.gracefulshutdown;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("WebMvc pre-stop hook tests")
class WebMvcPreStopHookTests {
  AnnotationConfigServletWebApplicationContext context;

  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    context = new AnnotationConfigServletWebApplicationContext();
    context.register(
        EndpointAutoConfiguration.class,
        WebEndpointAutoConfiguration.class,
        ManagementContextAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        GracefulShutdownAutoConfiguration.WebMvcGracefulShutdownConfiguration.class);

    TestPropertyValues.of(
            "spring.main.web-application-type:servlet",
            "management.endpoints.web.exposure.include:pre-stop-hook",
            "upmc-enterprises.graceful-shutdown.webmvc.async-timeout:1ms")
        .applyTo(context);

    context.setServletContext(new MockServletContext());
    context.refresh();
    mockMvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(print()).build();
  }

  @AfterEach
  void close() {
    context.close();
  }

  @Test
  @DisplayName("/actuator/preStopHook/1 returns an HTTP 200")
  void itExposesAGracefulShutdownEndpoint() throws Exception {
    MvcResult result =
        mockMvc
            .perform(get("/actuator/preStopHook/1000"))
            .andExpect(request().asyncStarted())
            .andExpect(request().asyncResult(notNullValue()))
            .andReturn();

    mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk());
  }
}
