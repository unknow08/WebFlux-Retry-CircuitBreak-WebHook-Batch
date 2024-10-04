package co.com.carlos.rangel.WebHook.Service;


import org.springframework.stereotype.Service;

@Service
public class WebHookService {

    public String mensajeHook(){
        return "Mensaje orquestador recibido";
    }
}
