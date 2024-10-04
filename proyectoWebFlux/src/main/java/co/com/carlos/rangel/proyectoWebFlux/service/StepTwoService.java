package co.com.carlos.rangel.proyectoWebFlux.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class StepTwoService {
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;
    private static final Logger LOG = LoggerFactory.getLogger(StepTwoService.class);

    public StepTwoService(CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        this.retry = retryRegistry.retry("stepTwo");
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("stepTwo");

        this.retry.getEventPublisher()
                .onRetry(e -> LOG.info("Retrying Step 2. \n Attempt: {}", e.getNumberOfRetryAttempts()));

        this.circuitBreaker.getEventPublisher()
                .onStateTransition(event -> LOG.info("Circuit Breaker Transition for step 2: from {} to {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()));
    }

    public Mono<String> steptwo(){
        String URL = "http://localhost:8083";
        String ENDPOINT = "/getStep";
        String jsonValue = "{\n" +
                "\"data\": [{\n" +
                "\"header\": {\n" +
                "\"id\": \"12345\",\n" +
                "\"type\": \"StepsGiraffeRefrigerator\"\n" +
                "},\n" +
                "\"step\": \"2\"\n" +
                "}]\n" +
                "}";

        WebClient webClient = WebClient.builder().baseUrl(URL).build();

        Mono<String> respuesta = webClient
                .post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON) // Establece el Content-Type a application/json
                .bodyValue(jsonValue)  // Aquí defines el cuerpo de la solicitud
                .retrieve()
                .bodyToMono(String.class)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(Throwable -> {
                    LOG.warn("Muchos intentos para contactar con el paso");
                    return Mono.just("Lo sentimos, no pudimos encontrar el paso");
                });

        return respuesta;

    }
}
