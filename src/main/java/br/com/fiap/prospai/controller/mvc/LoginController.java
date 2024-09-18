package br.com.fiap.prospai.controller.mvc;

import br.com.fiap.prospai.dto.request.LoginRequestDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDTO()); // Adicionando um objeto ao modelo
        return "login"; // Nome da sua página de login (login.html)
    }

    @GetMapping("/")
    public String home() {
        return "home"; // Página inicial após login (home.html)
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // Página de acesso negado (access-denied.html)
    }

    // Adicione um método para capturar erros de login
    @PostMapping("/login")
    public String loginSubmit(@Valid @ModelAttribute("loginRequest") LoginRequestDTO loginRequest,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "login"; // Retorna à página de login se houver erros
        }
        // Lógica de autenticação aqui
        return "redirect:/home";
    }
}
