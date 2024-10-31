package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.SalesStrategyRequestDTO;
import br.com.fiap.prospai.dto.response.SalesStrategyResponseDTO;
import br.com.fiap.prospai.entity.SalesStrategy;
import br.com.fiap.prospai.repository.SalesStrategyRepository;
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

    @Test
    void testGetAllSalesStrategies() {
        SalesStrategy strategy1 = new SalesStrategy();
        strategy1.setId(1L);
        strategy1.setTitulo("Strategy 1");

        SalesStrategy strategy2 = new SalesStrategy();
        strategy2.setId(2L);
        strategy2.setTitulo("Strategy 2");

        when(salesStrategyRepository.findAll()).thenReturn(Arrays.asList(strategy1, strategy2));

        List<SalesStrategyResponseDTO> strategies = salesStrategyService.getAllSalesStrategies();

        assertEquals(2, strategies.size());
        verify(salesStrategyRepository, times(1)).findAll();
    }

    @Test
    void testGetSalesStrategyById_ExistingId() {
        SalesStrategy strategy = new SalesStrategy();
        strategy.setId(1L);
        strategy.setTitulo("Strategy 1");

        when(salesStrategyRepository.findById(1L)).thenReturn(Optional.of(strategy));

        Optional<SalesStrategyResponseDTO> result = salesStrategyService.getSalesStrategyById(1L);

        assertTrue(result.isPresent());
        assertEquals("Strategy 1", result.get().getTitulo());
        verify(salesStrategyRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateSalesStrategy() {
        SalesStrategyRequestDTO requestDTO = new SalesStrategyRequestDTO();
        requestDTO.setTitulo("Nova Strategy");
        requestDTO.setDescricao("Descrição");
        requestDTO.setDataImplementacao(LocalDate.now());

        SalesStrategy savedStrategy = new SalesStrategy();
        BeanUtils.copyProperties(requestDTO, savedStrategy);
        savedStrategy.setId(1L);

        when(salesStrategyRepository.save(any(SalesStrategy.class))).thenReturn(savedStrategy);

        SalesStrategyResponseDTO responseDTO = salesStrategyService.createSalesStrategy(requestDTO);

        assertNotNull(responseDTO);
        assertEquals("Nova Strategy", responseDTO.getTitulo());
        verify(salesStrategyRepository, times(1)).save(any(SalesStrategy.class));
    }

    @Test
    void testDeleteSalesStrategy_ExistingId() {
        SalesStrategy existingStrategy = new SalesStrategy();
        existingStrategy.setId(1L);

        when(salesStrategyRepository.findById(1L)).thenReturn(Optional.of(existingStrategy));
        doNothing().when(salesStrategyRepository).delete(existingStrategy);

        salesStrategyService.deleteSalesStrategy(1L);

        verify(salesStrategyRepository, times(1)).findById(1L);
        verify(salesStrategyRepository, times(1)).delete(existingStrategy);
    }
}
