package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.FeedbackRequestDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.dto.response.FeedbackResponseDTO;
import br.com.fiap.prospai.entity.Feedback;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.FeedbackRepository;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ClienteRepository clienteRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, ClienteRepository clienteRepository, KafkaProducerService kafkaProducerService) {
        this.feedbackRepository = feedbackRepository;
        this.clienteRepository = clienteRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<FeedbackResponseDTO> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<FeedbackResponseDTO> getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO, Long clienteId) {
        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(feedbackRequestDTO, feedback);
        feedback.setDataCriacao(LocalDateTime.now());
        feedback.setCliente(clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + clienteId)));

        Feedback novoFeedback = feedbackRepository.save(feedback);

        // Enviar mensagem ao Kafka após criar feedback
        try {
            FeedbackResponseDTO feedbackResponseDTO = toResponseDTO(novoFeedback);
            kafkaProducerService.sendMessage("feedback_topic", feedbackResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(novoFeedback);
    }

    public FeedbackResponseDTO updateFeedback(Long id, FeedbackRequestDTO feedbackRequestDTO) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback não encontrado com id: " + id));

        BeanUtils.copyProperties(feedbackRequestDTO, feedback, "id", "dataCriacao", "cliente");
        Feedback feedbackAtualizado = feedbackRepository.save(feedback);

        // Enviar mensagem ao Kafka após atualizar feedback
        try {
            FeedbackResponseDTO feedbackResponseDTO = toResponseDTO(feedbackAtualizado);
            kafkaProducerService.sendMessage("feedback_topic", feedbackResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(feedbackAtualizado);
    }

    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback não encontrado com id: " + id));
        feedbackRepository.delete(feedback);

        // Enviar mensagem ao Kafka após excluir feedback
        try {
            FeedbackResponseDTO feedbackResponseDTO = toResponseDTO(feedback);
            kafkaProducerService.sendMessage("feedback_topic", feedbackResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FeedbackResponseDTO toResponseDTO(Feedback feedback) {
        FeedbackResponseDTO responseDTO = new FeedbackResponseDTO();
        BeanUtils.copyProperties(feedback, responseDTO);
        responseDTO.setCliente(new ClienteResponseDTO(feedback.getCliente()));
        return responseDTO;
    }
}
