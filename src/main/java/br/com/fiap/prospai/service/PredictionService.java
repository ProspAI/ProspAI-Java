package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.PredictionRequestDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.dto.response.PredictionResponseDTO;
import br.com.fiap.prospai.entity.Prediction;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.PredictionRepository;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final ClienteRepository clienteRepository;

    public PredictionService(PredictionRepository predictionRepository, ClienteRepository clienteRepository) {
        this.predictionRepository = predictionRepository;
        this.clienteRepository = clienteRepository;
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

        // Cria uma nova entidade Prediction e copia as propriedades do DTO
        Prediction prediction = new Prediction();
        BeanUtils.copyProperties(predictionRequestDTO, prediction);
        prediction.setDataGeracao(LocalDateTime.now());
        prediction.setCliente(cliente);  // Associa o cliente à predição

        // Salva a nova predição no repositório
        Prediction novaPrediction = predictionRepository.save(prediction);
        return toResponseDTO(novaPrediction);
    }

    public PredictionResponseDTO updatePrediction(Long id, PredictionRequestDTO predictionRequestDTO) {
        // Verifica se a predição existe no banco de dados
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prediction não encontrada com id: " + id));

        // Copia as propriedades do DTO para a entidade Prediction existente, excluindo certos campos
        BeanUtils.copyProperties(predictionRequestDTO, prediction, "id", "dataGeracao", "cliente");

        // Verifica se o cliente associado à predição existe
        Cliente cliente = clienteRepository.findById(predictionRequestDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + predictionRequestDTO.getClienteId()));
        prediction.setCliente(cliente);  // Atualiza o cliente da predição

        // Salva a predição atualizada no repositório
        Prediction predictionAtualizada = predictionRepository.save(prediction);
        return toResponseDTO(predictionAtualizada);
    }

    public void deletePrediction(Long id) {
        // Verifica se a predição existe no banco de dados
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prediction não encontrada com id: " + id));
        predictionRepository.delete(prediction);  // Exclui a predição
    }

    private PredictionResponseDTO toResponseDTO(Prediction prediction) {
        PredictionResponseDTO responseDTO = new PredictionResponseDTO();
        BeanUtils.copyProperties(prediction, responseDTO);
        responseDTO.setCliente(new ClienteResponseDTO(prediction.getCliente()));  // Converte o cliente para DTO
        return responseDTO;
    }
}
