package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.ClienteRequestDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.entity.Cliente;
import br.com.fiap.prospai.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Captor
    private ArgumentCaptor<Cliente> clienteCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllClientes_ShouldReturnListOfClientes() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setEmail("joao@example.com");
        cliente.setTelefone("123456789");
        cliente.setDataCriacao(LocalDateTime.now());

        when(clienteRepository.findAll()).thenReturn(Collections.singletonList(cliente));

        // Act
        List<ClienteResponseDTO> clientes = clienteService.getAllClientes();

        // Assert
        assertNotNull(clientes);
        assertEquals(1, clientes.size());
        assertEquals("João", clientes.get(0).getNome());
    }

    @Test
    void getClienteById_ShouldReturnCliente_WhenExists() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria");
        cliente.setEmail("maria@example.com");
        cliente.setTelefone("987654321");
        cliente.setDataCriacao(LocalDateTime.now());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Act
        Optional<ClienteResponseDTO> result = clienteService.getClienteById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Maria", result.get().getNome());
    }

    @Test
    void getClienteById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<ClienteResponseDTO> result = clienteService.getClienteById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void createCliente_ShouldSaveCliente() {
        // Arrange
        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("Pedro");
        requestDTO.setEmail("pedro@example.com");
        requestDTO.setTelefone("123456789");

        Cliente savedCliente = new Cliente();
        savedCliente.setId(1L);
        savedCliente.setNome("Pedro");
        savedCliente.setEmail("pedro@example.com");
        savedCliente.setTelefone("123456789");
        savedCliente.setDataCriacao(LocalDateTime.now());

        when(clienteRepository.save(any(Cliente.class))).thenReturn(savedCliente);

        // Act
        ClienteResponseDTO responseDTO = clienteService.createCliente(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Pedro", responseDTO.getNome());
        verify(kafkaProducerService, times(1)).sendMessage(eq("clientes_topic"), any(ClienteResponseDTO.class));
    }

    @Test
    void updateCliente_ShouldUpdateExistingCliente() {
        // Arrange
        Cliente existingCliente = new Cliente();
        existingCliente.setId(1L);
        existingCliente.setNome("Ana");
        existingCliente.setEmail("ana@example.com");
        existingCliente.setTelefone("123456789");
        existingCliente.setDataCriacao(LocalDateTime.now());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existingCliente));

        ClienteRequestDTO requestDTO = new ClienteRequestDTO();
        requestDTO.setNome("Ana Maria");
        requestDTO.setEmail("ana.maria@example.com");
        requestDTO.setTelefone("987654321");

        when(clienteRepository.save(any(Cliente.class))).thenReturn(existingCliente);

        // Act
        ClienteResponseDTO responseDTO = clienteService.updateCliente(1L, requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals("Ana Maria", responseDTO.getNome());
        verify(kafkaProducerService, times(1)).sendMessage(eq("clientes_topic"), any(ClienteResponseDTO.class));
    }

    @Test
    void updateCliente_ShouldThrowException_WhenClienteNotFound() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        ClienteRequestDTO requestDTO = new ClienteRequestDTO();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.updateCliente(1L, requestDTO);
        });
        assertEquals("Cliente não encontrado com id: 1", exception.getMessage());
    }

    @Test
    void deleteCliente_ShouldDeleteExistingCliente() {
        // Arrange
        Cliente existingCliente = new Cliente();
        existingCliente.setId(1L);
        existingCliente.setNome("Carlos");
        existingCliente.setEmail("carlos@example.com");
        existingCliente.setTelefone("123456789");
        existingCliente.setDataCriacao(LocalDateTime.now());

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existingCliente));

        // Act
        clienteService.deleteCliente(1L);

        // Assert
        verify(clienteRepository, times(1)).delete(existingCliente);
        verify(kafkaProducerService, times(1)).sendMessage(eq("clientes_topic"), any(ClienteResponseDTO.class));
    }

    @Test
    void deleteCliente_ShouldThrowException_WhenClienteNotFound() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.deleteCliente(1L);
        });
        assertEquals("Cliente não encontrado com id: 1", exception.getMessage());
    }
}
