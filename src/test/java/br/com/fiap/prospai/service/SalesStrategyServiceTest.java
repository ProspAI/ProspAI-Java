package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.SalesStrategyRequestDTO;
import br.com.fiap.prospai.dto.response.SalesStrategyResponseDTO;
import br.com.fiap.prospai.entity.SalesStrategy;
import br.com.fiap.prospai.repository.SalesStrategyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesStrategyServiceTest {

    @Mock
    private SalesStrategyRepository salesStrategyRepository;

    @InjectMocks
    private SalesStrategyService salesStrategyService;

    private SalesStrategy strategy;

    @BeforeEach
    void setUp() {
        // Configura uma instância de SalesStrategy antes de cada teste
        strategy = new SalesStrategy();
        strategy.setId(1L);
        strategy.setTitulo("Strategy 1");
        strategy.setDataImplementacao(LocalDate.now());
    }

    @Test
    void testGetAllSalesStrategies() {
        // Cria uma segunda estratégia para teste
        SalesStrategy strategy2 = new SalesStrategy();
        strategy2.setId(2L);
        strategy2.setTitulo("Strategy 2");

        // Mock do retorno do salesStrategyRepository
        when(salesStrategyRepository.findAll()).thenReturn(Arrays.asList(strategy, strategy2));

        // Executa o serviço
        List<SalesStrategyResponseDTO> strategies = salesStrategyService.getAllSalesStrategies();

        // Verificações
        assertEquals(2, strategies.size());
        assertEquals("Strategy 1", strategies.get(0).getTitulo());
        assertEquals("Strategy 2", strategies.get(1).getTitulo());
        verify(salesStrategyRepository, times(1)).findAll();
    }

    @Test
    void testGetSalesStrategyById_ExistingId() {
        // Mock do retorno do salesStrategyRepository para um ID existente
        when(salesStrategyRepository.findById(1L)).thenReturn(Optional.of(strategy));

        // Executa o serviço
        Optional<SalesStrategyResponseDTO> result = salesStrategyService.getSalesStrategyById(1L);

        // Verificações
        assertTrue(result.isPresent());
        assertEquals("Strategy 1", result.get().getTitulo());
        verify(salesStrategyRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateSalesStrategy() {
        // Cria o DTO de requisição para SalesStrategy
        SalesStrategyRequestDTO requestDTO = new SalesStrategyRequestDTO();
        requestDTO.setTitulo("Nova Strategy");
        requestDTO.setDescricao("Descrição");
        requestDTO.setDataImplementacao(LocalDate.now());

        // Mock do retorno do salesStrategyRepository para salvar a SalesStrategy
        when(salesStrategyRepository.save(any(SalesStrategy.class))).thenAnswer(invocation -> {
            SalesStrategy savedStrategy = invocation.getArgument(0);
            savedStrategy.setId(1L); // Define um ID para a estratégia salva
            return savedStrategy;
        });

        // Executa o serviço
        SalesStrategyResponseDTO responseDTO = salesStrategyService.createSalesStrategy(requestDTO);

        // Verificações
        assertNotNull(responseDTO);
        assertEquals("Nova Strategy", responseDTO.getTitulo());
        verify(salesStrategyRepository, times(1)).save(any(SalesStrategy.class));
    }

    @Test
    void testDeleteSalesStrategy_ExistingId() {
        // Mock do retorno do salesStrategyRepository para encontrar e deletar a estratégia
        when(salesStrategyRepository.findById(1L)).thenReturn(Optional.of(strategy));
        doNothing().when(salesStrategyRepository).delete(strategy);

        // Executa o serviço
        salesStrategyService.deleteSalesStrategy(1L);

        // Verificações
        verify(salesStrategyRepository, times(1)).findById(1L);
        verify(salesStrategyRepository, times(1)).delete(strategy);
    }
}
