# UPMC Enterprises Graceful Shutdown Spring Boot Starter


A Spring Boot starter that enables Spring's graceful shutdown support and supplies actuator endpoints that can be used as a preStop hook for Kubernetes.

## :star: Purpose

A number of organizations are leveraging [distroless images](https://github.com/GoogleContainerTools/distroless#why-should-i-use-distroless-images) to reduce the footprint and attack surface of their applications. Implementing a [Kubernetes preStop hook](https://kubernetes.io/docs/concepts/containers/container-lifecycle-hooks/#container-hooks) with a `sleep ...` command is not possible with such images, as they don't have a shell to run the command. The intent of this starter is to overcome such challenges, or to be used in images that have a shell and would prefer to keep the preStop hook within their application.

## :package: What's Inside?

1. A Spring Boot Actuator endpoint `GET /actuator/preStopHook/{delayInMillis}` that allows you to delay the shutdown of your pod (so the Kubernetes control plane components can react to the pod's termination, for example) to achieve zero downtime deployments
    * You'll notice the preStopHook endpoint accepts a `delayInMillis` path variable to adjust the amount of time you want the Kubernetes preStop hook to wait before proceeding with the pod's termination process. 
    * An appropriate [@RestControllerEndpoint](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/endpoint/web/annotation/RestControllerEndpoint.html) will be registered for either WebMvc or WebFlux, depending on the type of application the starter is added to (based on `spring.main.web-application-type`).
    * Because this starter leverages Spring's `@RestControllerEndpoint`, the preStopHook endpoint will be situated among the other actuator endpoints of your application. This means that if you have actuator listening on a different port than your main application, the preStopHook endpoint will also only be available on the actuator port `management.server.port` (in fact, all the `management.server.*` properties affect the preStopHook endpoint).
1. Configures the following Spring Boot properties:
    * `server.shutdown: graceful`
    * `spring.lifecycle.timeout-per-shutdown-phase: 5m`
    * `upmc-enterprises.graceful-shutdown.webmvc.async-timeout: 5m` (specific for WebMvc applications)
    > :bulb: All of these properties can be overridden in your application (`application.yml`, environment variables, etc.).

## :pencil2: Getting Started:

### Maven
```xml
<dependency>
  <groupId>io.github.upmc-enterprises</groupId>
  <artifactId>upmc-enterprises-graceful-shutdown-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle
```groovy
implementation 'io.github.upmc-enterprises:upmc-enterprises-graceful-shutdown-spring-boot-starter:1.0.0'
```

### Kubernetes Configuration
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: example-deployment
spec:
  template:
    spec:
      terminationGracePeriodSeconds: 300
      containers:
      - name: example
        startupProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
        lifecycle:
          preStop:
            httpGet:
              # The kubelet appends a leading `/`, unlike with startup, liveness, and readiness probes
              # (https://github.com/kubernetes/kubernetes/issues/56770)
              path: actuator/preStopHook/10000
              port: 8080
```

## :white_check_mark: Supported Spring Boot Versions
| Spring Boot Version | Supported |
| --- | --- |
| 2.5.x | :white_check_mark: |
| 2.4.x | :white_check_mark: |
| 2.3.x | :white_check_mark: |

## :boom: Gotchas
1. The kubelet cannot currently handle self-signed certificates for the preStop hook (unlike startup, liveness, and readiness probes). As such, all of your application's actuator endpoints will need to be available over `HTTP`. [See this GitHub issue](https://github.com/kubernetes/kubernetes/pull/86139).
1. The kubelet appends a leading `/` with the preStop hook (unlike with startup, liveness, and readiness probes). See [this GitHub issue](https://github.com/kubernetes/kubernetes/issues/56770).

## :page_facing_up: References
1. [Crossing The Streams - SpringOne 2020](https://springone.io/2020/sessions/crossing-the-streams-rollout-strategies-to-keep-your-users-happy): A talk that demonstrates how deployment rollouts in Kubernetes could result in application errors, and how we can prevent them with Spring's graceful shutdown support and Kubernetes preStop hooks.
1. [Spring Boot's documentation on graceful shutdown](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-graceful-shutdown)
1. [Kubernetes Container Lifecycle Hooks](https://kubernetes.io/docs/concepts/containers/container-lifecycle-hooks/)
---
<p align="center">:black_heart: Proudly made in Pittsburgh, PA :yellow_heart:</p>
