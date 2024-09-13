package br.com.fiap.prospai.controller.mvc;

import br.com.fiap.prospai.dto.request.ClienteRequestDTO;
import br.com.fiap.prospai.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteMvcController {

    @Autowired
    private ClienteService clienteService;

    // Listar todos os clientes
    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteService.getAllClientes());
        return "clientes/clientes";
    }

    // Visualizar um cliente específico
    @GetMapping("/{id}")
    public String visualizarCliente(@PathVariable Long id, Model model) {
        return clienteService.getClienteById(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    return "clientes/cliente-detalhes";
                })
                .orElse("redirect:/clientes");
    }

    // Formulário para criar um novo cliente
    @GetMapping("/novo")
    public String novoClienteForm(Model model) {
        model.addAttribute("cliente", new ClienteRequestDTO());
        return "clientes/cliente-form";
    }

    // Salvar o novo cliente
    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute ClienteRequestDTO clienteRequestDTO) {
        clienteService.createCliente(clienteRequestDTO);
        return "redirect:/clientes";
    }

    // Formulário para editar um cliente existente
    @GetMapping("/editar/{id}")
    public String editarClienteForm(@PathVariable Long id, Model model) {
        return clienteService.getClienteById(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    return "clientes/cliente-form";
                })
                .orElse("redirect:/clientes");
    }

    // Atualizar um cliente existente
    @PostMapping("/atualizar/{id}")
    public String atualizarCliente(@PathVariable Long id, @ModelAttribute ClienteRequestDTO clienteRequestDTO) {
        clienteService.updateCliente(id, clienteRequestDTO);
        return "redirect:/clientes";
    }

    // Deletar um cliente
    @GetMapping("/deletar/{id}")
    public String deletarCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return "redirect:/clientes";
    }
}
