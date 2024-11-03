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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.UserMessage;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PredictionServiceTest {

    @InjectMocks
    private PredictionService predictionService;

    @Mock
    private PredictionRepository predictionRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AzureOpenAiChatModel chatModel;

    @Captor
    private ArgumentCaptor<Prediction> predictionCaptor;

    // Não é necessário o método setUp() com @ExtendWith(MockitoExtension.class)

    @Test
    public void getAllPredictions_ShouldReturnListOfPredictions() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");

        Prediction prediction = new Prediction();
        prediction.setId(1L);
        prediction.setTitulo("Campanha X");
        prediction.setDescricao("Descrição da campanha");
        prediction.setDataGeracao(LocalDateTime.now());
        prediction.setCliente(cliente);

        when(predictionRepository.findAll()).thenReturn(Collections.singletonList(prediction));

        // Act
        List<PredictionResponseDTO> predictions = predictionService.getAllPredictions();

        // Assert
        assertNotNull(predictions);
        assertEquals(1, predictions.size());
        assertEquals("Campanha X", predictions.get(0).getTitulo());
        assertEquals("Cliente Teste", predictions.get(0).getCliente().getNome());
    }

    @Test
    public void getPredictionById_ShouldReturnPrediction_WhenExists() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");

        Prediction prediction = new Prediction();
        prediction.setId(1L);
        prediction.setTitulo("Promoção Y");
        prediction.setDescricao("Descrição da promoção");
        prediction.setDataGeracao(LocalDateTime.now());
        prediction.setCliente(cliente);

        when(predictionRepository.findById(1L)).thenReturn(Optional.of(prediction));

        // Act
        Optional<PredictionResponseDTO> result = predictionService.getPredictionById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Promoção Y", result.get().getTitulo());
        assertEquals("Cliente Teste", result.get().getCliente().getNome());
    }

    @Test
    public void getPredictionById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        when(predictionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<PredictionResponseDTO> result = predictionService.getPredictionById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void createPrediction_ShouldGenerateContentAndSavePrediction() {
        // Arrange
        PredictionRequestDTO requestDTO = new PredictionRequestDTO();
        requestDTO.setTitulo("Nova Estratégia");
        requestDTO.setClienteId(1L);

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Empresa XYZ");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        String generatedContent = "Conteúdo gerado pela IA";
        when(chatModel.call(any(UserMessage.class))).thenReturn(generatedContent);

        Prediction savedPrediction = new Prediction();
        savedPrediction.setId(1L);
        savedPrediction.setTitulo("Nova Estratégia");
        savedPrediction.setDescricao(generatedContent);
        savedPrediction.setDataGeracao(LocalDateTime.now());
        savedPrediction.setCliente(cliente);

        when(predictionRepository.save(any(Prediction.class))).thenReturn(savedPrediction);

        // Act
        PredictionResponseDTO responseDTO = predictionService.createPrediction(requestDTO, 1L);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Nova Estratégia", responseDTO.getTitulo());
        assertEquals("Conteúdo gerado pela IA", responseDTO.getDescricao());
        assertEquals("Empresa XYZ", responseDTO.getCliente().getNome());

        verify(chatModel, times(1)).call(any(UserMessage.class));
        verify(predictionRepository, times(1)).save(any(Prediction.class));
    }

    @Test
    public void createPrediction_ShouldThrowException_WhenClienteNotFound() {
        // Arrange
        PredictionRequestDTO requestDTO = new PredictionRequestDTO();
        requestDTO.setClienteId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            predictionService.createPrediction(requestDTO, 1L);
        });
        assertEquals("Cliente não encontrado com id: 1", exception.getMessage());
    }

    @Test
    public void updatePrediction_ShouldUpdateExistingPrediction() {
        // Arrange
        PredictionRequestDTO requestDTO = new PredictionRequestDTO();
        requestDTO.setTitulo("Estratégia Atualizada");
        requestDTO.setClienteId(1L);

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Empresa XYZ");

        Prediction existingPrediction = new Prediction();
        existingPrediction.setId(1L);
        existingPrediction.setTitulo("Estratégia Antiga");
        existingPrediction.setDescricao("Descrição antiga");
        existingPrediction.setDataGeracao(LocalDateTime.now());
        existingPrediction.setCliente(cliente);

        when(predictionRepository.findById(1L)).thenReturn(Optional.of(existingPrediction));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        String generatedContent = "Novo conteúdo gerado pela IA";
        when(chatModel.call(any(UserMessage.class))).thenReturn(generatedContent);

        when(predictionRepository.save(any(Prediction.class))).thenReturn(existingPrediction);

        // Act
        PredictionResponseDTO responseDTO = predictionService.updatePrediction(1L, requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals("Estratégia Atualizada", responseDTO.getTitulo());
        assertEquals(generatedContent, responseDTO.getDescricao());
        assertEquals("Empresa XYZ", responseDTO.getCliente().getNome());

        verify(chatModel, times(1)).call(any(UserMessage.class));
        verify(predictionRepository, times(1)).save(existingPrediction);
    }

    @Test
    public void updatePrediction_ShouldThrowException_WhenPredictionNotFound() {
        // Arrange
        PredictionRequestDTO requestDTO = new PredictionRequestDTO();
        when(predictionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            predictionService.updatePrediction(1L, requestDTO);
        });
        assertEquals("Predição não encontrada com id: 1", exception.getMessage());
    }

    @Test
    public void deletePrediction_ShouldDeleteExistingPrediction() {
        // Arrange
        Prediction existingPrediction = new Prediction();
        existingPrediction.setId(1L);

        when(predictionRepository.findById(1L)).thenReturn(Optional.of(existingPrediction));

        // Act
        predictionService.deletePrediction(1L);

        // Assert
        verify(predictionRepository, times(1)).delete(existingPrediction);
    }

    @Test
    public void deletePrediction_ShouldThrowException_WhenPredictionNotFound() {
        // Arrange
        when(predictionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            predictionService.deletePrediction(1L);
        });
        assertEquals("Predição não encontrada com id: 1", exception.getMessage());
    }
}