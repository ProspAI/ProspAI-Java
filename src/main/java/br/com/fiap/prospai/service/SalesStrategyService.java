package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.SalesStrategyRequestDTO;
import br.com.fiap.prospai.dto.response.SalesStrategyResponseDTO;
import br.com.fiap.prospai.entity.SalesStrategy;
import br.com.fiap.prospai.repository.SalesStrategyRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalesStrategyService {

    private final SalesStrategyRepository salesStrategyRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public SalesStrategyService(SalesStrategyRepository salesStrategyRepository, KafkaProducerService kafkaProducerService) {
        this.salesStrategyRepository = salesStrategyRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<SalesStrategyResponseDTO> getAllSalesStrategies() {
        return salesStrategyRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<SalesStrategyResponseDTO> getSalesStrategyById(Long id) {
        return salesStrategyRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public SalesStrategyResponseDTO createSalesStrategy(SalesStrategyRequestDTO salesStrategyRequestDTO) {
        SalesStrategy salesStrategy = new SalesStrategy();
        BeanUtils.copyProperties(salesStrategyRequestDTO, salesStrategy);
        SalesStrategy novaSalesStrategy = salesStrategyRepository.save(salesStrategy);

        // Enviar mensagem ao Kafka após a criação de uma nova estratégia de vendas
        try {
            SalesStrategyResponseDTO salesStrategyResponseDTO = toResponseDTO(novaSalesStrategy);
            kafkaProducerService.sendMessage("sales_strategy_topic", salesStrategyResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(novaSalesStrategy);
    }

    public SalesStrategyResponseDTO updateSalesStrategy(Long id, SalesStrategyRequestDTO salesStrategyRequestDTO) {
        SalesStrategy salesStrategy = salesStrategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales Strategy não encontrada com id: " + id));

        BeanUtils.copyProperties(salesStrategyRequestDTO, salesStrategy, "id");
        SalesStrategy salesStrategyAtualizada = salesStrategyRepository.save(salesStrategy);

        // Enviar mensagem ao Kafka após a atualização de uma estratégia de vendas
        try {
            SalesStrategyResponseDTO salesStrategyResponseDTO = toResponseDTO(salesStrategyAtualizada);
            kafkaProducerService.sendMessage("sales_strategy_topic", salesStrategyResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(salesStrategyAtualizada);
    }

    public void deleteSalesStrategy(Long id) {
        SalesStrategy salesStrategy = salesStrategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales Strategy não encontrada com id: " + id));
        salesStrategyRepository.delete(salesStrategy);

        // Enviar mensagem ao Kafka após a exclusão de uma estratégia de vendas
        try {
            SalesStrategyResponseDTO salesStrategyResponseDTO = toResponseDTO(salesStrategy);
            kafkaProducerService.sendMessage("sales_strategy_topic", salesStrategyResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SalesStrategyResponseDTO toResponseDTO(SalesStrategy salesStrategy) {
        SalesStrategyResponseDTO responseDTO = new SalesStrategyResponseDTO();
        BeanUtils.copyProperties(salesStrategy, responseDTO);
        return responseDTO;
    }
}
