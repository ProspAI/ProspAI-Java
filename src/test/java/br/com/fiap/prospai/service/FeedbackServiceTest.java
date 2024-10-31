package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.FeedbackRequestDTO;
import br.com.fiap.prospai.dto.response.FeedbackResponseDTO;
import br.com.fiap.prospai.entity.Feedback;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.FeedbackRepository;
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
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private Cliente cliente;
    private Feedback feedback;

    @BeforeEach
    void setUp() {
        // Inicializa Cliente e Feedback antes de cada teste
        cliente = new Cliente();
        cliente.setId(1L);

        feedback = new Feedback();
        feedback.setId(1L);
        feedback.setTitulo("Feedback 1");
        feedback.setCliente(cliente);
        feedback.setDataCriacao(LocalDateTime.now());
    }

    @Test
    void testGetAllFeedbacks() {
        // Cria feedback adicional para teste
        Feedback feedback2 = new Feedback();
        feedback2.setId(2L);
        feedback2.setTitulo("Feedback 2");
        feedback2.setCliente(cliente); // Garante que cliente não é nulo

        // Mock do retorno do feedbackRepository
        when(feedbackRepository.findAll()).thenReturn(Arrays.asList(feedback, feedback2));

        // Executa o serviço
        List<FeedbackResponseDTO> feedbacks = feedbackService.getAllFeedbacks();

        // Verificações
        assertEquals(2, feedbacks.size());
        assertEquals("Feedback 1", feedbacks.get(0).getTitulo());
        assertEquals("Feedback 2", feedbacks.get(1).getTitulo());
        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    void testGetFeedbackById_ExistingId() {
        // Mock do retorno do feedbackRepository para um ID existente
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        // Executa o serviço
        Optional<FeedbackResponseDTO> result = feedbackService.getFeedbackById(1L);

        // Verificações
        assertTrue(result.isPresent());
        assertEquals("Feedback 1", result.get().getTitulo());
        verify(feedbackRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateFeedback() {
        // Cria o DTO de requisição para o feedback
        FeedbackRequestDTO requestDTO = new FeedbackRequestDTO();
        requestDTO.setTitulo("Novo Feedback");
        requestDTO.setDescricao("Descrição");
        requestDTO.setNota(5);

        // Mock do retorno do clienteRepository para um ID existente
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Mock do retorno do feedbackRepository para salvar o feedback
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> {
            Feedback savedFeedback = invocation.getArgument(0);
            savedFeedback.setId(1L); // Atribui um ID ao feedback salvo
            return savedFeedback;
        });

        // Executa o serviço
        FeedbackResponseDTO responseDTO = feedbackService.createFeedback(requestDTO, 1L);

        // Verificações
        assertNotNull(responseDTO);
        assertEquals("Novo Feedback", responseDTO.getTitulo());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        verify(clienteRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateFeedback_NonExistingCliente() {
        // Cria o DTO de requisição para o feedback
        FeedbackRequestDTO requestDTO = new FeedbackRequestDTO();
        requestDTO.setTitulo("Novo Feedback");
        requestDTO.setDescricao("Descrição");
        requestDTO.setNota(5);

        // Mock do retorno do clienteRepository para um cliente inexistente
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Executa o serviço e verifica a exceção
        Exception exception = assertThrows(RuntimeException.class, () -> {
            feedbackService.createFeedback(requestDTO, 1L);
        });

        assertEquals("Cliente não encontrado com id: 1", exception.getMessage());
        verify(clienteRepository, times(1)).findById(1L);
        verify(feedbackRepository, never()).save(any(Feedback.class));
    }
}
