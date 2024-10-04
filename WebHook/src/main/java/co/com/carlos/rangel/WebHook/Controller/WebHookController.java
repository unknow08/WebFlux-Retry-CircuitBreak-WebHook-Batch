package co.com.carlos.rangel.WebHook.Controller;

import co.com.carlos.rangel.WebHook.Service.WebHookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebHookController {

    @Autowired
    private WebHookService webHookService;

    @GetMapping("/hook")
    public String hook(){
       return webHookService.mensajeHook();
    }
}
