package br.com.fiap.prospai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Envia uma mensagem genérica ao tópico especificado.
     *
     * @param topic   o tópico Kafka
     * @param message a mensagem a ser enviada
     */
    public void sendMessage(String topic, Object message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, jsonMessage);
            logger.info("Mensagem enviada ao tópico {}: {}", topic, jsonMessage);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem para o tópico {}: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar mensagem para o Kafka", e);
        }
    }
}
