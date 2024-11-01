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
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    private final PredictionRepository predictionRepository;
    private final ClienteRepository clienteRepository;
    private final ChatClient chatClient;

    public PredictionService(PredictionRepository predictionRepository,
                             ClienteRepository clienteRepository,
                             ChatClient.Builder chatClientBuilder) {
        this.predictionRepository = predictionRepository;
        this.clienteRepository = clienteRepository;
        this.chatClient = chatClientBuilder.build();
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

            return toResponseDTO(novaPrediction);
        } catch (Exception e) {
            logger.error("Erro ao gerar predição: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar predição", e);
        }
    }

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

        return toResponseDTO(predictionAtualizada);
    }

    public void deletePrediction(Long id) {
        logger.info("Deletando predição com ID: {}", id);
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Predição não encontrada com ID: {}", id);
                    return new RuntimeException("Predição não encontrada com id: " + id);
                });
        predictionRepository.delete(prediction);
        logger.info("Predição deletada com sucesso: ID={}", id);
    }

    private String generatePromptText(Cliente cliente, PredictionRequestDTO predictionRequestDTO) {
        return String.format("Gere um conteúdo de marketing para o cliente %s com base no título: '%s'.",
                cliente.getNome(), predictionRequestDTO.getTitulo());
    }

    private String generateMarketingContent(String promptText) {
        return chatClient.prompt()
                .user(promptText)
                .call()
                .content();
    }

    private PredictionResponseDTO toResponseDTO(Prediction prediction) {
        PredictionResponseDTO responseDTO = new PredictionResponseDTO();
        BeanUtils.copyProperties(prediction, responseDTO);
        responseDTO.setCliente(new ClienteResponseDTO(prediction.getCliente()));
        return responseDTO;
    }
}
