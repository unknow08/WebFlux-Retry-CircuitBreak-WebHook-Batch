package co.com.carlos.rangel.proyectoWebFlux.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebHookService {

    private final Retry retry;
    private final CircuitBreaker circuitBreaker;
    private static final Logger LOG = LoggerFactory.getLogger(WebHookService.class);

    public WebHookService(CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        this.retry = retryRegistry.retry("WebHook");
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("WebHook");

        this.retry.getEventPublisher()
                .onRetry(e -> LOG.info("Retrying Hook. \n Attempt: {}", e.getNumberOfRetryAttempts()));

        this.circuitBreaker.getEventPublisher()
                .onStateTransition(event -> LOG.info("Circuit Breaker Transition for hook: from {} to {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()));
    }

    public Mono<String> webHookMensaje(){
        String URL = "http://localhost:8085";
        String ENDPOINT = "/hook";

        WebClient webClient = WebClient.builder().baseUrl(URL).build();

        Mono<String> mensajeHook = webClient
                .get()
                .uri(ENDPOINT)
                .retrieve()
                .bodyToMono(String.class)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(Throwable -> {
                    LOG.warn("Muchos intentos para contactar con el hook");
                    return Mono.just("No se pudo contactar con el hook");
                });

        return mensajeHook;
    }
}
