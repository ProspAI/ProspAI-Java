package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.PredictionRequestDTO;
import br.com.fiap.prospai.dto.response.PredictionResponseDTO;
import br.com.fiap.prospai.entity.Prediction;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.PredictionRepository;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class PredictionServiceTest {

    @Mock
    private PredictionRepository predictionRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private PredictionService predictionService;

    @Test
    void testGetAllPredictions() {
        Prediction prediction1 = new Prediction();
        prediction1.setId(1L);
        prediction1.setTitulo("Prediction 1");

        Prediction prediction2 = new Prediction();
        prediction2.setId(2L);
        prediction2.setTitulo("Prediction 2");

        when(predictionRepository.findAll()).thenReturn(Arrays.asList(prediction1, prediction2));

        List<PredictionResponseDTO> predictions = predictionService.getAllPredictions();

        assertEquals(2, predictions.size());
        verify(predictionRepository, times(1)).findAll();
    }

    @Test
    void testGetPredictionById_ExistingId() {
        Prediction prediction = new Prediction();
        prediction.setId(1L);
        prediction.setTitulo("Prediction 1");

        when(predictionRepository.findById(1L)).thenReturn(Optional.of(prediction));

        Optional<PredictionResponseDTO> result = predictionService.getPredictionById(1L);

        assertTrue(result.isPresent());
        assertEquals("Prediction 1", result.get().getTitulo());
        verify(predictionRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePrediction() {
        PredictionRequestDTO requestDTO = new PredictionRequestDTO();
        requestDTO.setTitulo("Nova Prediction");
        requestDTO.setDescricao("Descrição");
        requestDTO.setClienteId(1L);

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Prediction savedPrediction = new Prediction();
        BeanUtils.copyProperties(requestDTO, savedPrediction);
        savedPrediction.setId(1L);
        savedPrediction.setDataGeracao(LocalDateTime.now());
        savedPrediction.setCliente(cliente);

        when(predictionRepository.save(any(Prediction.class))).thenReturn(savedPrediction);

        PredictionResponseDTO responseDTO = predictionService.createPrediction(requestDTO, 1L);

        assertNotNull(responseDTO);
        assertEquals("Nova Prediction", responseDTO.getTitulo());
        verify(predictionRepository, times(1)).save(any(Prediction.class));
    }

    @Test
    void testCreatePrediction_NullClienteId() {
        PredictionRequestDTO requestDTO = new PredictionRequestDTO();
        requestDTO.setTitulo("Nova Prediction");
        requestDTO.setDescricao("Descrição");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            predictionService.createPrediction(requestDTO, null);
        });

        assertEquals("Cliente ID não pode ser nulo.", exception.getMessage());
        verify(predictionRepository, times(0)).save(any(Prediction.class));
    }
}
