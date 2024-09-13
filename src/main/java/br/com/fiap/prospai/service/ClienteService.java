    package br.com.fiap.prospai.service;

    import br.com.fiap.prospai.dto.request.ClienteRequestDTO;
    import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
    import br.com.fiap.prospai.entity.Cliente;
    import br.com.fiap.prospai.repository.ClienteRepository;
    import org.springframework.beans.BeanUtils;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class ClienteService {

        private final ClienteRepository clienteRepository;

        public ClienteService(ClienteRepository clienteRepository) {
            this.clienteRepository = clienteRepository;
        }

        public List<ClienteResponseDTO> getAllClientes() {
            List<Cliente> clientes = clienteRepository.findAll();
            return clientes.stream()
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
            return toResponseDTO(novoCliente);
        }

        public ClienteResponseDTO updateCliente(Long id, ClienteRequestDTO clienteRequestDTO) {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));

            BeanUtils.copyProperties(clienteRequestDTO, cliente, "id", "dataCriacao");
            Cliente clienteAtualizado = clienteRepository.save(cliente);
            return toResponseDTO(clienteAtualizado);
        }

        public void deleteCliente(Long id) {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));
            clienteRepository.delete(cliente);
        }

        private ClienteResponseDTO toResponseDTO(Cliente cliente) {
            ClienteResponseDTO responseDTO = new ClienteResponseDTO();
            BeanUtils.copyProperties(cliente, responseDTO);
            return responseDTO;
        }
    }
