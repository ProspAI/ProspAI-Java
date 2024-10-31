package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.ReportRequestDTO;
import br.com.fiap.prospai.dto.response.ReportResponseDTO;
import br.com.fiap.prospai.entity.Report;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.ReportRepository;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
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

    private Cliente cliente;
    private Report report;

    @BeforeEach
    void setUp() {
        // Inicializa Cliente e Report antes de cada teste
        cliente = new Cliente();
        cliente.setId(1L);

        report = new Report();
        report.setId(1L);
        report.setTitulo("Report 1");
        report.setCliente(cliente);
        report.setDataCriacao(LocalDateTime.now());
    }

    @Test
    void testGetAllReports() {
        // Cria um segundo Report para teste
        Report report2 = new Report();
        report2.setId(2L);
        report2.setTitulo("Report 2");
        report2.setCliente(cliente); // Garante que cliente não é nulo

        // Mock do retorno do reportRepository
        when(reportRepository.findAll()).thenReturn(Arrays.asList(report, report2));

        // Executa o serviço
        List<ReportResponseDTO> reports = reportService.getAllReports();

        // Verificações
        assertEquals(2, reports.size());
        assertEquals("Report 1", reports.get(0).getTitulo());
        assertEquals("Report 2", reports.get(1).getTitulo());
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    void testGetReportById_ExistingId() {
        // Mock do retorno do reportRepository para um ID existente
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        // Executa o serviço
        Optional<ReportResponseDTO> result = reportService.getReportById(1L);

        // Verificações
        assertTrue(result.isPresent());
        assertEquals("Report 1", result.get().getTitulo());
        verify(reportRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateReport() {
        // Cria o DTO de requisição para Report
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setTitulo("Novo Report");
        requestDTO.setDescricao("Descrição");
        requestDTO.setPeriodoInicial(LocalDate.now());
        requestDTO.setPeriodoFinal(LocalDate.now().plusDays(1));

        // Mock do retorno do clienteRepository para um ID existente
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Mock do retorno do reportRepository para salvar o Report
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report savedReport = invocation.getArgument(0);
            savedReport.setId(1L); // Atribui um ID ao report salvo
            return savedReport;
        });

        // Executa o serviço
        ReportResponseDTO responseDTO = reportService.createReport(requestDTO, 1L);

        // Verificações
        assertNotNull(responseDTO);
        assertEquals("Novo Report", responseDTO.getTitulo());
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateReport_ExistingId() {
        // Configuração inicial do Report existente para teste de atualização
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setTitulo("Report Antigo");
        existingReport.setCliente(cliente); // Garante que cliente não é nulo

        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setTitulo("Report Atualizado");

        // Mock do retorno do reportRepository para encontrar e salvar o Report
        when(reportRepository.findById(1L)).thenReturn(Optional.of(existingReport));
        when(reportRepository.save(any(Report.class))).thenReturn(existingReport);

        // Executa o serviço
        ReportResponseDTO responseDTO = reportService.updateReport(1L, requestDTO);

        // Verificações
        assertNotNull(responseDTO);
        assertEquals("Report Atualizado", responseDTO.getTitulo());
        verify(reportRepository, times(1)).findById(1L);
        verify(reportRepository, times(1)).save(existingReport);
    }

}
