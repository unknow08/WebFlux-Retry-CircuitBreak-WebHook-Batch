package co.com.carlos.rangel.proyectoWebFlux.controller;

import co.com.carlos.rangel.proyectoWebFlux.service.StepOneService;
import co.com.carlos.rangel.proyectoWebFlux.service.StepThreeService;
import co.com.carlos.rangel.proyectoWebFlux.service.StepTwoService;
import co.com.carlos.rangel.proyectoWebFlux.service.WebHookService;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/api/steps")
public class GetStepApiController {
    private final StepOneService stepOneService;
    private final StepTwoService stepTwoService;
    private final StepThreeService stepThreeService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    @Autowired
    private WebHookService webHookService;

    private static final Logger LOG = LoggerFactory.getLogger(GetStepApiController.class);

    public GetStepApiController(StepOneService stepOneService, StepTwoService stepTwoService, StepThreeService stepThreeService, CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        this.stepOneService = stepOneService;
        this.stepTwoService = stepTwoService;
        this.stepThreeService = stepThreeService;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }


    @PostMapping("/orq")
    public Flux<String> getOrq(@RequestBody String requestBody){
        webHookService.webHookMensaje().subscribe(System.out::println);


        return Flux.merge(stepOneService.stepOne(), stepTwoService.steptwo(),stepThreeService.threeStep());
    }
}