package br.com.fiap.prospai.controller.mvc;

import br.com.fiap.prospai.dto.request.FeedbackRequestDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.dto.response.FeedbackResponseDTO;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.service.FeedbackService;
import br.com.fiap.prospai.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/feedbacks")
public class FeedbackMvcController {

    private final FeedbackService feedbackService;
    private final ClienteService clienteService;

    @Autowired
    public FeedbackMvcController(FeedbackService feedbackService, ClienteService clienteService) {
        this.feedbackService = feedbackService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listarFeedbacks(Model model) {
        model.addAttribute("feedbacks", feedbackService.getAllFeedbacks());
        return "feedbacks/feedbacks";
    }

    @GetMapping("/{id}")
    public String visualizarFeedback(@PathVariable Long id, Model model) {
        return feedbackService.getFeedbackById(id)
                .map(feedback -> {
                    model.addAttribute("feedback", feedback);
                    return "feedbacks/feedback-view";
                })
                .orElse("redirect:/feedbacks");
    }

    @GetMapping("/novo")
    public String novoFeedbackForm(Model model) {
        model.addAttribute("feedback", new FeedbackRequestDTO()); // Inicializa o objeto FeedbackRequestDTO
        List<ClienteResponseDTO> clientes = clienteService.getAllClientes(); // Carrega todos os clientes para o dropdown
        model.addAttribute("clientes", clientes); // Adiciona clientes ao modelo
        return "feedbacks/feedback-form";
    }

    @PostMapping("/salvar")
    public String salvarFeedback(@ModelAttribute FeedbackRequestDTO feedbackRequestDTO, @RequestParam Long clienteId) {
        feedbackService.createFeedback(feedbackRequestDTO, clienteId);
        return "redirect:/feedbacks";
    }

    @GetMapping("/editar/{id}")
    public String editarFeedbackForm(@PathVariable Long id, Model model) {
        return feedbackService.getFeedbackById(id)
                .map(feedback -> {
                    FeedbackRequestDTO feedbackRequestDTO = new FeedbackRequestDTO();
                    feedbackRequestDTO.setTitulo(feedback.getTitulo());
                    feedbackRequestDTO.setDescricao(feedback.getDescricao());
                    feedbackRequestDTO.setNota(feedback.getNota());
                    model.addAttribute("feedback", feedbackRequestDTO); // Adiciona o feedback como DTO de requisição
                    List<ClienteResponseDTO> clientes = clienteService.getAllClientes();
                    model.addAttribute("clientes", clientes);
                    return "feedbacks/feedback-form";
                })
                .orElse("redirect:/feedbacks");
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarFeedback(@PathVariable Long id, @ModelAttribute FeedbackRequestDTO feedbackRequestDTO) {
        feedbackService.updateFeedback(id, feedbackRequestDTO);
        return "redirect:/feedbacks";
    }

    @GetMapping("/deletar/{id}")
    public String deletarFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return "redirect:/feedbacks";
    }
}
