package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.ReportRequestDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.dto.response.ReportResponseDTO;
import br.com.fiap.prospai.entity.Report;
import br.com.fiap.prospai.repository.ReportRepository;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ClienteRepository clienteRepository;

    public ReportService(ReportRepository reportRepository, ClienteRepository clienteRepository) {
        this.reportRepository = reportRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<ReportResponseDTO> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<ReportResponseDTO> getReportById(Long id) {
        return reportRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public ReportResponseDTO createReport(ReportRequestDTO reportRequestDTO, Long clienteId) {
        Report report = new Report();
        BeanUtils.copyProperties(reportRequestDTO, report);
        report.setDataCriacao(LocalDateTime.now());
        report.setCliente(clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + clienteId)));
        Report novoReport = reportRepository.save(report);
        return toResponseDTO(novoReport);
    }

    public ReportResponseDTO updateReport(Long id, ReportRequestDTO reportRequestDTO) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report não encontrado com id: " + id));

        BeanUtils.copyProperties(reportRequestDTO, report, "id", "dataCriacao", "cliente");
        Report reportAtualizado = reportRepository.save(report);
        return toResponseDTO(reportAtualizado);
    }

    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report não encontrado com id: " + id));
        reportRepository.delete(report);
    }

    private ReportResponseDTO toResponseDTO(Report report) {
        ReportResponseDTO responseDTO = new ReportResponseDTO();
        BeanUtils.copyProperties(report, responseDTO);
        responseDTO.setCliente(new ClienteResponseDTO(report.getCliente()));
        return responseDTO;
    }
}