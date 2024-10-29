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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ClienteRepository clienteRepository;
    private final KafkaTemplate<String, String> kafkaTemplate; // Adicionando KafkaTemplate

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, ClienteRepository clienteRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.feedbackRepository = feedbackRepository;
        this.clienteRepository = clienteRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<FeedbackResponseDTO> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return feedbacks.stream()
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
        kafkaTemplate.send("feedback_topic", "Novo feedback criado com ID: " + novoFeedback.getId());

        return toResponseDTO(novoFeedback);
    }

    public FeedbackResponseDTO updateFeedback(Long id, FeedbackRequestDTO feedbackRequestDTO) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback não encontrado com id: " + id));

        BeanUtils.copyProperties(feedbackRequestDTO, feedback, "id", "dataCriacao", "cliente");
        Feedback feedbackAtualizado = feedbackRepository.save(feedback);

        // Enviar mensagem ao Kafka após atualizar feedback
        kafkaTemplate.send("feedback_topic", "Feedback atualizado com ID: " + feedbackAtualizado.getId());

        return toResponseDTO(feedbackAtualizado);
    }

    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback não encontrado com id: " + id));
        feedbackRepository.delete(feedback);

        // Enviar mensagem ao Kafka após excluir feedback
        kafkaTemplate.send("feedback_topic", "Feedback deletado com ID: " + id);
    }

    private FeedbackResponseDTO toResponseDTO(Feedback feedback) {
        FeedbackResponseDTO responseDTO = new FeedbackResponseDTO();
        BeanUtils.copyProperties(feedback, responseDTO);
        responseDTO.setCliente(new ClienteResponseDTO(feedback.getCliente()));
        return responseDTO;
    }
}
