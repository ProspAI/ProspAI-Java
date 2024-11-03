package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.ClienteRequestDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, KafkaProducerService kafkaProducerService) {
        this.clienteRepository = clienteRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<ClienteResponseDTO> getAllClientes() {
        return clienteRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<ClienteResponseDTO> getClienteById(Long id) {
        return clienteRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public ClienteResponseDTO createCliente(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = new Cliente();
        BeanUtils.copyProperties(clienteRequestDTO, cliente);
        cliente.setDataCriacao(LocalDateTime.now());
        Cliente novoCliente = clienteRepository.save(cliente);

        // Enviar mensagem de cliente criado
        try {
            ClienteResponseDTO clienteResponseDTO = toResponseDTO(novoCliente);
            kafkaProducerService.sendMessage("clientes_topic", clienteResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            // Logar o erro, mas não interromper o fluxo
        }

        return toResponseDTO(novoCliente);
    }

    public ClienteResponseDTO updateCliente(Long id, ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));

        BeanUtils.copyProperties(clienteRequestDTO, cliente, "id", "dataCriacao");
        Cliente clienteAtualizado = clienteRepository.save(cliente);

        // Enviar mensagem de cliente atualizado
        try {
            ClienteResponseDTO clienteResponseDTO = toResponseDTO(clienteAtualizado);
            kafkaProducerService.sendMessage("clientes_topic", clienteResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toResponseDTO(clienteAtualizado);
    }

    public void deleteCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));
        clienteRepository.delete(cliente);

        // Enviar mensagem de cliente deletado
        try {
            ClienteResponseDTO clienteResponseDTO = toResponseDTO(cliente);
            kafkaProducerService.sendMessage("clientes_topic", clienteResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        ClienteResponseDTO responseDTO = new ClienteResponseDTO();
        BeanUtils.copyProperties(cliente, responseDTO);
        return responseDTO;
    }
}
