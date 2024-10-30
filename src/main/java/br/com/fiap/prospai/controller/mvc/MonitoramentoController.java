package br.com.fiap.prospai.controller.mvc;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class MonitoramentoController {

    @GetMapping("/monitoramento")
    public String monitoramento(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String healthUrl = "http://localhost:8080/actuator/health";
        String metricsUrl = "http://localhost:8080/actuator/metrics";

        Object health = restTemplate.getForObject(healthUrl, Object.class);
        Object metrics = restTemplate.getForObject(metricsUrl, Object.class);

        model.addAttribute("health", health);
        model.addAttribute("metrics", metrics);

        return "monitoramento";
    }
}
