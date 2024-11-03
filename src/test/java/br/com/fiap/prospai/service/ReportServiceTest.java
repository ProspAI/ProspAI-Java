package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.ReportRequestDTO;
import br.com.fiap.prospai.dto.response.ReportResponseDTO;
import br.com.fiap.prospai.entity.Report;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.ReportRepository;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Captor
    private ArgumentCaptor<Report> reportCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllReports_ShouldReturnListOfReports() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");

        Report report = new Report();
        report.setId(1L);
        report.setTitulo("Relatório Mensal");
        report.setCliente(cliente); // Inicializando o cliente
        report.setDataCriacao(LocalDateTime.now());

        when(reportRepository.findAll()).thenReturn(Collections.singletonList(report));

        // Act
        List<ReportResponseDTO> reports = reportService.getAllReports();

        // Assert
        assertNotNull(reports);
        assertEquals(1, reports.size());
        assertEquals("Relatório Mensal", reports.get(0).getTitulo());
        assertEquals("Cliente Teste", reports.get(0).getCliente().getNome());
    }

    @Test
    void getReportById_ShouldReturnReport_WhenExists() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");

        Report report = new Report();
        report.setId(1L);
        report.setTitulo("Relatório Anual");
        report.setCliente(cliente); // Inicializando o cliente
        report.setDataCriacao(LocalDateTime.now());

        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        // Act
        Optional<ReportResponseDTO> result = reportService.getReportById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Relatório Anual", result.get().getTitulo());
        assertEquals("Cliente Teste", result.get().getCliente().getNome());
    }

    @Test
    void getReportById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        when(reportRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<ReportResponseDTO> result = reportService.getReportById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void createReport_ShouldSaveReport() {
        // Arrange
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setTitulo("Relatório Semanal");
        requestDTO.setDescricao("Descrição do relatório");
        requestDTO.setPeriodoInicial(LocalDate.now().minusDays(7));
        requestDTO.setPeriodoFinal(LocalDate.now());
        requestDTO.setClienteId(1L);

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Report savedReport = new Report();
        savedReport.setId(1L);
        savedReport.setTitulo("Relatório Semanal");
        savedReport.setCliente(cliente);
        savedReport.setDataCriacao(LocalDateTime.now());

        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        // Act
        ReportResponseDTO responseDTO = reportService.createReport(requestDTO, 1L);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Relatório Semanal", responseDTO.getTitulo());
        assertEquals("Cliente Teste", responseDTO.getCliente().getNome());
        verify(kafkaTemplate, times(1)).send("report_topic", "Novo relatório criado com ID: 1");
    }

    @Test
    void createReport_ShouldThrowException_WhenClienteNotFound() {
        // Arrange
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setClienteId(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.createReport(requestDTO, 1L);
        });
        assertEquals("Cliente não encontrado com id: 1", exception.getMessage());
    }

    @Test
    void updateReport_ShouldUpdateExistingReport() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");

        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setTitulo("Relatório Antigo");
        existingReport.setCliente(cliente);
        existingReport.setDataCriacao(LocalDateTime.now());

        when(reportRepository.findById(1L)).thenReturn(Optional.of(existingReport));

        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setTitulo("Relatório Atualizado");
        requestDTO.setDescricao("Nova descrição");

        when(reportRepository.save(any(Report.class))).thenReturn(existingReport);

        // Act
        ReportResponseDTO responseDTO = reportService.updateReport(1L, requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals("Relatório Atualizado", responseDTO.getTitulo());
        assertEquals("Cliente Teste", responseDTO.getCliente().getNome());
        verify(kafkaTemplate, times(1)).send("report_topic", "Relatório atualizado com ID: 1");
    }

    @Test
    void updateReport_ShouldThrowException_WhenReportNotFound() {
        // Arrange
        when(reportRepository.findById(1L)).thenReturn(Optional.empty());

        ReportRequestDTO requestDTO = new ReportRequestDTO();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.updateReport(1L, requestDTO);
        });
        assertEquals("Report não encontrado com id: 1", exception.getMessage());
    }

    @Test
    void deleteReport_ShouldDeleteExistingReport() {
        // Arrange
        Report existingReport = new Report();
        existingReport.setId(1L);

        when(reportRepository.findById(1L)).thenReturn(Optional.of(existingReport));

        // Act
        reportService.deleteReport(1L);

        // Assert
        verify(reportRepository, times(1)).delete(existingReport);
        verify(kafkaTemplate, times(1)).send("report_topic", "Relatório deletado com ID: 1");
    }

    @Test
    void deleteReport_ShouldThrowException_WhenReportNotFound() {
        // Arrange
        when(reportRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.deleteReport(1L);
        });
        assertEquals("Report não encontrado com id: 1", exception.getMessage());
    }
}