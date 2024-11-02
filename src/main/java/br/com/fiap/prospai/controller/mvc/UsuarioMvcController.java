package br.com.fiap.prospai.controller.mvc;

import br.com.fiap.prospai.dto.request.UsuarioRequestDTO;
import br.com.fiap.prospai.dto.response.UsuarioResponseDTO;
import br.com.fiap.prospai.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    public String novoUsuarioForm(Model model, Authentication authentication) {
        model.addAttribute("usuario", new UsuarioRequestDTO());
        model.addAttribute("isAdmin", authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        return "usuarios/usuario-form";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@ModelAttribute UsuarioRequestDTO usuarioRequestDTO, Authentication authentication) {
        if (usuarioRequestDTO.getPapel() == null || (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && "ROLE_ADMIN".equals(usuarioRequestDTO.getPapel()))) {
            usuarioRequestDTO.setPapel("ROLE_USER"); // Default to ROLE_USER if not an admin
        }

        if (usuarioRequestDTO.getId() != null) {
            usuarioService.updateUsuario(usuarioRequestDTO.getId(), usuarioRequestDTO);
        } else {
            usuarioService.createUsuario(usuarioRequestDTO);
        }
        return "redirect:/login";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model model, Authentication authentication) {
        return usuarioService.getUsuarioById(id)
                .map(usuario -> {
                    UsuarioRequestDTO usuarioRequestDTO = new UsuarioRequestDTO();
                    usuarioRequestDTO.setId(usuario.getId());
                    usuarioRequestDTO.setNome(usuario.getNome());
                    usuarioRequestDTO.setEmail(usuario.getEmail());
                    usuarioRequestDTO.setPapel(usuario.getPapel());
                    usuarioRequestDTO.setAtivo(usuario.isAtivo());
                    model.addAttribute("usuario", usuarioRequestDTO);
                    model.addAttribute("isAdmin", authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
                    return "usuarios/usuario-form";
                })
                .orElse("redirect:/usuarios");
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarUsuario(@PathVariable Long id, @ModelAttribute UsuarioRequestDTO usuarioRequestDTO, Authentication authentication) {
        if (usuarioRequestDTO.getPapel() == null || (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && "ROLE_ADMIN".equals(usuarioRequestDTO.getPapel()))) {
            usuarioRequestDTO.setPapel("ROLE_USER"); // Default to ROLE_USER if not an admin
        }

        usuarioService.updateUsuario(id, usuarioRequestDTO);
        return "redirect:/usuarios";
    }

    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return "redirect:/usuarios";
    }
}
