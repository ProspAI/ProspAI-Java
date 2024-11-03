package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.PredictionRequestDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.dto.response.PredictionResponseDTO;
import br.com.fiap.prospai.entity.Prediction;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.PredictionRepository;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    private final PredictionRepository predictionRepository;
    private final ClienteRepository clienteRepository;
    private final AzureOpenAiChatModel chatModel;
    private final KafkaProducerService kafkaProducerService; // Adicionado para uso do Kafka

    @Autowired
    public PredictionService(PredictionRepository predictionRepository,
                             ClienteRepository clienteRepository,
                             AzureOpenAiChatModel chatModel,
                             KafkaProducerService kafkaProducerService) {
        this.predictionRepository = predictionRepository;
        this.clienteRepository = clienteRepository;
        this.chatModel = chatModel;
        this.kafkaProducerService = kafkaProducerService; // Injeção do KafkaProducerService
    }

    public List<PredictionResponseDTO> getAllPredictions() {
        logger.info("Buscando todas as predições...");
        return predictionRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<PredictionResponseDTO> getPredictionById(Long id) {
        logger.info("Buscando predição com ID: {}", id);
        return predictionRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional
    public PredictionResponseDTO createPrediction(PredictionRequestDTO predictionRequestDTO, Long clienteId) {
        logger.info("Criando nova predição para o cliente com ID: {}", clienteId);

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> {
                    logger.error("Cliente não encontrado com ID: {}", clienteId);
                    throw new RuntimeException("Cliente não encontrado com id: " + clienteId);
                });

        String promptText = generatePromptText(cliente, predictionRequestDTO);

        try {
            String generatedContent = generateMarketingContent(promptText);

            Prediction prediction = new Prediction();
            BeanUtils.copyProperties(predictionRequestDTO, prediction);
            prediction.setDescricao(generatedContent);
            prediction.setDataGeracao(LocalDateTime.now());
            prediction.setCliente(cliente);

            Prediction novaPrediction = predictionRepository.save(prediction);
            logger.info("Predição criada com sucesso: ID={}", novaPrediction.getId());

            // Enviar mensagem ao Kafka
            try {
                PredictionResponseDTO predictionResponseDTO = toResponseDTO(novaPrediction);
                kafkaProducerService.sendMessage("prediction_topic", predictionResponseDTO);
            } catch (Exception e) {
                logger.error("Erro ao enviar mensagem para o Kafka: {}", e.getMessage(), e);
            }

            return toResponseDTO(novaPrediction);
        } catch (Exception e) {
            logger.error("Erro ao gerar predição: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar predição", e);
        }
    }

    @Transactional
    public PredictionResponseDTO updatePrediction(Long id, PredictionRequestDTO predictionRequestDTO) {
        logger.info("Atualizando predição com ID: {}", id);
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Predição não encontrada com ID: {}", id);
                    return new RuntimeException("Predição não encontrada com id: " + id);
                });

        Cliente cliente = clienteRepository.findById(predictionRequestDTO.getClienteId())
                .orElseThrow(() -> {
                    logger.error("Cliente não encontrado com ID: {}", predictionRequestDTO.getClienteId());
                    return new RuntimeException("Cliente não encontrado com id: " + predictionRequestDTO.getClienteId());
                });

        BeanUtils.copyProperties(predictionRequestDTO, prediction, "id", "dataGeracao", "cliente", "descricao");
        prediction.setCliente(cliente);

        // Regenerar a descrição usando a IA
        String promptText = generatePromptText(cliente, predictionRequestDTO);
        String generatedContent = generateMarketingContent(promptText);
        prediction.setDescricao(generatedContent);

        Prediction predictionAtualizada = predictionRepository.save(prediction);
        logger.info("Predição atualizada com sucesso: ID={}", predictionAtualizada.getId());

        // Enviar mensagem ao Kafka
        try {
            PredictionResponseDTO predictionResponseDTO = toResponseDTO(predictionAtualizada);
            kafkaProducerService.sendMessage("prediction_topic", predictionResponseDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para o Kafka: {}", e.getMessage(), e);
        }

        return toResponseDTO(predictionAtualizada);
    }

    @Transactional
    public void deletePrediction(Long id) {
        logger.info("Deletando predição com ID: {}", id);
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Predição não encontrada com ID: {}", id);
                    return new RuntimeException("Predição não encontrada com id: " + id);
                });
        predictionRepository.delete(prediction);
        logger.info("Predição deletada com sucesso: ID={}", id);

        // Enviar mensagem ao Kafka
        try {
            PredictionResponseDTO predictionResponseDTO = toResponseDTO(prediction);
            kafkaProducerService.sendMessage("prediction_topic", predictionResponseDTO);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para o Kafka: {}", e.getMessage(), e);
        }
    }

    private String generatePromptText(Cliente cliente, PredictionRequestDTO predictionRequestDTO) {
        return String.format("Gere um conteúdo de marketing para o cliente %s com base no título: '%s'.",
                cliente.getNome(), predictionRequestDTO.getTitulo());
    }

    private String generateMarketingContent(String promptText) {
        try {
            logger.info("Enviando prompt para IA: {}", promptText);

            // Criação do objeto UserMessage com o prompt
            UserMessage userMessage = new UserMessage(promptText);

            // Chamada ao modelo AzureOpenAiChatModel com UserMessage
            return chatModel.call(userMessage); // Mantido conforme sua lógica
        } catch (Exception e) {
            logger.error("Erro ao gerar conteúdo de marketing: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar conteúdo de marketing", e);
        }
    }

    private PredictionResponseDTO toResponseDTO(Prediction prediction) {
        PredictionResponseDTO responseDTO = new PredictionResponseDTO();
        BeanUtils.copyProperties(prediction, responseDTO);
        responseDTO.setCliente(new ClienteResponseDTO(prediction.getCliente()));
        return responseDTO;
    }
}
