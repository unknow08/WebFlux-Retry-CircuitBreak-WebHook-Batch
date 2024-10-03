package co.com.carlos.rangel.proyectoWebFlux.service;

import co.com.carlos.rangel.proyectoWebFlux.controller.GetStepApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchService {

    @Autowired
    private GetStepApiController getStepApiController;

    @Scheduled(fixedRate = 2000)
    public void tareaRandom(){
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

        getStepApiController.getOrq(jsonValue).subscribe(System.out::println);;
    }
}
