package co.com.carlos.rangel.Batch.Service;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BatchService {

    @Scheduled(fixedRate = 10000)
    public void llamarOrq(){
        String URL = "http://localhost:8080";
        String ENDPOINT = "/api/steps/orq";
        String jsonValue = "{\n" +
                "\"data\": [\n" +
                "{\n" +
                "\"header\": {\n" +
                "\"id\": \"${id}\",\n" +
                "\"type\": \"${type}\"\n" +
                "},\n" +
                "\"answer\": \"Step1: ${stepOneAnswer} - Step2: ${stepTwoAnswer} - Step3: ${stepThreeAnswer}\"\n" +
                "}\n" +
                "]\n" +
                "}";

        WebClient webClient = WebClient.builder().baseUrl(URL).build();

        Flux<String> orq = webClient
                .post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON) // Establece el Content-Type a application/json
                .bodyValue(jsonValue)  // Aquí defines el cuerpo de la solicitud
                .retrieve()
                .bodyToFlux(String.class);

        orq.subscribe(System.out::println);
    }
}
