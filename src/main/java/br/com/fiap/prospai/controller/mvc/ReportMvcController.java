package br.com.fiap.prospai.controller.mvc;

import br.com.fiap.prospai.dto.request.ReportRequestDTO;
import br.com.fiap.prospai.dto.response.ReportResponseDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.service.ReportService;
import br.com.fiap.prospai.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reports")
public class ReportMvcController {

    private final ReportService reportService;
    private final ClienteService clienteService;

    @Autowired
    public ReportMvcController(ReportService reportService, ClienteService clienteService) {
        this.reportService = reportService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listarReports(Model model) {
        List<ReportResponseDTO> reports = reportService.getAllReports();
        model.addAttribute("reports", reports);
        return "reports/reports";
    }

    @GetMapping("/{id}")
    public String visualizarReport(@PathVariable Long id, Model model) {
        Optional<ReportResponseDTO> reportOpt = reportService.getReportById(id);
        if (reportOpt.isPresent()) {
            model.addAttribute("report", reportOpt.get());
            return "reports/report-view";
        } else {
            return "redirect:/reports";
        }
    }

    @GetMapping("/novo")
    public String novoReportForm(Model model) {
        model.addAttribute("report", new ReportRequestDTO());
        List<ClienteResponseDTO> clientes = clienteService.getAllClientes();
        model.addAttribute("clientes", clientes);
        return "reports/report-form";
    }

    @PostMapping("/salvar")
    public String salvarReport(@ModelAttribute ReportRequestDTO reportRequestDTO) {
        reportService.createReport(reportRequestDTO, reportRequestDTO.getClienteId());
        return "redirect:/reports";
    }

    @GetMapping("/editar/{id}")
    public String editarReportForm(@PathVariable Long id, Model model) {
        Optional<ReportResponseDTO> reportOpt = reportService.getReportById(id);
        if (reportOpt.isPresent()) {
            ReportResponseDTO reportResponseDTO = reportOpt.get();
            ReportRequestDTO reportRequestDTO = new ReportRequestDTO();  // Converter para DTO de request
            reportRequestDTO.setId(reportResponseDTO.getId());
            reportRequestDTO.setTitulo(reportResponseDTO.getTitulo());
            reportRequestDTO.setDescricao(reportResponseDTO.getDescricao());
            reportRequestDTO.setPeriodoInicial(reportResponseDTO.getPeriodoInicial());
            reportRequestDTO.setPeriodoFinal(reportResponseDTO.getPeriodoFinal());
            reportRequestDTO.setClienteId(reportResponseDTO.getCliente().getId());

            model.addAttribute("report", reportRequestDTO);
            List<ClienteResponseDTO> clientes = clienteService.getAllClientes();
            model.addAttribute("clientes", clientes);
            return "reports/report-form";
        } else {
            return "redirect:/reports";
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarReport(@PathVariable Long id, @ModelAttribute ReportRequestDTO reportRequestDTO) {
        reportService.updateReport(id, reportRequestDTO);
        return "redirect:/reports";
    }

    @GetMapping("/deletar/{id}")
    public String deletarReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return "redirect:/reports";
    }
}
