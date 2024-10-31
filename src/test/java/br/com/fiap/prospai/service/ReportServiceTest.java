package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.ReportRequestDTO;
import br.com.fiap.prospai.dto.response.ReportResponseDTO;
import br.com.fiap.prospai.entity.Report;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.ReportRepository;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void testGetAllReports() {
        Report report1 = new Report();
        report1.setId(1L);
        report1.setTitulo("Report 1");

        Report report2 = new Report();
        report2.setId(2L);
        report2.setTitulo("Report 2");

        when(reportRepository.findAll()).thenReturn(Arrays.asList(report1, report2));

        List<ReportResponseDTO> reports = reportService.getAllReports();

        assertEquals(2, reports.size());
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    void testGetReportById_ExistingId() {
        Report report = new Report();
        report.setId(1L);
        report.setTitulo("Report 1");

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        Optional<ReportResponseDTO> result = reportService.getReportById(1L);

        assertTrue(result.isPresent());
        assertEquals("Report 1", result.get().getTitulo());
        verify(reportRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateReport() {
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setTitulo("Novo Report");
        requestDTO.setDescricao("Descrição");
        requestDTO.setPeriodoInicial(LocalDate.now());
        requestDTO.setPeriodoFinal(LocalDate.now().plusDays(1));

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Report savedReport = new Report();
        BeanUtils.copyProperties(requestDTO, savedReport);
        savedReport.setId(1L);
        savedReport.setDataCriacao(LocalDateTime.now());
        savedReport.setCliente(cliente);

        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        ReportResponseDTO responseDTO = reportService.createReport(requestDTO, 1L);

        assertNotNull(responseDTO);
        assertEquals("Novo Report", responseDTO.getTitulo());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void testUpdateReport_ExistingId() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setTitulo("Report Antigo");

        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setTitulo("Report Atualizado");

        when(reportRepository.findById(1L)).thenReturn(Optional.of(existingReport));
        when(reportRepository.save(any(Report.class))).thenReturn(existingReport);

        ReportResponseDTO responseDTO = reportService.updateReport(1L, requestDTO);

        assertNotNull(responseDTO);
        assertEquals("Report Atualizado", responseDTO.getTitulo());
        verify(reportRepository, times(1)).findById(1L);
        verify(reportRepository, times(1)).save(existingReport);
    }
}
