package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.FeedbackRequestDTO;
import br.com.fiap.prospai.dto.response.FeedbackResponseDTO;
import br.com.fiap.prospai.entity.Feedback;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.FeedbackRepository;
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
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    @Test
    void testGetAllFeedbacks() {
        Feedback feedback1 = new Feedback();
        feedback1.setId(1L);
        feedback1.setTitulo("Feedback 1");

        Feedback feedback2 = new Feedback();
        feedback2.setId(2L);
        feedback2.setTitulo("Feedback 2");

        when(feedbackRepository.findAll()).thenReturn(Arrays.asList(feedback1, feedback2));

        List<FeedbackResponseDTO> feedbacks = feedbackService.getAllFeedbacks();

        assertEquals(2, feedbacks.size());
        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    void testGetFeedbackById_ExistingId() {
        Feedback feedback = new Feedback();
        feedback.setId(1L);
        feedback.setTitulo("Feedback 1");

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        Optional<FeedbackResponseDTO> result = feedbackService.getFeedbackById(1L);

        assertTrue(result.isPresent());
        assertEquals("Feedback 1", result.get().getTitulo());
        verify(feedbackRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateFeedback() {
        FeedbackRequestDTO requestDTO = new FeedbackRequestDTO();
        requestDTO.setTitulo("Novo Feedback");
        requestDTO.setDescricao("Descrição");
        requestDTO.setNota(5);

        Cliente cliente = new Cliente();
        cliente.setId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Feedback savedFeedback = new Feedback();
        BeanUtils.copyProperties(requestDTO, savedFeedback);
        savedFeedback.setId(1L);
        savedFeedback.setDataCriacao(LocalDateTime.now());
        savedFeedback.setCliente(cliente);

        when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedFeedback);

        FeedbackResponseDTO responseDTO = feedbackService.createFeedback(requestDTO, 1L);

        assertNotNull(responseDTO);
        assertEquals("Novo Feedback", responseDTO.getTitulo());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void testCreateFeedback_NonExistingCliente() {
        FeedbackRequestDTO requestDTO = new FeedbackRequestDTO();
        requestDTO.setTitulo("Novo Feedback");
        requestDTO.setDescricao("Descrição");
        requestDTO.setNota(5);

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            feedbackService.createFeedback(requestDTO, 1L);
        });

        assertEquals("Cliente não encontrado com id: 1", exception.getMessage());
        verify(clienteRepository, times(1)).findById(1L);
        verify(feedbackRepository, times(0)).save(any(Feedback.class));
    }
}
