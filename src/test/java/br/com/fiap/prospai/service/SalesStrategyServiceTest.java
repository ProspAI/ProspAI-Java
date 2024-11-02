package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.SalesStrategyRequestDTO;
import br.com.fiap.prospai.dto.response.SalesStrategyResponseDTO;
import br.com.fiap.prospai.entity.SalesStrategy;
import br.com.fiap.prospai.repository.SalesStrategyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalesStrategyServiceTest {

    @InjectMocks
    private SalesStrategyService salesStrategyService;

    @Mock
    private SalesStrategyRepository salesStrategyRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Captor
    private ArgumentCaptor<SalesStrategy> salesStrategyCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllSalesStrategies_ShouldReturnListOfStrategies() {
        // Arrange
        SalesStrategy strategy = new SalesStrategy();
        strategy.setId(1L);
        strategy.setTitulo("Estratégia A");
        strategy.setDescricao("Descrição A");
        strategy.setDataImplementacao(LocalDate.now());

        when(salesStrategyRepository.findAll()).thenReturn(Collections.singletonList(strategy));

        // Act
        List<SalesStrategyResponseDTO> strategies = salesStrategyService.getAllSalesStrategies();

        // Assert
        assertNotNull(strategies);
        assertEquals(1, strategies.size());
        assertEquals("Estratégia A", strategies.get(0).getTitulo());
    }

    @Test
    void getSalesStrategyById_ShouldReturnStrategy_WhenExists() {
        // Arrange
        SalesStrategy strategy = new SalesStrategy();
        strategy.setId(1L);
        strategy.setTitulo("Estratégia B");
        strategy.setDescricao("Descrição B");
        strategy.setDataImplementacao(LocalDate.now());

        when(salesStrategyRepository.findById(1L)).thenReturn(Optional.of(strategy));

        // Act
        Optional<SalesStrategyResponseDTO> result = salesStrategyService.getSalesStrategyById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Estratégia B", result.get().getTitulo());
    }

    @Test
    void getSalesStrategyById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        when(salesStrategyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<SalesStrategyResponseDTO> result = salesStrategyService.getSalesStrategyById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void createSalesStrategy_ShouldSaveStrategy() {
        // Arrange
        SalesStrategyRequestDTO requestDTO = new SalesStrategyRequestDTO();
        requestDTO.setTitulo("Nova Estratégia");
        requestDTO.setDescricao("Descrição da estratégia");
        requestDTO.setDataImplementacao(LocalDate.now());

        SalesStrategy savedStrategy = new SalesStrategy();
        savedStrategy.setId(1L);
        savedStrategy.setTitulo("Nova Estratégia");
        savedStrategy.setDescricao("Descrição da estratégia");
        savedStrategy.setDataImplementacao(LocalDate.now());

        when(salesStrategyRepository.save(any(SalesStrategy.class))).thenReturn(savedStrategy);

        // Act
        SalesStrategyResponseDTO responseDTO = salesStrategyService.createSalesStrategy(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Nova Estratégia", responseDTO.getTitulo());
        verify(kafkaTemplate, times(1)).send("sales_strategy_topic", "Nova estratégia de vendas criada com ID: 1");
    }

    @Test
    void updateSalesStrategy_ShouldUpdateExistingStrategy() {
        // Arrange
        SalesStrategy existingStrategy = new SalesStrategy();
        existingStrategy.setId(1L);
        existingStrategy.setTitulo("Estratégia Antiga");
        existingStrategy.setDescricao("Descrição antiga");
        existingStrategy.setDataImplementacao(LocalDate.now());

        when(salesStrategyRepository.findById(1L)).thenReturn(Optional.of(existingStrategy));

        SalesStrategyRequestDTO requestDTO = new SalesStrategyRequestDTO();
        requestDTO.setTitulo("Estratégia Atualizada");
        requestDTO.setDescricao("Descrição atualizada");

        when(salesStrategyRepository.save(any(SalesStrategy.class))).thenReturn(existingStrategy);

        // Act
        SalesStrategyResponseDTO responseDTO = salesStrategyService.updateSalesStrategy(1L, requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals("Estratégia Atualizada", responseDTO.getTitulo());
        verify(kafkaTemplate, times(1)).send("sales_strategy_topic", "Estratégia de vendas atualizada com ID: 1");
    }

    @Test
    void updateSalesStrategy_ShouldThrowException_WhenStrategyNotFound() {
        // Arrange
        when(salesStrategyRepository.findById(1L)).thenReturn(Optional.empty());

        SalesStrategyRequestDTO requestDTO = new SalesStrategyRequestDTO();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            salesStrategyService.updateSalesStrategy(1L, requestDTO);
        });
        assertEquals("Sales Strategy não encontrada com id: 1", exception.getMessage());
    }

    @Test
    void deleteSalesStrategy_ShouldDeleteExistingStrategy() {
        // Arrange
        SalesStrategy existingStrategy = new SalesStrategy();
        existingStrategy.setId(1L);

        when(salesStrategyRepository.findById(1L)).thenReturn(Optional.of(existingStrategy));

        // Act
        salesStrategyService.deleteSalesStrategy(1L);

        // Assert
        verify(salesStrategyRepository, times(1)).delete(existingStrategy);
        verify(kafkaTemplate, times(1)).send("sales_strategy_topic", "Estratégia de vendas deletada com ID: 1");
    }

    @Test
    void deleteSalesStrategy_ShouldThrowException_WhenStrategyNotFound() {
        // Arrange
        when(salesStrategyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            salesStrategyService.deleteSalesStrategy(1L);
        });
        assertEquals("Sales Strategy não encontrada com id: 1", exception.getMessage());
    }
}
