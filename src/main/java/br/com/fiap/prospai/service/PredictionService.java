package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.PredictionRequestDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.dto.response.PredictionResponseDTO;
import br.com.fiap.prospai.entity.Prediction;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.PredictionRepository;
import br.com.fiap.prospai.repository.ClienteRepository;
import io.spring.ai.openai.chat.OpenAiChatModel;
import io.spring.ai.openai.chat.ChatResponse;
import io.spring.ai.openai.chat.Prompt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final ClienteRepository clienteRepository;
    private final OpenAiChatModel chatModel;  // Adicionando o modelo de chat para OpenAI
    private final EmailService emailService;  // Adicionando o serviço de email para enviar campanhas

    @Autowired
    public PredictionService(PredictionRepository predictionRepository,
                             ClienteRepository clienteRepository,
                             OpenAiChatModel chatModel,
                             EmailService emailService) {
        this.predictionRepository = predictionRepository;
        this.clienteRepository = clienteRepository;
        this.chatModel = chatModel;
        this.emailService = emailService;
    }

    public List<PredictionResponseDTO> getAllPredictions() {
        List<Prediction> predictions = predictionRepository.findAll();
        return predictions.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<PredictionResponseDTO> getPredictionById(Long id) {
        return predictionRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public PredictionResponseDTO createPrediction(PredictionRequestDTO predictionRequestDTO, Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente ID não pode ser nulo.");
        }

        // Verifica se o cliente existe no banco de dados
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + clienteId));

        // 1. Gere a predição e conteúdo de marketing personalizado usando OpenAI GPT
        String promptPrediction = String.format(
                "Baseado nos seguintes dados do cliente:\n" +
                        "Nome: %s\n" +
                        "Segmento de Mercado: %s\n" +
                        "Score de Engajamento: %s\n" +
                        "Descrição: %s\n" +
                        "Crie uma campanha de marketing personalizada que inclua um título e conteúdo para um email.",
                cliente.getNome(), cliente.getSegmentoMercado(), cliente.getScoreEngajamento(), predictionRequestDTO.getDescricao()
        );

        ChatResponse chatResponse = chatModel.call(new Prompt(promptPrediction));
        String generatedContent = chatResponse.getResult().getOutput().getContent();

        // 2. Salve a predição no banco de dados
        Prediction prediction = new Prediction();
        BeanUtils.copyProperties(predictionRequestDTO, prediction);
        prediction.setDescricao(generatedContent); // Atualize a descrição com o conteúdo gerado
        prediction.setDataGeracao(LocalDateTime.now());
        prediction.setCliente(cliente);  // Associa o cliente à predição

        // Salva a nova predição no repositório
        Prediction novaPrediction = predictionRepository.save(prediction);

        // 3. Envie a campanha de marketing gerada por email
        emailService.sendMarketingEmail(cliente.getEmail(), generatedContent);

        return toResponseDTO(novaPrediction);
    }

    public PredictionResponseDTO updatePrediction(Long id, PredictionRequestDTO predictionRequestDTO) {
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prediction não encontrada com id: " + id));

        BeanUtils.copyProperties(predictionRequestDTO, prediction, "id", "dataGeracao", "cliente");

        Cliente cliente = clienteRepository.findById(predictionRequestDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + predictionRequestDTO.getClienteId()));
        prediction.setCliente(cliente);

        Prediction predictionAtualizada = predictionRepository.save(prediction);
        return toResponseDTO(predictionAtualizada);
    }

    public void deletePrediction(Long id) {
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prediction não encontrada com id: " + id));
        predictionRepository.delete(prediction);
    }

    private PredictionResponseDTO toResponseDTO(Prediction prediction) {
        PredictionResponseDTO responseDTO = new PredictionResponseDTO();
        BeanUtils.copyProperties(prediction, responseDTO);
        responseDTO.setCliente(new ClienteResponseDTO(prediction.getCliente()));
        return responseDTO;
    }
}
