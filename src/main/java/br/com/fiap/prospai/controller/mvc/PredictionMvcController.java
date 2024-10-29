package br.com.fiap.prospai.controller.mvc;

import br.com.fiap.prospai.dto.request.PredictionRequestDTO;
import br.com.fiap.prospai.dto.response.PredictionResponseDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.service.PredictionService;
import br.com.fiap.prospai.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/predictions")
public class PredictionMvcController {

    private final PredictionService predictionService;
    private final ClienteService clienteService;

    @Autowired
    public PredictionMvcController(PredictionService predictionService, ClienteService clienteService) {
        this.predictionService = predictionService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listarPredicoes(Model model) {
        List<PredictionResponseDTO> predictions = predictionService.getAllPredictions();
        model.addAttribute("predictions", predictions);
        return "predictions/predictions";
    }

    @GetMapping("/{id}")
    public String visualizarPredicao(@PathVariable Long id, Model model) {
        Optional<PredictionResponseDTO> predictionOpt = predictionService.getPredictionById(id);
        if (predictionOpt.isPresent()) {
            model.addAttribute("prediction", predictionOpt.get());
            return "predictions/prediction-view";
        } else {
            return "redirect:/predictions";
        }
    }

    @GetMapping("/novo")
    public String novaPredicaoForm(Model model) {
        model.addAttribute("prediction", new PredictionRequestDTO());
        List<ClienteResponseDTO> clientes = clienteService.getAllClientes();
        model.addAttribute("clientes", clientes);
        return "predictions/prediction-form";
    }

    @PostMapping("/salvar")
    public String salvarPredicao(@ModelAttribute PredictionRequestDTO predictionRequestDTO, Model model) {
        Long clienteId = predictionRequestDTO.getClienteId();
        if (clienteId == null) {
            model.addAttribute("error", "Cliente deve ser selecionado.");
            List<ClienteResponseDTO> clientes = clienteService.getAllClientes();
            model.addAttribute("clientes", clientes);
            return "predictions/prediction-form";
        }

        predictionService.createPrediction(predictionRequestDTO, clienteId);
        return "redirect:/predictions";
    }

    @GetMapping("/editar/{id}")
    public String editarPredicaoForm(@PathVariable Long id, Model model) {
        Optional<PredictionResponseDTO> predictionOpt = predictionService.getPredictionById(id);
        if (predictionOpt.isPresent()) {
            PredictionResponseDTO predictionResponse = predictionOpt.get();
            PredictionRequestDTO predictionRequest = new PredictionRequestDTO();
            predictionRequest.setId(predictionResponse.getId());
            predictionRequest.setTitulo(predictionResponse.getTitulo());
            predictionRequest.setDescricao(predictionResponse.getDescricao());
            predictionRequest.setPrecisao(predictionResponse.getPrecisao());
            predictionRequest.setClienteId(predictionResponse.getCliente().getId());

            model.addAttribute("prediction", predictionRequest);
            List<ClienteResponseDTO> clientes = clienteService.getAllClientes();
            model.addAttribute("clientes", clientes);
            return "predictions/prediction-form";
        } else {
            return "redirect:/predictions";
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarPredicao(@PathVariable Long id, @ModelAttribute PredictionRequestDTO predictionRequestDTO) {
        predictionService.updatePrediction(id, predictionRequestDTO);
        return "redirect:/predictions";
    }

    @GetMapping("/deletar/{id}")
    public String deletarPredicao(@PathVariable Long id) {
        predictionService.deletePrediction(id);
        return "redirect:/predictions";
    }
}
