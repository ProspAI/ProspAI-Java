package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.PredictionRequestDTO;
import br.com.fiap.prospai.dto.response.PredictionResponseDTO;
import br.com.fiap.prospai.entity.Prediction;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.PredictionRepository;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
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

    private Cliente cliente;
    private Prediction prediction;

    @BeforeEach
    void setUp() {
        // Inicializa Cliente e Prediction antes de cada teste
        cliente = new Cliente();
        cliente.setId(1L);

        prediction = new Prediction();
        prediction.setId(1L);
        prediction.setTitulo("Prediction 1");
        prediction.setCliente(cliente);
        prediction.setDataGeracao(LocalDateTime.now());
    }

    @Test
    void testGetAllPredictions() {
        // Cria uma segunda Prediction para teste
        Prediction prediction2 = new Prediction();
        prediction2.setId(2L);
        prediction2.setTitulo("Prediction 2");
        prediction2.setCliente(cliente); // Garante que cliente não é nulo

        // Mock do retorno do predictionRepository
        when(predictionRepository.findAll()).thenReturn(Arrays.asList(prediction, prediction2));

        // Executa o serviço
        List<PredictionResponseDTO> predictions = predictionService.getAllPredictions();

        // Verificações
        assertEquals(2, predictions.size());
        assertEquals("Prediction 1", predictions.get(0).getTitulo());
        assertEquals("Prediction 2", predictions.get(1).getTitulo());
        verify(predictionRepository, times(1)).findAll();
    }

    @Test
    void testGetPredictionById_ExistingId() {
        // Mock do retorno do predictionRepository para um ID existente
        when(predictionRepository.findById(1L)).thenReturn(Optional.of(prediction));

        // Executa o serviço
        Optional<PredictionResponseDTO> result = predictionService.getPredictionById(1L);

        // Verificações
        assertTrue(result.isPresent());
        assertEquals("Prediction 1", result.get().getTitulo());
        verify(predictionRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePrediction() {
        // Cria o DTO de requisição para Prediction
        PredictionRequestDTO requestDTO = new PredictionRequestDTO();
        requestDTO.setTitulo("Nova Prediction");
        requestDTO.setDescricao("Descrição");
        requestDTO.setClienteId(1L);

        // Mock do retorno do clienteRepository para um ID existente
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Mock do retorno do predictionRepository para salvar a Prediction
        when(predictionRepository.save(any(Prediction.class))).thenAnswer(invocation -> {
            Prediction savedPrediction = invocation.getArgument(0);
            savedPrediction.setId(1L); // Atribui um ID ao prediction salvo
            return savedPrediction;
        });

        // Executa o serviço
        PredictionResponseDTO responseDTO = predictionService.createPrediction(requestDTO, 1L);

        // Verificações
        assertNotNull(responseDTO);
        assertEquals("Nova Prediction", responseDTO.getTitulo());
        verify(predictionRepository, times(1)).save(any(Prediction.class));
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePrediction_NullClienteId() {
        // Cria o DTO de requisição para Prediction sem Cliente ID
        PredictionRequestDTO requestDTO = new PredictionRequestDTO();
        requestDTO.setTitulo("Nova Prediction");
        requestDTO.setDescricao("Descrição");

        // Executa o serviço e verifica a exceção
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            predictionService.createPrediction(requestDTO, null);
        });

        assertEquals("Cliente ID não pode ser nulo.", exception.getMessage());
        verify(predictionRepository, never()).save(any(Prediction.class));
    }
}
