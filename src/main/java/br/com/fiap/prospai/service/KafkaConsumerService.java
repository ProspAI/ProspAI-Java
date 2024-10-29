package br.com.fiap.prospai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.fiap.prospai.dto.response.PredictionResponseDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.dto.response.FeedbackResponseDTO;
import br.com.fiap.prospai.dto.response.ReportResponseDTO;
import br.com.fiap.prospai.dto.response.SalesStrategyResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "clientes_topic", groupId = "cliente-group")
    public void listenClientes(String message, Acknowledgment ack) {
        try {
            ClienteResponseDTO cliente = objectMapper.readValue(message, ClienteResponseDTO.class);
            processCliente(cliente);
            ack.acknowledge();
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de cliente: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "feedback_topic", groupId = "feedback-group")
    public void listenFeedbacks(String message, Acknowledgment ack) {
        try {
            FeedbackResponseDTO feedback = objectMapper.readValue(message, FeedbackResponseDTO.class);
            processFeedback(feedback);
            ack.acknowledge();
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de feedback: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "prediction_topic", groupId = "prediction-group")
    public void listenPredictions(String message, Acknowledgment ack) {
        try {
            PredictionResponseDTO prediction = objectMapper.readValue(message, PredictionResponseDTO.class);
            processPrediction(prediction);
            ack.acknowledge();
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de predição: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "report_topic", groupId = "report-group")
    public void listenReports(String message, Acknowledgment ack) {
        try {
            ReportResponseDTO report = objectMapper.readValue(message, ReportResponseDTO.class);
            processReport(report);
            ack.acknowledge();
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de relatório: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "sales_strategy_topic", groupId = "sales-strategy-group")
    public void listenSalesStrategies(String message, Acknowledgment ack) {
        try {
            SalesStrategyResponseDTO salesStrategy = objectMapper.readValue(message, SalesStrategyResponseDTO.class);
            processSalesStrategy(salesStrategy);
            ack.acknowledge();
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem de estratégia de vendas: {}", e.getMessage(), e);
        }
    }

    private void processCliente(ClienteResponseDTO cliente) {
        logger.info("Processando cliente: ID={}, Nome={}", cliente.getId(), cliente.getNome());
        // Implementação adicional do processamento de clientes
    }

    private void processFeedback(FeedbackResponseDTO feedback) {
        logger.info("Processando feedback: ID={}, Título={}", feedback.getId(), feedback.getTitulo());
        // Implementação adicional do processamento de feedbacks
    }

    private void processPrediction(PredictionResponseDTO prediction) {
        logger.info("Processando predição: ID={}, Título={}", prediction.getId(), prediction.getTitulo());
        // Implementação adicional do processamento de predições
    }

    private void processReport(ReportResponseDTO report) {
        logger.info("Processando relatório: ID={}, Título={}", report.getId(), report.getTitulo());
        // Implementação adicional do processamento de relatórios
    }

    private void processSalesStrategy(SalesStrategyResponseDTO salesStrategy) {
        logger.info("Processando estratégia de vendas: ID={}, Título={}", salesStrategy.getId(), salesStrategy.getTitulo());
        // Implementação adicional do processamento de estratégias de vendas
    }
}
