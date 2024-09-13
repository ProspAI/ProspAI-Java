package br.com.fiap.prospai.controller.mvc;

import br.com.fiap.prospai.dto.request.UsuarioRequestDTO;
import br.com.fiap.prospai.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioMvcController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.getAllUsuarios());
        return "usuarios/usuarios";
    }

    @GetMapping("/{id}")
    public String visualizarUsuario(@PathVariable Long id, Model model) {
        return usuarioService.getUsuarioById(id)
                .map(usuario -> {
                    model.addAttribute("usuario", usuario);
                    return "usuarios/usuario-view";
                })
                .orElse("redirect:/usuarios");
    }

    @GetMapping("/novo")
    public String novoUsuarioForm(Model model) {
        model.addAttribute("usuario", new UsuarioRequestDTO());
        return "usuarios/usuario-form";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute UsuarioRequestDTO usuarioRequestDTO) {
        usuarioService.createUsuario(usuarioRequestDTO);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model model) {
        return usuarioService.getUsuarioById(id)
                .map(usuario -> {
                    model.addAttribute("usuario", usuario);
                    return "usuarios/usuario-form";
                })
                .orElse("redirect:/usuarios");
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarUsuario(@PathVariable Long id, @ModelAttribute UsuarioRequestDTO usuarioRequestDTO) {
        usuarioService.updateUsuario(id, usuarioRequestDTO);
        return "redirect:/usuarios";
    }

    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return "redirect:/usuarios";
    }
}
