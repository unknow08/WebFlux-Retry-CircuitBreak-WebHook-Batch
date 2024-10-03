package co.com.carlos.rangel.proyectoWebFlux.service;

import org.springframework.stereotype.Service;

@Service
public class WebHookService {

    public Void mensajeHook(){
        System.out.println("Mensaje orquestador recibido");
        return null;
    }
}
