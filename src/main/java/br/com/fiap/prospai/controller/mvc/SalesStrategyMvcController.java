package br.com.fiap.prospai.controller.mvc;

import br.com.fiap.prospai.dto.request.SalesStrategyRequestDTO;
import br.com.fiap.prospai.service.SalesStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sales-strategies")
public class SalesStrategyMvcController {

    @Autowired
    private SalesStrategyService salesStrategyService;

    @GetMapping
    public String listarEstrategias(Model model) {
        model.addAttribute("salesStrategies", salesStrategyService.getAllSalesStrategies());
        return "sales-strategies/sales-strategies";
    }

    @GetMapping("/{id}")
    public String visualizarEstrategia(@PathVariable Long id, Model model) {
        return salesStrategyService.getSalesStrategyById(id)
                .map(salesStrategy -> {
                    model.addAttribute("salesStrategy", salesStrategy);
                    return "sales-strategies/sales-strategy-view";
                })
                .orElse("redirect:/sales-strategies");
    }

    @GetMapping("/novo")
    public String novaEstrategiaForm(Model model) {
        model.addAttribute("salesStrategy", new SalesStrategyRequestDTO());
        return "sales-strategies/sales-strategy-form";
    }

    @PostMapping("/salvar")
    public String salvarEstrategia(@ModelAttribute SalesStrategyRequestDTO salesStrategyRequestDTO) {
        salesStrategyService.createSalesStrategy(salesStrategyRequestDTO);
        return "redirect:/sales-strategies";
    }

    @GetMapping("/editar/{id}")
    public String editarEstrategiaForm(@PathVariable Long id, Model model) {
        return salesStrategyService.getSalesStrategyById(id)
                .map(salesStrategy -> {
                    model.addAttribute("salesStrategy", salesStrategy);
                    return "sales-strategies/sales-strategy-form";
                })
                .orElse("redirect:/sales-strategies");
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarEstrategia(@PathVariable Long id, @ModelAttribute SalesStrategyRequestDTO salesStrategyRequestDTO) {
        salesStrategyService.updateSalesStrategy(id, salesStrategyRequestDTO);
        return "redirect:/sales-strategies";
    }

    @GetMapping("/deletar/{id}")
    public String deletarEstrategia(@PathVariable Long id) {
        salesStrategyService.deleteSalesStrategy(id);
        return "redirect:/sales-strategies";
    }
}
